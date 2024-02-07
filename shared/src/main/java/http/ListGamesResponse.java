package http;

import java.util.ArrayList;

/**
 * A record representing an HTTP response to a ListGamesService request.
 *
 * @param games   a list of games currently in the database
 * @param message the response message
 */
public record ListGamesResponse(ArrayList<GameListItem> games, String message) {
}

//    Success response	[200] { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
//    Failure response	[401] { "message": "Error: unauthorized" }
//    Failure response	[500] { "message": "Error: description" }
