package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.model.User;
import server.state.ServerState;

import java.io.IOException;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class DeletePlaylistServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 2;

    public DeletePlaylistServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() throws IOException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "delete playlist command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("delete playlist");
        }

        String playlistName = super.userInputArgs[1];

        if (!loggedUser.contains(playlistName)) {
            String message = "Not existing playlist!";

            return new CommandResult(Status.Error, message, Action.of(SEND));
        }

        loggedUser.removePlaylist(playlistName);
        String message = String.format("Playlist %s successfully removed!", playlistName);

        return new CommandResult(Status.OK, message, Action.of(SEND));
    }

}