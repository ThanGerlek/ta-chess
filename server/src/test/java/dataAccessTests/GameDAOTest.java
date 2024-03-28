package dataAccessTests;

import chess.*;
import dataAccess.*;
import http.GameListItem;
import model.Game;
import model.User;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {
    private static final boolean IS_SQL_DAO = false;

    private static UserDAO userDAO;
    private static GameDAO gameDAO;

    private static final User user = new User("user1", "pass1", "email1");
    private static final User user2 = new User("user2", "pass2", "email2");
    private static Game game = new Game(1, "GameDAOTest Game");
    private static final Game game2 = new Game(2, "GameDAOTest Game 2");

    private static final int invalidGameID = 42;

    @BeforeAll
    static void init() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        userDAO.clearUsers();
        userDAO.insertNewUser(user);
        userDAO.insertNewUser(user2);
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = IS_SQL_DAO ? new DatabaseGameDAO(userDAO) : new MemoryGameDAO(userDAO);
        gameDAO.clearGames();
        game = new Game(1, "GameDAOTest Game");
    }

    @AfterAll
    static void deInit() throws DataAccessException {
        userDAO.clearUsers();
        gameDAO.clearGames();
    }

    @Test
    void find_preexisting_game_returns_nonnull() throws DataAccessException {
        gameDAO.insertNewGame(game);
        assertNotNull(gameDAO.findGame(game.gameID()));
    }

    @Test
    void insertNewGame_with_preexisting_gameID_throws() throws DataAccessException {
        gameDAO.insertNewGame(game);
        assertThrows(ValueAlreadyTakenException.class, () -> gameDAO.insertNewGame(game));
    }

    @Test
    void find_nonexistent_game_throws() {
        assertThrows(NoSuchItemException.class, () -> gameDAO.findGame(invalidGameID));
    }

    @Test
    void allGames_after_inserting_returns_correctly_sized_list() throws DataAccessException {
        gameDAO.insertNewGame(game);
        gameDAO.insertNewGame(game2);
        Collection<GameListItem> games = gameDAO.allGames();
        assertEquals(2, games.size());
    }

    @Test
    void allGames_without_inserting_returns_empty_list() throws DataAccessException {
        Collection<GameListItem> games = gameDAO.allGames();
        assertEquals(0, games.size());
    }

    @Test
    void assignPlayerRole_WHITE_then_get_has_correct_role_usernames() throws DataAccessException {
        gameDAO.insertNewGame(game);
        System.out.println(gameDAO.findGame(1));
        gameDAO.assignPlayerRole(game.gameID(), user.username(), PlayerRole.WHITE_PLAYER);

        Game retrievedGame = gameDAO.findGame(game.gameID());
        assertEquals(user.username(), retrievedGame.whiteUsername());
        assertEquals("", retrievedGame.blackUsername());
    }

    @Test
    void assignPlayerRole_BLACK_then_get_has_correct_role_usernames() throws DataAccessException {
        gameDAO.insertNewGame(game);
        gameDAO.assignPlayerRole(game.gameID(), user2.username(), PlayerRole.BLACK_PLAYER);

        Game retrievedGame = gameDAO.findGame(game.gameID());
        assertEquals("", retrievedGame.whiteUsername());
        assertEquals(user2.username(), retrievedGame.blackUsername());
    }

    @Test
    void assignPlayerRole_SPECTATOR_does_not_change_role_usernames() throws DataAccessException {
        gameDAO.insertNewGame(game);
        gameDAO.assignPlayerRole(game.gameID(), user.username(), PlayerRole.SPECTATOR);

        Game retrievedGame = gameDAO.findGame(game.gameID());
        assertEquals("", retrievedGame.whiteUsername());
        assertEquals("", retrievedGame.blackUsername());
    }

    @Test
    void assignPlayerRole_both_roles_then_get_has_correct_role_usernames() throws DataAccessException {
        gameDAO.insertNewGame(game);
        gameDAO.assignPlayerRole(game.gameID(), user.username(), PlayerRole.WHITE_PLAYER);
        gameDAO.assignPlayerRole(game.gameID(), user2.username(), PlayerRole.BLACK_PLAYER);

        Game retrievedGame = gameDAO.findGame(game.gameID());
        assertEquals(user.username(), retrievedGame.whiteUsername());
        assertEquals(user2.username(), retrievedGame.blackUsername());
    }

    @Test
    void assignPlayerRole_invalid_gameID_throws() {
        assertThrows(NoSuchItemException.class,
                () -> gameDAO.assignPlayerRole(invalidGameID, user.username(), PlayerRole.WHITE_PLAYER));
    }

    @Test
    void assignPlayerRole_invalid_username_throws() throws DataAccessException {
        gameDAO.insertNewGame(game);

        User invalidUser = new User("invalidUsername", "password", "email");
        assertThrows(UnauthorizedAccessException.class,
                () -> gameDAO.assignPlayerRole(game.gameID(), invalidUser.username(), PlayerRole.WHITE_PLAYER));
    }

    @Test
    void updateGameState_then_get_returns_updated_board() throws DataAccessException, InvalidMoveException {
        gameDAO.insertNewGame(game);

        ChessGame updatedChessGame = new ChessGame();
        ChessMove move = new ChessMove(new ChessPosition(2, 1), new ChessPosition(4, 1));
        updatedChessGame.makeMove(move);
        Game updatedGame = new Game(game.gameID(), game.gameName(), updatedChessGame);

        gameDAO.updateGameState(updatedGame);

        Game retrievedGame = gameDAO.findGame(game.gameID());
        ChessBoard board = retrievedGame.chessGame().getBoard();
        assertNotNull(board.getPiece(new ChessPosition(4, 1)));
    }

    @Test
    void removeGame_then_findGame_throws() throws DataAccessException {
        gameDAO.insertNewGame(game);
        gameDAO.removeGame(game.gameID());

        assertThrows(NoSuchItemException.class, () -> gameDAO.findGame(game.gameID()));
    }

    @Test
    void allGames_after_clearGames_returns_empty_list() throws DataAccessException {
        gameDAO.insertNewGame(game);
        gameDAO.insertNewGame(game2);
        gameDAO.clearGames();
        Collection<GameListItem> games = gameDAO.allGames();
        assertEquals(0, games.size());
    }

    @Test
    void two_generated_gameIDs_are_different() throws DataAccessException {
        Assertions.assertNotEquals(gameDAO.generateNewGameID(), gameDAO.generateNewGameID());
    }
}