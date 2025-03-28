package command.hierarchy.client;

import client.state.ClientState;
import command.structure.CommandResult;
import exception.IOChannelException;

import static validation.ObjectValidator.validateOperatorIsApplied;

public class SearchByKeywordsClientCommand extends ClientCommand {

    private static final int MINIMUM_INPUT_LENGTH = 2;

    public SearchByKeywordsClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws IOChannelException {
        String msg = String.format("Search by keywords command takes at least %s parameters!", MINIMUM_INPUT_LENGTH);
        validateOperatorIsApplied(super.userInputArgs.length, MINIMUM_INPUT_LENGTH, (a, b) -> a >= b, msg);

        return super.getBaseUserClientCommand("search by keywords", true);
    }

}