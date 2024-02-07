package model;

/**
 * An authorization token for the given user.
 *
 * @param authToken the unique token string
 * @param username  the user this {@code AuthToken} represents
 */
public record AuthToken(String authToken, String username) {
}
