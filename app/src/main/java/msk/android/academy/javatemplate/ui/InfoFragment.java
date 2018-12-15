package msk.android.academy.javatemplate.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.network.FullInfo;
import msk.android.academy.javatemplate.network.InfoResponse;
import msk.android.academy.javatemplate.network.MusicResponse;
import msk.android.academy.javatemplate.network.util.NetworkObserver;

public class InfoFragment extends Fragment {
    private static final String INTENT_ARTIST = "args:artist";
    private static final String INTENT_TRACK = "args:track";
    private String artist;
    private String track;
    private ProgressBar progressLoad;
    private TextView viewTrackText;
    private NetworkObserver<FullInfo> loadObserver;

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
        loadObserver = new NetworkObserver<>(this::successfulLoad, this::errorNetwork);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Observable singleTrack = App.getLyricAPI().getText(artist, track);
        Observable singleInfo = App.getInfoAPI().searchArtist(artist);

        Observable.combineLatest(
                singleTrack, singleInfo,
                (MusicResponse trackText, InfoResponse trackInfo) -> new FullInfo(trackText, trackInfo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadObserver);
    }

    @Override
    public void onStop() {
        super.onStop();
        loadObserver.unsubscribe();
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

    private void successfulLoad(FullInfo info) {
        InfoResponse infoResponse = info.getInfoResponse();
        MusicResponse musicResponse = info.getMusicResponse();

        if (musicResponse.getError() == null) {
            viewTrackText.setText(musicResponse.getLyrics());
        } else {
            viewTrackText.setText(musicResponse.getError());
        }

        progressLoad.setVisibility(View.GONE);
    }

    private void errorNetwork(Throwable t) {
        progressLoad.setVisibility(View.GONE);
        App.logE(t.getMessage());
    }
}
