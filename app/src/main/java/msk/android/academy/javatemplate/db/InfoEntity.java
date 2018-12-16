package msk.android.academy.javatemplate.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import msk.android.academy.javatemplate.network.dto.ArtistDTO;

@Entity
public class InfoEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String artist;
    public String track;
    @Nullable
    public String lyrics;
    @Nullable
    public String artistName;
    @Nullable
    public String style;
    @Nullable
    public String genre;
    @Nullable
    public String biographyRu;
    @Nullable
    public String biographyEn;
    @Nullable
    public String artUrl;
    @Nullable
    public String facebookUrl;
    @Nullable
    public String artistLogoUrl;
    @Nullable
    public String webSiteUrl;

    public InfoEntity() {}

    public InfoEntity(String artist, String track, ArtistDTO dto) {
        this.artist = artist;
        this.track = track;
        fromArtistDTO(dto);
    }

    public InfoEntity(String artist, String track, String lyric) {
        this.artist = artist;
        this.track = track;
        fromLyric(lyric);
    }

    public void fromLyric(String lyric) {
        this.lyrics = lyric;
    }

    public void fromArtistDTO(ArtistDTO dto) {
        this.artistName = dto.getArtistName();
        this.style = dto.getStyle();
        this.genre = dto.getGenre();
        this.biographyRu = dto.getBiographyRu();
        this.biographyEn = dto.getBiographyEn();
        this.artUrl = dto.getArtUrl();
        this.facebookUrl = dto.getFacebookUrl();
        this.artistLogoUrl = dto.getArtistLogoUrl();
        this.webSiteUrl = dto.getWebSiteUrl();
    }

    public ArtistDTO toAtristDTO() {
        return new ArtistDTO(
                artistName, style, genre, biographyRu, biographyEn, artUrl, facebookUrl, artistLogoUrl, webSiteUrl
        );
    }

    public String getLyric() {
        return lyrics;
    }
}
