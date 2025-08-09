package com.davidg.syncinteractive.test.data.remote;

import android.support.annotation.NonNull;

import com.davidg.syncinteractive.test.utils.AppConstants;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHelper {

    private static RetrofitHelper instance = null;
    private OkHttpClient httpClient = null;

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

        httpClient.connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);

        // Basic cache: 10 MB
        try {
            File cacheDir = new File(
                    new File("/data/data/com.davidg.syncinteractive.test"),
                    "http-cache");
            Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);
            httpClient.cache(cache);
        } catch (Exception ignored) { }

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();

                final HttpUrl url = originalHttpUrl.newBuilder()
                        .build();

                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url)
                        .header("Cache-Control", "public, max-age=60");

                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }

    /**
     * Creates a pre configured Retrofit instance
     */
    private Retrofit createRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .client(httpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }
}