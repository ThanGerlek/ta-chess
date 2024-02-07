package server.handlers;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import http.MessageResponse;
import server.services.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends HttpHandler {
    private final LogoutService service;

    public LogoutHandler(AuthDAO authDAO) {
        service = new LogoutService(authDAO);
    }

    @Override
    public Object route(Request req, Response res) throws DataAccessException {
        String authToken = gson.fromJson(req.headers("authorization"), String.class);
        MessageResponse response = service.logout(authToken);
        return parseToBody(res, response, 200);
    }
}

/*

| **Request class**    | N/A (no request body)                           |
| **Response class**   | MessageResponse                                 |
| **Headers**          | `authorization: <authToken>`                    |
 */
