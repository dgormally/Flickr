package com.davidg.syncinteractive.test.ui.base;

import android.arch.lifecycle.ViewModel;

public abstract class BaseViewModel<N> extends ViewModel {

    private N mNavigator;

    public BaseViewModel() {
    }

    public void setNavigator(N navigator) {
        this.mNavigator = navigator;
    }

    public void onViewCreated() {
        // No-op by default
    }

    public void onDestroyView() {
        // No-op by default
    }

    public N getNavigator() {
        return mNavigator;
    }
}
