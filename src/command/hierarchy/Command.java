package command.hierarchy;

import command.structure.CommandResult;
import exception.ConnectionException;
import exception.IOChannelException;
import validation.ObjectValidator;

import java.io.IOException;

public abstract class Command {

    private static final String INPUT_SPLIT_PATTERN = " ";

    protected final String userInput;
    protected final String[] userInputArgs;

    public Command(String userInput) {
        ObjectValidator.validateNotNull(userInput, "user input string");

        this.userInput = userInput;
        this.userInputArgs = userInput.split(INPUT_SPLIT_PATTERN);
    }

    public abstract CommandResult execute() throws IOException, ConnectionException, IOChannelException;

}