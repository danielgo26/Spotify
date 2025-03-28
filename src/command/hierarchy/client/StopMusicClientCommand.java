package command.hierarchy.client;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import client.state.ClientState;
import exception.IOChannelException;

import java.util.Optional;

import static command.structure.Action.VISUALIZE;
import static validation.ObjectValidator.validateArrayLength;
import static validation.ObjectValidator.validateNotNull;

public class StopMusicClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public StopMusicClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "stop music command user input");

        Optional<CommandResult> optionalCommandResult = prepareForStopMusic();
        if (optionalCommandResult.isPresent()) {
            return optionalCommandResult.get();
        }

        setStopMusicState(super.clientState);

        return new CommandResult(Status.OK, "Music stopped!", Action.of(VISUALIZE));
    }

    public static void setStopMusicState(ClientState clientState) {
        validateNotNull(clientState, "client state");

        if (clientState.getAudioStreamReceiver() != null) {
            clientState.getAudioStreamReceiver().stop();
        }
    }

    private Optional<CommandResult> prepareForStopMusic() {
        if (!super.clientState.isClientLogged()) {
            String response = "The user must be logged in order to stop music!";

            return Optional.of(new CommandResult(Status.Error, response, Action.of(VISUALIZE)));
        }

        if (!super.clientState.isClientStreaming()) {
            String response = "There is no music playing at the moment!";

            return Optional.of(new CommandResult(Status.Error, response, Action.of(VISUALIZE)));
        }

        return Optional.empty();
    }

}