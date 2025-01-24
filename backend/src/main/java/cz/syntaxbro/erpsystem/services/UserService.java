// REVIEWED

package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.dtos.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}