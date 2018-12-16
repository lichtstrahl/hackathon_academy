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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.db.InfoEntity;
import msk.android.academy.javatemplate.network.dto.ArtistDTO;
import msk.android.academy.javatemplate.network.dto.InfoResponse;
import msk.android.academy.javatemplate.network.dto.LyricResponse;
import msk.android.academy.javatemplate.network.util.GlideApp;
import msk.android.academy.javatemplate.network.util.NetworkObserver;
import msk.android.academy.javatemplate.network.util.UrlAdapter;
import retrofit2.HttpException;

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
    private Button buttonReconnect;
    private ProgressBar progressLoadInfo;
    private ProgressBar progressLoadText;
    private NetworkObserver<LyricResponse> loadLyricObserver;
    private NetworkObserver<InfoResponse> loadInfoObserver;
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
        buttonReconnect = view.findViewById(R.id.buttonReconnect);
        progressLoadInfo = view.findViewById(R.id.progressInfo);
        progressLoadText = view.findViewById(R.id.progressText);
        loadLyricObserver = new NetworkObserver<>(this::successfulLoadText, this::errorLoadText);
        loadInfoObserver = new NetworkObserver<>(this::successfulLoadInfo, this::errorLoadInfo);
        buttonReconnect.setOnClickListener(this::clickReconnect);

        Bundle bundle = getArguments();
        if (bundle != null) {
            artist = bundle.getString(INTENT_ARTIST);
            track = bundle.getString(INTENT_TRACK);
            getActivity().setTitle(artist);
            viewTrackName.setText(track);
        }

        // Если поворот экрана
        if (savedInstanceState != null) {
            artistDTO = (ArtistDTO)savedInstanceState.getSerializable(SAVE_ARTIST);
            textTrack = savedInstanceState.getString(SAVE_TEXT);
            viewTrackText.setText(textTrack);
            bindArtist(artistDTO);
            progressLoadInfo.setVisibility(View.GONE);
            progressLoadText.setVisibility(View.GONE);
        }

        // Даже если это поворот экрана, возможно загрузка не была завершена
        if (savedInstanceState == null || artistDTO == null) {
            startLoad();
        }

        return view;
    }


    private void clickReconnect(View v) {
        v.setVisibility(View.GONE);
        startLoad();
    }

    private void startLoad() {
        Observable oLyric = App.getLyricAPI().getText(artist, track);
        Observable oInfo = App.getInfoAPI().searchArtist(artist);

        oLyric
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadLyricObserver);

        oInfo
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadInfoObserver);

//        Observable.combineLatest(
//                oLyric, oInfo,
//                (res1, res2) -> {
//                    return "";
//                }
//        )
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(mainLoadObserver);

        progressLoadText.setVisibility(View.VISIBLE);
        progressLoadInfo.setVisibility(View.VISIBLE);
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
        loadInfoObserver.unsubscribe();
        loadLyricObserver.unsubscribe();
//        mainLoadObserver.unsubscribe();
    }

    public static InfoFragment getInstance(String artist, String track) {
        InfoFragment iFragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_ARTIST, artist);
        bundle.putString(INTENT_TRACK, track);
        iFragment.setArguments(bundle);
        return iFragment;
    }

    private void successfulLoadInfo(InfoResponse res) {
        if (res.getArtists() == null) {
            viewStyle.setText(R.string.notFoundInfoForArtist);
        } else {
            artistDTO = res.getArtists().get(0);
            bindArtist(artistDTO);
            // Есть запись в БД для этого артиста и трека
            InfoEntity bdEntity = App.getDB().getEntityDao().searchInfiEntity(artist, track);
            if (bdEntity == null) {
                InfoEntity entity = new InfoEntity(artist, track, artistDTO);
                App.getDB().getEntityDao().insert(entity);
            } else {
                bdEntity.fromArtistDTO(artistDTO);
                App.getDB().getEntityDao().update(bdEntity);
            }
        }
        progressLoadInfo.setVisibility(View.GONE);
    }

    private void successfulLoadText(LyricResponse res) {
        if (res.getError() == null) {
            textTrack = res.getLyrics();
            viewTrackText.setText(textTrack);

            // Есть запись в БД для этого артиста и трека
            InfoEntity bdEntity = App.getDB().getEntityDao().searchInfiEntity(artist, track);
            if (bdEntity == null) {
                InfoEntity entity = new InfoEntity(artist, track, textTrack);
                App.getDB().getEntityDao().insert(entity);
            } else {
                bdEntity.fromLyric(textTrack);
                App.getDB().getEntityDao().update(bdEntity);
            }
        } else {
            textTrack = "";
            viewTrackText.setText(R.string.notFoundTextForTrack);
        }
        progressLoadText.setVisibility(View.GONE);
    }

    // HttpException, UnkownHostException
    private void errorLoadInfo(Throwable t) {
        // Нет интернета
        if (t instanceof UnknownHostException) {
            buttonReconnect.setVisibility(View.VISIBLE);
        }

        // Ошибка при запросе
        if (t instanceof HttpException) {
            viewStyle.setText(R.string.notFoundInfoForArtist);
        }


        progressLoadInfo.setVisibility(View.GONE);
        App.logE(t.getMessage());
    }

    private void errorLoadText(Throwable t) {
        // Нет интернета
        if (t instanceof UnknownHostException) {
            buttonReconnect.setVisibility(View.VISIBLE);
        }

        // Ошибка при запросе
        if (t instanceof HttpException) {
            viewTrackText.setText(R.string.notFoundTextForTrack);
        }

        progressLoadText.setVisibility(View.GONE);
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
