package me.worric.bakingtime;

import android.app.Activity;
import android.app.Application;
import android.app.Service;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class App extends Application implements HasActivityInjector, HasServiceInjector {

    @Inject DispatchingAndroidInjector<Activity> activityDispatcher;

    @Inject DispatchingAndroidInjector<Service> serviceDispatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);

        Timber.plant(new Timber.DebugTree());
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatcher;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceDispatcher;
    }
}
