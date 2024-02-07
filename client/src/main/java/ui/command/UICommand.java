package ui.command;

import client.AuthorizationRole;

public class UICommand extends Command {
    private final String description;

    UICommand(String commandString, String description, AuthorizationRole acceptedAuthRole) {
        super(commandString, acceptedAuthRole);
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCommandString() {
        return this.getCommandID();
    }
}
