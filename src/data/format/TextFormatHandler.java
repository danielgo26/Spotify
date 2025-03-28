package data.format;

import exception.InvalidTextFormatException;

import java.util.Map;

public interface TextFormatHandler {

    <T, K> String getFormat(Map<T, K> map);

    <T, K> Map<T, K> loadMapFromFormat(String jsonString, Class<T> keyClass, Class<K> valueClass)
        throws InvalidTextFormatException;

}
