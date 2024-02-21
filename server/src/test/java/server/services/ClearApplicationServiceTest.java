package server.services;

import dataAccess.DataAccessException;
import dataAccess.NoSuchItemException;
import http.MessageResponse;
import model.AuthToken;
import model.Game;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClearApplicationServiceTest extends ServiceTest {
    private ClearApplicationService service;

    // TODO 500?

    @BeforeEach
    void setUp() {
        initDAOs();
        service = new ClearApplicationService(authDAO, gameDAO, userDAO, null);
    }

    // Positive test
    @Test
    void has_cleared_Users_is_false() throws DataAccessException {
        userDAO.insertNewUser(new User("user1", "pass1", "mail1"));
        userDAO.insertNewUser(new User("user2", "pass2", "mail2"));

        service.clearApplication();

        Assertions.assertFalse(userDAO.hasUser("user1"));
        Assertions.assertFalse(userDAO.hasUser("user2"));
    }

    @Test
    void clearing_does_not_throw() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            MessageResponse response = service.clearApplication();
        });
    }

    @Test
    void clearing_twice_does_not_throw() throws DataAccessException {
        service.clearApplication();
        Assertions.assertDoesNotThrow(() -> {
            service.clearApplication();
        });
    }

    @Test
    void finding_cleared_Games_errors() throws DataAccessException {
        gameDAO.insertNewGame(new Game(1, "game1"));
        gameDAO.insertNewGame(new Game(2, "game2"));

        service.clearApplication();

        Assertions.assertThrows(NoSuchItemException.class, () -> gameDAO.findGame(1));
        Assertions.assertThrows(NoSuchItemException.class, () -> gameDAO.findGame(2));
    }

    @Test
    void cleared_AuthTokens_are_invalid() throws DataAccessException {
        AuthToken token1 = new AuthToken("1234", "user1");
        AuthToken token2 = new AuthToken("2468", "user2");
        userDAO.insertNewUser(new User("user1", "pass1", "mail1"));
        userDAO.insertNewUser(new User("user2", "pass2", "mail2"));
        authDAO.addAuthToken(token1);
        authDAO.addAuthToken(token2);

        service.clearApplication();

        Assertions.assertFalse(authDAO.isValidAuthToken(token1.authToken()));
        Assertions.assertFalse(authDAO.isValidAuthToken(token2.authToken()));
    }
}