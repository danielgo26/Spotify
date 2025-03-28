package server.repository.song;

import server.model.Song;
import server.repository.SerializableRepository;

import java.util.Map;

public interface SongRepositoryAPI extends SerializableRepository {

    int getSongsCount();

    //Note: unified means a unique name, corresponding to a song filename(like song01)
    Song getSongByUnifiedKey(String unifiedKey);

    Map<String, Song>  getAllSongEntities();

    boolean contains(String unifiedKey);

    void incrementPlayingsCount(String unifiedKey);

}
