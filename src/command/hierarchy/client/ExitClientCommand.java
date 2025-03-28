package command.hierarchy.client;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import client.state.ClientState;
import exception.IOChannelException;

import java.nio.channels.SocketChannel;

import static command.hierarchy.client.LogoutClientCommand.setLogoutState;
import static command.structure.Action.EXIT;
import static command.structure.Action.VISUALIZE;
import static validation.ObjectValidator.validateArrayLength;

public class ExitClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public ExitClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "exit command user input");

        if (super.clientState.isClientLogged()) {
            setLogoutState(super.clientState);
        }

        return getExitCommand();
    }

    private CommandResult getExitCommand() throws IOChannelException {
        SocketChannel clientChannel = super.clientState.getClientChannel();
        super.channelWriter.writeTo(clientChannel, userInput);

        String response = "Successfully exited the program!";

        return new CommandResult(Status.OK, response, Action.of(EXIT | VISUALIZE));
    }

}