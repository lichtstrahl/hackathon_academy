package msk.android.academy.javatemplate.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import msk.android.academy.javatemplate.PlayerActivity;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.model.Song;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    private List<Song> songsList;

    public SongAdapter(Context context, List<Song> songsList) {
        this.context = context;
        this.songsList = songsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.songTextView.setText(songsList.get(position).getTitle());
        viewHolder.artistTextView.setText(songsList.get(position).getArtist());
        viewHolder.durationTextView.setText(songsList.get(position).getDuration());
        viewHolder.songId = songsList.get(position).getAudioResourceId();
        viewHolder.position = position;

        String albumArtUri = String.valueOf(ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),songsList.get(position).getCover()));

        Glide.with(context)
                .asBitmap()
                .load(albumArtUri)
                .into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView songTextView;
        private TextView artistTextView;
        private TextView durationTextView;
        private ImageView image;
        private long songId;
        private int position;

        public ViewHolder(View view) {
            super(view);

            songTextView = view.findViewById(R.id.song);
            artistTextView = view.findViewById(R.id.artist);
            durationTextView = view.findViewById(R.id.duration);
            image = view.findViewById(R.id.primary_action);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayerActivity.start(context, songsList, position);
                }
            });
        }
    }
}

