package ui.command;

import client.AuthorizationRole;

public abstract class Commands {
    // ANY or higher
    public static UICommand HELP = new UICommand("help", "Print a list of available commands", AuthorizationRole.ANY);
    public static UICommand QUIT = new UICommand("quit", "Quit the game", AuthorizationRole.ANY);

    // GUEST
    public static UICommand REGISTER =
            new UICommand("register", "Register a new user account", AuthorizationRole.GUEST);
    public static UICommand LOGIN = new UICommand("login", "Log in an existing user", AuthorizationRole.GUEST);

    // USER or higher
    public static UICommand LOGOUT = new UICommand("logout", "Log out the current user", AuthorizationRole.USER);
    public static UICommand CREATE_GAME = new UICommand("create", "Create a new chess game", AuthorizationRole.USER);
    public static UICommand LIST_GAMES = new UICommand("list", "List all games", AuthorizationRole.USER);
    public static UICommand JOIN_GAME = new UICommand("join", "Join an existing game", AuthorizationRole.USER);
    public static UICommand OBSERVE_GAME =
            new UICommand("observe", "Observe an existing game as a spectator", AuthorizationRole.USER);

    // OBSERVER or higher
    public static UICommand DRAW = new UICommand("draw", "Redraw the game board", AuthorizationRole.OBSERVER);
    public static UICommand LEAVE =
            new UICommand("leave", "Leave the current game",
                    AuthorizationRole.OBSERVER);

    // PLAYER
    public static UICommand MAKE_MOVE = new UICommand("move", "Make a move", AuthorizationRole.PLAYER);
    public static UICommand RESIGN = new UICommand("resign", "Resign the game", AuthorizationRole.PLAYER);
    public static UICommand HIGHLIGHT_MOVES =
            new UICommand("highlight", "Highlight available moves", AuthorizationRole.PLAYER);

    // CONSOLE or higher
    public static Command INVALID = new Command("invalid", AuthorizationRole.CONSOLE);
    public static Command IDENTITY = new Command("identity", AuthorizationRole.CONSOLE);
    public static Command NO_INPUT = new Command("", AuthorizationRole.CONSOLE);

    public static UICommand[] UI_COMMANDS =
            {HELP, QUIT, REGISTER, LOGIN, LOGOUT, CREATE_GAME, LIST_GAMES, JOIN_GAME, OBSERVE_GAME, DRAW, LEAVE,
                    MAKE_MOVE, RESIGN, HIGHLIGHT_MOVES};

    public static Command parse(String input) {
        for (UICommand cmd : UI_COMMANDS) {
            if (cmd.getCommandID().equals(input)) {
                return cmd;
            }
        }

        if ("".equals(input)) {
            return NO_INPUT;
        } else {
            return INVALID;
        }
    }
}