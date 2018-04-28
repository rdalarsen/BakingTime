package me.worric.bakingtime.ui.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import me.worric.bakingtime.di.ActivityScope;

@SuppressWarnings("unused")
@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(BakingViewModel.class)
    @ActivityScope
    abstract ViewModel bindViewModel(BakingViewModel bakingViewModel);

    @Binds
    @ActivityScope
    abstract ViewModelProvider.Factory bindFactory(ViewModelFactory viewModelFactory);

}
