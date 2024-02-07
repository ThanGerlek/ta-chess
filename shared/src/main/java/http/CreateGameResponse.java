package http;

/**
 * A record representing an HTTP response to a CreateGameService request.
 *
 * @param gameID  the unique ID of the newly created game
 * @param message the response message
 */
public record CreateGameResponse(Integer gameID, String message) {
}

//    Success response	[200] { "gameID": 1234 }
//    Failure response	[400] { "message": "Error: bad request" }
//    Failure response	[401] { "message": "Error: unauthorized" }
//    Failure response	[500] { "message": "Error: description" }
