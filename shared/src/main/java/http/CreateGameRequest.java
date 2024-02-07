package http;

/**
 * A record representing an HTTP request to the CreateGameService.
 *
 * @param gameName a human-readable name for the game to create
 */
public record CreateGameRequest(String gameName) {
    //    { "gameName":"" }
}
