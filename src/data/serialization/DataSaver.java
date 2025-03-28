package data.serialization;

import exception.DataSerializationException;

public interface DataSaver {

    void save(String data, boolean append) throws DataSerializationException;

}
