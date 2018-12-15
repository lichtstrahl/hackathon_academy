package msk.android.academy.javatemplate.network;

import msk.android.academy.javatemplate.network.dto.InfoResponse;
import msk.android.academy.javatemplate.network.dto.MusicResponse;

public class FullInfo {
    private MusicResponse musicResponse;
    private InfoResponse infoResponse;

    public FullInfo(MusicResponse musicResponse, InfoResponse infoResponse) {
        this.musicResponse = musicResponse;
        this.infoResponse = infoResponse;
    }

    public MusicResponse getMusicResponse() {
        return musicResponse;
    }

    public InfoResponse getInfoResponse() {
        return infoResponse;
    }
}
