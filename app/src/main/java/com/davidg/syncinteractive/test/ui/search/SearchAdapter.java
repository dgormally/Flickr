package com.davidg.syncinteractive.test.ui.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidg.syncinteractive.test.R;
import com.davidg.syncinteractive.test.data.model.api.Photo;
import com.davidg.syncinteractive.test.databinding.SingleRowRecyclerPictureBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private ArrayList<Photo> arrayList = new ArrayList<>();
    private Context context;
    private OnNextPageLoadListener listener = null;
    private OnPictureClickListener onPictureClickListener = null;
    private boolean isLoading = false;
    
    // Glide request options for optimization
    private RequestOptions requestOptions;


    SearchAdapter(ArrayList<Photo> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
        
        // Initialize optimized Glide request options
        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_search)
                .error(R.drawable.ic_search)
                .centerCrop()
                .override(300, 300); // Fixed size for better memory management
    }

    void setNextPageLoadListener(OnNextPageLoadListener listener) {
        this.listener = listener;
    }

    void setOnPictureClickListener(OnPictureClickListener listener) {
        onPictureClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SingleRowRecyclerPictureBinding singleRowRecyclerPictureBinding = 
                DataBindingUtil.inflate(LayoutInflater.from(context), 
                        R.layout.single_row_recycler_picture, parent, false);
        return new MyViewHolder(singleRowRecyclerPictureBinding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Photo photo = arrayList.get(position);
        
        // Optimized image loading with Glide
        String imageUrl = photo.getUrl_z();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(holder.binding.ivImage);
        }

        // Efficient pagination loading
        if (!isLoading && position >= arrayList.size() - 5) { // Load 5 items before end
            if (listener != null) {
                listener.loadNextPage();
            }
        }
    }

    void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Cancel Glide requests to prevent memory leaks
        Glide.with(context).clear(holder.binding.ivImage);
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        // Cancel Glide requests when view is recycled
        Glide.with(context).clear(holder.binding.ivImage);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SingleRowRecyclerPictureBinding binding;

        MyViewHolder(SingleRowRecyclerPictureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onPictureClickListener != null) {
                onPictureClickListener.onPictureClicked(getAdapterPosition());
            }
        }
    }
}
