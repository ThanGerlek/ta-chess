package server.services;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedAccessException;
import http.GameListItem;
import http.ListGamesResponse;
import model.AuthToken;
import model.Game;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ListGamesServiceTest extends ServiceTest {
    private final User user = new User("user1", "pass1", "mail1");
    private final AuthToken token = new AuthToken("1234", "user1");
    private final Game game1 = new Game(1, "game1");
    private final Game game2 = new Game(2, "game2");
    private ListGamesService service;

    // TODO 500?

    @BeforeEach
    void setUp() throws DataAccessException {
        initDAOs();
        userDAO.insertNewUser(user);
        authDAO.addAuthToken(token);
        service = new ListGamesService(authDAO, gameDAO);

        game1.chessGame().getBoard().resetBoard();
        game2.chessGame().getBoard().resetBoard();
        game1.chessGame().getBoard().addPiece(new ChessPosition(3, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        game2.chessGame().getBoard().addPiece(new ChessPosition(3, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));

        gameDAO.insertNewGame(game1);
        gameDAO.insertNewGame(game2);
    }

    // Positive test
    @Test
    void list_Games_returns_GameListItems() throws DataAccessException {
        GameListItem[] expected = {new GameListItem(1, null, null, "game1"), new GameListItem(2, null, null, "game2")};

        ListGamesResponse response = service.listGames(token.authToken());

        ArrayList<GameListItem> actual = response.games();
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(expected[0], actual.get(0));
        Assertions.assertEquals(expected[1], actual.get(1));
    }

    // Negative test
    @Test
    void list_Games_with_invalid_token_returns_forbidden() {
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> service.listGames("iDoNotExist"));
    }

    @Test
    void list_Games_returns_okay() throws DataAccessException {
        ListGamesResponse response = service.listGames(token.authToken());
        Assertions.assertEquals("Okay!", response.message());
    }

}