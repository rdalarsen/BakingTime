package me.worric.bakingtime.ui.common;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import me.worric.bakingtime.R;

@SuppressWarnings("WeakerAccess")
public abstract class BaseFragment extends Fragment {

    @Inject protected ViewModelProvider.Factory mFactory;

    private Unbinder mUnbinder;
    protected boolean mLandscapeMode;
    protected boolean mTabletMode;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        mLandscapeMode = context.getResources().getBoolean(R.bool.landscape_mode);
        mTabletMode = context.getResources().getBoolean(R.bool.tablet_mode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    protected abstract @LayoutRes int getLayout();

    protected boolean isPhoneLandscapeMode() {
        return !mTabletMode && mLandscapeMode;
    }

}
