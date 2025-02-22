package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.dtos.UserDto;
import cz.syntaxbro.erpsystem.validates.LoginRequest;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;

public interface AuthService {

    void registerUser(SignUpRequest signUpRequest);

    String authenticateUser(LoginRequest loginRequest);

    UserDto getCurrentUser();
}
