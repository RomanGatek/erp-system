package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.User;
import cz.syntaxbro.erpsystem.requests.LoginRequest;
import cz.syntaxbro.erpsystem.requests.SignUpRequest;

public interface AuthService {

    void registerUser(SignUpRequest signUpRequest);

    String authenticateUser(LoginRequest loginRequest);

    User getCurrentUser();
}
