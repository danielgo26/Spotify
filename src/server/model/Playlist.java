package server.model;

import com.google.gson.annotations.Expose;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static validation.ObjectValidator.validateNotNull;

public class Playlist {

    @Expose
    private final String name;

    @Expose
    private final Set<String> songsKeys;

    public Playlist(String name) {
        validateNotNull(name, "name");

        this.name = name;
        this.songsKeys = new LinkedHashSet<>();
    }

    public Playlist(String name, Set<String> songsKeys) {
        this(name);

        validateNotNull(songsKeys, "songs key collection");

        this.songsKeys.addAll(songsKeys);
    }

    public String getName() {
        return name;
    }

    public Set<String> getSongKeys() {
        return songsKeys;
    }

    public boolean contains(String songKey) {
        validateNotNull(songKey, "song key");

        return songsKeys.contains(songKey);
    }

    public void addSong(String songKey) {
        validateNotNull(songKey, "song key");

        songsKeys.add(songKey);
    }

    public void removeSong(String songKey) {
        validateNotNull(songKey, "song key");

        songsKeys.remove(songKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(name, playlist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}