package me.worric.bakingtime.ui.detail;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.worric.bakingtime.R;
import me.worric.bakingtime.ui.GlideApp;
import me.worric.bakingtime.ui.viewmodels.BakingViewModel;
import timber.log.Timber;

public class DetailFragment extends Fragment {

    @BindView(R.id.tv_detail_step_instructions)
    protected TextView mInstructions;
    @BindView(R.id.detail_exoplayer)
    protected ImageView mExoplayer;
    @Inject
    protected ViewModelProvider.Factory mFactory;
    private BakingViewModel mViewModel;
    private Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity(), mFactory).get(BakingViewModel.class);
        mViewModel.getChosenStep().observe(this, step -> {
            mInstructions.setText(step.getDescription());
            GlideApp.with(getContext())
                    .load(R.drawable.ic_launcher_foreground)
                    .fitCenter()
                    .into(mExoplayer);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public DetailFragment() {
        // Required empty public constructor
    }

}
