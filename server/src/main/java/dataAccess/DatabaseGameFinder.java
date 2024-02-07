package dataAccess;

import chess.ChessGame;
import http.ChessSerializer;
import model.Game;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseGameFinder {

    public static Game find(int gameID) throws DataAccessException {
        GameQueryResult gameQueryResult = queryForGame(gameID);
        String gameName = gameQueryResult.gameName();
        String chessGameJson = gameQueryResult.chessGameJson();

        ChessGame chessGame = ChessSerializer.gson().fromJson(chessGameJson, ChessGame.class);

        Game game = new Game(gameID, gameName, chessGame);

        ArrayList<RoleQueryResult> roleQueryResults = queryForRoles(gameID);
        parseRolesIntoGameObject(game, roleQueryResults);

        return game;
    }

    private static GameQueryResult queryForGame(int gameID) throws DataAccessException {
        String sqlString = "SELECT gameName, game FROM games WHERE gameId=?";
        GameQueryResult result;

        Connection conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setInt(1, gameID);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    String msg = String.format("Tried to access a Game with an unrecognized gameID: '%d'", gameID);
                    throw new NoSuchItemException(msg);
                }
                String gameName = rs.getString(1);
                String chessGameJson = rs.getString(2);
                result = new GameQueryResult(gameName, chessGameJson);
            }

        } catch (SQLException e) {
            System.out.println("Failed to run executeQuery() on SQL String: `" + sqlString + "`");
            throw new DataAccessException(e.getMessage());
            // TODO return connection?
//        } finally {
//            database.returnConnection(conn);
        }

        return result;
    }

    private static ArrayList<RoleQueryResult> queryForRoles(int gameID) throws DataAccessException {
        String sqlString = "SELECT username, role FROM roles WHERE gameId=?";
        ArrayList<RoleQueryResult> results = new ArrayList<>();

        Connection conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(sqlString)) {
            preparedStatement.setInt(1, gameID);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString(1);
                    String role = rs.getString(2);
                    results.add(new RoleQueryResult(username, role));
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to run executeQuery() on SQL String: `" + sqlString + "`");
            throw new DataAccessException(e.getMessage());
            // TODO return connection?
//        } finally {
//            database.returnConnection(conn);
        }
        return results;
    }

    private static void parseRolesIntoGameObject(Game game, ArrayList<RoleQueryResult> roleQueryResults) {
        for (RoleQueryResult roleResult : roleQueryResults) {
            String username = roleResult.username();
            PlayerRole role = PlayerRole.stringToRole(roleResult.role());

            if (role.equals(PlayerRole.WHITE_PLAYER)) {
                game.setWhiteUsername(username);
            } else if (role.equals(PlayerRole.BLACK_PLAYER)) {
                game.setBlackUsername(username);
            } else {
                game.addSpectator(username);
            }
        }
    }

    record GameQueryResult(String gameName, String chessGameJson) {
    }

    record RoleQueryResult(String username, String role) {
    }
}
