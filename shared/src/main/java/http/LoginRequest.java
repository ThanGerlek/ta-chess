package http;

/**
 * A record representing an HTTP request to the LoginService.
 *
 * @param username the username to log in with
 * @param password the password to log in with
 */
public record LoginRequest(String username, String password) {
    //    { "username":"", "password":"" }
}
