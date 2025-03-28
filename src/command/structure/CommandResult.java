package command.structure;

import exception.runtime.InvalidServerResponseException;

import java.nio.charset.StandardCharsets;

import static validation.ObjectValidator.validateArrayLength;
import static validation.ObjectValidator.validateNotNull;

public record CommandResult(Status status, String responseMessage, Action action) {

    private static final String RESPONSE_SPLIT_PATTERN = ":";

    public static CommandResult of(String response, Action action) {
        validateNotNull(response, "server response string");
        validateNotNull(action, "command result action");

        String[] responseArgs = response.split(RESPONSE_SPLIT_PATTERN);
        validateArrayLength(responseArgs, 2, "server response");
        
        try {
            Status responseStatus = Status.valueOf(responseArgs[0]);

            return new CommandResult(responseStatus, responseArgs[1], action);
        } catch (IllegalArgumentException e) {
            throw new InvalidServerResponseException("Invalid status of server response: " + responseArgs[0], e);
        }
    }

    public byte[] toByteArray() {
        String stringRepresentation = status.toString() + RESPONSE_SPLIT_PATTERN + responseMessage;

        return stringRepresentation.getBytes(StandardCharsets.UTF_8);
    }

    public boolean isCommandSuccessful() {
        return status == Status.OK;
    }

    public boolean isCommandTerminatingProgram() {
        return action.isExiting();
    }

    public boolean isCommandVisualizing() {
        return action.isVisualizing();
    }

    public boolean isCommandSending() {
        return action.isSending();
    }

}