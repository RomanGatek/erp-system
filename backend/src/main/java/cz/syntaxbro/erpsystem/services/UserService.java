package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;


import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    User getUserByUsername(String username);

    UserDto getUserById(Long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}