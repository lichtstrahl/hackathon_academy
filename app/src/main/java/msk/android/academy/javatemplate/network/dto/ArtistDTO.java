package msk.android.academy.javatemplate.network.dto;

import com.google.gson.annotations.SerializedName;

public class ArtistDTO {
    @SerializedName("strArtist")
    String artistName;
    @SerializedName("strStyle")
    String style;
    @SerializedName("strGenre")
    String genre;
    @SerializedName("strBiographyRU")
    String biographyRu;
    @SerializedName("strBiographyEN")
    String biographyEn;

    public String getArtistName() {
        return artistName;
    }

    public String getStyle() {
        return style;
    }

    public String getGenre() {
        return genre;
    }

    public String getBiographyRu() {
        return biographyRu;
    }

    public String getBiographyEn() {
        return biographyEn;
    }
}
