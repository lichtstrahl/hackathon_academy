package msk.android.academy.javatemplate.model;

import android.net.Uri;

import java.io.Serializable;

public class Song implements Serializable{
    private String artist;
    private String title;
    //private Uri audioResourceId;
    private long id;
    private String duration;

    public Song(String title, String artist, String duration, long id/*Uri audioResourceId*/) {
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.id = id;
        //this.audioResourceId = audioResourceId;
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

    //public Uri getAudioResourceId() {
    //    return audioResourceId;
    //}

    public long getId(){
        return id;
    }
}
