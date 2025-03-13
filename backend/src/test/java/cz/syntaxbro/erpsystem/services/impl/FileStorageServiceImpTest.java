package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.security.FileStorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
class FileStorageServiceImpTest {

    @Mock
    private FileStorageConfig fileStorageConfig;

    private FileStorageServiceImp fileStorageService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        String uploadDir = "../frontend/public/uploads";
        when(fileStorageConfig.getUploadDir()).thenReturn(uploadDir);
        
        // Vytvořit skutečnou instanci třídy, kterou testujeme
        fileStorageService = new FileStorageServiceImp(fileStorageConfig);
    }

    @Test
    void storeFile() throws IOException {
        // Připravit testovací data
        String username = UUID.randomUUID().toString();
        String fileName = username + ".txt";
        
        // Vytvořit a nakonfigurovat mock
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getContentType()).thenReturn("text/plain");
        
        // Mockovat getInputStream místo transferTo
        // FileStorageServiceImp používá file.getInputStream(), ne file.transferTo()
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("testový obsah".getBytes()));
        
        // Zavolat testovanou metodu
        String result = fileStorageService.storeFile(mockFile, username);
        
        // Asserty
        assertNotNull(result, "Vrácený název souboru by neměl být null");
        assertTrue(result.contains(username), "Vrácený název souboru by měl obsahovat uživatelské jméno");
        assertTrue(result.endsWith(".txt"), "Vrácený název souboru by měl končit správnou příponou");
        
        // Ověřit, že byly volány očekávané metody na mock objektech
        verify(mockFile).getOriginalFilename();
        verify(mockFile).getInputStream();  // Ověřit správnou metodu
    }
    
    @Test
    void storeFileWithInvalidOriginalFileName() {
        // Test scénáře, kdy je originalFilename null
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(null);
        
        String username = "testUser";
        
        // Očekáváme výjimku při zpracování null názvu souboru
        Exception exception = assertThrows(RuntimeException.class, () -> fileStorageService.storeFile(mockFile, username));

        assertNotNull(exception, "Měla by být vyhozena výjimka při null názvu souboru");
    }
}