package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;

import static command.hierarchy.server.LogoutClientServerCommand.setLogoutState;
import static command.structure.Action.NONE;
import static validation.ObjectValidator.validateArrayLength;

public class ExitServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public ExitServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "exit command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser != null) {
            setLogoutState(loggedUser, super.serverState);
        }

        return new CommandResult(Status.OK, "", Action.of(NONE));
    }

}