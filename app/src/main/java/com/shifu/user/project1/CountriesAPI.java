package com.shifu.user.project1;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CountriesAPI {

    String BASE_URL = "/v1/Country/getCountries";

    @GET(BASE_URL)
    Call<Countries> loadCountries();

    @GET(BASE_URL)
    Call<Countries> loadRegion(@Query("pRegion") String region);
}