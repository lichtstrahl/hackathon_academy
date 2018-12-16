package msk.android.academy.javatemplate.events;

public class UpdateViewEvent {
    private int seconds;
    private String name;
    private String artist;
    private int duration;
    private long songId;
    private long coverId;


    public UpdateViewEvent(int seconds, int duration, String name, String artist, long songId, long coverId) {

        this.seconds = seconds;
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.songId = songId;
        this.coverId = coverId;
    }

    public int getSeconds() {
        return seconds;
    }

    public String getName() {
        return name;
    }

    public int getDuration(){
        return duration;
    }

    public String getArtist() {
        return artist;
    }

    public long getSongId() {
        return songId;
    }

    public long getCoverId() {
        return coverId;
    }
}
