package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import exception.CouldNotProceedOperationException;
import server.model.Playlist;
import server.model.Song;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class AddSongToPlaylistServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 3;

    public AddSongToPlaylistServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "add song to playlist command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("add songs to playlist");
        }

        String songKey = super.userInputArgs[1];
        String playlistName = super.userInputArgs[2];

        try {
            verifyOperationCouldBeExecuted(songKey, playlistName, loggedUser);
        } catch (CouldNotProceedOperationException e) {
            return new CommandResult(Status.Error, e.getMessage(), Action.of(SEND));
        }

        String message =
            String.format("Song with key \"%s\" successfully added to playlist %s!", songKey, playlistName);

        return new CommandResult(Status.OK, message, Action.of(SEND));
    }

    private void verifyOperationCouldBeExecuted(String songKey, String playlistName, User loggedUser)
        throws CouldNotProceedOperationException {
        Song songToAdd = super.serverState.getSongRepository().getSongByUnifiedKey(songKey);
        verifySongExists(songToAdd);

        Playlist playlistToAddTo = loggedUser.getPlaylistsEntities().get(playlistName);
        verifyPlaylistExists(playlistToAddTo);

        verifyPlaylistDoesNotContainSong(playlistToAddTo, songKey);

        playlistToAddTo.addSong(songKey);
    }

    private void verifySongExists(Song song) throws CouldNotProceedOperationException {
        if (song == null) {
            String message = "Could not find any song, corresponding to the given key!";

            throw new CouldNotProceedOperationException(message);
        }
    }

    private void verifyPlaylistExists(Playlist playlist) throws CouldNotProceedOperationException {
        if (playlist == null) {
            String message = "Not existing playlist!";

            throw new CouldNotProceedOperationException(message);
        }
    }

    private void verifyPlaylistDoesNotContainSong(Playlist playlist, String songKey)
        throws CouldNotProceedOperationException {
        if (playlist.contains(songKey)) {
            String message = String.format("Playlist %s already contains %s!", playlist.getName(), songKey);

            throw new CouldNotProceedOperationException(message);
        }
    }

}