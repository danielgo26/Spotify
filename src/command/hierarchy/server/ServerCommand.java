package command.hierarchy.server;

import command.hierarchy.Command;
import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.repository.user.UserCredentials;
import server.state.ServerState;

import java.io.IOException;
import java.util.Collection;
import java.util.function.BiConsumer;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateNotNull;

public abstract class ServerCommand extends Command {

    protected final ServerState serverState;

    public ServerCommand(String clientInput, ServerState serverState) {
        super(clientInput);

        validateNotNull(serverState, "server state");

        this.serverState = serverState;
    }

    public abstract CommandResult execute() throws IOException;

    public UserCredentials getCredentials() {
        String email = super.userInputArgs[1];
        String password = super.userInputArgs[2];

        return new UserCredentials(email, password);
    }

    public String getIdTokenFromInput() {
        return super.userInputArgs[1];
    }

    protected CommandResult getNotLoggedUserCommandResult(String commandName) {
        String message = String.format("The user must be logged in in order to %s!", commandName);

        return new CommandResult(Status.Error, message, Action.of(SEND));
    }

    protected <T> String getCollectionFormattedStringRepresentation(Collection<T> collection,
                                                                    BiConsumer<T, StringBuilder> appendElementString) {
        StringBuilder sb = new StringBuilder();
        boolean addNewLine = false;
        int index = 1;

        for (T element : collection) {
            if (addNewLine) {
                sb.append(System.lineSeparator());
            }

            sb.append(index++).append(". ");
            appendElementString.accept(element, sb);

            addNewLine = true;
        }

        if (sb.isEmpty()) {
            sb.append("<none>");
        }

        return sb.toString();
    }

}
