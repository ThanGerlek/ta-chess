package server.handlers;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import http.CreateGameRequest;
import http.CreateGameResponse;
import server.services.CreateGameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends HttpHandler {
    private final CreateGameService service;

    public CreateGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        service = new CreateGameService(authDAO, gameDAO);
    }

    @Override
    protected Object route(Request req, Response res) throws DataAccessException {
        CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);
        String authToken = gson.fromJson(req.headers("authorization"), String.class);
        CreateGameResponse response = service.createGame(createGameRequest, authToken);
        return parseToBody(res, response, 200);
    }
}

/*

| **Request class**    | CreateGameRequest                            |
| **Response class**   | CreateGameResponse                           |
| **Description**      | Creates a new game.                          |
| **Headers**          | `authorization: <authToken>`                 |
| **Body**             | `{ "gameName":"" }`                          |
 */
