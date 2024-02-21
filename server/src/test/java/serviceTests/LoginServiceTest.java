package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.UnauthorizedAccessException;
import http.AuthResponse;
import http.LoginRequest;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.LoginService;

class LoginServiceTest extends ServiceTest {
    private LoginService service;

    // TODO 200, 401 forbidden, 500?

    @BeforeEach
    void setUp() throws DataAccessException {
        initDAOs();
        userDAO.insertNewUser(new User("user1", "pass1", "mail1"));
        service = new LoginService(authDAO, userDAO);
    }

    // Positive test
    @Test
    void login_successfully_returns_valid_authToken() throws DataAccessException {
        AuthResponse response = service.login(new LoginRequest("user1", "pass1"));
        Assertions.assertTrue(authDAO.isValidAuthToken(response.authToken()));
    }

    // Negative test
    @Test
    void login_incorrect_password_returns_forbidden() {
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> service.login(new LoginRequest("user1", "iAmIncorrect")));
    }

    @Test
    void login_successfully_returns_okay() throws DataAccessException {
        AuthResponse response = service.login(new LoginRequest("user1", "pass1"));
        Assertions.assertEquals("Okay!", response.message());
    }

    @Test
    void login_successfully_returns_authToken() throws DataAccessException {
        AuthResponse response = service.login(new LoginRequest("user1", "pass1"));
        Assertions.assertNotNull(response.authToken());
    }

    @Test
    void login_incorrect_username_returns_forbidden() {
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> service.login(new LoginRequest("iAmIncorrect", "pass1")));
    }

}