package http;

/**
 * A record representing an HTTP request to the RegisterService.
 *
 * @param username the username for the new user
 * @param password the password for the new user
 * @param email    the email for the new user
 */
public record RegisterRequest(String username, String password, String email) {
    //    { "username":"", "password":"", "email":"" }
}
