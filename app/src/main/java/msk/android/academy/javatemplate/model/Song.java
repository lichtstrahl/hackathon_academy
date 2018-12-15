package msk.android.academy.javatemplate.model;

import java.io.Serializable;

public class Song implements Serializable {
    private String artist;
    private String title;
    private long audioResourceId;
    private String duration;
    private long cover;

    public Song(String title, String artist, String duration, long audioResourceId, long cover) {
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.audioResourceId = audioResourceId;
        this.cover = cover;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public long getAudioResourceId() {
        return audioResourceId;
    }

    public long getCover() {
        return cover;
    }
}
