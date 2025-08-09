package com.davidg.syncinteractive.test.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.davidg.syncinteractive.test.BR;
import com.davidg.syncinteractive.test.R;
import com.davidg.syncinteractive.test.data.model.api.Photo;
import com.davidg.syncinteractive.test.data.model.api.SearchModel;
import com.davidg.syncinteractive.test.databinding.ActivitySearchBinding;
import com.davidg.syncinteractive.test.ui.base.BaseActivity;
import com.davidg.syncinteractive.test.ui.picture.PictureActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by David Gormally on 10/6/2017.
 */

public class SearchActivity extends BaseActivity<ActivitySearchBinding, SearchViewModel> 
        implements SearchNavigator, OnNextPageLoadListener, OnPictureClickListener {

    @Inject
    SearchViewModel searchViewModel;

    private ActivitySearchBinding activitySearchBinding = null;
    private int currentPage = 1;
    private int totalPages = 0;
    private SearchAdapter adapter = null;
    private ArrayList<Photo> photoList = new ArrayList<>();
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySearchBinding = getViewDataBinding();
        setUpRecyclerView();
        searchViewModel.setNavigator(this);
        setUpEditTextActionListener();
    }

    private void showDialog() {
        if (adapter != null) {
            adapter.setLoading(true);
        }
        if (photoList.isEmpty()) {
            getViewDataBinding().progressBarNoData.setVisibility(View.VISIBLE);
        } else {
            getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideDialog() {
        if (adapter != null) {
            adapter.setLoading(false);
        }
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

                    String query = getViewDataBinding().etSearch.getText().toString().trim();
                    if (query.length() < 1) {
                        Toast.makeText(SearchActivity.this, R.string.empty_search, Toast.LENGTH_SHORT).show();
                        getViewDataBinding().etSearch.requestFocus();
                        return false;
                    }

                    // Reset search state
                    currentQuery = query;
                    photoList.clear();
                    currentPage = 1;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    
                    hideKeyboard();
                    showDialog();
                    searchViewModel.getSearchQuery(String.valueOf(currentPage), query);
                    return true;
                }
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        activitySearchBinding.recyclerView.setLayoutManager(layoutManager);
        
        // RecyclerView optimizations
        activitySearchBinding.recyclerView.setHasFixedSize(true);
        activitySearchBinding.recyclerView.setItemViewCacheSize(20);
        activitySearchBinding.recyclerView.setDrawingCacheEnabled(true);
        activitySearchBinding.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        
        adapter = new SearchAdapter(photoList, this);
        adapter.setNextPageLoadListener(this);
        adapter.setOnPictureClickListener(this);
        adapter.setHasStableIds(true);
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
            
            List<Photo> newPhotos = searchModel.getPhotos().getPhoto();
            if (newPhotos != null) {
                int startPosition = photoList.size();
                photoList.addAll(newPhotos);
                
                if (adapter != null) {
                    if (startPosition == 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyItemRangeInserted(startPosition, newPhotos.size());
                    }
                }
            }
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
        // Clear references to prevent memory leaks
        if (adapter != null) {
            adapter = null;
        }
        photoList.clear();
        activitySearchBinding = null;
    }

    @Override
    public void loadNextPage() {
        if (totalPages > currentPage && !currentQuery.isEmpty()) {
            currentPage++;
            showDialog();
            searchViewModel.getSearchQuery(String.valueOf(currentPage), currentQuery);
        }
    }

    @Override
    public void onPictureClicked(int layoutPosition) {
        if (layoutPosition >= 0 && layoutPosition < photoList.size()) {
            Intent intent = new Intent(this, PictureActivity.class);
            intent.putExtra(PictureActivity.POSITION, layoutPosition);
            // Pass the photo list to the next activity
            intent.putParcelableArrayListExtra("photoList", photoList);
            startActivity(intent);
        }
    }
}
