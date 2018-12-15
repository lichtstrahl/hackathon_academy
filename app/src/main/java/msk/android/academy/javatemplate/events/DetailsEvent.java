package msk.android.academy.javatemplate.events;

import java.util.List;

import msk.android.academy.javatemplate.model.Song;

public class DetailsEvent {
    private String artist;
    private String name;

    public DetailsEvent(String artist, String name) {
        this.artist = artist;
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }
}
