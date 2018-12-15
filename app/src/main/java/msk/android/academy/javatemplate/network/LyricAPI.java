package msk.android.academy.javatemplate.network;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LyricAPI {
    @GET("/v1/{artist}/{title}")
    Single<EmptyDTO> getText(@Path("artist") String artist, @Path("title") String title);
}
