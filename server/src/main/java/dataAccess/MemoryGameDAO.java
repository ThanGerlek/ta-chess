package dataAccess;

import http.GameListItem;
import model.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A DAO (Data Access Object) for CRUD operations on Games currently being played.
 */
public class MemoryGameDAO implements GameDAO {

    private final Map<Integer, Game> gameDatabase = new HashMap<>();
    private final UserDAO userDAO;
    private int maxUsedGameID = 0;

    public MemoryGameDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Inserts a new Game into the database.
     *
     * @param game the Game to insert
     * @throws DataAccessException if a Game with the same gameID already exists
     */
    public void insertNewGame(Game game) throws DataAccessException {
        // Failures: can't access database; game already exists (same gameID)
        Integer id = game.gameID();
        if (gameDatabase.containsKey(id)) {
            throw new ValueAlreadyTakenException(
                    "Tried to insert a new Game with a gameID that already exists in the database");
        }
        gameDatabase.put(id, game);
        maxUsedGameID = Math.max(maxUsedGameID, game.gameID());
    }

    /**
     * Fetches the Game with the given ID from the database.
     *
     * @param gameID the ID of the Game to return
     * @return the Game with the given ID
     * @throws DataAccessException if a Game with the given ID was not found
     */
    public Game findGame(int gameID) throws DataAccessException {
        // Failures: can't access database; game not found
        assertIDExists(gameID);
        return gameDatabase.get(gameID);
    }

    /**
     * Returns a list containing data about each Game in the database.
     *
     * @return a list of data about each Game in the database
     */
    public ArrayList<GameListItem> allGames() throws DataAccessException {
        // Failures: can't access database
        ArrayList<GameListItem> gameList = new ArrayList<>();
        for (Game game : gameDatabase.values()) {
            // TODO remove hack making empty strings nulls
            // provided Service tests require null, not ""
            String whiteUsername = (game.whiteUsername().isEmpty()) ? null : game.whiteUsername();
            String blackUsername = (game.blackUsername().isEmpty()) ? null : game.blackUsername();
            gameList.add(new GameListItem(game.gameID(), whiteUsername, blackUsername, game.gameName()));
        }
        return gameList;
    }

    /**
     * Assigns a role to a user if not already assigned.
     *
     * @param gameID   the ID of the game to add the user to
     * @param username the username of the user
     * @param role     the role to assign to the user
     * @throws DataAccessException if the game or the user was not found
     */
    public void assignPlayerRole(int gameID, String username, PlayerRole role) throws DataAccessException {
        // Failures: can't access database; game not found; user not found
        assertIDExists(gameID);

        if (!userDAO.hasUser(username)) {
            throw new UnauthorizedAccessException("Unrecognized username");
        }

        if (role == null) role = PlayerRole.SPECTATOR;

        // TODO style: neaten this up using game.hasRole()
        Game game = gameDatabase.get(gameID);
        if (PlayerRole.WHITE_PLAYER.equals(role)) {
            if (!game.whiteUsername().isEmpty() && !game.whiteUsername().equals(username)) {
                throw new ValueAlreadyTakenException("Role already taken.");
            } else {
                game.setWhiteUsername(username);
            }
        } else if (PlayerRole.BLACK_PLAYER.equals(role)) {
            if (!game.blackUsername().isEmpty() && !game.blackUsername().equals(username)) {
                throw new ValueAlreadyTakenException("Role already taken.");
            } else {
                game.setBlackUsername(username);
            }
        } else if (PlayerRole.SPECTATOR.equals(role)) {
            game.addSpectator(username);
        }

        /* TODO Not handled:
        role already claimed by different user (violation of SRP?)
        user already claimed different role? (just overwrite?) (violation of SRP?)
        (if user already has the role, just return.)
        */
    }

    /**
     * Updates the game state of a Game in the database to match the given version.
     *
     * @param game the updated version of the Game
     * @throws DataAccessException if no Game with a matching gameID was found
     */
    public void updateGameState(Game game) throws DataAccessException {
        // Failures: can't access database; game not found
        assertIDExists(game.gameID());
        gameDatabase.put(game.gameID(), game);

        /* Not handled:
        games don't match, i.e. different players (violation of SRP)
        */
    }

    /**
     * Removes a single game from the database.
     *
     * @param gameID the ID of the Game to remove
     */
    public void removeGame(int gameID) throws DataAccessException {
        // Failures: can't access database (if game DNE, just return)
        gameDatabase.remove(gameID);
    }

    /**
     * Removes every game from the database.
     */
    public void clearGames() throws DataAccessException {
        // Failures: can't access database (if no games, just return)
        gameDatabase.clear();
    }

    /**
     * Generates a new, unused gameID.
     *
     * @return a new gameID
     */
    public int generateNewGameID() {
        maxUsedGameID++;
        return maxUsedGameID;
    }

    private void assertIDExists(Integer gameID) throws DataAccessException {
        if (!gameDatabase.containsKey(gameID)) {
            throw new NoSuchItemException("Tried to access a Game with an unrecognized gameID");
        }
    }
}
