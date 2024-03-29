package dataAccessTests;

import dataAccess.*;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private static final boolean IS_SQL_DAO = false;

    private static UserDAO userDAO;

    private static final User user = new User("validUser", "password", "email");

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = IS_SQL_DAO ? new DatabaseUserDAO() : new MemoryUserDAO();
        userDAO.clearUsers();
    }

    @AfterAll
    static void deInit() throws DataAccessException {
        userDAO.clearUsers();
    }

    @Test
    void get_inserted_user_has_correct_username() throws DataAccessException {
        userDAO.insertNewUser(user);
        User retrievedUser = userDAO.getUser(user.username());
        assertEquals(retrievedUser.username(), user.username());
    }

    @Test
    void hasUser_on_inserted_user() throws DataAccessException {
        userDAO.insertNewUser(user);
        assertTrue(userDAO.hasUser(user.username()));
    }

    @Test
    void insert_existing_user_throws() throws DataAccessException {
        userDAO.insertNewUser(user);
        assertThrows(ValueAlreadyTakenException.class, () -> userDAO.insertNewUser(user));
    }

    @Test
    void hasUser_on_nonexistent_user() throws DataAccessException {
        assertFalse(userDAO.hasUser("invalidUsername"));
    }

    @Test
    void hasUser_on_removed_user() throws DataAccessException {
        userDAO.insertNewUser(user);
        userDAO.removeUser(user);
        assertFalse(userDAO.hasUser("invalidUsername"));
    }

    @Test
    void remove_nonexistent_user_does_not_throw() throws DataAccessException {
        userDAO.removeUser(user);
    }

    @Test
    void hasUser_on_cleared_users() throws DataAccessException {
        User user2 = new User("username2", "pass2", "email2");
        userDAO.insertNewUser(user);
        userDAO.insertNewUser(user2);
        userDAO.clearUsers();
        assertFalse(userDAO.hasUser(user.username()));
        assertFalse(userDAO.hasUser(user2.username()));
    }

    @Test
    void clearUsers_when_empty_does_not_throw() throws DataAccessException {
        userDAO.clearUsers();
    }
}