package dataAccess;

import http.ChessSerializer;
import http.GameListItem;
import model.Game;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseGameDAO implements GameDAO {
    private static final String CREATE_GAMES_TABLE = """
            CREATE TABLE IF NOT EXISTS games (
                gameId INT NOT NULL AUTO_INCREMENT,
                gameName VARCHAR(256),
                game TEXT NOT NULL,
                PRIMARY KEY (gameId)
            )""";
    private static final String CREATE_ROLES_TABLE = """
            CREATE TABLE IF NOT EXISTS roles (
                id INT NOT NULL AUTO_INCREMENT,
                gameId INT NOT NULL,
                username VARCHAR(128) NOT NULL,
                role VARCHAR(32) NOT NULL,
                PRIMARY KEY (id),
                INDEX (gameId)
            )""";
    private final UserDAO userDAO;

    public DatabaseGameDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Set up this GameDAO.
     */
    @Override
    public void initialize() throws DataAccessException {
        ChessDatabaseManager.update(CREATE_GAMES_TABLE);
        ChessDatabaseManager.update(CREATE_ROLES_TABLE);
    }

    /**
     * Inserts a new Game into the database.
     *
     * @param game the Game to insert
     * @throws DataAccessException if a Game with the same gameID already exists
     */
    @Override
    public void insertNewGame(Game game) throws DataAccessException {
        // Failures: game already exists (same gameID)
        String sqlString = "SELECT gameId FROM games WHERE gameId=?";
        boolean gameIdAlreadyExists = ChessDatabaseManager.booleanQueryWithParam(sqlString, game.gameID());
        if (gameIdAlreadyExists) {
            String msg = "Tried to insert a new Game with a gameID that already exists in the database";
            throw new ValueAlreadyTakenException(msg);
        }

        String chessGameStr = ChessSerializer.gson().toJson(game.chessGame());

        ChessDatabaseManager.update("INSERT INTO games (gameId, gameName, game) VALUES (?, ?, ?)", preparedStatement -> {
            preparedStatement.setInt(1, game.gameID());
            preparedStatement.setString(2, game.gameName());
            preparedStatement.setString(3, chessGameStr);
        });
    }

    /**
     * Fetches the Game with the given ID from the database.
     *
     * @param gameID the ID of the {@code Game} to return
     * @return the fetched {@code Game}
     * @throws DataAccessException if a {@code Game} with the given ID was not found
     */
    @Override
    public Game findGame(int gameID) throws DataAccessException {
        // Failures: game not found
        return DatabaseGameFinder.find(gameID);
    }

    /**
     * Returns a list containing data about each Game in the database.
     *
     * @return a list of data about each Game in the database
     */
    @Override
    public ArrayList<GameListItem> allGames() throws DataAccessException {
        ArrayList<GameListItem> gameListItems = new ArrayList<>();

        String sqlString = """
                SELECT
                    games.gameName,
                    games.gameId,
                    MAX(CASE WHEN roles.role='white' THEN roles.username END) AS whiteUsername,
                    MAX(CASE WHEN roles.role='black' THEN roles.username END) AS blackUsername
                FROM games LEFT JOIN roles ON games.gameId=roles.gameId
                GROUP BY games.gameId
                """;
        Connection conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(sqlString)) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    String gameName = rs.getString("gameName");
                    int gameID = rs.getInt("gameId");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    GameListItem item = new GameListItem(gameID, whiteUsername, blackUsername, gameName);
                    gameListItems.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to run executeQuery() on SQL String: `" + sqlString + "`");
            throw new DataAccessException(e.getMessage());
            // TODO return connection?
//        } finally {
//            database.returnConnection(conn);
        }

        return gameListItems;
    }

    /**
     * Assigns a role to a user if not already assigned.
     *
     * @param gameID   the ID of the game to add the user to
     * @param username the username of the user
     * @param role     the role to assign to the user
     * @throws DataAccessException if the game or the user was not found
     */
    @Override
    public void assignPlayerRole(int gameID, String username, PlayerRole role) throws DataAccessException {
        // Failures: game not found, user not found
        assertIDExists(gameID);
        if (!userDAO.hasUser(username)) {
            throw new UnauthorizedAccessException("Unrecognized username");
        }

        if (role == null) role = PlayerRole.SPECTATOR;

        Game game = findGame(gameID);
        if (isRoleTakenForUser(game, username, role)) {
            String msg = String.format("Failed to assign player role %s, that role is already taken", role);
            throw new ValueAlreadyTakenException(msg);
        }

        String roleString = PlayerRole.roleToString(role);
        ChessDatabaseManager.update("INSERT INTO roles (username, role, gameId) VALUES (?, ?, ?)", preparedStatement -> {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, roleString);
            preparedStatement.setInt(3, gameID);
        });
    }

    private void assertIDExists(int gameID) throws DataAccessException {
        if (!ChessDatabaseManager.booleanQueryWithParam("SELECT gameId FROM games WHERE gameId=?", gameID)) {
            String msg = String.format("Tried to access a Game with an unrecognized gameID: '%d'", gameID);
            throw new NoSuchItemException(msg);
        }
    }

    private boolean isRoleTakenForUser(Game game, String username, PlayerRole role) {
        if (PlayerRole.WHITE_PLAYER.equals(role)) {
            return game.whiteUsername() != null && !game.whiteUsername().isEmpty() &&
                    !game.whiteUsername().equals(username);
        } else if (PlayerRole.BLACK_PLAYER.equals(role)) {
            return game.blackUsername() != null && !game.blackUsername().isEmpty() &&
                    !game.blackUsername().equals(username);
        } else if (PlayerRole.SPECTATOR.equals(role)) {
            return false;
        } else {
            throw new IllegalArgumentException("Called isRoleTakenForUser() with an unrecognized role type");
        }
    }

    /**
     * Updates the game state of a Game in the database to match the given version.
     *
     * @param game the updated version of the Game
     * @throws DataAccessException if no Game with a matching gameID was found
     */
    @Override
    public void updateGameState(Game game) throws DataAccessException {
        // Failures: game not found
        assertIDExists(game.gameID());

        String chessGameString = ChessSerializer.gson().toJson(game.chessGame());

        ChessDatabaseManager.update("UPDATE games SET game=? WHERE gameId=?", preparedStatement -> {
            preparedStatement.setString(1, chessGameString);
            preparedStatement.setInt(2, game.gameID());
        });
    }

    /**
     * Removes a single game from the database.
     *
     * @param gameID the ID of the Game to remove
     */
    @Override
    public void removeGame(int gameID) throws DataAccessException {
        ChessDatabaseManager.updateWithParam("DELETE FROM games WHERE gameId=?", gameID);
        ChessDatabaseManager.updateWithParam("DELETE FROM roles WHERE gameId=?", gameID);
    }

    /**
     * Removes every game from the database.
     */
    @Override
    public void clearGames() throws DataAccessException {
        ChessDatabaseManager.update("TRUNCATE games");
        ChessDatabaseManager.update("TRUNCATE roles");
    }

    /**
     * Generates a new, unused gameID.
     *
     * @return a new gameID
     */
    @Override
    public int generateNewGameID() throws DataAccessException {
        int newID = addEmptyGameAndReturnGeneratedID();
        clearEmptyGames();
        return newID;
    }

    // TODO Extract these into a helper class
    private int addEmptyGameAndReturnGeneratedID() throws DataAccessException {
        String sqlString = "INSERT INTO games (gameName, game) VALUES ('', '')";
        Connection conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(sqlString, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException(
                        "Failed to generateNewGameID: inserted with no errors but no generated keys were returned.");
            } else {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Failed to run executeUpdate() (with generated keys) on SQL String: `" + sqlString + "`");
            throw new DataAccessException(e.getMessage());
            // TODO return connection?
//        } finally {
//            database.returnConnection(conn);
        }
    }

    private void clearEmptyGames() throws DataAccessException {
        String sqlString = "DELETE FROM games WHERE game=''";
        ChessDatabaseManager.update(sqlString);
    }

}
