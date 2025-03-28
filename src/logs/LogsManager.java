package logs;

import data.serialization.DataSaver;
import exception.DataSerializationException;
import exception.InvalidLogOperationException;

import java.time.LocalDateTime;

import static validation.ObjectValidator.validateNotNull;

public class LogsManager {

    private final DataSaver dataSaver;

    public LogsManager(DataSaver dataSaver) {
        validateNotNull(dataSaver, "data saver");

        this.dataSaver = dataSaver;
    }

    public void log(String message) throws InvalidLogOperationException {
        validateNotNull(message, "string message");

        StringBuilder toLog = new StringBuilder();

        LocalDateTime dateTimeNow = LocalDateTime.now();
        toLog.append("Exception thrown at [").append(dateTimeNow).append("]");

        toLog.append(System.lineSeparator());
        toLog.append(message);
        toLog.append(System.lineSeparator());

        try {
            dataSaver.save(toLog.toString(), true);
        } catch (DataSerializationException e) {
            throw new InvalidLogOperationException("Could not log message!", e);
        }
    }

}