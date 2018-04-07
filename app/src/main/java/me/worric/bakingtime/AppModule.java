package me.worric.bakingtime;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import me.worric.bakingtime.di.ActivityScope;
import me.worric.bakingtime.di.AppContext;
import me.worric.bakingtime.ui.main.MainActivity;
import me.worric.bakingtime.ui.main.MainActivityModule;

@Module(includes = AndroidSupportInjectionModule.class)
public abstract class AppModule {

    @Binds
    @Singleton
    @AppContext
    abstract Context bindContext(Application application);

    @ActivityScope
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity contributesMainActivity();

}
