package msk.android.academy.javatemplate.events;

public class UpdateViewEvent {
    private int seconds;
    private String name;
    private int duration;

    public UpdateViewEvent(int seconds, int duration, String name) {
        this.seconds = seconds;
        this.name = name;
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
}
