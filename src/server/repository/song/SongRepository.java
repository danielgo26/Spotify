package server.repository.song;

import data.format.TextFormatHandler;
import data.serialization.DataLoader;
import data.serialization.DataSaver;
import exception.DataDeserializationException;
import exception.DataSerializationException;
import exception.InvalidTextFormatException;
import exception.LoadDataException;
import exception.SaveDataException;
import server.model.Song;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.Verify.verifyNotNull;
import static validation.ObjectValidator.validateNotNull;

public class SongRepository implements SongRepositoryAPI {

    private final LinkedHashMap<String, Song> songs;

    public SongRepository() {
        songs = new LinkedHashMap<>();
    }

    @Override
    public int getSongsCount() {
        return songs.size();
    }

    @Override
    public Song getSongByUnifiedKey(String unifiedKey) {
        validateNotNull(unifiedKey, "song key");

        return songs.get(unifiedKey);
    }

    @Override
    public Map<String, Song> getAllSongEntities() {
        return songs;
    }

    @Override
    public boolean contains(String unifiedKey) {
        validateNotNull(unifiedKey, "song key");

        return songs.containsKey(unifiedKey);
    }

    @Override
    public void incrementPlayingsCount(String unifiedKey) {
        validateNotNull(unifiedKey, "song key");

        Song song = songs.get(unifiedKey);
        if (song != null) {
            song.incrementCountOfPlayings();
        }
    }

    @Override
    public void loadFrom(DataLoader loader, TextFormatHandler handler) throws LoadDataException {
        verifyNotNull(loader, "songs data loader");
        verifyNotNull(handler, "text format handler");

        try {
            String loadedSongsData = loader.load();
            songs.putAll(handler.loadMapFromFormat(loadedSongsData, String.class, Song.class));
        } catch (DataDeserializationException | InvalidTextFormatException e) {
            throw new LoadDataException("Could not load songs into the system!", e);
        }
    }

    @Override
    public void saveTo(DataSaver saver, TextFormatHandler handler) throws SaveDataException {
        verifyNotNull(saver, "songs data saver");
        verifyNotNull(handler, "text format handler");

        try {
            String formattedSongsData = handler.getFormat(songs);
            saver.save(formattedSongsData, false);
        } catch (DataSerializationException e) {
            throw new SaveDataException("Could not save the songs from system!", e);
        }
    }

}