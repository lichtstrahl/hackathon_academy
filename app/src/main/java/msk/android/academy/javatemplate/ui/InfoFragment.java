package msk.android.academy.javatemplate.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.network.EmptyDTO;
import msk.android.academy.javatemplate.network.util.NetworkObserver;

public class InfoFragment extends Fragment {
    private static final String INTENT_ARTIST = "args:artist";
    private static final String INTENT_TRACK = "args:track";
    private Button buttonNetwork;
    private NetworkObserver<EmptyDTO> lyricObserver;
    private String artist;
    private String track;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        buttonNetwork = view.findViewById(R.id.buttonNetwork);

        Bundle bundle = getArguments();
        if (bundle != null) {
            artist = bundle.getString(INTENT_ARTIST);
            track = bundle.getString(INTENT_TRACK);
        }

        lyricObserver = new NetworkObserver<>(this::successfulLyric, this::errorNetwork);

        buttonNetwork.setOnClickListener((b) -> {
            App.getLyricAPI().getText(artist, track)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lyricObserver);
        });
        return view;
    }

    public static InfoFragment getInstance(String artist, String track) {
        InfoFragment iFragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_ARTIST, artist);
        bundle.putString(INTENT_TRACK, track);
        iFragment.setArguments(bundle);
        return iFragment;
    }

    private void successfulLyric(EmptyDTO dto) {
        App.logI(dto.getClass().getName());
    }

    private void errorNetwork(Throwable t) {
        App.logE(t.getMessage());
    }
}
