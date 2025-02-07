package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.dtos.LoginRequest;
import cz.syntaxbro.erpsystem.models.dtos.SignUpRequest;
import cz.syntaxbro.erpsystem.models.dtos.UserDto;

public interface AuthService {

    void registerUser(SignUpRequest signUpRequest);

    String authenticateUser(LoginRequest loginRequest);

    UserDto getCurrentUser();
}
