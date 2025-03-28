package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import server.idtoken.IdTokenGenerator;
import server.model.User;
import server.repository.user.UserCredentials;
import server.state.ServerState;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class LoginClientServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 3;
    private static final IdTokenGenerator ID_TOKEN_GENERATOR;

    static {
        ID_TOKEN_GENERATOR = new IdTokenGenerator();
    }

    public LoginClientServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "login command client input");

        User loggedUser = super.serverState.getCurrentlyLoggedUser();
        if (loggedUser != null) {
            String message = "Already logged! First logout!";

            return new CommandResult(Status.Error, message, Action.of(SEND));
        }

        UserCredentials userCredentials = super.getCredentials();

        String idToken = ID_TOKEN_GENERATOR.generateCheckSum(userCredentials.getCredentialsString());
        User userToLogin = super.serverState.getUserRepository().getUser(idToken);

        return getLoginCommandResult(userToLogin, idToken);
    }

    private CommandResult getLoginCommandResult(User userToLogin, String idToken) {
        Status status;
        String message;

        if (userToLogin != null) {
            if (userToLogin.isLoggedIn()) {
                status = Status.Error;
                message = "User is already logged in into the system!";
            } else {
                userToLogin.setIsLoggedIn(true);
                status = Status.OK;
                message = idToken;

                super.serverState.getKey().attach(idToken);
            }
        } else {
            status = Status.Error;
            message = "User does not exists into the system!";
        }

        return new CommandResult(status, message, Action.of(SEND));
    }

}