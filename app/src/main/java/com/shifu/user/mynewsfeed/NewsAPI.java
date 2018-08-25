package com.shifu.user.mynewsfeed;

import com.shifu.user.mynewsfeed.json.JsonArticles;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

public interface NewsAPI {

    String BASE_URL = "https://newsapi.org/v2/";

    @GET("./sources")
    Call<String> loadSources(@QueryMap Map<String,String> options);

    @GET("./top-headlines")
    Call<JsonArticles> loadNews(@QueryMap Map<String, String> options, @Header("X-Api-Key") String key);

}