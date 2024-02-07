package server.services;

import dataAccess.*;
import http.JoinGameRequest;
import http.MessageResponse;

/**
 * Provides the Join Game service, which connects a user to an existing game as either a player or spectator. This
 * request is idempotent.
 */
public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    /**
     * Connect a user to an existing game as either a player or spectator. Verifies that the specified game exists, and,
     * if a color is specified, adds the caller as the requested color to the game. If no color is specified the user is
     * joined as an observer. This request is idempotent.
     *
     * @param request   a JoinGameRequest representing the HTTP request.
     * @param authToken the AuthToken representing the user to assign.
     * @return a MessageResponse representing the resulting HTTP response.
     */
    public MessageResponse joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        if (authDAO.isValidAuthToken(authToken)) {

            PlayerRole role = request.playerColor() == null
                    ? PlayerRole.SPECTATOR
                    : PlayerRole.stringToRole(request.playerColor());

            int gameID = request.gameID();
            String username = authDAO.getUsername(authToken);
            gameDAO.assignPlayerRole(gameID, username, role);

            return new MessageResponse("Okay!");
        } else {
            throw new UnauthorizedAccessException("Could not join game: provided token was invalid");
        }

    }

/*

| **Headers**          | `authorization: <authToken>`
| **Body**             | `{ "playerColor":"WHITE/BLACK", "gameID": 1234 }`
| **Success response** | [200]
| **Failure response** | [400] `{ "message": "Error: bad request" }`
| **Failure response** | [401] `{ "message": "Error: unauthorized" }`
| **Failure response** | [403] `{ "message": "Error: already taken" }`
| **Failure response** | [500] `{ "message": "Error: description" }`
     */

}
