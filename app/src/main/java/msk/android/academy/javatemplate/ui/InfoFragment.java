package msk.android.academy.javatemplate.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.network.MusicResponse;
import msk.android.academy.javatemplate.network.util.NetworkObserver;

public class InfoFragment extends Fragment {
    private static final String INTENT_ARTIST = "args:artist";
    private static final String INTENT_TRACK = "args:track";
    private NetworkObserver<MusicResponse> lyricObserver;
    private String artist;
    private String track;
    private ProgressBar progressLoad;
    private TextView viewTrackText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            artist = bundle.getString(INTENT_ARTIST);
            track = bundle.getString(INTENT_TRACK);
        }

        progressLoad = view.findViewById(R.id.progressLoad);
        viewTrackText = view.findViewById(R.id.viewTrackText);

        lyricObserver = new NetworkObserver<>(this::successfulLyric, this::errorNetwork);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        App.getLyricAPI().getText(artist, track)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(lyricObserver);
    }

    @Override
    public void onStop() {
        super.onStop();
        lyricObserver.unsubscribe();
    }

    public static InfoFragment getInstance(String artist, String track) {
        InfoFragment iFragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_ARTIST, artist);
        bundle.putString(INTENT_TRACK, track);
        iFragment.setArguments(bundle);
        return iFragment;
    }

    private void successfulLyric(MusicResponse res) {
        if (res.getError() == null) {
            viewTrackText.setText(res.getLyrics());
        } else {
            viewTrackText.setText(res.getError());
        }
        progressLoad.setVisibility(View.GONE);
    }

    private void errorNetwork(Throwable t) {
        progressLoad.setVisibility(View.GONE);
        App.logE(t.getMessage());
    }
}
