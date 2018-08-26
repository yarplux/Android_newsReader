package com.shifu.user.mynewsfeed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Integer timeout = 5;

    private  static ApiClient instance;
    private  Retrofit.Builder retrofitBuilder;

    private ApiClient(){
        buildRetrofit();
    }

    public static ApiClient getInstance(){
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    private Retrofit.Builder buildRetrofit(){

        Gson gson = new GsonBuilder()
//                .setDateFormat(strDateFormat)
                .setLenient()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder
                .addInterceptor(interceptor)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS);

        retrofitBuilder = new Retrofit.Builder()
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        return  retrofitBuilder;
    }

    public ApiInterface getApi(){
        return  retrofitBuilder
                .baseUrl(ApiInterface.BASE_URL)
                .build()
                .create(ApiInterface.class);
    }
}
