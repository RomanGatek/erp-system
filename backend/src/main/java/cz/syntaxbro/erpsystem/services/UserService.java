package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createUser(CreateUserRequest createUserRequest);

    UserDto updateUser(Long id, CreateUserRequest userDto);

    void deleteUser(Long id);
}