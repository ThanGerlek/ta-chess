package server.services;

import dataAccess.BadRequestException;
import dataAccess.DataAccessException;
import dataAccess.ValueAlreadyTakenException;
import http.AuthResponse;
import http.RegisterRequest;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterServiceTest extends ServiceTest {
    private RegisterService service;

    // TODO 200, 400 bad req, 500

    @BeforeEach
    void setUp() throws DataAccessException {
        initDAOs();
        userDAO.insertNewUser(new User("existingUser", "existingPass", "existingMail"));
        service = new RegisterService(authDAO, userDAO);
    }

    // Positive test
    @Test
    void get_registered_user_returns_user() throws DataAccessException {
        service.register(new RegisterRequest("user1", "pass1", "mail1"));
        Assertions.assertEquals(userDAO.getUser("user1"), new User("user1", "pass1", "mail1"));
    }

    // Negative test
    @Test
    void register_existing_user_throws_already_taken() {
        Assertions.assertThrows(ValueAlreadyTakenException.class,
                () -> service.register(new RegisterRequest("existingUser", "pass1", "mail1")));
    }

    @Test
    void register_new_user_returns_okay() throws DataAccessException {
        AuthResponse response = service.register(new RegisterRequest("user1", "pass1", "mail1"));
        Assertions.assertEquals("Okay!", response.message());
    }

    @Test
    void register_with_null_username_throws_bad_request_error() {
        Assertions.assertThrows(BadRequestException.class,
                () -> service.register(new RegisterRequest(null, "pass1", "mail1")));
    }

    @Test
    void register_with_null_password_throws_bad_request_error() {
        Assertions.assertThrows(BadRequestException.class,
                () -> service.register(new RegisterRequest("user1", null, "mail1")));
    }

    @Test
    void register_with_null_email_returns_user() throws DataAccessException {
        service.register(new RegisterRequest("user1", "pass1", null));
        Assertions.assertEquals(userDAO.getUser("user1"), new User("user1", "pass1", null));
    }
}