package ui.command;

import client.AuthorizationRole;

import java.util.Objects;

public class Command {
    private final String commandID;
    private final AuthorizationRole[] acceptedAuthRoles;

    Command(String commandID, AuthorizationRole[] acceptedAuthRoles) {
        this.commandID = commandID;
        this.acceptedAuthRoles = acceptedAuthRoles;
    }

    Command(String commandID, AuthorizationRole acceptedAuthRole) {
        this.commandID = commandID;
        this.acceptedAuthRoles = new AuthorizationRole[]{acceptedAuthRole};
    }

    String getCommandID() {
        return this.commandID;
    }

    public boolean canBeRunBy(AuthorizationRole role) {
        for (AuthorizationRole acceptedRole : acceptedAuthRoles) {
            if (role.hasPermission(acceptedRole)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Command command)) return false;
        return Objects.equals(commandID, command.commandID);
    }
}