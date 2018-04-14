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
import butterknife.Optional;
import butterknife.Unbinder;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

import static me.worric.bakingtime.ui.util.UiUtils.EXTRA_PLAYER_STATE;

public class DetailFragment extends Fragment implements Player.EventListener {

    @Nullable @BindView(R.id.tv_detail_step_instructions)
    protected TextView mInstructions;
    @Nullable @BindView(R.id.detail_exoplayer)
    protected PlayerView mPlayerView;
    @Inject
    protected ViewModelProvider.Factory mFactory;
    private BakingViewModel mViewModel;
    private Unbinder mUnbinder;
    private SimpleExoPlayer mExoPlayer;
    private boolean mVideoChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Optional
    @OnClick(R.id.btn_detail_next)
    protected void handleNextButtonClick(View v) {
        mVideoChanged = true;
        mViewModel.goToNextStep();
    }

    @Optional
    @OnClick(R.id.btn_detail_previous)
    protected void handlePreviousButtonClick(View v) {
        mVideoChanged = true;
        mViewModel.goToPreviousStep();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Timber.e("onCreateView: Called!");
        super.onViewCreated(view, savedInstanceState);
        initializePlayer();
        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getMoreSteps().observe(this, stepAndSteps -> {
            Timber.e("step ID: %d, and steps toString: %s",
                    stepAndSteps.currentStep.getId(), stepAndSteps.steps.toString());

            boolean isLandscapeMode = getContext().getResources().getBoolean(R.bool.landscape_mode);
            Timber.d("Landscape mode: %s", isLandscapeMode);

            if (!isLandscapeMode) {
                mInstructions.setText(stepAndSteps.currentStep.getDescription());

                int index = stepAndSteps.steps.indexOf(stepAndSteps.currentStep);
                if (index == 0) {
                    Timber.d("Disable PREVIOUS button");
                } else if (index == stepAndSteps.steps.size() - 1) {
                    Timber.d("Disable NEXT button");
                }
            }

            String videoUrl = stepAndSteps.currentStep.getVideoURL();
            long playerPosition = 0L;
            if (savedInstanceState != null && !mVideoChanged) playerPosition = savedInstanceState
                    .getLong(EXTRA_PLAYER_STATE, 0L);
            loadMediaForPlayer(videoUrl, playerPosition);
        });
    }

    private void loadMediaForPlayer(String videoUrl, long playerPosition) {
        Uri videoUri = TextUtils.isEmpty(videoUrl) ? null : Uri.parse(videoUrl);
        String userAgent = Util.getUserAgent(getContext(), "Baking-Time");
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(getContext(), userAgent))
                .createMediaSource(videoUri, null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
        if (playerPosition != 0L) mExoPlayer.seekTo(playerPosition);
    }

    private void initializePlayer() {
        if (mExoPlayer == null) {
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(),
                    new DefaultTrackSelector());
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_PLAYER_STATE, mExoPlayer.getCurrentPosition());
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        releasePlayer();
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
