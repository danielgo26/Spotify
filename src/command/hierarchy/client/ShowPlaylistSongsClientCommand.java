package command.hierarchy.client;

import client.state.ClientState;
import command.structure.CommandResult;
import exception.ConnectionException;
import exception.IOChannelException;

import static validation.ObjectValidator.validateArrayLength;

public class ShowPlaylistSongsClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public ShowPlaylistSongsClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws ConnectionException, IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "show playlist songs command user input");

        return super.getBaseUserClientCommand("show playlist songs", true);
    }

}