package com.davidg.syncinteractive.test.data.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {

    @GET("rest/")
    Call<ResponseBody> getSearchData(@Query("method") String method, @Query("format") String format,
                                     @Query("api_key") String apiKey, @Query("text") String searchQuery,
                                     @Query("per_page") String perPage, @Query("extras") String extras,
                                     @Query("page") String currentPage, @Query("nojsoncallback") String noJsonCallback);
}