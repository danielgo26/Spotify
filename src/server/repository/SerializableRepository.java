package server.repository;

import data.serialization.DataLoader;
import data.serialization.DataSaver;
import exception.LoadDataException;
import exception.SaveDataException;
import data.format.TextFormatHandler;

public interface SerializableRepository {

    void loadFrom(DataLoader loader, TextFormatHandler handler) throws LoadDataException;

    void saveTo(DataSaver saver, TextFormatHandler handler) throws SaveDataException;

}
