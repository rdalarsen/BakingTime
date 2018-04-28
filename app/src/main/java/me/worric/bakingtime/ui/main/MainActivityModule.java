package me.worric.bakingtime.ui.main;

import android.content.Context;

import dagger.Binds;
import dagger.Module;
import me.worric.bakingtime.di.ActivityContext;
import me.worric.bakingtime.di.ActivityScope;
import me.worric.bakingtime.ui.viewmodels.ViewModelModule;

@SuppressWarnings("unused")
@Module(includes = ViewModelModule.class)
public abstract class MainActivityModule {

    @Binds
    @ActivityScope
    @ActivityContext
    abstract Context bindContext(MainActivity mainActivity);

}
