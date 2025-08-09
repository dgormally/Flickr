package com.davidg.syncinteractive.test.ui.picture;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.davidg.syncinteractive.test.App;
import com.davidg.syncinteractive.test.R;
import com.davidg.syncinteractive.test.databinding.ActivityPictureBinding;
import com.davidg.syncinteractive.test.databinding.LayoutDialogPictureDetailsBinding;
import com.davidg.syncinteractive.test.utils.RotationGestureDetector;


public class PictureActivity extends AppCompatActivity implements View.OnClickListener, RotationGestureDetector.OnRotationGestureListener {

    public static final String POSITION = "position";
    private int position = -1;
    private Dialog dialog = null;

    private RotationGestureDetector rotationGestureDetector = null;
    private ActivityPictureBinding viewBinding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_picture);

        getIntentData();
        setUpViewPager();
        setClickListener();
        setUpGesture();
    }

    private void setUpGesture() {
        rotationGestureDetector = new RotationGestureDetector(this);
    }

    private void setClickListener() {
        viewBinding.ivLeft.setOnClickListener(this);
        viewBinding.ivRight.setOnClickListener(this);
    }

    private void getIntentData() {
        position = getIntent().getExtras().getInt(POSITION);
    }

    private void setUpViewPager() {
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(this, App.getInstance().getArrayList());
        viewBinding.pager.setOffscreenPageLimit(1);
        viewBinding.pager.setAdapter(pagerAdapter);
        viewBinding.pager.setCurrentItem(position);
        preloadNeighbors(position);
        viewBinding.pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                preloadNeighbors(position);
            }
        });
    }

    private void preloadNeighbors(int pos) {
        if (pos > 0) {
            Glide.with(this).load(App.getInstance().getArrayList().get(pos - 1).getUrl_l()).preload();
        }
        if (pos + 1 < App.getInstance().getArrayList().size()) {
            Glide.with(this).load(App.getInstance().getArrayList().get(pos + 1).getUrl_l()).preload();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:

                if (viewBinding.pager.getCurrentItem() > 0) {
                    int pos = viewBinding.pager.getCurrentItem();
                    pos--;
                    viewBinding.pager.setCurrentItem(pos);
                }

                break;
            case R.id.iv_right:

                if (viewBinding.pager.getCurrentItem() < App.getInstance().getArrayList().size() - 1) {
                    int pos = viewBinding.pager.getCurrentItem();
                    pos++;
                    viewBinding.pager.setCurrentItem(pos);
                }

                break;
            case R.id.iv_cross:
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        rotationGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onRotation(RotationGestureDetector rotationDetector) {
        float angle = rotationDetector.getAngle();
        if (angle < 0) {
            angle = (-1) * angle;
        }
        if (angle > 45) {
            if (dialog == null) {
                showDialog();
            }
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutDialogPictureDetailsBinding viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_dialog_picture_details, null, false);
        builder.setView(viewBinding.getRoot());
        builder.setCancelable(false);
        viewBinding.ivCross.setOnClickListener(this);

        viewBinding.tvTitle.setText(App.getInstance().getArrayList().get(this.viewBinding.pager.getCurrentItem()).getTitle());
        viewBinding.tvDesc.setText(App.getInstance().getArrayList().get(this.viewBinding.pager.getCurrentItem()).getDescription().get_content());
        viewBinding.tvTakenBy.setText(App.getInstance().getArrayList().get(this.viewBinding.pager.getCurrentItem()).getOwnername());

        dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }
}
