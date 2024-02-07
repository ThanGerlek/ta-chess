package http;

/**
 * A serializable representation of the basic data about a single game. Designed for use in ListGamesService HTTP
 * responses.
 *
 * @param gameID        the ID of the game.
 * @param whiteUsername the username of the user playing White.
 * @param blackUsername the username of the user playing Black.
 * @param gameName      a human-readable name for the game.
 */
public record GameListItem(int gameID, String whiteUsername, String blackUsername, String gameName) {
    // {"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""}
}