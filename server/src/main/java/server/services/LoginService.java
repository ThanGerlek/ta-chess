package server.services;

import dataAccess.*;
import http.AuthResponse;
import http.LoginRequest;
import model.AuthToken;
import model.User;

import java.util.UUID;

/**
 * Provides the Login service, which authenticates an existing user.
 */
public class LoginService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public LoginService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    /**
     * Log in an existing user. Returns a new authToken for that user.
     *
     * @param request a LoginRequest representing the HTTP request.
     * @return an AuthResponse representing the resulting HTTP response.
     */
    public AuthResponse login(LoginRequest request) throws DataAccessException {
        User user;
        try {
            user = userDAO.getUser(request.username());
        } catch (NoSuchItemException e) {
            throw new UnauthorizedAccessException("Unrecognized username.");
        }
        AuthToken token = authenticate(user, request.password());
        return new AuthResponse(token.authToken(), request.username(), "Okay!");
    }

    private AuthToken authenticate(User user, String password) throws DataAccessException {
        if (!user.password().equals(password)) {
            throw new UnauthorizedAccessException("Invalid credentials.");
        }
        return registerNewAuthToken(user.username());
    }

    // TODO Consolidate with RegisterService's registerNewAuthToken() method
    private AuthToken registerNewAuthToken(String username) throws DataAccessException {
        String tokenString = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken(tokenString, username);
        authDAO.addAuthToken(authToken);
        return authToken;
    }

/*

| **Body**             | `{ "username":"", "password":"" }`                  |
| **Success response** | [200] `{ "username":"", "authToken":"" }`           |
| **Failure response** | [401] `{ "message": "Error: unauthorized" }`        |
| **Failure response** | [500] `{ "message": "Error: description" }`         |
     */

}
