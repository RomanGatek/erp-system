package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.UserRequest;
import cz.syntaxbro.erpsystem.repositories.UserRepository;
import cz.syntaxbro.erpsystem.requests.PasswordChangeRequest;
import cz.syntaxbro.erpsystem.security.FileStorageConfig;
import cz.syntaxbro.erpsystem.services.AuthService;
import cz.syntaxbro.erpsystem.services.impl.FileStorageServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageServiceImp fileStorageService;

    @InjectMocks
    private MeController meController;

    private User testUser;

    private final FileStorageConfig fileStorageConfig = new FileStorageConfig();

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setActive(true);
        testUser.setPassword("hashedPassword");
    }

    /**
     * Test: Should return the currently authenticated user.
     */
    @Test
    void getCurrentUser_ShouldReturnUser() {
        when(authService.getCurrentUser()).thenReturn(testUser);

        ResponseEntity<User> response = meController.getCurrentUser();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(testUser);
    }

    /**
     * Test: Should update the currently authenticated user's details.
     */
    @Test
    void updateCurrentUser_ShouldUpdateAndReturnUser() {
        UserRequest updatedUser = new UserRequest();
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setActive(false);

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = meController.updateCurrentUser(updatedUser);

        // Ensure response is not null before accessing its body
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo(updatedUser.getUsername());
        assertThat(response.getBody().getEmail()).isEqualTo(updatedUser.getEmail());

        verify(userRepository, times(1)).save(testUser);
    }

    /**
     * Test: Should change the user's password and return the updated user.
     */
    @Test
    void changePassword_ShouldUpdateUserPassword() {
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setPassword("NewSecurePassword!123");

        when(authService.getCurrentUser()).thenReturn(testUser);

        ResponseEntity<User> response = meController.changePassword(passwordChangeRequest);

        // Ensure response is not null before accessing its body
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPassword()).isNotBlank();

        verify(authService, times(1)).getCurrentUser();
    }

    /**
     * Test: Should update the user's avatar when a valid file is provided.
     */
    @Test
    void updateAvatar_ShouldStoreFileAndReturnUpdatedUser() {
        MultipartFile mockFile = new MockMultipartFile(
                "avatar", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[10]
        );

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(fileStorageService.storeFile(any(MultipartFile.class), anyString())).thenReturn("avatar.jpg");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = meController.updateAvatar(mockFile);

        // Ensure response is not null before accessing its body
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAvatar()).isNotNull().isEqualTo("/uploads/avatar.jpg");

        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class), anyString());
        verify(userRepository, times(1)).save(testUser);
    }

    /**
     * Test: Should throw an exception when the uploaded file is not an image.
     */
    @Test
    void updateAvatar_ShouldThrowException_WhenFileIsNotImage() throws IOException, URISyntaxException {
        MultipartFile invalidFile = new MockMultipartFile(
                "avatar", "avatar.jpg", MediaType.TEXT_PLAIN_VALUE, new byte[10]
        );

        // Handle exception properly to prevent unexpected VM behavior
        Exception exception = assertThrows(RuntimeException.class, () -> meController.updateAvatar(invalidFile));

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Only image files are allowed");

        verify(fileStorageService, never()).storeFile(any(MultipartFile.class), anyString());
        verify(userRepository, never()).save(any(User.class));
        deleteDirectoryRecursively(Paths.get(this.fileStorageConfig.getUploadDir()).toAbsolutePath().normalize());
    }

    static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> stream = Files.walk(path)) {
                stream.sorted(Comparator.reverseOrder()) // nejprve smazat soubory, potom adresáře
                      .forEach(p -> {
                          try {
                              Files.delete(p);
                          } catch (IOException e) {
                              throw new RuntimeException("Chyba při mazání " + p, e);
                          }
                      });
            }
        }
    }
}