package msk.android.academy.javatemplate;

import android.content.ContentResolver;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

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
    private EditText input;
    private EditTextListener inputListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_list_activity, container, false);
        input = view.findViewById(R.id.searchInput);
        inputListener = new EditTextListener(input);
        init(view);
        adapter();
        inputListener.subscribe(adapter::setFilter);
        getAllMediaMp3Files();
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        inputListener.unsubscribe();
    }

    private void init(View view) {
        listSongs = new ArrayList<>();
        recyclerView = view.findViewById(R.id.list);
    }

    private void adapter() {
        recyclerView.setHasFixedSize(true);

        adapter = new SongAdapter(getLayoutInflater(), listSongs);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        }


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

            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int atrist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                int songID = cursor.getInt(id);

                String songTitle = cursor.getString(title);
                String songArtist = cursor.getString(atrist);
                int songDuration = cursor.getInt(duration);
                long songAlbum = cursor.getLong(albumId);

                Song song = new Song(songTitle, songArtist, getDuration(songDuration), songID, songAlbum);
                Song list = App.getFavoritesDB().songDao().searchSongs(songArtist, songTitle);
                if (list == null) {
                    App.getFavoritesDB().songDao().insert(song);
                    adapter.append(song);
                } else {
                    adapter.append(list);
                }

            } while (cursor.moveToNext());

            adapter.notifyOriginSong();
            adapter.sort();
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
