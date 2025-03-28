package command.hierarchy.client;

import client.state.ClientState;
import command.structure.CommandResult;
import exception.IOChannelException;

import static validation.ObjectValidator.validateArrayLength;

public class ShowSongsStatisticsClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public ShowSongsStatisticsClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "show songs statistics command user input");

        return super.getBaseUserClientCommand("show songs statistics", true);
    }

}