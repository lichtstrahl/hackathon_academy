package msk.android.academy.javatemplate.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Song implements Serializable {
    public static final Comparator Comparator = new Song.Comparator();

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String artist;
    private String title;
    private long audioResourceId;
    private String duration;
    private long cover;
    private int count = 0;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void incrementCount() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static class Comparator implements java.util.Comparator<Song> {
        @Override
        public int compare(Song o1, Song o2) {
            if (o1.getCount() > o2.getCount()) {
                return -1;
            } else if (o1.getCount() == o2.getCount()) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
