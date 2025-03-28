package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.audiostream.AudioStreamSender;
import server.model.User;
import server.state.ServerState;

import static command.structure.Action.NONE;
import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class PlayMusicServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public PlayMusicServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "play music command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("play music");
        }

        String songKey = super.userInputArgs[1];
        if (!super.serverState.getSongRepository().contains(songKey)) {
            String message = "Could not find song, corresponding to the given key!";

            return new CommandResult(Status.Error, message, Action.of(SEND));
        }
        super.serverState.getSongRepository().incrementPlayingsCount(songKey);

        AudioStreamSender audioStreamSender = new AudioStreamSender(songKey, super.serverState);

        super.serverState.getKey().cancel();
        super.serverState.getExecutorService().execute(audioStreamSender);

        return new CommandResult(Status.OK, "", Action.of(NONE));
    }

}