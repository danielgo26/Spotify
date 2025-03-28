package command.hierarchy.client;

import client.state.ClientState;
import command.structure.CommandResult;
import exception.ConnectionException;
import exception.IOChannelException;

import static validation.ObjectValidator.validateArrayLength;

public class RemoveSongFromPlaylistClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 3;

    public RemoveSongFromPlaylistClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws ConnectionException, IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "remove song from playlist command user input");

        return super.getBaseUserClientCommand("remove song from playlist", true);
    }

}