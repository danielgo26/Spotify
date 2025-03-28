package server.model;

import com.google.gson.annotations.Expose;

import java.util.Objects;
import java.util.Set;

import static validation.ObjectValidator.validateNotNull;

public class Song implements Comparable<Song> {

    @Expose
    private String title;

    @Expose
    private Set<String> singers;

    @Expose
    private Integer countOfPlayings;

    public Song(String title, Set<String> singers) {
        validateNotNull(title, "song title string");
        validateNotNull(singers, "singers collection");

        this.title = title;
        this.singers = singers;
        this.countOfPlayings = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        validateNotNull(title, "song title");

        this.title = title;
    }

    public Set<String> getSingers() {
        return singers;
    }

    public void setSingers(Set<String> singers) {
        validateNotNull(singers, "singers collection");

        this.singers = singers;
    }

    public Integer getCountOfPlayings() {
        return countOfPlayings;
    }

    public void incrementCountOfPlayings() {
        this.countOfPlayings++;
    }

    public String getSearchByKeywordsString() {
        String singersStr = String.join(" ", singers);
        String searchSongString = title + " " + singersStr;

        return searchSongString.toLowerCase();
    }

    @Override
    public String toString() {
        String singersStr = String.join(", ", singers);

        return "\"" + title + "\"" + ", by " + singersStr;
    }

    @Override
    public int compareTo(Song other) {
        return other.countOfPlayings.compareTo(countOfPlayings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(title, song.title) && Objects.equals(singers, song.singers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, singers);
    }

}