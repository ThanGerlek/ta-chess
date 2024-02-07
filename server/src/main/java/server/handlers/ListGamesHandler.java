package server.handlers;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import http.ListGamesResponse;
import server.services.ListGamesService;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends HttpHandler {
    private final ListGamesService service;

    public ListGamesHandler(AuthDAO authDAO, GameDAO gameDAO) {
        service = new ListGamesService(authDAO, gameDAO);
    }

    @Override
    public Object route(Request req, Response res) throws DataAccessException {
        String authToken = gson.fromJson(req.headers("authorization"), String.class);
        ListGamesResponse response = service.listGames(authToken);
        return parseToBody(res, response, 200);
    }
}

/*
Note that `whiteUsername` and `blackUsername` may be `null`.

| **Request class**    | N/A (no request body)
| **Response class**   | ListGamesResponse
| **Headers**          | `authorization: <authToken>`
 */
