package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import exception.CouldNotProceedOperationException;
import server.model.Playlist;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class CreatePlaylistServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;
    private static final String PLAYLIST_NAME_REGEX = "[a-zA-Z0-9]+";

    public CreatePlaylistServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "create playlist command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("create playlist");
        }

        String playlistName = super.userInputArgs[1];
        try {
            validatePlaylistName(playlistName, loggedUser);
        } catch (CouldNotProceedOperationException e) {
            return new CommandResult(Status.Error, e.getMessage(), Action.of(SEND));
        }

        loggedUser.addPlaylist(new Playlist(playlistName));
        String message = "Playlist successfully created!";

        return new CommandResult(Status.OK, message, Action.of(SEND));
    }

    private void validatePlaylistName(String playlistName, User user) throws CouldNotProceedOperationException {
        if (playlistName.isEmpty() || playlistName.isBlank() || !playlistName.matches(PLAYLIST_NAME_REGEX)) {
            String message = "The given playlist name is invalid! It must contain only letters and digits!";

            throw new CouldNotProceedOperationException(message);
        }

        if (user.contains(playlistName)) {
            String message = String.format("Playlist with name %s already exists!", playlistName);

            throw new CouldNotProceedOperationException(message);
        }
    }

}