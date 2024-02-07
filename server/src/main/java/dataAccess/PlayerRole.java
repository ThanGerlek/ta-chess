package dataAccess;

public class PlayerRole {
    public static final PlayerRole WHITE_PLAYER = new PlayerRole("WHITE");
    public static final PlayerRole BLACK_PLAYER = new PlayerRole("BLACK");
    public static final PlayerRole SPECTATOR = new PlayerRole("SPECTATOR");
    private final String roleString;

    private PlayerRole(String roleString) {
        this.roleString = roleString;
    }

    public static String roleToString(PlayerRole role) {
        return role.roleString;
    }

    public static PlayerRole stringToRole(String roleString) {
        if (roleString.isEmpty() || SPECTATOR.roleString.equals(roleString)) {
            return SPECTATOR;
        } else if (WHITE_PLAYER.roleString.equals(roleString)) {
            return WHITE_PLAYER;
        } else if (BLACK_PLAYER.roleString.equals(roleString)) {
            return BLACK_PLAYER;
        } else {
            String msg = String.format("Called stringToRole() with an unrecognized role string: '%s'", roleString);
            throw new IllegalArgumentException(msg);
        }
    }

    public String toString() {
        return "PlayerRole." + this.roleString;
    }
}
