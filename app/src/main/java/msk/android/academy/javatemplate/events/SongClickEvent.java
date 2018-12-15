package msk.android.academy.javatemplate.events;

import java.util.List;

import msk.android.academy.javatemplate.model.Song;

public class SongClickEvent {
    private List<Song> songs;
    private int position;

    public SongClickEvent(List<Song> songs, int position) {
        this.songs = songs;
        this.position = position;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public int getPosition() {
        return position;
    }
}
