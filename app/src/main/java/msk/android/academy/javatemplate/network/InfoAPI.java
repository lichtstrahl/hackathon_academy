package msk.android.academy.javatemplate.network;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InfoAPI {
    @GET("/api/v1/json/1/search.php")
    Observable<InfoResponse> searchArtist(@Query("s") String artist);
}
