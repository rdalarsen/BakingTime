package me.worric.bakingtime.ui.detail;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

public class DetailFragment extends Fragment implements Player.EventListener {

    @BindView(R.id.tv_detail_step_instructions)
    protected TextView mInstructions;
    @BindView(R.id.detail_exoplayer)
    protected PlayerView mPlayerView;
    @Inject
    protected ViewModelProvider.Factory mFactory;
    private BakingViewModel mViewModel;
    private Unbinder mUnbinder;
    private SimpleExoPlayer mExoPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.btn_detail_next_previous)
    protected void handleNextButtonClick(View v) {
        mViewModel.getNext();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated: called...");
        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getChosenStep().observe(this, step -> {
            Timber.d("Step received. Id: %d", step.getId());
            String videoUrl = step.getVideoURL();
            initializePlayer(TextUtils.isEmpty(videoUrl) ? null : Uri.parse(videoUrl));
        });
    }

    private void initializePlayer(Uri videoUri) {
        if (mExoPlayer != null) return;
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());
        mPlayerView.setPlayer(mExoPlayer);
        String userAgent = Util.getUserAgent(getContext(), "Baking-Time");
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(getContext(), userAgent))
                .createMediaSource(videoUri, null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.addListener(this);
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroyView() {
        releasePlayer();
        mUnbinder.unbind();
        super.onDestroyView();
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_IDLE:
                Timber.d("Player is: %d", Player.STATE_IDLE);
                break;
            case Player.STATE_READY:
                Timber.d("Player is: %d", Player.STATE_READY);
                break;
            case Player.STATE_ENDED:
                Timber.d("Player is: %d", Player.STATE_ENDED);
                break;
            case Player.STATE_BUFFERING:
                Timber.d("Player is: %d", Player.STATE_BUFFERING);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + playbackState);
        }
    }

    // -------------- unused Player.EventListener callback methods -----------------------

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
