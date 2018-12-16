package msk.android.academy.javatemplate.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import msk.android.academy.javatemplate.MusicService;
import msk.android.academy.javatemplate.PlayerFragment;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.SongListFragment;
import msk.android.academy.javatemplate.events.DetailsEvent;
import msk.android.academy.javatemplate.events.GoPlayerEvent;
import msk.android.academy.javatemplate.events.PausePlayerEvent;
import msk.android.academy.javatemplate.events.PlayNextEvent;
import msk.android.academy.javatemplate.events.PlayPrevEvent;
import msk.android.academy.javatemplate.events.PlaySongEvent;
import msk.android.academy.javatemplate.events.SeekEvent;
import msk.android.academy.javatemplate.events.SongClickEvent;
import msk.android.academy.javatemplate.model.Song;

public class MainActivity extends AppCompatActivity implements PlayerFragment.PlayerFragmentListener {
    @BindView(R.id.layoutBG)
    ViewGroup layoutBG;

    @BindView(R.id.ll_control)
    ViewGroup llControl;

    @BindView(R.id.btn_startstop)
    ImageButton btnStartStop;

    //service
    private MusicService musicSrv;
    private Intent playIntent;


    private List<Song> songs;
    private int curPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layoutBG, new SongListFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(playIntent);
            } else {
                startService(playIntent);
            }*/
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        //unbindService(musicConnection);
        super.onStop();

        //unbindService(musicConnection);
    }

    @Override
    protected void onDestroy() {
        //unbindService(musicConnection);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSongClicked(@NonNull SongClickEvent event) {
        PlayerFragment playerFragment = PlayerFragment.getInstance(event.getSongs(), event.getPosition());
        PlayerFragment.sStart = true;

        llControl.setVisibility(View.VISIBLE);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50,
                r.getDisplayMetrics()
        );
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutBG.getLayoutParams();
        params.setMargins(0, 0, 0, px);
        layoutBG.setLayoutParams(params);


        songs = event.getSongs();
        curPos = event.getPosition();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutBG, playerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDetailsClicked(@NonNull DetailsEvent event) {
        InfoFragment infoFragment = InfoFragment.getInstance(event.getArtist(), event.getName());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutBG, infoFragment)
                .addToBackStack(null)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGo(@NonNull GoPlayerEvent event) {
        musicSrv.go();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPause(@NonNull PausePlayerEvent event) {
        musicSrv.pausePlayer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayNext(@NonNull PlayNextEvent event) {
        musicSrv.playNext();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayPrev(@NonNull PlayPrevEvent event) {
        musicSrv.playPrev();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSeek(@NonNull SeekEvent event) {
        musicSrv.seek(event.getProgress());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaySong(@NonNull PlaySongEvent event) {
        if (musicSrv != null) {
            musicSrv.setList(songs);
            musicSrv.setSong(curPos);
            musicSrv.playSong();
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            //musicSrv.setList(songs);
            //musicSrv.setSong(curPos);
            //if (PlayerFragment.sStart) {
            //musicSrv.playSong();
            //    PlayerFragment.sStart = false;
            //}
            //playing = musicSrv.isPlaying();
            //musicBound = true;

            if (musicSrv.isPlaying()){
                llControl.setVisibility(View.VISIBLE);
                Resources r = getResources();
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50,
                        r.getDisplayMetrics()
                );
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutBG.getLayoutParams();
                params.setMargins(0, 0, 0, px);
                layoutBG.setLayoutParams(params);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            /*musicBound = false;*/
        }
    };

    @Override
    public void startService() {
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
    public boolean isPlaying() {
        return musicSrv.isPlaying();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount <= 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @OnClick(R.id.btn_forward)
    void onButtonForward() {
        musicSrv.playNext();
        //EventBus.getDefault().post(new PlayNextEvent());
    }

    @OnClick(R.id.btn_back)
    void onButtonBack() {
        musicSrv.playPrev();
        //EventBus.getDefault().post(new PlayPrevEvent());
    }

    @OnClick(R.id.btn_startstop)
    void onButtonStartStop() {
        if (musicSrv.isPlaying()) {
            musicSrv.pausePlayer();
            btnStartStop.setImageResource(R.drawable.ic_play_circle_outline);
            //EventBus.getDefault().post(new PausePlayerEvent());
        } else {
            musicSrv.go();
            btnStartStop.setImageResource(R.drawable.ic_pause_circle_outline);
            //EventBus.getDefault().post(new GoPlayerEvent());
        }

    }
}
