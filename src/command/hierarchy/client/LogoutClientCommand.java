package command.hierarchy.client;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import output.Color;
import client.state.ClientState;
import exception.IOChannelException;

import static command.hierarchy.client.StopMusicClientCommand.setStopMusicState;
import static command.structure.Action.VISUALIZE;
import static output.StandardOutputVisualizer.visualize;
import static validation.ObjectValidator.validateArrayLength;
import static validation.ObjectValidator.validateNotNull;

public class LogoutClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public LogoutClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "logout command user input");

        CommandResult logoutCommandServerResponse = getBaseUserClientCommand("logout user", true);

        if (!logoutCommandServerResponse.isCommandSuccessful()) {
            return logoutCommandServerResponse;
        }

        setLogoutState(super.clientState);

        return getLogoutCommand(logoutCommandServerResponse);
    }

    public static void setLogoutState(ClientState clientState) {
        validateNotNull(clientState, "client state");

        if (clientState.isClientStreaming()) {
            visualize("Stopping music audio...", Color.GREEN, true);

            setStopMusicState(clientState);

            visualize("Music audio stopped.", Color.GREEN, true);
        }

        clientState.setClientLogged(false);
        clientState.setIdToken(null);
    }

    private CommandResult getLogoutCommand(CommandResult logoutCommandServerResponse) {
        String response = String.format("%s Goodbye, %s!",
            logoutCommandServerResponse.responseMessage(), super.clientState.getProbableUsername());

        return new CommandResult(Status.OK, response, Action.of(VISUALIZE));
    }

}