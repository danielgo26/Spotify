package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.model.User;
import server.state.ServerState;

import java.util.Set;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class GetSupportedSongsKeysServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public GetSupportedSongsKeysServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "get song names command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser == null) {
            return super.getNotLoggedUserCommandResult("get song names");
        }

        Set<String> availableSongKeys = super.serverState.getSongRepository().getAllSongEntities().keySet();
        String message = String.join(",", availableSongKeys);

        return new CommandResult(Status.OK, message, Action.of(SEND));
    }

}