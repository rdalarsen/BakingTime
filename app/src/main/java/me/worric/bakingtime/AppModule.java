package me.worric.bakingtime;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import me.worric.bakingtime.data.network.BakingWebService;
import me.worric.bakingtime.data.repository.RepositoryModule;
import me.worric.bakingtime.di.ActivityScope;
import me.worric.bakingtime.di.AppContext;
import me.worric.bakingtime.ui.detail.DetailActivity;
import me.worric.bakingtime.ui.detail.DetailActivityModule;
import me.worric.bakingtime.ui.main.MainActivity;
import me.worric.bakingtime.ui.main.MainActivityModule;
import me.worric.bakingtime.ui.util.NetUtils;
import me.worric.bakingtime.ui.viewmodels.ViewModelModule;
import me.worric.bakingtime.ui.widget.BakingRemoteViewsService;
import me.worric.bakingtime.ui.widget.WidgetConfigActivity;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = {AndroidSupportInjectionModule.class, RepositoryModule.class})
public abstract class AppModule {

    @Binds
    @Singleton
    @AppContext
    abstract Context bindContext(Application application);

    @Provides
    @Singleton
    static BakingWebService provideWebService() {
        return new Retrofit.Builder()
                .baseUrl(NetUtils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient().newBuilder().build())
                .build()
                .create(BakingWebService.class);
    }

    @ActivityScope
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity contributesMainActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = ViewModelModule.class)
    abstract WidgetConfigActivity contributesWidgetActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = DetailActivityModule.class)
    abstract DetailActivity contributesDetailActivity();

    @ContributesAndroidInjector
    abstract BakingRemoteViewsService contributesRemoteViewsService();

}
