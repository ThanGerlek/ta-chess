package dataAccess;

import model.User;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A DAO (Data Access Object) for CRUD operations on the list of registered Users.
 */
public class MemoryUserDAO implements UserDAO {

    private final ArrayList<User> userDatabase = new ArrayList<>();

    /**
     * Adds a new User to the database.
     *
     * @param user the User to insert
     * @throws DataAccessException if the username is already in the database
     */
    public void insertNewUser(User user) throws DataAccessException {
        // Failures: can't access database; username already exists
        if (hasUser(user.username())) {
            throw new ValueAlreadyTakenException("Tried to insert a user with an already-taken username");
        }
        userDatabase.add(user);
    }

    /**
     * Gets the User with the given username from the database.
     *
     * @param username the username of the User to fetch
     * @return the User
     * @throws DataAccessException if the User was not found
     */
    public User getUser(String username) throws DataAccessException {
        // Failures: can't access database; user not found
        for (User user : userDatabase) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new NoSuchItemException("User not found");
    }

    /**
     * Returns true if a User with the given username exists in the database.
     *
     * @param username the username of the User to fetch
     * @return true if the User was found, false otherwise
     */
    public boolean hasUser(String username) throws DataAccessException {
        // Failures: can't access database
        for (User user : userDatabase) {
            if (Objects.equals(user.username(), username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a single User from the database.
     *
     * @param user the User to remove
     */
    public void removeUser(User user) throws DataAccessException {
        // Failures: can't access database (if user DNE, just return)
        userDatabase.remove(user);
    }

    /**
     * Removes every User from the database.
     */
    public void clearUsers() throws DataAccessException {
        // Failures: can't access database (if no users, just return)
        userDatabase.clear();
    }
}
