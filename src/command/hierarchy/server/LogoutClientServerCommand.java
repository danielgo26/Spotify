package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;
import static validation.ObjectValidator.validateNotNull;

public class LogoutClientServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public LogoutClientServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "logout command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();

        if (loggedUser == null) {
            Status status = Status.Error;
            String message = "Could not logout user! That user cannot be found in the system!";

            return new CommandResult(status, message, Action.of(SEND));
        }

        return getLogoutCommandResult(loggedUser);
    }

    public static void setLogoutState(User userToLogout, ServerState serverState) {
        validateNotNull(userToLogout, "user to logout");
        validateNotNull(serverState, "server state");

        if (userToLogout.isUserCurrentlyStreaming()) {
            userToLogout.getAudioStreamSender().stop();
        }

        userToLogout.setIsLoggedIn(false);
        serverState.getKey().attach(null);
    }

    private CommandResult getLogoutCommandResult(User userToLogout) throws IOException {
        Status status;
        String message;

        if (!userToLogout.isLoggedIn()) {
            status = Status.Error;
            message = "Could not logout user! That user is not logged in!";
        } else {
            setLogoutState(userToLogout, super.serverState);

            status = Status.OK;
            message = "Successfully logged out!";
        }

        return new CommandResult(status, message, Action.of(SEND));
    }

}