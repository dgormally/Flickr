package com.davidg.syncinteractive.test.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.davidg.syncinteractive.test.App;
import com.davidg.syncinteractive.test.BR;
import com.davidg.syncinteractive.test.R;
import com.davidg.syncinteractive.test.data.model.api.Photo;
import com.davidg.syncinteractive.test.data.model.api.SearchModel;
import com.davidg.syncinteractive.test.databinding.ActivitySearchBinding;
import com.davidg.syncinteractive.test.ui.base.BaseActivity;
import com.davidg.syncinteractive.test.ui.picture.PictureActivity;

import javax.inject.Inject;

/**
 * Created by David Gormally on 10/6/2017.
 */

public class SearchActivity extends BaseActivity<ActivitySearchBinding, SearchViewModel> implements SearchNavigator, OnNextPageLoadListener, OnPictureClickListener {


    @Inject
    SearchViewModel searchViewModel;

    private ActivitySearchBinding activitySearchBinding = null;
    private int currentPage = 1;
    private int totalPages = 0;
    private SearchAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySearchBinding = getViewDataBinding();

        setUpRecyclerView();

        searchViewModel.setNavigator(this);

        setUpEditTextActionListener();
    }

    private void showDialog() {
        adapter.setLoading(true);
        if (App.getInstance().getArrayList().isEmpty()) {
            getViewDataBinding().progressBarNoData.setVisibility(View.VISIBLE);
        } else {
            getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideDialog() {
        adapter.setLoading(false);
        getViewDataBinding().progressBar.setVisibility(View.GONE);
        getViewDataBinding().progressBarNoData.setVisibility(View.GONE);
    }

    private void setUpEditTextActionListener() {
        getViewDataBinding().etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if (!isNetworkConnected()) {
                        Toast.makeText(SearchActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (getViewDataBinding().etSearch.getText().toString().trim().length() < 1) {
                        Toast.makeText(SearchActivity.this, R.string.empty_search, Toast.LENGTH_SHORT).show();
                        getViewDataBinding().etSearch.requestFocus();
                        return false;
                    }

                    App.getInstance().getArrayList().clear();
                    currentPage = 1;
                    getViewDataBinding().recyclerView.getAdapter().notifyDataSetChanged();
                    hideKeyboard();
                    showDialog();
                    searchViewModel.getSearchQuery(String.valueOf(currentPage), getViewDataBinding().etSearch.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        activitySearchBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new SearchAdapter(App.getInstance().getArrayList(), this);
        adapter.setNextPageLoadListener(this);
        adapter.setOnPictureClickListener(this);
        activitySearchBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public SearchViewModel getViewModel() {
        return searchViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR._all;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void onResponseSuccess(SearchModel searchModel) {
        if (searchModel.getStat().equalsIgnoreCase("ok")) {
            currentPage = Integer.parseInt(searchModel.getPhotos().getPage());
            totalPages = Integer.parseInt(searchModel.getPhotos().getPages());
            int previousSize = App.getInstance().getArrayList().size();
            for (Photo photo : searchModel.getPhotos().getPhoto()) {
                App.getInstance().getArrayList().add(photo);
            }
            // Notify only the newly inserted range to minimize redraws
            getViewDataBinding().recyclerView.getAdapter().notifyItemRangeInserted(previousSize,
                    searchModel.getPhotos().getPhoto().size());
        }
        hideDialog();
    }

    @Override
    public void onResponseFailed() {
        hideDialog();
        Toast.makeText(this, R.string.data_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void loadNextPage() {
        if (totalPages > currentPage) {
            currentPage++;
            showDialog();
            searchViewModel.getSearchQuery(String.valueOf(currentPage), getViewDataBinding().etSearch.getText().toString().trim());
        }
    }

    @Override
    public void onPictureClicked(int layoutPosition) {
        Intent intent = new Intent(this, PictureActivity.class);
        intent.putExtra(PictureActivity.POSITION, layoutPosition);
        startActivity(intent);
    }
}
