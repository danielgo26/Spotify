package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.model.Playlist;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;
import java.util.Collection;
import java.util.function.BiConsumer;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class ShowPlaylistsServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public ShowPlaylistsServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "show songs command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("see playlists");
        }

        return getShowPlaylistsCommandResult(loggedUser);
    }

    private CommandResult getShowPlaylistsCommandResult(User loggedUser) {
        Collection<Playlist> playlists = loggedUser.getPlaylists();
        BiConsumer<Playlist, StringBuilder> appendPlaylistsString = this::appendPlaylistsString;

        String response = super.getCollectionFormattedStringRepresentation(playlists, appendPlaylistsString);

        return new CommandResult(Status.OK, response, Action.of(SEND));
    }

    private void appendPlaylistsString(Playlist playlist, StringBuilder playlistsSb) {
        playlistsSb.append(playlist.getName());

        playlistsSb.append(" (").append(playlist.getSongKeys().size()).append(" songs)");
    }

}