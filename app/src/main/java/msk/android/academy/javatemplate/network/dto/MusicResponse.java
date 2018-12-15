package msk.android.academy.javatemplate.network.dto;

import com.google.gson.annotations.SerializedName;

public class MusicResponse {
    @SerializedName("lyrics")
    String lyrics;
    @SerializedName("error")
    String error;


    public String getLyrics() {
        return lyrics;
    }

    public String getError() {
        return error;
    }
}
