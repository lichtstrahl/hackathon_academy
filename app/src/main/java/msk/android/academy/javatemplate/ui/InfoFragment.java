package msk.android.academy.javatemplate.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.network.FullInfo;
import msk.android.academy.javatemplate.network.dto.ArtistDTO;
import msk.android.academy.javatemplate.network.dto.InfoResponse;
import msk.android.academy.javatemplate.network.dto.MusicResponse;
import msk.android.academy.javatemplate.network.util.GlideApp;
import msk.android.academy.javatemplate.network.util.NetworkObserver;
import msk.android.academy.javatemplate.network.util.UrlAdapter;

public class InfoFragment extends Fragment {
    private static final String INTENT_ARTIST = "args:artist";
    private static final String INTENT_TRACK = "args:track";
    private static final String ruCountry = "RU";
    private String artist;
    private String track;
    private ProgressBar progressLoad;
    private TextView viewTrackText;
    private TextView viewStyle;
    private TextView viewGenre;
    private TextView viewBiography;
    private ImageView viewArtistArt;
    private TextView viewTrackName;
    private ImageButton buttonFacebook;
    private ImageButton buttonWebSite;
    private NetworkObserver<FullInfo> loadObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        progressLoad = view.findViewById(R.id.progressLoad);
        viewTrackText = view.findViewById(R.id.viewTrackText);
        viewStyle = view.findViewById(R.id.viewStyle);
        viewGenre = view.findViewById(R.id.viewGenre);
        viewBiography = view.findViewById(R.id.viewBiography);
        viewArtistArt = view.findViewById(R.id.viewImage);
        viewTrackName = view.findViewById(R.id.viewTrackName);
        buttonFacebook = view.findViewById(R.id.buttonFacebook);
        buttonWebSite = view.findViewById(R.id.buttonWebSite);


        loadObserver = new NetworkObserver<>(this::successfulLoad, this::errorNetwork);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            artist = bundle.getString(INTENT_ARTIST);
            track = bundle.getString(INTENT_TRACK);
            getActivity().setTitle(artist);
            viewTrackName.setText(track);

        }

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

    private void successfulLoad(FullInfo info) {
        InfoResponse infoResponse = info.getInfoResponse();
        MusicResponse musicResponse = info.getMusicResponse();

        if (musicResponse.getError() == null) {
            viewTrackText.setText(musicResponse.getLyrics());
        } else {
            viewTrackText.setText(musicResponse.getError());
        }

        ArtistDTO artist = infoResponse.getArtists().get(0);
        viewStyle.setText(artist.getStyle());
        viewGenre.setText(artist.getGenre());

        if (Locale.getDefault().getCountry().equals(ruCountry) && artist.getBiographyRu() != null) {
            viewBiography.setText(artist.getBiographyRu());
        } else {
            viewBiography.setText(artist.getBiographyEn());
        }

        GlideApp.with(this).load(artist.getArtUrl()).centerCrop().into(viewArtistArt);
        GlideApp.with(this).load(artist.getArtistLogoUrl()).centerCrop().into(buttonWebSite);

        if (artist.getFacebookUrl() != null) {
            buttonFacebook.setOnClickListener(btn -> {
                try {
                    String url = UrlAdapter.adapt(artist.getFacebookUrl());
                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(facebookIntent);
                } catch (Exception e){
                    App.logE(e.getMessage());
                }
            });
        }

        if (artist.getWebSiteUrl() != null) {
            buttonWebSite.setOnClickListener(btn -> {
                try {
                    String url = UrlAdapter.adapt(artist.getWebSiteUrl());
                    Intent webSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(webSiteIntent);
                } catch (Exception e) {
                    App.logE(e.getMessage());
                }
            });
        }

        progressLoad.setVisibility(View.GONE);
    }

    private void errorNetwork(Throwable t) {
        progressLoad.setVisibility(View.GONE);
        App.logE(t.getMessage());
    }
}
