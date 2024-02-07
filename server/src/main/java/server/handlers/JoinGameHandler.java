package server.handlers;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import http.JoinGameRequest;
import http.MessageResponse;
import server.services.JoinGameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler extends HttpHandler {
    private final JoinGameService service;

    public JoinGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        service = new JoinGameService(authDAO, gameDAO);
    }

    @Override
    protected Object route(Request req, Response res) throws DataAccessException {
        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
        String authToken = req.headers("authorization");
        MessageResponse response = service.joinGame(joinGameRequest, authToken);
        return parseToBody(res, response, 200);
    }
}

/*

| **Request class**    | JoinGameRequest
| **Response class**   | MessageResponse
| **Headers**          | `authorization: <authToken>`
| **Body**             | `{ "playerColor":"WHITE/BLACK", "gameID": 1234 }`
 */