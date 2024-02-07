package server.services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UnauthorizedAccessException;
import http.CreateGameRequest;
import http.CreateGameResponse;
import model.Game;

/**
 * Provides the Create New Game service, which registers and initializes a new empty game.
 */
public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    /**
     * Create a new game from the given CreateGameRequest.
     *
     * @param request a CreateGameRequest representing the HTTP request.
     * @return a CreateGameResponse representing the resulting HTTP response.
     */
    public CreateGameResponse createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        if (authDAO.isValidAuthToken(authToken)) {
            int gameID = registerNewGame(request.gameName());
            return new CreateGameResponse(gameID, "Okay!");
        } else {
            throw new UnauthorizedAccessException("Could not create game: provided token was invalid");
        }
    }

    private int registerNewGame(String gameName) throws DataAccessException {
        int gameID = gameDAO.generateNewGameID();
        Game game = new Game(gameID, gameName);
        gameDAO.insertNewGame(game);
        return gameID;
    }

/*

| **Headers**          | `authorization: <authToken>`                 |
| **Body**             | `{ "gameName":"" }`                          |
| **Success response** | [200] `{ "gameID": 1234 }`                   |
| **Failure response** | [400] `{ "message": "Error: bad request" }`  |
| **Failure response** | [401] `{ "message": "Error: unauthorized" }` |
| **Failure response** | [500] `{ "message": "Error: description" }`  |
     */

}
