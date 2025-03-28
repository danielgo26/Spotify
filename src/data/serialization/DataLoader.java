package data.serialization;

import exception.DataDeserializationException;

public interface DataLoader {

    String load() throws DataDeserializationException;

}
