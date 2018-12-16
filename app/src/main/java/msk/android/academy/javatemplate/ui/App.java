package msk.android.academy.javatemplate.ui;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.util.Log;

import com.facebook.stetho.Stetho;

import msk.android.academy.javatemplate.BuildConfig;
import msk.android.academy.javatemplate.bd.AppDatabase;
import msk.android.academy.javatemplate.db.AppDatabase;
import msk.android.academy.javatemplate.network.InfoAPI;
import msk.android.academy.javatemplate.network.LyricAPI;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static final String DATABASE_NAME = "alexDataBase";
    private static AppDatabase database;
    private static final String nameDB = "dbMusicInfo";
    private static LyricAPI lyricAPI;
    private static InfoAPI infoAPI;
    private static AppDatabase favoritesDB;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        favoritesDB = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        database = Room.databaseBuilder(this, AppDatabase.class, nameDB)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_LYRIC)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        lyricAPI = retrofit.create(LyricAPI.class);

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_INFO)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        infoAPI = retrofit.create(InfoAPI.class);
    }

    public static LyricAPI getLyricAPI() {
        return lyricAPI;
    }

    public static InfoAPI getInfoAPI() {
        return infoAPI;
    }

    public static AppDatabase getDB() {
        return database;
    }

    public static void logI(String msg) {
        Log.i(BuildConfig.TAG_GLOBAL, msg);
    }

    public static void logE(String msg) {
        Log.e(BuildConfig.TAG_GLOBAL, msg);
    }

    public static void logW(String msg) {
        Log.w(BuildConfig.TAG_GLOBAL, msg);
    }

    public static AppDatabase getFavoritesDB() {
        return favoritesDB;
    }
}
