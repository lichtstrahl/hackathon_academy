package msk.android.academy.javatemplate.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import msk.android.academy.javatemplate.PlayerFragment;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.events.SongClickEvent;
import msk.android.academy.javatemplate.model.Song;
import msk.android.academy.javatemplate.ui.App;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private int count;
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
                .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_foreground))
                .load(albumArtUri)
                .into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Song song);
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
                    //PlayerFragment.start(context, songsList, position);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        EventBus.getDefault().post(new SongClickEvent(songsList, position));
                    }

                    Song song = songsList.get(position);
                    song.incrementCount();
                    App.getDatabase().songDao().update(song);
                }
            });
        }
    }
}

