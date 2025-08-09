package com.davidg.syncinteractive.test.ui.search;

import android.support.annotation.NonNull;

import com.davidg.syncinteractive.test.utils.AppConstants;
import com.google.gson.Gson;
import com.davidg.syncinteractive.test.data.model.api.SearchModel;
import com.davidg.syncinteractive.test.data.remote.RetrofitHelper;
import com.davidg.syncinteractive.test.ui.base.BaseViewModel;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchViewModel extends BaseViewModel<SearchNavigator> {


    SearchViewModel() {
        super();
    }

    void getSearchQuery(String page, String query) {
        RetrofitHelper.getInstance().getHttpClient().dispatcher().cancelAll();
        Call<ResponseBody> result = RetrofitHelper.getInstance().getSearchService().getSearchData(AppConstants.FLICKR_PHOTO_SEARCH, AppConstants.RESPONSE_TYPE, AppConstants.API_KEY, query, AppConstants.PIC_PER_PAGE, AppConstants.EXTRA_QUERY, page);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                String data = null;
                try {
                    //noinspection ConstantConditions
                    data = response.body().string();
                    data = data.substring(14, data.length() - 1);
                } catch (IOException e) {
                    getNavigator().onResponseFailed();
                }

                SearchModel searchModel = new Gson().fromJson(data, SearchModel.class);
                getNavigator().onResponseSuccess(searchModel);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                getNavigator().onResponseFailed();
            }
        });
    }

}
