package server.services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UnauthorizedAccessException;
import http.ListGamesResponse;

/**
 * Provides the List Games service, which returns data about all games currently in progress.
 */
public class ListGamesService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListGamesService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    /**
     * Get a list of all games currently in progress.
     *
     * @return a ListGamesResponse representing the resulting HTTP response.
     */
    public ListGamesResponse listGames(String authToken) throws DataAccessException {
        if (authDAO.isValidAuthToken(authToken)) {
            return new ListGamesResponse(gameDAO.allGames(), "Okay!");
        } else {
            throw new UnauthorizedAccessException("Could not list games: provided token was invalid");
        }

    }

/*

| **Headers**          | `authorization: <authToken>`                                                                 |
| **Success response** | [200] `{ "games": ["gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}` |
| **Failure response** | [401] `{ "message": "Error: unauthorized" }`                                                 |
| **Failure response** | [500] `{ "message": "Error: description" }`                                                  |
     */

}
