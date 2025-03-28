package command.hierarchy.client;

import client.audiostream.AudioStreamReceiver;
import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import exception.runtime.InvalidServerResponseException;
import output.Color;
import client.state.ClientState;
import exception.ConnectionException;
import exception.IOChannelException;

import java.nio.channels.SocketChannel;
import java.util.Optional;

import static command.structure.Action.NONE;
import static command.structure.Action.VISUALIZE;
import static output.StandardOutputVisualizer.visualize;
import static validation.ObjectValidator.validateArrayLength;

public class PlayMusicClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public PlayMusicClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws ConnectionException, IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "play music command user input");

        Optional<CommandResult> optionalCommandResult = prepareForMusicPlay();
        if (optionalCommandResult.isPresent()) {
            return optionalCommandResult.get();
        }

        CommandResult identifyCommandResult = getIdentifyChannelCommand();

        if (!identifyCommandResult.isCommandSuccessful()) {
            String response = "Could not start streaming! " + identifyCommandResult.responseMessage();

            return new CommandResult(Status.Error, response, Action.of(VISUALIZE));
        }

        String message = String.format("Starting playing %s...", super.userInputArgs[1]);
        visualize(message, Color.GREEN, true);

        new Thread(super.clientState.getAudioStreamReceiver()).start();

        return identifyCommandResult;
    }

    private Optional<CommandResult> prepareForMusicPlay() throws IOChannelException {
        if (!super.clientState.isClientLogged()) {
            String response = "The user must be logged in order to play music!";

            return Optional.of(new CommandResult(Status.Error, response, Action.of(VISUALIZE)));
        }

        if (super.clientState.isClientStreaming()) {
            String response = "First stop the currently playing music!";

            return Optional.of(new CommandResult(Status.Error, response, Action.of(VISUALIZE)));
        }

        String songKey = super.userInputArgs[1];
        if (!checkSongKeyExists(songKey)) {
            String response = "The given song key does not exist!";

            return Optional.of(new CommandResult(Status.Error, response, Action.of(VISUALIZE)));
        }

        return Optional.empty();
    }

    private CommandResult getIdentifyChannelCommand() throws IOChannelException, ConnectionException {
        SocketChannel streamingChannel = identifyStreamingChannel();
        String serverResponse = super.channelReader.readStringFrom(streamingChannel);

        return CommandResult.of(serverResponse, Action.of(NONE));
    }

    private SocketChannel identifyStreamingChannel() throws IOChannelException, ConnectionException {
        String songName = super.userInputArgs[1];

        super.clientState.setAudioStreamReceiver(new AudioStreamReceiver(songName, super.clientState));

        SocketChannel streamingChannel = super.clientState.getAudioStreamReceiver().getSocketChannel();
        String identifyCommand = "[identify] " + super.clientState.getIdToken();

        super.channelWriter.writeTo(streamingChannel, identifyCommand);

        return streamingChannel;
    }

    private boolean checkSongKeyExists(String songKey) throws IOChannelException {
        SocketChannel streamingChannel = super.clientState.getClientChannel();
        String getSongKeysCommand = "[get-supported-song-names]";

        super.channelWriter.writeTo(streamingChannel, getSongKeysCommand);
        String serverResponse = super.channelReader.readStringFrom(streamingChannel);
        CommandResult serverResponseCommandResult = CommandResult.of(serverResponse, Action.of(NONE));

        if (!serverResponseCommandResult.isCommandSuccessful()) {
            String message = "The server could not respond for retrieving the available song keys!";

            throw new InvalidServerResponseException(message);
        }

        String[] validSongKeys = serverResponseCommandResult.responseMessage().split(",");
        for (String key : validSongKeys) {
            if (key.equals(songKey)) {
                return true;
            }
        }

        return false;
    }

}