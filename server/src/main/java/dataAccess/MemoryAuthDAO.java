package dataAccess;

import model.AuthToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO Update Javadocs with DataAccessException subclasses

/**
 * A DAO (Data Access Object) for {@code AuthToken} objects and authentication data.
 */
public class MemoryAuthDAO implements AuthDAO {

    private final Map<String, String> tokenDatabase = new HashMap<>();
    private final UserDAO userDAO;

    public MemoryAuthDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers the given {@code AuthToken} as a valid token.
     *
     * @param token the AuthToken to register
     * @throws DataAccessException if the AuthToken already exists or the user doesn't exist
     */
    public void addAuthToken(AuthToken token) throws DataAccessException {
        // Failures: can't access database; token already exists; username doesn't exist
        if (!userDAO.hasUser(token.username())) {
            throw new UnauthorizedAccessException("User not found");
        }

        for (String existingToken : tokenDatabase.keySet()) {
            if (Objects.equals(existingToken, token.authToken())) {
                throw new ValueAlreadyTakenException("Tried to register an already existing token");
            }
        }

        tokenDatabase.put(token.authToken(), token.username());
    }

    /**
     * Checks if the given {@code AuthToken} is currently valid.
     *
     * @param token the token to validate
     * @return true iff the given token is currently valid
     */
    public boolean isValidAuthToken(String token) throws DataAccessException {
        // Failures: can't access database
        for (String existingToken : tokenDatabase.keySet()) {
            if (existingToken.equals(token)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Invalidates the given {@code AuthToken}. Future calls requiring authorization for the given user will need to
     * generate a new token by re-authenticating.
     *
     * @param token the token to invalidate
     */
    public void removeAuthToken(String token) throws DataAccessException {
        // Failures: can't access database, invalid token
        tokenDatabase.remove(token);
    }

    /**
     * Invalidates every currently valid {@code AuthToken}. Future calls requiring authorization will need to generate
     * new tokens by re-authenticating.
     */
    public void clearAuthTokens() throws DataAccessException {
        // Failures: can't access database (if no tokens, just return)
        tokenDatabase.clear();
    }

    /**
     * Returns the username corresponding to the given token.
     *
     * @param authToken the token to look up
     * @return the username corresponding to the token
     * @throws DataAccessException if the token is invalid
     */
    public String getUsername(String authToken) throws DataAccessException {
        if (isValidAuthToken(authToken)) {
            return tokenDatabase.get(authToken);
        } else {
            throw new UnauthorizedAccessException("Could not get username of invalid token");
        }
    }
}
