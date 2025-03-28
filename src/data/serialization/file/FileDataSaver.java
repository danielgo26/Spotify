package data.serialization.file;

import data.serialization.DataSaver;
import exception.DataSerializationException;
import exception.FileNotCreatedException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static data.serialization.file.FileHandler.createFilePathIfNotExists;
import static validation.ObjectValidator.validateNotNull;

public class FileDataSaver implements DataSaver {

    private final Path filePathToWriteTo;

    public FileDataSaver(Path filePathToWriteTo) {
        validateNotNull(filePathToWriteTo, "file path");

        this.filePathToWriteTo = filePathToWriteTo;
    }

    @Override
    public void save(String data, boolean append) throws DataSerializationException {
        validateNotNull(data, "data to save");

        try {
            createFilePathIfNotExists(filePathToWriteTo);
            write(data, append);
        } catch (IOException | FileNotCreatedException e) {
            throw new DataSerializationException("Could not serialize string data into file!", e);
        }
    }

    private void write(String data, boolean append) throws IOException {
        StandardOpenOption option = append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING;

        try (BufferedWriter writer = Files.newBufferedWriter(filePathToWriteTo ,
            StandardCharsets.UTF_8, option)) {

            writer.write(data);
            writer.flush();
        }
    }

}