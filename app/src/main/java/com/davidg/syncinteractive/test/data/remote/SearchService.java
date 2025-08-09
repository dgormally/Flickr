package com.davidg.syncinteractive.test.data.remote;

import com.davidg.syncinteractive.test.data.model.api.SearchModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {

    @GET("rest")
    Call<SearchModel> getSearchData(
            @Query("method") String method,
            @Query("format") String format,
            @Query("api_key") String apiKey,
            @Query("text") String text,
            @Query("per_page") String perPage,
            @Query("extras") String extras,
            @Query("page") String page
    );
}