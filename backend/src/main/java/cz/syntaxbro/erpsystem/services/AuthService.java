package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.validates.LoginRequest;
import cz.syntaxbro.erpsystem.validates.SignUpRequest;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;

public interface AuthService {

    void registerUser(SignUpRequest signUpRequest);

    String authenticateUser(LoginRequest loginRequest);

    UserDto getCurrentUser();
}
