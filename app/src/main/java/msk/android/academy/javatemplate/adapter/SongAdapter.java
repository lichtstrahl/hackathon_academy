package msk.android.academy.javatemplate.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.events.SongClickEvent;
import msk.android.academy.javatemplate.model.Song;
import msk.android.academy.javatemplate.network.util.GlideApp;
import msk.android.academy.javatemplate.ui.App;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private List<Song> songsList;
    private LayoutInflater inflater;

    public SongAdapter(LayoutInflater inflater, List<Song> songsList) {
        this.inflater = inflater;
        this.songsList = songsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.song_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindSong(songsList.get(position));
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView songTextView;
        private TextView artistTextView;
        private TextView durationTextView;
        private TextView viewCount;
        private ImageView image;
        private long songId;

        public ViewHolder(View view) {
            super(view);

            songTextView = view.findViewById(R.id.song);
            artistTextView = view.findViewById(R.id.artist);
            durationTextView = view.findViewById(R.id.viewDuration);
            viewCount = view.findViewById(R.id.viewCount);
            image = view.findViewById(R.id.primary_action);


            view.setOnClickListener((v) -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        EventBus.getDefault().post(new SongClickEvent(songsList, position));
                    }

                    Song song = songsList.get(position);
                    song.incrementCount();
                    App.getFavoritesDB().songDao().update(song);
                });
        }


        public  void bindSong(Song song) {
            songTextView.setText(song.getTitle());
            artistTextView.setText(song.getArtist());
            durationTextView.setText(song.getDuration());
            viewCount.setText(String.valueOf(song.getCount()));
            songId = song.getAudioResourceId();

            String albumArtUri = String.valueOf(ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),song.getCover()));

            GlideApp.with(image.getContext())
                    .load(albumArtUri)
                    .into(image);
        }
    }
}

