package dataAccess;

import model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DatabaseUserDAO implements UserDAO {
    private static final String CREATE_USER_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                id INT NOT NULL AUTO_INCREMENT,
                username VARCHAR(128) NOT NULL,
                password VARCHAR(128) NOT NULL,
                email VARCHAR(128),
                PRIMARY KEY (id),
                UNIQUE INDEX (username)
            )""";

    public DatabaseUserDAO() {
    }

    /**
     * Set up this UserDAO.
     */
    @Override
    public void initialize() throws DataAccessException {
        ChessDatabaseManager.update(CREATE_USER_TABLE);
    }

    /**
     * Adds a new User to the database.
     *
     * @param user the User to insert
     * @throws DataAccessException if the username is already in the database
     */
    @Override
    public void insertNewUser(User user) throws DataAccessException {
        // Failures: username already exists
        String sqlString = "SELECT id FROM users WHERE username=?";
        boolean userAlreadyExists = ChessDatabaseManager.booleanQueryWithParam(sqlString, user.username());
        if (userAlreadyExists) {
            throw new ValueAlreadyTakenException("Tried to insert a user with an already-taken username");
        }

        ChessDatabaseManager.update("INSERT INTO users (username, password, email) VALUES (?, ?, ?)", preparedStatement -> {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            if (user.email() == null || user.email().isEmpty()) {
                preparedStatement.setNull(3, Types.VARCHAR);
            } else {
                preparedStatement.setString(3, user.email());
            }
        });
    }

    /**
     * Gets the User with the given username from the database.
     *
     * @param username the username of the User to fetch
     * @return the fetched User
     * @throws DataAccessException if the User was not found
     */
    @Override
    public User getUser(String username) throws DataAccessException {
        // Failures: user not found
        String sqlString = "SELECT password, email FROM users WHERE username=?";
        User user;

        Connection conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    String msg = String.format("Unrecognized username: '%s'", username);
                    throw new NoSuchItemException(msg);
                }
                String password = rs.getString(1);
                String email = rs.getString(2);
                user = new User(username, password, email);
            }

        } catch (SQLException e) {
            System.out.println("Failed to run executeQuery() on SQL String: `" + sqlString + "`");
            throw new DataAccessException(e.getMessage());
            // TODO return connection?
//        } finally {
//            database.returnConnection(conn);
        }

        return user;
    }

    /**
     * Returns true if a User with the given username exists in the database.
     *
     * @param username the username of the User to fetch
     * @return true if the User was found, false otherwise
     */
    @Override
    public boolean hasUser(String username) throws DataAccessException {
        String sqlString = "SELECT username FROM users WHERE username=?";
        return ChessDatabaseManager.booleanQueryWithParam(sqlString, username);
    }

    /**
     * Removes a single user from the database.
     *
     * @param user the user to remove
     */
    @Override
    public void removeUser(User user) throws DataAccessException {
        ChessDatabaseManager.updateWithParam("DELETE FROM users WHERE username=?", user.username());
    }

    /**
     * Removes every user from the database.
     */
    @Override
    public void clearUsers() throws DataAccessException {
        ChessDatabaseManager.update("TRUNCATE users");
    }
}
