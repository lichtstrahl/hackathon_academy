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

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import msk.android.academy.javatemplate.model.Song;

public class PlayerActivity extends AppCompatActivity {

    public static final String KEY_CURPOS = "KEY_CURPOS";
    public static final String KEY_LIST = "KEY_LIST";
    //service
    private MusicService musicSrv;

    private List<Song> songs;
    private int curPos;

    //binding
    private boolean musicBound=false;

    private boolean playing = false;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            //musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public static void start(Context activity, List<Song> songs, int currentPos){
        Intent startIntent = new Intent(activity, PlayerActivity.class);

        startIntent.putExtra(KEY_CURPOS, currentPos);
        startIntent.putExtra(KEY_LIST, (ArrayList<Song>)songs);
        activity.startActivity(startIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_player);

        curPos = getIntent().getIntExtra(KEY_CURPOS, 0);
        songs = (List<Song>) getIntent().getSerializableExtra(KEY_LIST);

        Intent playIntent = new Intent(this, MusicService.class);
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(playIntent);
        } else {
            startService(playIntent);
        }

        Button btnStart = findViewById(R.id.btn_startstop);
        btnStart.setOnClickListener(view -> {
            if (playing){
                musicSrv.pausePlayer();
            } else {
                musicSrv.playSong();
            }
            playing = !playing;
        });
    }
}
