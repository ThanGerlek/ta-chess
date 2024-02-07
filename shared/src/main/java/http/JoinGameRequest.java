package http;

/**
 * A record representing an HTTP request to the JoinGameService.
 *
 * @param playerColor the requested team color of the joining player, or null to join as a spectator
 * @param gameID      the ID of the game to join
 */
public record JoinGameRequest(String playerColor, int gameID) {
    //    { "playerColor":"WHITE/BLACK", "gameID": 1234 }
}
