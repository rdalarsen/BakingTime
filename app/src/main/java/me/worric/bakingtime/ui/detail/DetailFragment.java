package me.worric.bakingtime.ui.detail;


import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.common.BaseFragment;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

import static me.worric.bakingtime.ui.util.UiUtils.EXTRA_PLAYER_STATE;

public class DetailFragment extends BaseFragment {

    public static final String EXTRA_IS_RESTORING = "me.worric.bakingtime.is_restoring";

    @Inject
    protected ExtractorMediaSource.Factory mMediaSourceFactory;
    @Nullable @BindView(R.id.tv_detail_step_instructions)
    protected TextView mInstructions;
    @Nullable @BindView(R.id.btn_detail_previous)
    protected Button mPreviousButton;
    @Nullable @BindView(R.id.btn_detail_next)
    protected Button mNextButton;
    @BindView(R.id.detail_exoplayer)
    protected PlayerView mPlayerView;

    private BakingViewModel mViewModel;
    private SimpleExoPlayer mExoPlayer;
    private boolean mIsRestoring = false;

    // Lifecycle callbacks

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("OnViewCreated: called");

        setIsRestoring(savedInstanceState);

        initializePlayer();

        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getChosenStepWithSteps().observe(this, stepWithSteps -> {
            if (stepWithSteps == null) return;
            Timber.e("currentStep ID: %d, and currentRecipeSteps toString: %s",
                    stepWithSteps.currentStep.getId(), stepWithSteps.currentRecipeSteps.toString());

            // Every configuration except phone landscape
            if (!isPhoneLandscape()) {
                mInstructions.setText(stepWithSteps.currentStep.getDescription());

                if (!mIsTabletMode) {
                    int index = stepWithSteps.getIndexOfCurrentStep();
                    if (index == 0) {
                        mPreviousButton.setEnabled(false);
                    } else if (index == stepWithSteps.currentRecipeSteps.size() - 1) {
                        mNextButton.setEnabled(false);
                    }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroyView: called");
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy: called");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("onSaveInstanceState: called");
        outState.putLong(EXTRA_PLAYER_STATE, mExoPlayer.getCurrentPosition());
        outState.putBoolean(EXTRA_IS_RESTORING, true);
    }

    // Helper/onClick methods

    @Optional
    @OnClick(R.id.btn_detail_next)
    protected void handleNextButtonClick(View v) {
        if (!mPreviousButton.isEnabled()) mPreviousButton.setEnabled(true);
        mViewModel.goToNextStep();
    }

    @Optional
    @OnClick(R.id.btn_detail_previous)
    protected void handlePreviousButtonClick(View v) {
        if (!mNextButton.isEnabled()) mNextButton.setEnabled(true);
        mViewModel.goToPreviousStep();
    }

    private boolean isPhoneLandscape() {
        return mIsLandscapeMode && !mIsTabletMode;
    }

    private void setIsRestoring(@Nullable Bundle savedInstanceState) {
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
            mExoPlayer.addListener(new PlayerEventListener());
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    // Base class methods

    @Override
    protected int getLayout() {
        return R.layout.fragment_detail;
    }

    // Constructors

    public DetailFragment() {
        // Required empty public constructor
    }

    // Interfaces/classes

    private class PlayerEventListener extends Player.DefaultEventListener {
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
    }
}
