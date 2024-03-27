package server;

import dataAccess.*;
import http.MessageResponse;
import server.handlers.*;
import server.webSocket.GameSessionManager;
import server.webSocket.WebSocketServer;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

    private final ClearApplicationHandler clearApplicationHandler;
    private final CreateGameHandler createGameHandler;
    private final JoinGameHandler joinGameHandler;
    private final ListGamesHandler listGamesHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final RegisterHandler registerHandler;

    private final WebSocketServer webSocketServer;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Server() {
        // TODO Proper dependency injection

        userDAO = new DatabaseUserDAO();
        authDAO = new DatabaseAuthDAO(userDAO);
        gameDAO = new DatabaseGameDAO(userDAO);
//        userDAO = new MemoryUserDAO();
//        authDAO = new MemoryAuthDAO(userDAO);
//        gameDAO = new MemoryGameDAO(userDAO);

        createGameHandler = new CreateGameHandler(authDAO, gameDAO);
        joinGameHandler = new JoinGameHandler(authDAO, gameDAO);
        listGamesHandler = new ListGamesHandler(authDAO, gameDAO);
        loginHandler = new LoginHandler(authDAO, userDAO);
        logoutHandler = new LogoutHandler(authDAO);
        registerHandler = new RegisterHandler(authDAO, userDAO);

        webSocketServer = new WebSocketServer(authDAO, gameDAO);
        GameSessionManager sessionManager = new GameSessionManager(webSocketServer);

        clearApplicationHandler = new ClearApplicationHandler(authDAO, gameDAO, userDAO, sessionManager);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public int run(int desiredPort) {
        System.out.println("Starting the server");

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        setupHttpRouting();

        try {
            setupDataAccess();
        } catch (DataAccessException e) {
            System.out.println("Failed to setup data access!");
            Spark.stop();
            System.exit(1);
        }

        Spark.awaitInitialization();
        int port = Spark.port();
        System.out.println("Listening on port " + port);
        return port;
    }

    private void setupHttpRouting() {
        exposeWebSocketServer();
        createRoutes();
        addShutdownHook();
    }

    private void setupDataAccess() throws DataAccessException {
        ChessDatabaseManager.configureDatabase();
        userDAO.initialize();
        authDAO.initialize();
        gameDAO.initialize();
    }

    private void exposeWebSocketServer() {
        Spark.webSocket("/connect", webSocketServer);
    }

    private void createRoutes() {
        createErrorRoutes();
        createBeforeRoutes();
        createServiceRoutes();
        createAfterRoutes();
    }

    /**
     * Create a shutdown hook to ensure the server has a chance to complete any routes in progress and release resources
     * before shutting down.
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping the server...");
            Spark.stop();
            System.out.println("Server stopped");
        }));
    }

    private void createErrorRoutes() {
        // See web-api/example-code/.../Custom...Server2
        Spark.notFound((req, res) -> {
            String errMsg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            return errorHandler(new Exception(errMsg), req, res);
        });
        Spark.exception(Exception.class, this::errorHandler);
    }

    private void createBeforeRoutes() {
        Spark.before(
                (req, res) -> System.out.println("Executing route: " + req.requestMethod() + " " + req.pathInfo()));
        // Filters take an optional pattern to restrict the routes to which they are applied:
        // before("/protected/*", (req, res) -> {…});
    }

    private void createServiceRoutes() {
        Spark.delete("/db", clearApplicationHandler::handleRequest);
        Spark.post("/user", registerHandler::handleRequest);
        Spark.post("/session", loginHandler::handleRequest);
        Spark.delete("/session", logoutHandler::handleRequest);
        Spark.get("/game", listGamesHandler::handleRequest);
        Spark.post("/game", createGameHandler::handleRequest);
        Spark.put("/game", joinGameHandler::handleRequest);
    }

    private void createAfterRoutes() {
        Spark.after((req, res) -> System.out.println("Finished executing route: " + req.pathInfo()));
    }

    private Object errorHandler(Exception e, Request req, Response res) {
        String msg = String.format("[ERROR] Unknown server error: %s", e.getMessage());
        System.out.println(msg);
        MessageResponse response = new MessageResponse(msg);
        return HttpHandler.parseToBody(res, response, 500);
    }

}