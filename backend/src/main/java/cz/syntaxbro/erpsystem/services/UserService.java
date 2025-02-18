package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    User getUserByUsername(String username);

    UserDto getUserById(Long id);

    @Transactional
    UserDto createUser(CreateUserRequest request);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    void importUsersFromCsv(MultipartFile file);
}