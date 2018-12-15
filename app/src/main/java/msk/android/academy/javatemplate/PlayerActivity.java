package msk.android.academy.javatemplate;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import msk.android.academy.javatemplate.events.UpdateViewEvent;
import msk.android.academy.javatemplate.model.Song;

public class PlayerActivity extends AppCompatActivity {

    public static boolean sStart = false;
    public static final String KEY_CURPOS = "KEY_CURPOS";
    public static final String KEY_LIST = "KEY_LIST";
    //service
    private MusicService musicSrv;
    private Intent playIntent;

    private List<Song> songs;
    private int curPos;

    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.tv_name)
    TextView tvName;


    //binding
    private boolean musicBound = false;

    private boolean playing = false;

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songs);
            musicSrv.setSong(curPos);
            if (PlayerActivity.sStart) {
                musicSrv.playSong();
                PlayerActivity.sStart = false;
            }
            playing = musicSrv.isPlaying();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public static void start(Context activity, List<Song> songs, int currentPos) {
        Intent startIntent = new Intent(activity, PlayerActivity.class);
        PlayerActivity.sStart = true;

        startIntent.putExtra(KEY_CURPOS, currentPos);
        startIntent.putExtra(KEY_LIST, (ArrayList<Song>) songs);
        activity.startActivity(startIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_player);

        ButterKnife.bind(this);

        curPos = getIntent().getIntExtra(KEY_CURPOS, 0);
        songs = (List<Song>) getIntent().getSerializableExtra(KEY_LIST);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicSrv != null) {
                    musicSrv.seek(progress);
                    //mediaPlayer.seekTo(progress);
                    //updateTime();
                }
            }
        });
    }

    @OnClick(R.id.btn_startstop)
    void onButtonStartStop() {
        if (playing) {
            musicSrv.pausePlayer();
        } else {
            musicSrv.go();
        }
        playing = !playing;
    }

    @OnClick(R.id.btn_forward)
    void onButtonForward() {
        musicSrv.playNext();
    }

    @OnClick(R.id.btn_back)
    void onButtonBack() {
        musicSrv.playPrev();
    }

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //startService(playIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(playIntent);
            } else {
                startService(playIntent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateView(UpdateViewEvent event) {
        tvName.setText(event.getName());
        tvTime.setText(getTimeText(event.getSeconds(), event.getDuration()));
        seekBar.setMax(event.getDuration());
        seekBar.setProgress(event.getSeconds());
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


}
