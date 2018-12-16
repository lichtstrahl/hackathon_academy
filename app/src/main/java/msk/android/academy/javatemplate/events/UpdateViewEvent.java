package msk.android.academy.javatemplate.events;

public class UpdateViewEvent {
    private int seconds;
    private String name;
    private String artist;
    private int duration;

    public UpdateViewEvent(int seconds, int duration, String name, String artist) {
        this.seconds = seconds;
        this.name = name;
        this.artist = artist;
        this.duration = duration;
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
}
