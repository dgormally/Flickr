package com.davidg.syncinteractive.test.ui.picture;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.davidg.syncinteractive.test.R;
import com.bumptech.glide.Glide;
import com.davidg.syncinteractive.test.data.model.api.Photo;

import java.util.ArrayList;

class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<Photo> arrayList;


    CustomPagerAdapter(Context context, ArrayList<Photo> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {

        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {


        View itemView = LayoutInflater.from(mContext).inflate(R.layout.single_pager_layout, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.pager_imageView);

        String preferred = arrayList.get(position).getUrl_z();
        String fallback = arrayList.get(position).getUrl_l();
        String url = preferred != null && preferred.length() > 0 ? preferred : fallback;

        Glide.with(mContext)
                .load(url)
                .thumbnail(0.25f)
                .dontAnimate()
                .into(imageView);

        container.addView(itemView);
        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}