package output;

import command.structure.CommandResult;

import java.util.function.Consumer;

import static validation.ObjectValidator.validateNotNull;

public class StandardOutputVisualizer {

    public static void visualize(CommandResult response) {
        validateNotNull(response, "command result");

        Color color = response.isCommandSuccessful() ? Color.GREEN : Color.RED;

        visualize(response.responseMessage(), color, true);
    }

    public static void visualize(Exception e) {
        validateNotNull(e, "exception");

        visualize(e.getMessage(), Color.RED, true);
    }

    public static void visualize(String message, Color color, boolean addNewLine) {
        validateNotNull(message, "string message");
        validateNotNull(color, "color");

        Consumer<String> visualizeAction =  addNewLine ? System.out::println : System.out::print;

        visualizeAction.accept(color.getColorCode() + message + Color.WHITE.getColorCode());
    }

}