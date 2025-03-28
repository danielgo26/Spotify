package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.state.ServerState;

import static command.structure.Action.SEND;

public class UnknownServerCommand extends ServerCommand {

    public UnknownServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() {
        Status status = Status.Error;
        String message = "Unknown to server command entered!";

        return new CommandResult(status, message, Action.of(SEND));
    }

}