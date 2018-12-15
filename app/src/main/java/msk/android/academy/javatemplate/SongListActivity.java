package msk.android.academy.javatemplate;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import msk.android.academy.javatemplate.adapter.SongAdapter;
import msk.android.academy.javatemplate.model.Song;

public class SongListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private ContentResolver contentResolver;
    private Cursor cursor;
    private Uri uri;
    private ArrayList<Song> listSongs;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_activity);

        init();
        adapter();
        getAllMediaMp3Files();
    }

    private void init() {
        listSongs = new ArrayList<>();
        recyclerView = findViewById(R.id.list);
    }

    private void adapter() {
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new SongAdapter(this, listSongs);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getAllMediaMp3Files() {
        contentResolver = getContentResolver();
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            Toast.makeText(getApplicationContext(), "Something Went Wrong.", Toast.LENGTH_LONG).show();
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(getApplicationContext(), "No Music Found on SD Card.", Toast.LENGTH_LONG).show();
        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int Artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                int SongID = cursor.getInt(id);

                String SongTitle = cursor.getString(Title);
                String SongArtist = cursor.getString(Artist);
                int SongDuration = cursor.getInt(duration);
                long songAlbum = cursor.getLong(albumId);


                listSongs.add(new Song(SongTitle, SongArtist, getDuration(SongDuration), SongID, songAlbum));

            } while (cursor.moveToNext());
        }
    }

    private String getDuration(int msecs) {
        int seconds = msecs / 1000 % 60;
        String correctSecs;
        if (seconds < 10) {
            correctSecs = "0" + Integer.toString(seconds);
        } else {
            correctSecs = Integer.toString(seconds);
        }
        return (msecs / (1000 * 60)) % 60 + ":" + correctSecs;
    }
}