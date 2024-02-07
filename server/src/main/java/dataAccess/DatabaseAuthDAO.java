package dataAccess;

import model.AuthToken;

import java.util.ArrayList;

public class DatabaseAuthDAO implements AuthDAO {
    private static final String CREATE_AUTH_TABLE = """
            CREATE TABLE IF NOT EXISTS auth (
                id INT NOT NULL AUTO_INCREMENT,
                token VARCHAR(128) NOT NULL UNIQUE,
                username VARCHAR(128) NOT NULL,
                PRIMARY KEY (id),
                UNIQUE INDEX (token)
            )""";
    private final UserDAO userDAO;

    public DatabaseAuthDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Set up this AuthDAO.
     */
    @Override
    public void initialize() throws DataAccessException {
        ChessDatabaseManager.update(CREATE_AUTH_TABLE);
    }

    /**
     * Registers the given {@code AuthToken} as a valid token.
     *
     * @param token the AuthToken to register
     * @throws DataAccessException if the AuthToken already exists or the user doesn't exist
     */
    @Override
    public void addAuthToken(AuthToken token) throws DataAccessException {
        // Failures: token already exists (incl. for different user), username doesn't exist

        if (!userDAO.hasUser(token.username())) {
            throw new UnauthorizedAccessException("User not found");
        }
        if (isValidAuthToken(token.authToken())) {
            throw new ValueAlreadyTakenException("Tried to register an already existing token");
        }

        String sqlString = "INSERT INTO auth (token, username) VALUES (?, ?)";
        ChessDatabaseManager.update(sqlString, (preparedStatement -> {
            preparedStatement.setString(1, token.authToken());
            preparedStatement.setString(2, token.username());
        }));
    }

    /**
     * Checks if the given {@code AuthToken} is currently valid.
     *
     * @param token the token to validate
     * @return true iff the given token is currently valid
     * @throws DataAccessException if there was a problem accessing the data store
     */
    @Override
    public boolean isValidAuthToken(String token) throws DataAccessException {
        return ChessDatabaseManager.booleanQueryWithParam("SELECT id FROM auth WHERE token=?", token);
    }

    /**
     * Invalidates the given {@code AuthToken}. Future calls requiring authorization for the given user will need to
     * generate a new token by re-authenticating.
     *
     * @param token the token to invalidate
     */
    @Override
    public void removeAuthToken(String token) throws DataAccessException {
        ChessDatabaseManager.updateWithParam("DELETE FROM auth WHERE token=?", token);
    }

    /**
     * Invalidates every currently valid {@code AuthToken}. Future calls requiring authorization will need to generate
     * new tokens by re-authenticating.
     */
    @Override
    public void clearAuthTokens() throws DataAccessException {
        ChessDatabaseManager.update("TRUNCATE auth");
    }

    /**
     * Returns the username corresponding to the given token.
     *
     * @param authToken the token to look up
     * @return the username corresponding to the token
     * @throws DataAccessException if the token is invalid
     */
    @Override
    public String getUsername(String authToken) throws DataAccessException {
        // Failures: token is invalid
        String sqlString = "SELECT username FROM auth WHERE token=?";
        ArrayList<String> usernames = ChessDatabaseManager.queryForString("username", sqlString, preparedStatement -> {
            preparedStatement.setString(1, authToken);
        });

        if (usernames.isEmpty()) {
            throw new UnauthorizedAccessException("Could not get username of invalid token");
        }
        return usernames.get(0);
    }
}


