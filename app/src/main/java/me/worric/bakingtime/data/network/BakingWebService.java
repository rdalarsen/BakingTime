package me.worric.bakingtime.data.network;

import java.util.List;

import me.worric.bakingtime.data.models.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingWebService {

    @GET("android-baking-app-json")
    Call<List<Recipe>> fetchRecipes();

}
