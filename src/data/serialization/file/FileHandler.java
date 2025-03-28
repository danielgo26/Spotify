package data.serialization.file;

import exception.FileNotCreatedException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {

    public static void createFilePathIfNotExists(Path filePath) throws FileNotCreatedException {
        try {
            Files.createDirectories(filePath.getParent());

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

        } catch (IOException e) {
            throw new FileNotCreatedException("Could not create file with path: " + filePath);
        }
    }

    public static void verifyPathExists(Path path) throws FileNotFoundException {
        if (!Files.exists(path)) {
            String message = String.format("Could not find file named %s!", path);

            throw new FileNotFoundException(message);
        }
    }

}