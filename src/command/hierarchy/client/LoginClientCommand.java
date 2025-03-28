package command.hierarchy.client;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import client.state.ClientState;
import exception.IOChannelException;

import static command.structure.Action.VISUALIZE;
import static validation.ObjectValidator.validateArrayLength;

public class LoginClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 3;

    public LoginClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "login command user input");

        CommandResult loginCommandServerResponse = getBaseUserClientCommand("login user", false);

        if (!loginCommandServerResponse.isCommandSuccessful()) {
            return loginCommandServerResponse;
        }
        super.clientState.setProbableUsername(super.userInputArgs[1].split("@")[0]);

        return getLoginCommand(loginCommandServerResponse);
    }

    private CommandResult getLoginCommand(CommandResult loginCommandServerResponse) {
        super.clientState.setIdToken(loginCommandServerResponse.responseMessage());
        super.clientState.setClientLogged(true);

        String response = String.format("Login successful! Hello, %s!", super.clientState.getProbableUsername());

        return new CommandResult(Status.OK, response, Action.of(VISUALIZE));
    }

}