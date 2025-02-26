package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User createUser(CreateUserRequest createUserRequest);

    User updateUser(Long id, CreateUserRequest userDto);

    void deleteUser(Long id);

    User getUserByEmail(String email);
}