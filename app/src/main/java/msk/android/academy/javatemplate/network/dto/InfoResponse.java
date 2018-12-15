package msk.android.academy.javatemplate.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InfoResponse {
    @SerializedName("artists")
    List<ArtistDTO> artists;

    public List<ArtistDTO> getArtists() {
        return artists;
    }
}
