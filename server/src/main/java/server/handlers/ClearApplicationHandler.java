package server.handlers;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import http.MessageResponse;
import server.services.ClearApplicationService;
import server.webSocket.GameSessionManager;
import spark.Request;
import spark.Response;

public class ClearApplicationHandler extends HttpHandler {
    private final ClearApplicationService service;

    public ClearApplicationHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO,
            GameSessionManager sessionManager) {
        service = new ClearApplicationService(authDAO, gameDAO, userDAO, sessionManager);
    }

    @Override
    protected Object route(Request req, Response res) throws DataAccessException {
        MessageResponse response = service.clearApplication();
        return parseToBody(res, response, 200);
    }
}

/*
| **Request class**    | N/A (no request body)                                          |
| **Response class**   | MessageResponse                                                |
 */