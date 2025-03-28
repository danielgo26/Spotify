package command.hierarchy.client;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import client.state.ClientState;

import static command.structure.Action.VISUALIZE;

public class UnknownClientCommand extends ClientCommand {

    public UnknownClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() {
        String message = "Unknown command entered (type \"help\" for more)!";

        return new CommandResult(Status.Error, message, Action.of(VISUALIZE));
    }

}