package server.handlers;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import http.AuthResponse;
import http.LoginRequest;
import server.services.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler extends HttpHandler {
    private final LoginService service;

    public LoginHandler(AuthDAO authDAO, UserDAO userDAO) {
        service = new LoginService(authDAO, userDAO);
    }

    @Override
    public Object route(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        AuthResponse response = service.login(loginRequest);
        return parseToBody(res, response, 200);
    }
}

/*

| **Request class**    | LoginRequest                                        |
| **Response class**   | AuthResponse                                        |
| **Body**             | `{ "username":"", "password":"" }`                  |
 */
