package command.hierarchy.client;

import client.state.ClientState;
import command.structure.CommandResult;
import exception.ConnectionException;
import exception.IOChannelException;

import static validation.ObjectValidator.validateArrayLength;

public class CreatePlaylistClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public CreatePlaylistClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws ConnectionException, IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "create playlist command user input");

        return super.getBaseUserClientCommand("create playlist", true);
    }

}