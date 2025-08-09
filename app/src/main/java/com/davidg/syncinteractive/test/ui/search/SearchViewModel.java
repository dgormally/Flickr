package com.davidg.syncinteractive.test.ui.search;

import android.support.annotation.NonNull;

import com.davidg.syncinteractive.test.utils.AppConstants;
import com.davidg.syncinteractive.test.data.model.api.SearchModel;
import com.davidg.syncinteractive.test.data.remote.RetrofitHelper;
import com.davidg.syncinteractive.test.ui.base.BaseViewModel;
import com.davidg.syncinteractive.test.utils.rx.SchedulerProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchViewModel extends BaseViewModel<SearchNavigator> {


    SearchViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    void getSearchQuery(String page, String query) {
        // Cancel any pending requests to avoid multiple concurrent calls
        RetrofitHelper.getInstance().getHttpClient().dispatcher().cancelAll();
        
        Call<SearchModel> result = RetrofitHelper.getInstance().getSearchService()
                .getSearchData(
                        AppConstants.FLICKR_PHOTO_SEARCH, 
                        AppConstants.RESPONSE_TYPE, 
                        AppConstants.API_KEY, 
                        query, 
                        AppConstants.PIC_PER_PAGE, 
                        AppConstants.EXTRA_QUERY, 
                        page
                );
                
        result.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchModel> call, @NonNull Response<SearchModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SearchModel searchModel = response.body();
                    if (searchModel.getStat() != null && searchModel.getStat().equalsIgnoreCase("ok")) {
                        getNavigator().onResponseSuccess(searchModel);
                    } else {
                        getNavigator().onResponseFailed();
                    }
                } else {
                    getNavigator().onResponseFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchModel> call, @NonNull Throwable t) {
                if (!call.isCanceled()) {
                    getNavigator().onResponseFailed();
                }
            }
        });
    }

}
