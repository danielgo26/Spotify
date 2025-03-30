package data.format.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import data.format.TextFormatHandler;
import exception.InvalidTextFormatException;

import java.lang.reflect.Type;
import java.util.Map;

import static validation.ObjectValidator.validateNotNull;

public class JsonFormatHandler implements TextFormatHandler {

    private static final String EMPTY_MAP_JSON = "{}";

    private final Gson json;

    public JsonFormatHandler() {
        json = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    }

    @Override
    public <T, K> String getFormat(Map<T, K> map) {
        validateNotNull(map, "map to convert to json");

        return json.toJson(map);
    }

    @Override
    public <T, K> Map<T, K> loadMapFromFormat(String jsonString, Class<T> keyClass, Class<K> valueClass)
        throws InvalidTextFormatException {
        validateNotNull(jsonString, "json string");
        validateNotNull(keyClass, "map to load key class");
        validateNotNull(valueClass, "map to load value class");

        Type type = TypeToken.getParameterized(Map.class, keyClass, valueClass).getType();

        if (jsonString.isEmpty() || jsonString.isBlank()) {
            return json.fromJson(EMPTY_MAP_JSON, type);
        }

        try {
            return json.fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            throw new InvalidTextFormatException("The given json string is in invalid json format!", e);
        }
    }

}
