package com.davidg.syncinteractive.test.ui.base;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;


public abstract class BaseViewModel<N> extends ViewModel {

    private N mNavigator;
    private final ObservableBoolean mIsLoading = new ObservableBoolean(false);

    public BaseViewModel() { }

    public void setNavigator(N navigator) {
        this.mNavigator = navigator;
    }

    public void onViewCreated() { }

    public void onDestroyView() { }

    public N getNavigator() {
        return mNavigator;
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

}
