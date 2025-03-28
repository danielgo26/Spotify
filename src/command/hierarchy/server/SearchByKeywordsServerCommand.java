package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.model.Song;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateOperatorIsApplied;

public class SearchByKeywordsServerCommand extends ServerCommand {

    private static final int MINIMUM_INPUT_LENGTH = 2;

    public SearchByKeywordsServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        String msg = String.format("The search command takes at least %s parameters!", MINIMUM_INPUT_LENGTH);
        validateOperatorIsApplied(super.userInputArgs.length, MINIMUM_INPUT_LENGTH, (a, b) -> a >= b, msg);

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("search songs by keywords");
        }

        return getSearchByKeywordsCommandResult();
    }

    private CommandResult getSearchByKeywordsCommandResult() {
        Map<String, Song> availableSongs = super.serverState.getSongRepository().getAllSongEntities();
        Set<Map.Entry<String, Song>> matchingSongsEntries = getAllMatchingSongsEntries(availableSongs);
        BiConsumer<Map.Entry<String, Song>, StringBuilder> appendSongString = this::appendSongString;

        String response = super.getCollectionFormattedStringRepresentation(matchingSongsEntries, appendSongString);

        return new CommandResult(Status.OK, response, Action.of(SEND));
    }

    private Set<Map.Entry<String, Song>> getAllMatchingSongsEntries(Map<String, Song> availableSongs) {
        String[] keywords = Arrays.stream(super.userInputArgs)
            .skip(1)
            .map(String::toLowerCase)
            .toArray(String[]::new);

        Set<Map.Entry<String, Song>> foundSongs = new LinkedHashSet<>();
        for (Map.Entry<String, Song> songEntry : availableSongs.entrySet()) {
            if (matchesKeywords(songEntry.getValue(), keywords)) {
                foundSongs.add(songEntry);
            }
        }

        return foundSongs;
    }

    private boolean matchesKeywords(Song song, String[] keywords) {
        String searchInto = song.getSearchByKeywordsString();

        return Arrays.stream(keywords).anyMatch((searchInto::contains));
    }

    private void appendSongString(Map.Entry<String, Song> songEntry, StringBuilder songsSb) {
        songsSb.append(songEntry.getKey());
        songsSb.append(" - ");

        String songStrWithoutColons = songEntry.getValue().toString().replace(':', '-');
        songsSb.append(songStrWithoutColons);
    }

}