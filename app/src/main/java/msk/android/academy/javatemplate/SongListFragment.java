package msk.android.academy.javatemplate;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import msk.android.academy.javatemplate.adapter.SongAdapter;
import msk.android.academy.javatemplate.model.Song;
import msk.android.academy.javatemplate.ui.App;

public class SongListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private ContentResolver contentResolver;
    private Cursor cursor;
    private Uri uri;
    private ArrayList<Song> listSongs;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.song_list_activity);
        View view = inflater.inflate(R.layout.song_list_activity, container, false);

        init(view);
        adapter();
        getAllMediaMp3Files();

        return view;
    }

    private void init(View view) {
        listSongs = new ArrayList<>();
        recyclerView = view.findViewById(R.id.list);
    }

    private void adapter() {
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new SongAdapter(getLayoutInflater(), listSongs);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getAllMediaMp3Files() {
        contentResolver = getContext().getContentResolver();
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            Toast.makeText(getContext(), "Something Went Wrong.", Toast.LENGTH_LONG).show();
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(getContext(), "No Music Found on SD Card.", Toast.LENGTH_LONG).show();
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

                Song song = new Song(SongTitle, SongArtist, getDuration(SongDuration), SongID, songAlbum);
                Song list = App.getFavoritesDB().songDao().searchSongs(SongArtist, SongTitle);
                if (list == null) {
                    App.getFavoritesDB().songDao().insert(song);
                    listSongs.add(list);
                } else {
                    listSongs.add(list);
                }

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
