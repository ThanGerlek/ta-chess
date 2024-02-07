package dataAccess;

import http.GameListItem;
import model.Game;

import java.util.ArrayList;

/**
 * A DAO (Data Access Object) for CRUD operations on Games currently being played.
 */
public interface GameDAO {

    /**
     * Set up this GameDAO.
     */
    default void initialize() throws DataAccessException {
    }

    /**
     * Inserts a new Game into the database.
     *
     * @param game the Game to insert
     * @throws DataAccessException if a Game with the same gameID already exists
     */
    void insertNewGame(Game game) throws DataAccessException;
        /* Failures
        can't access database
        game already exists (same gameID)
        */

    /**
     * Fetches the Game with the given ID from the database.
     *
     * @param gameID the ID of the {@code Game} to return
     * @return the fetched {@code Game}
     * @throws DataAccessException if a {@code Game} with the given ID was not found
     */
    Game findGame(int gameID) throws DataAccessException;
        /* Failures
        can't access database
        game not found
        */

    /**
     * Returns a list containing data about each Game in the database.
     *
     * @return a list of data about each Game in the database
     */
    ArrayList<GameListItem> allGames() throws DataAccessException;
        /* Failures
        can't access database
        */

    /**
     * Assigns a role to a user if not already assigned.
     *
     * @param gameID   the ID of the game to add the user to
     * @param username the username of the user
     * @param role     the role to assign to the user
     * @throws DataAccessException if the game or the user was not found
     */
    void assignPlayerRole(int gameID, String username, PlayerRole role) throws DataAccessException;
        /* Failures
        can't access database
        game not found
        user not found
        */

        /* TODO Not handled:
        role already claimed by different user (violation of SRP?)
        user already claimed different role? (just overwrite?) (violation of SRP?)
        (if user already has the role, just return.)
        */

    /**
     * Updates the game state of a Game in the database to match the given version.
     *
     * @param game the updated version of the Game
     * @throws DataAccessException if no Game with a matching gameID was found
     */
    void updateGameState(Game game) throws DataAccessException;
        /* Failures
        can't access database
        game not found
        */

        /* Not handled:
        games don't match, i.e. different players (violation of SRP)
        */

    /**
     * Removes a single game from the database.
     *
     * @param gameID the ID of the Game to remove
     */
    void removeGame(int gameID) throws DataAccessException;
        /* Failures
        can't access database
        (if game DNE, just return)
        */

    /**
     * Removes every game from the database.
     */
    void clearGames() throws DataAccessException;
        /* Failures
        can't access database
        (if no games, just return)
        */

    /**
     * Generates a new, unused gameID.
     *
     * @return a new gameID
     */
    int generateNewGameID() throws DataAccessException;
}
