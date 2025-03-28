package command.hierarchy.client;

import command.structure.CommandResult;
import client.state.ClientState;
import exception.IOChannelException;

import static validation.ObjectValidator.validateArrayLength;

public class RegisterClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 3;

    public RegisterClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "register command user input");

        return getBaseUserClientCommand("register user", false);
    }

}