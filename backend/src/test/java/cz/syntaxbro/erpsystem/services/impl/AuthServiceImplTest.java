package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;


class AuthServiceImplTest {

    AuthServiceImpl authServiceImpl;

    @Mock
    UserRepository userRepository;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void registerUser() {

    }

    @Test
    void authenticateUser() {
    }
}