package exception;

import output.Color;
import logs.LogsManager;

import java.io.PrintWriter;
import java.io.StringWriter;

import static output.StandardOutputVisualizer.visualize;
import static validation.ObjectValidator.validateNotNull;

public class ExceptionHandler {

    private final LogsManager logsManager;

    public ExceptionHandler(LogsManager logsManager) {
        validateNotNull(logsManager, "logs manager");

        this.logsManager = logsManager;
    }

    public void handle(Exception e, boolean shouldVisualize) {
        handle(e, shouldVisualize, true);
    }

    public void handle(Exception e, boolean shouldVisualize, boolean addCauseMessage) {
        validateNotNull(e, "exception");

        if (shouldVisualize) {
            String message;

            if (addCauseMessage) {
                message = getFullExceptionMessage(e);
            } else {
                message = e.getMessage();
            }

            visualize(message, Color.RED, true);
        }

        try {
            String toLog = getStackTraceString(e);
            logsManager.log(toLog);
        } catch (InvalidLogOperationException logEx) {
            visualize(logEx.getMessage(), Color.RED, true);
        }
    }

    private String getFullExceptionMessage(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage());

        Throwable next = e.getCause();
        while (next != null) {
            sb.append(" ");
            sb.append(next.getMessage());

            next = next.getCause();
        }

        return sb.toString();
    }

    private String getStackTraceString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        return sw.toString();
    }

}