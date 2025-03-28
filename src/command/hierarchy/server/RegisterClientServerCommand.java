package command.hierarchy.server;

import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import exception.InvalidUserCredentialsException;
import server.model.User;
import server.repository.user.UserCredentials;
import server.repository.user.UserRepositoryAPI;
import server.state.ServerState;

import static command.structure.Action.SEND;
import static validation.ObjectValidator.validateArrayLength;

public class RegisterClientServerCommand extends ServerCommand {

    private static final int EXPECTED_INPUT_LENGTH = 3;

    public RegisterClientServerCommand(String clientInput, ServerState serverState) {
        super(clientInput, serverState);
    }

    @Override
    public CommandResult execute() {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "register command client input");

        UserCredentials userCredentials = super.getCredentials();
        UserRepositoryAPI userRepository = super.serverState.getUserRepository();

        return getRegisterCommandResult(userCredentials, userRepository);
    }

    private CommandResult getRegisterCommandResult(UserCredentials userCredentials, UserRepositoryAPI userRepository) {
        Status status;
        String message;

        if (userRepository.contains(userCredentials)) {
            status = Status.Error;
            message = "User already exists!";
        } else {
            try {
                userCredentials.verifyAreValid();

                return sentRegisterRequestToServer(userCredentials, userRepository);
            } catch (InvalidUserCredentialsException e) {
                status = Status.Error;
                message = "Invalid credentials! " + e.getMessage();
            }
        }

        return new CommandResult(status, message, Action.of(SEND));
    }

    private CommandResult sentRegisterRequestToServer(UserCredentials userCredentials, UserRepositoryAPI userRepository) {
        Status status;
        String message;

        boolean registered = userRepository.addNewUser(new User(userCredentials));
        if (registered) {
            status = Status.OK;
            message = "User successfully registered into the system!";
        } else {
            status = Status.Error;
            message = "Server error! Could not register user!";
        }

        return new CommandResult(status, message, Action.of(SEND));
    }

}