package server.handlers;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import http.AuthResponse;
import http.RegisterRequest;
import server.services.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends HttpHandler {
    private final RegisterService service;

    public RegisterHandler(AuthDAO authDAO, UserDAO userDAO) {
        service = new RegisterService(authDAO, userDAO);
    }

    @Override
    public Object route(Request req, Response res) throws DataAccessException {
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
        AuthResponse response = service.register(registerRequest);
        return parseToBody(res, response, 200);
    }
}

/*
| **Request class**    | RegisterRequest                                |
| **Response class**   | AuthResponse                                   |
| **Body**             | `{ "username":"", "password":"", "email":"" }` |
*/
