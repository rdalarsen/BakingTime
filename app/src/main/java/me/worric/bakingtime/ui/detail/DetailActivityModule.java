package me.worric.bakingtime.ui.detail;

import android.content.Context;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import me.worric.bakingtime.di.ActivityContext;
import me.worric.bakingtime.di.ActivityScope;
import me.worric.bakingtime.ui.viewmodels.ViewModelModule;

@Module(includes = ViewModelModule.class)
public abstract class DetailActivityModule {

    @Binds
    @ActivityScope
    @ActivityContext
    abstract Context bindsContext(DetailActivity detailActivity);

    @ContributesAndroidInjector
    abstract MasterFragment contributeMasterFragment();

    @ContributesAndroidInjector
    abstract DetailFragment contributeDetailFragment();

}
