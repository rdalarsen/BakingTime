package me.worric.bakingtime.data.repository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import me.worric.bakingtime.data.models.Recipe;

@Module
public abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract Repository<Recipe> bindRepo(RecipeRepository recipeRepository);

}
