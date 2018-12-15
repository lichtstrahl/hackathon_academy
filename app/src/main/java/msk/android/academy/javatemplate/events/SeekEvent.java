package msk.android.academy.javatemplate.events;

public class SeekEvent {
    private int progress;

    public SeekEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
