package data.serialization.file;

import data.serialization.DataLoader;
import exception.DataDeserializationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static data.serialization.file.FileHandler.verifyPathExists;
import static validation.ObjectValidator.validateNotNull;

public class FileDataLoader implements DataLoader {

    private final Path fileToLoadFrom;

    public FileDataLoader(Path fileToLoadFrom) {
        validateNotNull(fileToLoadFrom, "file path");

        this.fileToLoadFrom = fileToLoadFrom;
    }

    @Override
    public String load() throws DataDeserializationException {
        try {
            verifyPathExists(fileToLoadFrom);

            try (BufferedReader reader = Files.newBufferedReader(fileToLoadFrom)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            throw new DataDeserializationException("Could not deserialize data!", e);
        }
    }

}
