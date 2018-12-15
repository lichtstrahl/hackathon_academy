package msk.android.academy.javatemplate.model;

public class Song {
    private String artist;
    private String title;
    private long audioResourceId;
    private String duration;

    public Song(String title, String artist, String duration, long audioResourceId) {
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.audioResourceId = audioResourceId;
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
}
