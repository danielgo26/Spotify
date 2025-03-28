package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.state.ServerState;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class IdentifyChannelServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public IdentifyChannelServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "identify command client input");

        String idToken = super.getIdTokenFromInput();

        Status status;
        String message;

        if (super.serverState.getUserRepository().contains(idToken)) {
            super.serverState.getKey().attach(idToken);

            status = Status.OK;
            message = "Successfully identified channel!";
        } else {
            status = Status.Error;
            message = "No existing user corresponding to the given id token!";
        }

        return new CommandResult(status, message, Action.of(SEND));
    }

}