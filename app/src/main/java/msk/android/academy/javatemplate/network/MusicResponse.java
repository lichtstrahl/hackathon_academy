package msk.android.academy.javatemplate.network;

import com.google.gson.annotations.SerializedName;

public class MusicResponse {
    @SerializedName("lyrics")
    String lyrics;

    public String getLyrics() {
        return lyrics;
    }
}
