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
import me.worric.bakingtime.data.models.StepDetails;
import me.worric.bakingtime.ui.common.BaseFragment;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

public class DetailFragment extends BaseFragment {

    private static final String EXTRA_PLAYER_POSITION = "me.worric.bakingtime.extra_player_state";
    private static final String EXTRA_STEP_DETAILS = "me.worric.bakingtime.extra_step_details";
    private static final String EXTRA_SHOULD_START_PLAYING = "me.worric.bakingtime.extra_should_start_playing";
    private static final long START_OF_VIDEO = 0L;

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

    private StepDetails mStepDetails;
    private boolean mNavButtonClicked = false;
    private long mPlayerPosition = START_OF_VIDEO;
    private boolean mShouldStartPlaying = true;

    // Lifecycle callbacks

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate: called");

        if (savedInstanceState != null) {
            Timber.d("restoring instance state");
            mStepDetails = savedInstanceState.getParcelable(EXTRA_STEP_DETAILS);
            mPlayerPosition = savedInstanceState.getLong(EXTRA_PLAYER_POSITION, START_OF_VIDEO);
            mShouldStartPlaying = savedInstanceState.getBoolean(EXTRA_SHOULD_START_PLAYING, true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("OnViewCreated: called");

        restoreNavButtonsState(mStepDetails);

        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getStepDetails().observe(this, stepDetails -> {
            Timber.d("Observe method called");
            if (stepDetails == null) return;

            boolean isFirstRun = mStepDetails == null;

            mStepDetails = stepDetails;

            restoreNavButtonsState(mStepDetails);

            if (mExoPlayer == null) {
                Timber.e("ExoPlayer is null - let initializePlayer() load media");
                return;
            }

            if (mIsTabletMode) {
                boolean clickEventIsNull = mViewModel.getStepButtonClicked().getValue() == null;
                boolean stepButtonClicked = clickEventIsNull ?
                        false : mViewModel.getStepButtonClicked().getValue();
                Timber.d("First run: %s - stepButtonClicked: %s",
                        isFirstRun, stepButtonClicked);
                if (isFirstRun || stepButtonClicked) {
                    Timber.i("Callback: Loading media...");
                    loadMediaForPlayer(mStepDetails.stepUrl, START_OF_VIDEO);
                    mViewModel.setStepButtonClicked(false);
                } else {
                    Timber.e("No conditions match - initializePlayer loads media");
                }
            } else {
                Timber.d("First run: %s - navButtonClicked: %s",
                        isFirstRun, mNavButtonClicked);
                if (isFirstRun || mNavButtonClicked) {
                    Timber.i("Callback: Loading media...");
                    loadMediaForPlayer(mStepDetails.stepUrl, START_OF_VIDEO);
                    mNavButtonClicked = false;
                } else {
                    Timber.e("No conditions match - initializePlayer loads media");
                }
            }
        });
    }

    private void restoreNavButtonsState(StepDetails stepDetails) {
        if (mStepDetails == null) return;

        if (mInstructions != null) mInstructions.setText(mStepDetails.stepInstructions);
        if (mPreviousButton != null && mNextButton != null) {
            if (mStepDetails.stepIndex == 0) {
                mPreviousButton.setEnabled(false);
            } else if (mStepDetails.stepIndex == stepDetails.numSteps - 1) {
                mNextButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart: called. Initializing Player");
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume: called.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause: called. Saving position");
        savePlayerState();
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("onStop: called. Releasing player");
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroyView: called");
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
        outState.putLong(EXTRA_PLAYER_POSITION, mPlayerPosition);
        outState.putParcelable(EXTRA_STEP_DETAILS, mStepDetails);
        outState.putBoolean(EXTRA_SHOULD_START_PLAYING, mShouldStartPlaying);
    }

    // Helper/onClick methods

    @Optional
    @OnClick(R.id.btn_detail_next)
    protected void handleNextButtonClick(View v) {
        if (!mPreviousButton.isEnabled()) mPreviousButton.setEnabled(true);
        mNavButtonClicked = true;
        mViewModel.goToStep(BakingViewModel.NEXT);
    }

    @Optional
    @OnClick(R.id.btn_detail_previous)
    protected void handlePreviousButtonClick(View v) {
        if (!mNextButton.isEnabled()) mNextButton.setEnabled(true);
        mNavButtonClicked = true;
        mViewModel.goToStep(BakingViewModel.PREVIOUS);
    }

    private void savePlayerState() {
        mPlayerPosition = mExoPlayer.getCurrentPosition();
        mShouldStartPlaying = mExoPlayer.getPlayWhenReady();
    }

    private void loadMediaForPlayer(String videoUrl, long playerPosition) {
        Timber.d("Video URL is: %s", videoUrl);
        Uri videoUri = TextUtils.isEmpty(videoUrl) ? null : Uri.parse(videoUrl);
        MediaSource mediaSource = mMediaSourceFactory.createMediaSource(videoUri, null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(mShouldStartPlaying);
        if (playerPosition != START_OF_VIDEO) mExoPlayer.seekTo(playerPosition);
    }

    private void initializePlayer() {
        if (mExoPlayer == null) {
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(),
                    new DefaultTrackSelector());
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(new PlayerEventListener());
            if (mStepDetails == null) {
                Timber.e("StepDetails is null - let the callback load media");
                return;
            }
            Timber.i("InitializePlayer: Loading media...");
            loadMediaForPlayer(mStepDetails.stepUrl, mPlayerPosition);
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

    // Nested interfaces/classes

    private class PlayerEventListener extends Player.DefaultEventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE:
                    Timber.d("Player is IDLE: %d", Player.STATE_IDLE);
                    break;
                case Player.STATE_BUFFERING:
                    Timber.d("Player is BUFFERING: %d", Player.STATE_BUFFERING);
                    break;
                case Player.STATE_READY:
                    Timber.d("Player is READY: %d", Player.STATE_READY);
                    break;
                case Player.STATE_ENDED:
                    Timber.d("Player is ENDED: %d", Player.STATE_ENDED);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown state: " + playbackState);
            }
        }
    }
}
