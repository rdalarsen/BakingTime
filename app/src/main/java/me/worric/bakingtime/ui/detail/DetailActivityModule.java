package me.worric.bakingtime.ui.detail;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import me.worric.bakingtime.R;
import me.worric.bakingtime.di.ActivityContext;
import me.worric.bakingtime.di.ActivityScope;
import me.worric.bakingtime.ui.viewmodels.ViewModelModule;

@Module(includes = ViewModelModule.class)
public abstract class DetailActivityModule {

    @Binds
    @ActivityScope
    @ActivityContext
    abstract Context bindsContext(DetailActivity detailActivity);

    @Provides
    @ActivityScope
    static DataSource.Factory provideDataSourceFactory(@ActivityContext Context context) {
        return new DefaultDataSourceFactory(context, Util.getUserAgent(context,
                context.getString(R.string.app_name_user_agent)));
    }

    @Provides
    @ActivityScope
    static ExtractorMediaSource.Factory provideMediaSource(DataSource.Factory dataSourceFactory) {
        return new ExtractorMediaSource.Factory(dataSourceFactory);
    }

    @Provides
    @ActivityScope
    static FragmentManager provideFragmentManager(DetailActivity detailActivity) {
        return detailActivity.getSupportFragmentManager();
    }

    @ContributesAndroidInjector
    abstract MasterFragment contributeMasterFragment();

    @ContributesAndroidInjector
    abstract DetailFragment contributeDetailFragment();

}
