package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import http.GameListItem;
import model.AuthToken;
import model.Game;
import model.User;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

class GameDAOTest {
    private static final boolean IS_SQL_DAO = true;

    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static final User validUser = new User("validUser", "password", "email");
    private static final AuthToken validToken = new AuthToken("validTokenString", validUser.username());
    private static final Game validGame = new Game(3, "gameName", new ChessGame());

    @BeforeAll
    static void init() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        userDAO.clearUsers();
        userDAO.insertNewUser(validUser);
        authDAO = new MemoryAuthDAO(userDAO);
        authDAO.clearAuthTokens();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = IS_SQL_DAO ? new DatabaseGameDAO(userDAO) : new MemoryGameDAO(userDAO);
        gameDAO.clearGames();
    }

    @AfterAll
    static void deInit() throws DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuthTokens();
        gameDAO.clearGames();
    }

    @Test
    void find_preexisting_game_returns_nonnull() throws DataAccessException{

    }

    @Test
    void insertNewGame_with_preexisting_gameID_throws() throws DataAccessException{
    }

    @Test
    void find_nonexistent_game_throws() throws DataAccessException{
    }

    @Test
    void allGames_after_inserting_returns_correctly_sized_list() throws DataAccessException{
    }

    @Test
    void allGames_without_inserting_returns_empty_list() throws DataAccessException{
    }

    @Test
    void assignPlayerRole_WHITE_then_get_has_correct_role_usernames() throws DataAccessException{
    }

    @Test
    void assignPlayerRole_BLACK_then_get_has_correct_role_usernames() throws DataAccessException{
    }

    @Test
    void assignPlayerRole_both_roles_then_get_has_correct_role_usernames() throws DataAccessException{
    }

    @Test
    void assignPlayerRole_invalid_gameID_throws() throws DataAccessException{
    }

    @Test
    void assignPlayerRole_invalid_username_throws() throws DataAccessException{
    }

    @Test
    void updateGameState_then_get_returns_updated_board() throws DataAccessException{
    }

    @Test
    void removeGame_then_findGame_throws() throws DataAccessException{
    }

    @Test
    void allGames_after_clearGames_returns_empty() throws DataAccessException{
    }

    @Test
    void two_generated_gameIDs_are_different() throws DataAccessException{
        Assertions.assertNotEquals(gameDAO.generateNewGameID(), gameDAO.generateNewGameID());
    }
}