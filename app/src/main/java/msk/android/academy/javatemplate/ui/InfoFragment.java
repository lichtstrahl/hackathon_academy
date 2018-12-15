package msk.android.academy.javatemplate.ui;

import android.app.AlertDialog;
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
    private static final String SAVE_ARTIST = "save:artist";
    private static final String SAVE_TEXT = "save:text";
    private static final String RU_COUNTRY = "RU";
    private String artist;
    private String track;
    private TextView viewTrackText;
    private TextView viewStyle;
    private TextView viewGenre;
    private TextView viewBiography;
    private ImageView viewArtistArt;
    private TextView viewTrackName;
    private ImageButton buttonFacebook;
    private ImageButton buttonWebSite;
    private NetworkObserver<FullInfo> loadObserver;
    private AlertDialog loadDialog;
    private ArtistDTO artistDTO;
    private String textTrack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        viewTrackText = view.findViewById(R.id.viewTrackText);
        viewStyle = view.findViewById(R.id.viewStyle);
        viewGenre = view.findViewById(R.id.viewGenre);
        viewBiography = view.findViewById(R.id.viewBiography);
        viewArtistArt = view.findViewById(R.id.viewImage);
        viewTrackName = view.findViewById(R.id.viewTrackName);
        buttonFacebook = view.findViewById(R.id.buttonFacebook);
        buttonWebSite = view.findViewById(R.id.buttonWebSite);
        loadObserver = new NetworkObserver<>(this::successfulLoad, this::errorNetwork);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext())
                .setView(R.layout.layout_dialog_loading)
                .setCancelable(false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            artist = bundle.getString(INTENT_ARTIST);
            track = bundle.getString(INTENT_TRACK);
            getActivity().setTitle(artist);
            viewTrackName.setText(track);
        }

        if (savedInstanceState == null) {
            loadDialog = builder.create();
            loadDialog.show();

            Observable singleTrack = App.getLyricAPI().getText(artist, track);
            Observable singleInfo = App.getInfoAPI().searchArtist(artist);

            Observable.combineLatest(
                    singleTrack, singleInfo,
                    (MusicResponse trackText, InfoResponse trackInfo) -> new FullInfo(trackText, trackInfo))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(loadObserver);
        } else {
            artistDTO = (ArtistDTO)savedInstanceState.getSerializable(SAVE_ARTIST);
            textTrack = savedInstanceState.getString(SAVE_TEXT);
            viewTrackText.setText(textTrack);
            bindArtist(artistDTO);
        }



//        App.logI("Fragment created. Bundle: " + savedInstanceState);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_ARTIST, artistDTO);
        outState.putString(SAVE_TEXT, textTrack);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
            textTrack = musicResponse.getLyrics();
            viewTrackText.setText(textTrack);
        } else {
            textTrack = "";
            viewTrackText.setText(musicResponse.getError());
        }

        artistDTO = infoResponse.getArtists().get(0);
        bindArtist(artistDTO);
        loadDialog.dismiss();
    }

    private void errorNetwork(Throwable t) {
        loadDialog.dismiss();
        App.logE(t.getMessage());
    }

    private void bindArtist(@Nullable ArtistDTO artist) {
        if (artist == null)
            return;
        viewStyle.setText(artist.getStyle());
        viewGenre.setText(artist.getGenre());

        if (Locale.getDefault().getCountry().equals(RU_COUNTRY) && artist.getBiographyRu() != null) {
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
    }
}
