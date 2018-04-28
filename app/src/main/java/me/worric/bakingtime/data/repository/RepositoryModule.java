package me.worric.bakingtime.data.repository;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import me.worric.bakingtime.data.db.AppDatabase;
import me.worric.bakingtime.di.AppContext;

@SuppressWarnings("unused")
@Module
public abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract Repository bindRepo(RecipeRepository recipeRepository);

    @Provides
    @Singleton
    static AppDatabase provideDatabase(@AppContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "recipedb").build();
    }

}
