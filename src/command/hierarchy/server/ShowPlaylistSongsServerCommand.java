package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import exception.runtime.InconsistentDataException;
import server.model.Playlist;
import server.model.Song;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class ShowPlaylistSongsServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public ShowPlaylistSongsServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "view playlist songs command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("view playlist songs");
        }

        try {
            return getShowPlaylistSongsCommandResult(loggedUser);
        } catch (InconsistentDataException e) {
            String message = "Internal server error! Could not execute command!" + e.getMessage();

            return new CommandResult(Status.Error, message, Action.of(SEND));
        }
    }

    private CommandResult getShowPlaylistSongsCommandResult(User loggedUser) {
        Playlist playlistToShow = loggedUser.getPlaylistsEntities().get(super.userInputArgs[1]);

        if (playlistToShow == null) {
            String message = "Not existing playlist!";

            return new CommandResult(Status.Error, message, Action.of(SEND));
        }

        Set<Map.Entry<String, Song>> playlistSongs = getSongsFromRepository(playlistToShow.getSongKeys()).entrySet();
        BiConsumer<Map.Entry<String, Song>, StringBuilder> appendSongString = this::appendSongString;

        String response = super.getCollectionFormattedStringRepresentation(playlistSongs, appendSongString);

        return new CommandResult(Status.OK, response, Action.of(SEND));
    }

    private Map<String, Song> getSongsFromRepository(Set<String> songKeys) {
        Map<String, Song> playlistSongs = new LinkedHashMap<>();

        for (String songKey : songKeys) {
            Song song = super.serverState.getSongRepository().getSongByUnifiedKey(songKey);
            if (song == null) {
                String message =
                    String.format("Could not find a playlist's song with key %s in the songs repository!", songKey);

                throw new InconsistentDataException(message);
            }
            playlistSongs.put(songKey, song);
        }

        return playlistSongs;
    }

    private void appendSongString(Map.Entry<String, Song> songEntry, StringBuilder songsSb) {
        songsSb.append(songEntry.getKey());
        songsSb.append(" - ");

        String songStrWithoutColons = songEntry.getValue().toString().replace(':', '-');
        songsSb.append(songStrWithoutColons);
    }

}