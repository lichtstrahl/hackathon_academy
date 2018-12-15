package msk.android.academy.javatemplate.network;

import msk.android.academy.javatemplate.network.dto.InfoResponse;
import msk.android.academy.javatemplate.network.dto.LyricResponse;

public class FullInfo {
    private LyricResponse lyricResponse;
    private InfoResponse infoResponse;

    public FullInfo(LyricResponse lyricResponse, InfoResponse infoResponse) {
        this.lyricResponse = lyricResponse;
        this.infoResponse = infoResponse;
    }

    public LyricResponse getLyricResponse() {
        return lyricResponse;
    }

    public InfoResponse getInfoResponse() {
        return infoResponse;
    }
}
