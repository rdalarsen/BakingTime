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

    private boolean mIsFirstRun = true;
    private boolean mChangePressed = false;
    private long mCurrentPlayerPosition = 0L;
    private String mCurrentUrl;
    private String mCurrentInstructions;
    private int mCurrentStepIndex;

    // Lifecycle callbacks

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("OnViewCreated: called");

        if (savedInstanceState != null) {
            mCurrentPlayerPosition = savedInstanceState.getLong(EXTRA_PLAYER_STATE, 0L);
            mCurrentUrl = savedInstanceState.getString("key_current_url", "");
            mCurrentInstructions = savedInstanceState.getString("key_current_instructions", "");
            mCurrentStepIndex = savedInstanceState.getInt("key_current_step_index", -1);
            mIsFirstRun = savedInstanceState.getBoolean("key_is_first_run", true);
        }

        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getChosenStepWithSteps().observe(this, stepWithSteps -> {
            Timber.d("Observe method called");
            if (stepWithSteps == null) return;

            // TODO: Create data model for this class
            mCurrentUrl = stepWithSteps.currentStep.getVideoURL();
            mCurrentStepIndex = stepWithSteps.getIndexOfCurrentStep();
            mCurrentInstructions = stepWithSteps.currentStep.getDescription();

            if (mInstructions != null) mInstructions.setText(mCurrentInstructions);
            if (mPreviousButton != null && mNextButton != null) {
                int index = stepWithSteps.getIndexOfCurrentStep();
                if (index == 0) {
                    mPreviousButton.setEnabled(false);
                } else if (index == stepWithSteps.currentRecipeSteps.size() - 1) {
                    mNextButton.setEnabled(false);
                }
            }

            if (mExoPlayer == null) {
                Timber.e("ExoPlayer is null; returning");
                return;
            }

            if (mIsTabletMode) {
                boolean isNull = mViewModel.getIsClicked().getValue() == null;
                boolean isClicked = isNull ? false : mViewModel.getIsClicked().getValue();
                Timber.d("First run: %s - isClicked: %s", mIsFirstRun, isClicked);
                if (mIsFirstRun || isClicked) {
                    loadMediaForPlayer(mCurrentUrl, 0L);
                    mViewModel.setIsClicked(false);
                    mIsFirstRun = false;
                }
            } else {
                if (mIsFirstRun || mChangePressed) {
                    loadMediaForPlayer(mCurrentUrl, 0L);
                    mChangePressed = false;
                    mIsFirstRun = false;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart: called.");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume: called. Initializing Player");
        initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause: called. Saving position and releasing player");
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("onStop: called.");
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
        outState.putLong(EXTRA_PLAYER_STATE, mCurrentPlayerPosition);
        outState.putBoolean(EXTRA_IS_RESTORING, true);

        outState.putString("key_current_url", mCurrentUrl);
        outState.putString("key_current_instructions", mCurrentInstructions);
        outState.putInt("key_current_step_index", mCurrentStepIndex);
        outState.putBoolean("key_is_first_run", mIsFirstRun);
    }

    // Helper/onClick methods

    @Optional
    @OnClick(R.id.btn_detail_next)
    protected void handleNextButtonClick(View v) {
        if (!mPreviousButton.isEnabled()) mPreviousButton.setEnabled(true);
        mChangePressed = true;
        mViewModel.goToNextStep();
    }

    @Optional
    @OnClick(R.id.btn_detail_previous)
    protected void handlePreviousButtonClick(View v) {
        if (!mNextButton.isEnabled()) mNextButton.setEnabled(true);
        mChangePressed = true;
        mViewModel.goToPreviousStep();
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
            if (mCurrentUrl == null) {
                Timber.e("URL is NULL. NOT loading player media");
                return;
            }
            loadMediaForPlayer(mCurrentUrl, mCurrentPlayerPosition);
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mCurrentPlayerPosition = mExoPlayer.getCurrentPosition();
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
