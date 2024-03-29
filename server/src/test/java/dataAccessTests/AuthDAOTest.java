package dataAccessTests;

import dataAccess.*;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    private static final boolean IS_SQL_DAO = false;

    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    private static final User validUser = new User("validUser", "password", "email");
    private static final AuthToken validToken = new AuthToken("validTokenString", validUser.username());

    @BeforeAll
    static void init() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        userDAO.clearUsers();
        userDAO.insertNewUser(validUser);
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = IS_SQL_DAO ? new DatabaseAuthDAO(userDAO) : new MemoryAuthDAO(userDAO);
        authDAO.clearAuthTokens();
    }

    @AfterAll
    static void deInit() throws DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuthTokens();
    }

    @Test
    void add_new_authToken_makes_token_valid() throws DataAccessException {
        authDAO.addAuthToken(validToken);
        assertTrue(authDAO.isValidAuthToken(validToken.authToken()));
    }

    @Test
    void add_existing_authToken_throws() throws DataAccessException {
        authDAO.addAuthToken(validToken);
        assertThrows(ValueAlreadyTakenException.class, () -> authDAO.addAuthToken(validToken));
    }

    @Test
    void addAuthToken_with_incorrect_username_throws() {
        AuthToken invalidToken = new AuthToken(validToken.authToken(), "invalidUsername");
        assertThrows(UnauthorizedAccessException.class, () -> authDAO.addAuthToken(invalidToken));
    }

    @Test
    void nonexistent_token_is_invalid() throws DataAccessException {
        AuthToken invalidToken = new AuthToken("invalidTokenString", validUser.username());
        assertFalse(authDAO.isValidAuthToken(invalidToken.authToken()));
    }

    @Test
    void removeAuthToken_makes_token_invalid() throws DataAccessException {
        authDAO.addAuthToken(validToken);
        authDAO.removeAuthToken(validToken.authToken());
        assertFalse(authDAO.isValidAuthToken(validToken.authToken()));
    }

    @Test
    void remove_nonexistent_authToken_does_not_throw() throws DataAccessException {
        authDAO.removeAuthToken("invalidAuthToken");
    }

    @Test
    void clearAuthTokens_makes_tokens_invalid() throws DataAccessException {
        authDAO.addAuthToken(validToken);
        AuthToken validToken2 = new AuthToken("tokenString2", validUser.username());
        authDAO.addAuthToken(validToken2);

        authDAO.clearAuthTokens();
        assertFalse(authDAO.isValidAuthToken(validToken.authToken()));
        assertFalse(authDAO.isValidAuthToken(validToken2.authToken()));
    }

    @Test
    void clearAuthTokens_when_empty_does_not_throw() throws DataAccessException {
        authDAO.clearAuthTokens();
    }

    @Test
    void getUsername_returns_correct_username() throws DataAccessException {
        authDAO.addAuthToken(validToken);
        assertEquals(validUser.username(), authDAO.getUsername(validToken.authToken()));
    }

    @Test
    void getUsername_of_nonexistent_token_throws() {
        assertThrows(UnauthorizedAccessException.class, () -> authDAO.getUsername("invalidTokenString"));
    }
}