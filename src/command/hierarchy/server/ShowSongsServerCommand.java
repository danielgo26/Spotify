package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.model.Song;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class ShowSongsServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public ShowSongsServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "show songs command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("see available songs");
        }

        return getShowSongsCommandResult();
    }

    private CommandResult getShowSongsCommandResult() {
        Map<String, Song> availableSongs = super.serverState.getSongRepository().getAllSongEntities();
        Set<Map.Entry<String, Song>> songsEntries = availableSongs.entrySet();
        BiConsumer<Map.Entry<String, Song>, StringBuilder> appendSongString = this::appendSongString;

        String response = super.getCollectionFormattedStringRepresentation(songsEntries, appendSongString);

        return new CommandResult(Status.OK, response, Action.of(SEND));
    }

    private void appendSongString(Map.Entry<String, Song> songEntry, StringBuilder songsSb) {
        songsSb.append(songEntry.getKey());
        songsSb.append(" - ");

        String songStrWithoutColons = songEntry.getValue().toString().replace(':', '-');
        songsSb.append(songStrWithoutColons);
    }

}