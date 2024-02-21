package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.UnauthorizedAccessException;
import http.CreateGameRequest;
import http.CreateGameResponse;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.CreateGameService;

class CreateGameServiceTest extends ServiceTest {
    private final User user = new User("user1", "pass1", "mail1");
    private final AuthToken token = new AuthToken("1234", "user1");
    private final CreateGameRequest request = new CreateGameRequest("game1");
    private CreateGameService service;

    // TODO 400 bad req, 401 forbidden, 500?

    @BeforeEach
    void setUp() throws DataAccessException {
        initDAOs();
        userDAO.insertNewUser(user);
        authDAO.addAuthToken(token);
        service = new CreateGameService(authDAO, gameDAO);
    }

    // Positive test
    @Test
    void find_created_Game_returns_Game() throws DataAccessException {
        service.createGame(request, token.authToken());
        Assertions.assertEquals("game1", gameDAO.findGame(1).gameName());
    }

    // Negative test
    @Test
    void create_Game_with_invalid_token_returns_forbidden() {
        Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> service.createGame(request, "iDoNotExist"));
    }

    @Test
    void create_Game_returns_okay() throws DataAccessException {
        CreateGameResponse response = service.createGame(request, token.authToken());
        Assertions.assertEquals("Okay!", response.message());
    }

}