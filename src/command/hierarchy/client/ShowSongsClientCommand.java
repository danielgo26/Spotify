package command.hierarchy.client;

import client.state.ClientState;
import command.structure.CommandResult;
import exception.ConnectionException;
import exception.IOChannelException;

import static validation.ObjectValidator.validateArrayLength;

public class ShowSongsClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public ShowSongsClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws ConnectionException, IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "show songs command user input");

        return super.getBaseUserClientCommand("show available songs", true);
    }

}