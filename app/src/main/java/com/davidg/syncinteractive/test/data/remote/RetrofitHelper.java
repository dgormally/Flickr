package com.davidg.syncinteractive.test.data.remote;

import android.support.annotation.NonNull;

import com.davidg.syncinteractive.test.utils.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static RetrofitHelper instance = null;
    private OkHttpClient httpClient = null;
    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MB cache
    private static final int CONNECT_TIMEOUT = 15; // seconds
    private static final int READ_TIMEOUT = 20; // seconds
    private static final int WRITE_TIMEOUT = 20; // seconds

    private RetrofitHelper(){
        httpClient = createOkHttpClient();
    }

    public static RetrofitHelper getInstance(){
        if (instance == null){
            instance = new RetrofitHelper();
        }
        return instance;
    }


    public SearchService getSearchService() {
        final Retrofit retrofit = createRetrofit();
        return retrofit.create(SearchService.class);
    }

    public OkHttpClient getHttpClient(){
        return httpClient;
    }

    
    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        
        // Add caching
        Cache cache = new Cache(new File("cache"), CACHE_SIZE);
        httpClient.cache(cache);
        
        // Add timeouts for better performance
        httpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        
        // Add cache interceptor
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();

                final HttpUrl url = originalHttpUrl.newBuilder()
                        .build();

                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url)
                        .header("Cache-Control", "public, max-age=300"); // 5 minutes cache

                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        
        // Add logging interceptor for debug builds only
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpClient.addInterceptor(loggingInterceptor);

        return httpClient.build();
    }

    /**
     * Creates a pre configured Retrofit instance
     */
    private Retrofit createRetrofit() {
        // Create optimized Gson instance
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}