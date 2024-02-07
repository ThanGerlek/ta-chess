package client;

public class AuthorizationRole {
    // ANY < GUEST
    // ANY < USER < CONSOLE < SUPERUSER
    // ANY < OBSERVER < PLAYER
    public static AuthorizationRole ANY = new AuthorizationRole(Role.ANY);
    public static AuthorizationRole GUEST = new AuthorizationRole(Role.GUEST);
    public static AuthorizationRole USER = new AuthorizationRole(Role.USER);
    public static AuthorizationRole CONSOLE = new AuthorizationRole(Role.CONSOLE);
    public static AuthorizationRole SUPERUSER = new AuthorizationRole(Role.SUPERUSER);
    public static AuthorizationRole PLAYER = new AuthorizationRole(Role.PLAYER);
    public static AuthorizationRole OBSERVER = new AuthorizationRole(Role.OBSERVER);

    private final Role role;

    private AuthorizationRole(Role role) {
        this.role = role;
    }

    public boolean hasPermission(AuthorizationRole required) {
        switch (required.role) {
            case ANY -> {
                return true;
            }

            // USER < CONSOLE < SUPERUSER
            case USER -> {
                return role == Role.USER || role == Role.CONSOLE || role == Role.SUPERUSER;
            }
            case CONSOLE -> {
                return role == Role.CONSOLE || role == Role.SUPERUSER;
            }

            // OBSERVER < PLAYER
            case OBSERVER -> {
                return role == Role.OBSERVER || role == Role.PLAYER;
            }

            // GUEST, SUPERUSER, or PLAYER (the highest levels in their respective chains)
            default -> {
                return role == required.role;
            }
        }
    }

    private enum Role {
        ANY,
        GUEST,
        USER,
        PLAYER,
        OBSERVER,
        CONSOLE,
        SUPERUSER
    }
}
