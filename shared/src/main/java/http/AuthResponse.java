package http;

/**
 * A record representing an HTTP response to an authorization request. Used by LoginService and RegisterService.
 *
 * @param authToken a new authorization string
 * @param username  the username of the user the token represents
 * @param message   the response message
 */
public record AuthResponse(String authToken, String username, String message) {
}

// Login:
//    Success response	[200] { "username":"", "authToken":"" }
//    Failure response	[401] { "message": "Error: unauthorized" }
//    Failure response	[500] { "message": "Error: description" }

// Register:
//    Success response	[200] { "username":"", "authToken":"" }
//    Failure response	[400] { "message": "Error: bad request" }
//    Failure response	[403] { "message": "Error: already taken" }
//    Failure response	[500] { "message": "Error: description" }
