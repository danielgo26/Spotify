package command.hierarchy.client;

import client.state.ClientState;
import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import exception.IOChannelException;

import java.util.Map;

import static command.structure.Action.VISUALIZE;
import static validation.ObjectValidator.validateArrayLength;

public class HelpClientCommand extends ClientCommand {

    private static final int EXPECTED_INPUT_LENGTH = 1;

    public HelpClientCommand(String userInput, ClientState clientState) {
        super(userInput, clientState);
    }

    @Override
    public CommandResult execute() throws IOChannelException {
        validateArrayLength(super.userInputArgs, EXPECTED_INPUT_LENGTH, "help command user input");

        return new CommandResult(Status.OK, getHelpMenu(), Action.of(VISUALIZE));
    }

    private String getHelpMenu() {
        StringBuilder sb = new StringBuilder();

        sb.append("---------Available commands---------");
        sb.append(System.lineSeparator());

        Map<String, String> availableCommandFormats = ClientCommandCreator.getAvailableCommandFormats();
        int index = 1;

        for (Map.Entry<String, String> commandEntry : availableCommandFormats.entrySet()) {
            String commandName = commandEntry.getKey();
            String commandFormat = commandEntry.getValue();

            sb.append(index++).append(". ");

            sb.append(commandName).append(" ").append(commandFormat);
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

}