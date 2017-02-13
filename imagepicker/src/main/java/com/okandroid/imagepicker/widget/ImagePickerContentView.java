package com.okandroid.imagepicker.widget;

import android.content.Context;
import android.net.Uri;
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
import com.okandroid.imagepicker.OnBackPressedInterceptor;
import com.okandroid.imagepicker.R;
import com.okandroid.imagepicker.util.ImageUtil;

import java.io.File;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerContentView extends FrameLayout implements OnBackPressedInterceptor {

    @NonNull
    private final ImagePicker mImagePicker;
    @NonNull
    private final ImagePicker.ImageSizePreviewInfo mImageSizePreviewInfo;
    @NonNull
    private final Images mImages;
    @NonNull
    private final LayoutInflater mLayoutInflater;
    @NonNull
    private final SubContentGridView mSubContentGridView;
    @NonNull
    private final SubContentBucketView mSubContentBucketView;
    @NonNull
    private final SubContentPagerView mSubContentPagerView;

    public ImagePickerContentView(Context context, @NonNull ImagePicker imagePicker, @NonNull Images images) {
        super(context);
        mImagePicker = imagePicker;
        mImageSizePreviewInfo = mImagePicker.createImageSizePreviewInfo();
        mImages = images;
        mLayoutInflater = LayoutInflater.from(context);

        mSubContentGridView = new SubContentGridView(context, mLayoutInflater, this);
        mSubContentBucketView = new SubContentBucketView(context, mLayoutInflater, this);
        mSubContentPagerView = new SubContentPagerView(context, mLayoutInflater, this);

        mSubContentGridView.hide();
        mSubContentBucketView.hide();
        mSubContentPagerView.hide();

        // 展示所有
        showBucket(images.getAllBucket());
    }

    private Images.Bucket mCurrentBucket;

    private void showBucket(Images.Bucket bucket) {
        if (mCurrentBucket == bucket) {
            return;
        }

        mCurrentBucket = bucket;
        mSubContentGridView.show();
        mSubContentBucketView.hide();
        mSubContentPagerView.hide();
    }

    @Override
    public boolean onInterceptBackPressed() {
        if (mSubContentPagerView.onInterceptBackPressed()) {
            return true;
        }
        if (mSubContentBucketView.onInterceptBackPressed()) {
            return true;
        }
        if (mSubContentGridView.onInterceptBackPressed()) {
            return true;
        }
        return false;
    }

    public interface OnImagesSelectCompleteListener {
        void onImageSelectComplete(Images images);
    }

    private OnImagesSelectCompleteListener mOnImagesSelectCompleteListener;

    public void setOnImagesSelectCompleteListener(OnImagesSelectCompleteListener onImagesSelectCompleteListener) {
        mOnImagesSelectCompleteListener = onImagesSelectCompleteListener;
    }

    private void tryFinishSelect() {
        if (mImagePicker.canFinishImageSelect(mImages)) {
            if (mOnImagesSelectCompleteListener != null) {
                mOnImagesSelectCompleteListener.onImageSelectComplete(mImages);
            }
        }
    }

    /**
     * 显示指定相册下的默认 grid 视图
     */
    private class SubContentGridView extends SubContentView {

        private View mAppBar;
        private View mAppBarBack;
        private TextView mAppBarTitle;
        private TextView mAppBarMore;
        private RecyclerView mRecyclerView;
        private View mBottomBar;
        private TextView mBottomBarSubmit;

        @NonNull
        private DataAdapter mDataAdapter;

        public SubContentGridView(Context context, LayoutInflater inflater, ViewGroup parent) {
            super(context, inflater, R.layout.okandroid_imagepicker_content_grid_view, parent);
            mAppBar = ViewUtil.findViewByID(mView, R.id.app_bar);
            mAppBarBack = ViewUtil.findViewByID(mAppBar, R.id.app_bar_back);
            mAppBarTitle = ViewUtil.findViewByID(mAppBar, R.id.app_bar_title);
            mAppBarMore = ViewUtil.findViewByID(mAppBar, R.id.app_bar_more);
            mRecyclerView = ViewUtil.findViewByID(mView, R.id.grid_recycler);
            mBottomBar = ViewUtil.findViewByID(mView, R.id.bottom_bar);
            mBottomBarSubmit = ViewUtil.findViewByID(mView, R.id.bottom_bar_submit);

            // init recycler
            mDataAdapter = new DataAdapter();
            mRecyclerView.setLayoutManager(new GridLayoutManager(
                    context,
                    3,
                    GridLayoutManager.VERTICAL,
                    false
            ));
            mRecyclerView.setAdapter(mDataAdapter);
            mBottomBarSubmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tryFinishSelect();
                }
            });
        }

        private void syncBottomBarStatus() {
            int size = mImages.getSelectedImagesSize();
            if (size > 0) {
                mBottomBarSubmit.setText("完成 (" + size + ")");
                mBottomBarSubmit.setEnabled(true);
            } else {
                mBottomBarSubmit.setText("完成");
                mBottomBarSubmit.setEnabled(false);
            }
        }

        @Override
        public void show() {
            mRecyclerView.setAdapter(mDataAdapter);
            syncBottomBarStatus();
            super.show();
        }

        @Override
        public void hide() {
            mRecyclerView.setAdapter(null);
            super.hide();
        }

        private class DataAdapter extends RecyclerView.Adapter {

            private class GridItemViewHolder extends RecyclerView.ViewHolder {

                private View mItemView;
                private SimpleDraweeView mSimpleDraweeView;
                private View mItemSelectFlag;

                public GridItemViewHolder(View itemView) {
                    super(itemView);
                    mItemView = ViewUtil.findViewByID(itemView, R.id.grid_item_view);
                    mSimpleDraweeView = ViewUtil.findViewByID(mItemView, R.id.sample_drawee_view);
                    mItemSelectFlag = ViewUtil.findViewByID(mItemView, R.id.grid_item_view_select_flag);
                }

                public void show(final ImageInfo imageInfo) {
                    Uri uri = Uri.fromFile(new File(imageInfo.filePath));
                    ImageUtil.showImage(mSimpleDraweeView,
                            uri,
                            mImageSizePreviewInfo.getImageCellWidth(),
                            mImageSizePreviewInfo.getImageCellHeight());

                    mItemSelectFlag.setSelected(mImages.isImageSelected(imageInfo));
                    mItemSelectFlag.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mItemSelectFlag.isSelected()) {
                                // 从选中到未选中
                                if (mImagePicker.canSelectImage(mImages, imageInfo, false)) {
                                    mImages.selectImage(imageInfo, false);
                                    mItemSelectFlag.setSelected(false);
                                }
                            } else {
                                // 从未选中到选中
                                if (mImagePicker.canSelectImage(mImages, imageInfo, true)) {
                                    mImages.selectImage(imageInfo, true);
                                    mItemSelectFlag.setSelected(true);
                                }
                            }
                            SubContentGridView.this.syncBottomBarStatus();
                        }
                    });
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = mLayoutInflater.inflate(R.layout.okandroid_imagepicker_content_grid_item_view, parent, false);
                return new GridItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ImageInfo imageInfo = mCurrentBucket.imageInfos.get(position);
                ((GridItemViewHolder) holder).show(imageInfo);
            }

            @Override
            public int getItemCount() {
                if (mCurrentBucket == null) {
                    return 0;
                }
                return mCurrentBucket.imageInfos.size();
            }
        }
    }

    /**
     * 相册选择器
     */
    private class SubContentBucketView extends SubContentView {

        public SubContentBucketView(Context context, LayoutInflater inflater, ViewGroup parent) {
            super(context, inflater, R.layout.okandroid_imagepicker_content_grid_view, parent);
        }

    }

    /**
     * 大图模式
     */
    private class SubContentPagerView extends SubContentView {
        public SubContentPagerView(Context context, LayoutInflater inflater, ViewGroup parent) {
            super(context, inflater, R.layout.okandroid_imagepicker_content_grid_view, parent);
        }
    }

    private class SubContentView implements OnBackPressedInterceptor {
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

        @Override
        public boolean onInterceptBackPressed() {
            return false;
        }

    }

}
