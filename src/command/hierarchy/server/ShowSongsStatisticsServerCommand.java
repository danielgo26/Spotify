package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import exception.InputParsingException;
import server.model.Song;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class ShowSongsStatisticsServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public ShowSongsStatisticsServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "show songs statistics command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("see songs statistics");
        }

        return getShowSongsStatisticsCommandResult();
    }

    private CommandResult getShowSongsStatisticsCommandResult() {
        int countSongsToShow;
        try {
            countSongsToShow = getCountSongsToShow(super.serverState.getSongRepository().getSongsCount());
        } catch (InputParsingException e) {
            return new CommandResult(Status.Error, e.getMessage(), Action.of(SEND));
        }

        List<Map.Entry<String, Song>> topSongs = getTopNSongs(countSongsToShow);
        BiConsumer<Map.Entry<String, Song>, StringBuilder> appendSongString = this::appendSongString;

        String response = super.getCollectionFormattedStringRepresentation(topSongs, appendSongString);

        return new CommandResult(Status.OK, response, Action.of(SEND));
    }

    private int getCountSongsToShow(int totalSongs) throws InputParsingException {
        int countSongsToShow = parseInput(super.userInputArgs[1]);

        if (countSongsToShow <= 0) {
            String response = "The given count of songs to visualize is not a positive number!";

            throw new InputParsingException(response);
        }

        if (countSongsToShow > totalSongs) {
            countSongsToShow = totalSongs;
        }

        return countSongsToShow;
    }

    private int parseInput(String input) throws InputParsingException {
        int result;

        try {
            result = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            String response = "The given count of songs to visualize is not a valid number!";

            throw new InputParsingException(response);
        }

        return result;
    }

    private List<Map.Entry<String, Song>> getTopNSongs(int n) {
        Set<Map.Entry<String, Song>> songs = super.serverState.getSongRepository().getAllSongEntities().entrySet();

        return songs
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(n)
            .toList();
    }

    private void appendSongString(Map.Entry<String, Song> songEntry, StringBuilder songsSb) {
        songsSb.append(songEntry.getKey());
        songsSb.append(" - ");

        Song currentSong = songEntry.getValue();

        String songStrWithoutColons = currentSong.toString().replace(':', '-');
        songsSb.append(songStrWithoutColons);

        songsSb.append(" (").append(currentSong.getCountOfPlayings()).append(" playings)");
    }

}