package com.davidg.syncinteractive.test.ui.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.davidg.syncinteractive.test.R;
import com.davidg.syncinteractive.test.data.model.api.Photo;
import com.davidg.syncinteractive.test.databinding.SingleRowRecyclerPictureBinding;
import java.util.ArrayList;


class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private ArrayList<Photo> arrayList = new ArrayList<>();
    private Context context;
    private OnNextPageLoadListener listener = null;
    private OnPictureClickListener onPictureClickListener = null;
    private boolean isLoading = false;


    SearchAdapter(ArrayList<Photo> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
        setHasStableIds(true);
    }

    void setNextPageLoadListener(OnNextPageLoadListener listener) {
        this.listener = listener;
    }

    void setOnPictureClickListener(OnPictureClickListener listener) {
        onPictureClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SingleRowRecyclerPictureBinding singleRowRecyclerPictureBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.single_row_recycler_picture, parent, false);
        return new MyViewHolder(singleRowRecyclerPictureBinding);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Glide.with(context)
                .load(arrayList.get(position).getUrl_z())
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.binding.ivImage);

        if (!isLoading && arrayList.size() - 1 == position) {
            listener.loadNextPage();
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

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SingleRowRecyclerPictureBinding binding;

        MyViewHolder(SingleRowRecyclerPictureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPictureClickListener.onPictureClicked(getLayoutPosition());
        }
    }


}
