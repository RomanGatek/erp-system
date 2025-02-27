package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.partials.UserPartial;
import cz.syntaxbro.erpsystem.requests.CreateUserRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User createUser(CreateUserRequest createUserRequest);

    User updateUser(Long id, UserPartial userDto);

    void deleteUser(Long id);

    User getUserByEmail(String email);
}