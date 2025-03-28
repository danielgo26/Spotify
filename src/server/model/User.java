package server.model;

import com.google.gson.annotations.Expose;
import server.audiostream.AudioStreamSender;
import server.repository.user.UserCredentials;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static validation.ObjectValidator.validateNotNull;

public class User {

    @Expose
    private final UserCredentials userCredentials;

    @Expose
    private final Map<String, Playlist> playlists;

    private boolean isLoggedIn;
    private AudioStreamSender audioStreamSender;

    public User(UserCredentials userCredentials) {
        this(userCredentials, new ArrayList<>());
    }

    public User(UserCredentials userCredentials, List<Playlist> playlists) {
        validateNotNull(userCredentials, "user credentials");
        validateNotNull(playlists, "playlists collection");

        this.userCredentials = userCredentials;
        isLoggedIn = false;
        this.audioStreamSender = null;

        this.playlists = playlists
            .stream()
            .collect(Collectors.toMap(
                Playlist::getName,
                playlist -> playlist,
                (existing, replacement) -> existing,
                LinkedHashMap::new));
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public Collection<Playlist> getPlaylists() {
        return playlists.values();
    }

    public Map<String, Playlist> getPlaylistsEntities() {
        return playlists;
    }

    public boolean contains(String playlistName) {
        validateNotNull(playlistName, "playlist name");

        return playlists.containsKey(playlistName);
    }

    public void addPlaylist(Playlist playlist) {
        validateNotNull(playlist, "playlist");

        playlists.put(playlist.getName(), playlist);
    }

    public void removePlaylist(String playlistName) {
        validateNotNull(playlistName, "playlist name");

        playlists.remove(playlistName);
    }

    public boolean isLoggedIn() {
        return this.isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public AudioStreamSender getAudioStreamSender() {
        return audioStreamSender;
    }

    public void setAudioStreamSender(AudioStreamSender audioStreamSender) {
        this.audioStreamSender = audioStreamSender;
    }

    public boolean isUserCurrentlyStreaming() {
        return this.audioStreamSender != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userCredentials.email(), user.userCredentials.email())
            && Objects.equals(userCredentials.password(), user.userCredentials.password());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCredentials.email(), userCredentials.password());
    }

}