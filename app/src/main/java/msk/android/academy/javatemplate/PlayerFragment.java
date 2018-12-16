package msk.android.academy.javatemplate;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import msk.android.academy.javatemplate.events.DetailsEvent;
import msk.android.academy.javatemplate.events.PlaySongEvent;
import msk.android.academy.javatemplate.events.SeekEvent;
import msk.android.academy.javatemplate.events.UpdateViewEvent;
import msk.android.academy.javatemplate.model.Song;
import msk.android.academy.javatemplate.network.dto.ArtistDTO;
import msk.android.academy.javatemplate.network.util.GlideApp;

public class PlayerFragment extends Fragment {

    public static boolean sStart = false;
    public static final String KEY_CURPOS = "KEY_CURPOS";
    public static final String KEY_LIST = "KEY_LIST";
    public static final String SAVE_PROGRESS = "SAVE_PROGRESS";
    public static final String SAVE_DURATION = "SAVE_DURATION";
    public static final String SAVE_NAME = "SAVE_NAME";
    public static final String SAVE_ARTIST = "SAVE_ARTIST";

    private List<Song> songs;
    private int curPos;

    private String name;
    private String artist;

    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.image)
    ImageView image;

    @Nullable
    private PlayerFragmentListener listener;

    private int progress;

    private long songId = -1;
    //private boolean playing = false;

    private int duration;
    //private int progress;



//    public static void start(Context activity, List<Song> songs, int currentPos) {
//        Intent startIntent = new Intent(activity, PlayerFragment.class);
//        PlayerFragment.sStart = true;
//
//        startIntent.putExtra(KEY_CURPOS, currentPos);
//        startIntent.putExtra(KEY_LIST, (ArrayList<Song>) songs);
//        activity.startActivity(startIntent);
//    }

    public static PlayerFragment getInstance(List<Song> songs, int currentPos) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_CURPOS, currentPos);
        bundle.putSerializable(KEY_LIST, (ArrayList<Song>) songs);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_player);
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        ButterKnife.bind(this, view);

        curPos = getArguments().getInt(KEY_CURPOS, 0);
        songs = (List<Song>) getArguments().getSerializable(KEY_LIST);

        String albumArtUri = String.valueOf(ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),songs.get(curPos).getCover()));

        GlideApp.with(getContext())
                .asBitmap()
                .placeholder(R.drawable.ic_library_music)
                .centerInside()
                .load(albumArtUri)
                .into(image);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    //musicSrv.seek(progress);
                    EventBus.getDefault().post(new SeekEvent(progress));
                    tvTime.setText(getTimeText(progress, duration));
                    //mediaPlayer.seekTo(progress);
                    //updateTime();
                }
            }
        });

        if (savedInstanceState != null) {
            progress = savedInstanceState.getInt(SAVE_PROGRESS);
            duration = savedInstanceState.getInt(SAVE_DURATION);
            name = savedInstanceState.getString(SAVE_NAME);
            artist = savedInstanceState.getString(SAVE_ARTIST);

        }

        seekBar.setMax(duration);
        seekBar.setProgress(progress);
        if (name != null && artist != null) {
            tvName.setText(name + " - " + artist);
        }
        tvTime.setText(getTimeText(progress, duration));


        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PROGRESS, seekBar.getProgress());
        outState.putInt(SAVE_DURATION, seekBar.getMax());
        outState.putString(SAVE_ARTIST, artist);
        outState.putString(SAVE_NAME, name);
    }

    //start and bind the service when the activity starts
    @Override
    public void onStart() {
        super.onStart();
        if (listener != null) {
            listener.startService();
        }

        if (PlayerFragment.sStart) {
            PlayerFragment.sStart = false;
            EventBus.getDefault().post(new PlaySongEvent());
        }

        /*if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //startService(playIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(playIntent);
            } else {
                startService(playIntent);
            }
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        progress = seekBar.getProgress();
    }

    //@Override
    //protected void onDestroy() {
    //    stopService(playIntent);
    //    musicSrv = null;
    //    super.onDestroy();
    //}

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateView(UpdateViewEvent event) {
        name = event.getName();
        artist = event.getArtist();
        if (name != null && artist != null) {
            tvName.setText(name + " - " + artist);
        }
        tvTime.setText(getTimeText(event.getSeconds(), event.getDuration()));
        seekBar.setMax(event.getDuration());
        seekBar.setProgress(event.getSeconds());

        progress = event.getSeconds();
        duration = event.getDuration();

        if (songId != event.getSongId()){
            songId = event.getSongId();

            String albumArtUri = String.valueOf(ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),event.getCoverId()));

            GlideApp.with(getContext())
                    .asBitmap()
                    .placeholder(R.drawable.ic_library_music)
                    .centerInside()
                    .load(albumArtUri)
                    .into(image);

            getActivity().setTitle(artist);
        }
    }

    private String getTimeText(int seconds, int duration) {
        int dSeconds = (duration / 1000) % 60;
        int dMinutes = ((duration / (1000 * 60)) % 60);
        int dHours = ((duration / (1000 * 60 * 60)) % 24);

        int cSeconds = (seconds / 1000) % 60;
        int cMinutes = ((seconds / (1000 * 60)) % 60);
        int cHours = ((seconds / (1000 * 60 * 60)) % 24);

        if (dHours == 0) {
            return String.format("%02d:%02d / %02d:%02d", cMinutes, cSeconds, dMinutes, dSeconds);
        } else {
            return String.format("%02d:%02d:%02d / %02d:%02d:%02d", cHours, cMinutes, cSeconds, dHours, dMinutes, dSeconds);
        }
    }

    public interface PlayerFragmentListener {
        void startService();

        boolean isPlaying();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PlayerFragmentListener) {
            listener = (PlayerFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.info_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                EventBus.getDefault().post(new DetailsEvent(artist, name));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
