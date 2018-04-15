package me.worric.bakingtime.ui.detail;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

import static me.worric.bakingtime.ui.util.UiUtils.EXTRA_PLAYER_STATE;

public class DetailFragment extends Fragment implements Player.EventListener {

    public static final String EXTRA_IS_RESTORING = "me.worric.bakingtime.is_restoring";

    @Nullable @BindView(R.id.tv_detail_step_instructions)
    protected TextView mInstructions;
    @BindView(R.id.detail_exoplayer)
    protected PlayerView mPlayerView;
    @Inject
    protected ViewModelProvider.Factory mFactory;
    @Inject
    protected ExtractorMediaSource.Factory mMediaSourceFactory;
    private BakingViewModel mViewModel;
    private Unbinder mUnbinder;
    private SimpleExoPlayer mExoPlayer;
    private boolean mIsRestoring = false;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

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
        mViewModel.goToNextStep();
    }

    @Optional
    @OnClick(R.id.btn_detail_previous)
    protected void handlePreviousButtonClick(View v) {
        mViewModel.goToPreviousStep();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkIfRestoring(savedInstanceState);
        initializePlayer();
        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getChosenStepWithSteps().observe(this, stepWithSteps -> {
            Timber.e("currentStep ID: %d, and currentRecipeSteps toString: %s",
                    stepWithSteps.currentStep.getId(), stepWithSteps.currentRecipeSteps.toString());

            // Every configuration except phone landscape
            if (!isPhoneLandscape()) {
                mInstructions.setText(stepWithSteps.currentStep.getDescription());

                int index = stepWithSteps.getIndexOfCurrentStep();
                if (index == 0) {
                    Timber.d("Disable PREVIOUS button");
                } else if (index == stepWithSteps.currentRecipeSteps.size() - 1) {
                    Timber.d("Disable NEXT button");
                }
            }

            String videoUrl = stepWithSteps.currentStep.getVideoURL();

            long playerPosition = 0L;
            if (mIsRestoring && savedInstanceState != null) {
                playerPosition = savedInstanceState.getLong(EXTRA_PLAYER_STATE, 0L);
                mIsRestoring = false;
            }
            loadMediaForPlayer(videoUrl, playerPosition);
        });
    }

    private boolean isPhoneLandscape() {
        final boolean isLandscapeMode = getContext().getResources().getBoolean(R.bool.landscape_mode);
        final boolean isTabletMode = getContext().getResources().getBoolean(R.bool.tablet_mode);

        Timber.d("Landscape mode: %s, tablet mode is: %s", isLandscapeMode, isTabletMode);

        return isLandscapeMode && !isTabletMode;
    }

    private void checkIfRestoring(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mIsRestoring = savedInstanceState.getBoolean(EXTRA_IS_RESTORING);
        }
    }

    private void loadMediaForPlayer(String videoUrl, long playerPosition) {
        Uri videoUri = TextUtils.isEmpty(videoUrl) ? null : Uri.parse(videoUrl);
        MediaSource mediaSource = mMediaSourceFactory.createMediaSource(videoUri, null, null);
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
        outState.putBoolean(EXTRA_IS_RESTORING, true);
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
                Timber.d("Player is IDLE: %d", Player.STATE_IDLE);
                break;
            case Player.STATE_READY:
                Timber.d("Player is READY: %d", Player.STATE_READY);
                break;
            case Player.STATE_ENDED:
                Timber.d("Player is ENDED: %d", Player.STATE_ENDED);
                break;
            case Player.STATE_BUFFERING:
                Timber.d("Player is BUFFERING: %d", Player.STATE_BUFFERING);
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
