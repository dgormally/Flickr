package com.davidg.syncinteractive.test.ui.search;

import dagger.Module;
import dagger.Provides;


@Module
public class SearchActivityModule {

    @Provides
    SearchViewModel provideSearchViewModel() {
        return new SearchViewModel();
    }

}
