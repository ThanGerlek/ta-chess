package server.services;

import dataAccess.DataAccessException;
import dataAccess.UnauthorizedAccessException;
import http.MessageResponse;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogoutServiceTest extends ServiceTest {
    private final User user = new User("user1", "pass1", "mail1");
    private final AuthToken token = new AuthToken("1234", "user1");
    private LogoutService service;

    // TODO 500?

    @BeforeEach
    void setUp() throws DataAccessException {
        initDAOs();
        userDAO.insertNewUser(user);
        authDAO.addAuthToken(token);

        service = new LogoutService(authDAO);
    }

    // Positive test
    @Test
    void logout_existing_user_invalidates_token() throws DataAccessException {
        MessageResponse response = service.logout(token.authToken());
        Assertions.assertFalse(authDAO.isValidAuthToken(token.authToken()));
    }

    // Negative test
    @Test
    void logout_fake_user_returns_bad_request_error() throws DataAccessException {
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> service.logout(new AuthToken("iDoNotExist", "1234").authToken()));
    }

    @Test
    void logout_existing_user_returns_okay() throws DataAccessException {
        MessageResponse response = service.logout(token.authToken());
        Assertions.assertEquals("Okay!", response.message());
    }

    @Test
    void logout_invalid_token_errors() throws DataAccessException {
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> service.logout("iAmIncorrect"));
    }

    @Test
    void logout_token_twice_errors() throws DataAccessException {
        service.logout(token.authToken());
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> service.logout(token.authToken()));
    }

}