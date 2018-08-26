package com.shifu.user.mynewsfeed;

import com.shifu.user.mynewsfeed.json.JsonArticles;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    String BASE_URL = "https://newsapi.org/v2/";

    @GET("./sources")
    Call<String> loadSources(@QueryMap Map<String,String> options);

    @GET("./top-headlines")
    Flowable<Response<JsonArticles>> loadNews(@QueryMap Map<String, String> options, @Header("X-Api-Key") String key);

}