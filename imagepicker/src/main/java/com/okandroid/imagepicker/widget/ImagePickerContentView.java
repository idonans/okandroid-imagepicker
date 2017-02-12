package com.okandroid.imagepicker.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.imagepicker.ImageInfo;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;
import com.okandroid.imagepicker.R;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerContentView extends FrameLayout {

    @NonNull
    private final ImagePicker mImagePicker;
    @NonNull
    private final Images mImages;
    @NonNull
    private final LayoutInflater mLayoutInflater;
    @NonNull
    private final SubContentGridView mSubContentGridView;
    @NonNull
    private final SubContentPagerView mSubContentPagerView;

    public ImagePickerContentView(Context context, @NonNull ImagePicker imagePicker, @NonNull Images images) {
        super(context);
        mImagePicker = imagePicker;
        mImages = images;
        mLayoutInflater = LayoutInflater.from(context);

        mSubContentGridView = new SubContentGridView(context, mLayoutInflater, this);
        mSubContentPagerView = new SubContentPagerView(context, mLayoutInflater, this);

        mSubContentGridView.show();
        mSubContentPagerView.hide();

        // 展示所有
        mSubContentGridView.mDataAdapter.setBucket(images.getAllBucket());
    }

    private class SubContentGridView extends SubContentView {

        private View mAppBar;
        private View mAppBarBack;
        private TextView mAppBarTitle;
        private TextView mAppBarSubmit;
        private RecyclerView mRecyclerView;
        private View mBottomBar;

        @NonNull
        private DataAdapter mDataAdapter;

        public SubContentGridView(Context context, LayoutInflater inflater, ViewGroup parent) {
            super(context, inflater, R.layout.okandroid_imagepicker_content_grid_view, parent);
            mAppBar = ViewUtil.findViewByID(mView, R.id.app_bar);
            mAppBarBack = ViewUtil.findViewByID(mAppBar, R.id.app_bar_back);
            mAppBarTitle = ViewUtil.findViewByID(mAppBar, R.id.app_bar_title);
            mAppBarSubmit = ViewUtil.findViewByID(mAppBar, R.id.app_bar_submit);
            mRecyclerView = ViewUtil.findViewByID(mView, R.id.grid_recycler);
            mBottomBar = ViewUtil.findViewByID(mView, R.id.bottom_bar);

            // init recycler
            mDataAdapter = new DataAdapter();
            mRecyclerView.setLayoutManager(new GridLayoutManager(
                    context,
                    3,
                    GridLayoutManager.VERTICAL,
                    false
            ));
            mRecyclerView.setAdapter(mDataAdapter);
        }

        private class DataAdapter extends RecyclerView.Adapter {

            private Images.Bucket mBucket;

            private class GridItemViewHolder extends RecyclerView.ViewHolder {

                private View mItemView;
                private SimpleDraweeView mSimpleDraweeView;

                public GridItemViewHolder(View itemView) {
                    super(itemView);
                    mItemView = ViewUtil.findViewByID(itemView, R.id.grid_item_view);
                    mSimpleDraweeView = ViewUtil.findViewByID(mItemView, R.id.sample_drawee_view);
                }

                public void show(ImageInfo imageInfo) {
                    mSimpleDraweeView.setImageURI("res://okandroid/" + R.drawable.okandroid_imagepicker_ic_back);
                }
            }

            public void setBucket(Images.Bucket bucket) {
                mBucket = bucket;
                notifyDataSetChanged();
            }

            public Images.Bucket getBucket() {
                return mBucket;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = mLayoutInflater.inflate(R.layout.okandroid_imagepicker_content_grid_item_view, parent, false);
                return new GridItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ImageInfo imageInfo = mBucket.imageInfos.get(position);
                ((GridItemViewHolder) holder).show(imageInfo);
            }

            @Override
            public int getItemCount() {
                if (mBucket == null) {
                    return 0;
                }
                return mBucket.imageInfos.size();
            }
        }
    }

    private class SubContentPagerView extends SubContentView {
        public SubContentPagerView(Context context, LayoutInflater inflater, ViewGroup parent) {
            super(context, inflater, R.layout.okandroid_imagepicker_content_pager_view, parent);
        }
    }

    private class SubContentView {
        public final View mView;
        public final Context mContext;

        public SubContentView(Context context, LayoutInflater inflater, int layout, ViewGroup parent) {
            mContext = context;
            mView = inflater.inflate(layout, parent, false);
            parent.addView(mView);
        }

        public boolean isVisibile() {
            return mView.getVisibility() == View.VISIBLE;
        }

        public void show() {
            mView.setVisibility(View.VISIBLE);
        }

        public void hide() {
            mView.setVisibility(View.GONE);
        }
    }

}
