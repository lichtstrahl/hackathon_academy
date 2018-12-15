package msk.android.academy.javatemplate.network;

import io.reactivex.Observable;
import msk.android.academy.javatemplate.network.dto.LyricResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LyricAPI {
    @GET("/v1/{artist}/{title}")
    Observable<LyricResponse> getText(@Path("artist") String artist, @Path("title") String title);
}
