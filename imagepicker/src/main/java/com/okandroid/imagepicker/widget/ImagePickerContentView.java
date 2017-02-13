package com.okandroid.imagepicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.okandroid.boot.util.DimenUtil;
import com.okandroid.boot.util.SystemUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.imagepicker.BackPressedHost;
import com.okandroid.imagepicker.ImageInfo;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;
import com.okandroid.imagepicker.OnBackPressedInterceptor;
import com.okandroid.imagepicker.R;
import com.okandroid.imagepicker.util.ImageUtil;

import java.io.File;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerContentView extends FrameLayout implements OnBackPressedInterceptor {

    private final ImagePicker mImagePicker;
    private final ImagePicker.ImageSizePreviewInfo mImageSizePreviewInfo;
    private final Images mImages;
    private final LayoutInflater mLayoutInflater;
    private final SubContentGridView mSubContentGridView;
    private final SubContentBucketView mSubContentBucketView;
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
        boolean bucketChanged = mCurrentBucket != bucket;

        mCurrentBucket = bucket;
        mSubContentGridView.show(bucketChanged);
        mSubContentBucketView.hide();
        mSubContentPagerView.hide();
    }

    private void requestSystemFullscreen(boolean fullscreen) {
        if (fullscreen) {
            SystemUtil.setFullscreenWithSystemUi(this);
        } else {
            SystemUtil.unsetFullscreenWithSystemUi(this);
        }
    }

    @Override
    public boolean onInterceptBackPressed() {
        if (mSubContentPagerView.onInterceptBackPressed()) {
            if (!mSubContentPagerView.isVisible()) {
                // pager 视图关闭时, 刷新 grid 视图，确保选中状态一致
                mSubContentGridView.updateSelf();
                // 取消全屏
                requestSystemFullscreen(false);
            }
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

    private BackPressedHost mBackPressedHost;

    public void setBackPressedHost(BackPressedHost backPressedHost) {
        mBackPressedHost = backPressedHost;
    }

    private void callBackPressedHost() {
        if (mBackPressedHost != null) {
            mBackPressedHost.onBackPressed();
        }
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
        private DataAdapter mDataAdapter;
        private View mBottomBar;
        private TextView mBottomBarSubmit;


        public SubContentGridView(Context context, LayoutInflater inflater, ViewGroup parent) {
            super(context, inflater, R.layout.okandroid_imagepicker_content_grid_view, parent);
            mAppBar = ViewUtil.findViewByID(mView, R.id.app_bar);
            mAppBarBack = ViewUtil.findViewByID(mAppBar, R.id.app_bar_back);
            mAppBarTitle = ViewUtil.findViewByID(mAppBar, R.id.app_bar_title);
            mAppBarMore = ViewUtil.findViewByID(mAppBar, R.id.app_bar_more);
            mRecyclerView = ViewUtil.findViewByID(mView, R.id.grid_recycler);
            mBottomBar = ViewUtil.findViewByID(mView, R.id.bottom_bar);
            mBottomBarSubmit = ViewUtil.findViewByID(mView, R.id.bottom_bar_submit);

            mAppBarBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBackPressedHost();
                }
            });
            mAppBarMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSubContentBucketView != null) {
                        mSubContentBucketView.show(false);
                    }
                }
            });

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

        @Override
        public void updateSelf() {
            super.updateSelf();
            mDataAdapter.notifyDataSetChanged();
            syncBottomBarStatus();
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
        public void show(boolean bucketChanged) {
            if (bucketChanged || mRecyclerView.getAdapter() != mDataAdapter) {
                mRecyclerView.setAdapter(mDataAdapter);
            } else {
                mDataAdapter.notifyDataSetChanged();
            }
            syncBottomBarStatus();
            super.show(bucketChanged);
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
                    mItemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int adapterPosition = getAdapterPosition();
                            if (adapterPosition >= 0) {
                                mSubContentPagerView.show(false, adapterPosition);
                            }
                        }
                    });

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
                ImageInfo item = mCurrentBucket.imageInfos.get(position);
                ((GridItemViewHolder) holder).show(item);
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

        private RecyclerView mRecyclerView;
        private DataAdapter mDataAdapter;

        public SubContentBucketView(Context context, LayoutInflater inflater, ViewGroup parent) {
            super(context, inflater, R.layout.okandroid_imagepicker_content_bucket_view, parent);
            mView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            mRecyclerView = ViewUtil.findViewByID(mView, R.id.bucket_recycler);

            // init recycler
            mDataAdapter = new DataAdapter();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

                private int mDividerSize = 1; // 1px
                private int mBottomArea = DimenUtil.dp2px(20);
                private final Paint mDividerPaint;

                {
                    mDividerPaint = new Paint();
                    mDividerPaint.setColor(0xffe0e0e0);
                }

                @Override
                public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                    int childCount = parent.getChildCount();
                    if (childCount <= 0) {
                        return;
                    }

                    int left = parent.getPaddingLeft();
                    int right = parent.getWidth() - parent.getPaddingRight();

                    for (int i = 0; i < childCount; i++) {
                        View childView = parent.getChildAt(i);
                        // draw divider
                        c.drawRect(left, childView.getBottom(), right, childView.getBottom() + mDividerSize, mDividerPaint);
                    }
                }

                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    if (parent == null || parent.getAdapter() == null) {
                        return;
                    }

                    int count = parent.getAdapter().getItemCount();
                    if (count <= 0) {
                        return;
                    }

                    int position = parent.getChildAdapterPosition(view);

                    if (position < 0) {
                        return;
                    }

                    // 每个后面有分割线
                    outRect.bottom = mDividerSize;

                    // 最后一个后面额外有一定的空白区域
                    if (position == count - 1) {
                        outRect.bottom += mBottomArea;
                    }
                }

            });

            mRecyclerView.setAdapter(mDataAdapter);
        }

        private class DataAdapter extends RecyclerView.Adapter {

            private class BucketItemViewHolder extends RecyclerView.ViewHolder {

                private final int BUCKET_COVER_SIZE = DimenUtil.dp2px(60);
                private SimpleDraweeView mBucketCover;
                private TextView mBucketName;
                private TextView mBucketSize;

                public BucketItemViewHolder(View itemView) {
                    super(itemView);
                    mBucketCover = ViewUtil.findViewByID(itemView, R.id.bucket_cover);
                    mBucketName = ViewUtil.findViewByID(itemView, R.id.bucket_name);
                    mBucketSize = ViewUtil.findViewByID(itemView, R.id.bucket_size);
                }

                public void show(final Images.Bucket bucket) {
                    itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCurrentBucket == bucket) {
                                SubContentBucketView.this.dismiss();
                                return;
                            }

                            showBucket(bucket);
                        }
                    });

                    mBucketName.setText(bucket.bucketName);

                    Uri uri = Uri.fromFile(new File(bucket.cover.filePath));
                    ImageUtil.showImage(mBucketCover, uri, BUCKET_COVER_SIZE, BUCKET_COVER_SIZE);

                    mBucketSize.setText("(" + bucket.imageInfos.size() + ")");
                }

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = mLayoutInflater.inflate(R.layout.okandroid_imagepicker_content_bucket_item_view, parent, false);
                return new BucketItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Images.Bucket item = getItem(position);
                ((BucketItemViewHolder) holder).show(item);
            }

            public Images.Bucket getItem(int position) {
                if (position == 0) {
                    return mImages.getAllBucket();
                }
                return mImages.getSubBuckets().get(position - 1);
            }

            @Override
            public int getItemCount() {
                return mImages.getSubBuckets().size() + 1;
            }

        }

        public void dismiss() {
            if (isVisible()) {
                hide();
            }
        }

        @Override
        public boolean onInterceptBackPressed() {
            if (isVisible()) {
                dismiss();
                return true;
            }

            return super.onInterceptBackPressed();
        }

    }

    /**
     * 大图模式
     */
    private class SubContentPagerView extends SubContentView {

        private boolean mFullscreen;

        private ViewPager mPager;
        private DataAdapter mDataAdapter;

        private ViewGroup mAppBar;
        private View mAppBarBack;
        private View mAppBarSelectFlag;
        private View mBottomBar;
        private TextView mBottomBarSubmit;

        public SubContentPagerView(Context context, LayoutInflater inflater, ViewGroup parent) {
            super(context, inflater, R.layout.okandroid_imagepicker_content_pager_view, parent);
            mPager = ViewUtil.findViewByID(mView, R.id.pager);
            mAppBar = ViewUtil.findViewByID(mView, R.id.app_bar);
            mAppBarBack = ViewUtil.findViewByID(mAppBar, R.id.app_bar_back);
            mAppBarSelectFlag = ViewUtil.findViewByID(mAppBar, R.id.app_bar_select_flag);
            mBottomBar = ViewUtil.findViewByID(mView, R.id.bottom_bar);
            mBottomBarSubmit = ViewUtil.findViewByID(mView, R.id.bottom_bar_submit);

            mBottomBarSubmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tryFinishSelect();
                }
            });

            mDataAdapter = new DataAdapter();
            mPager.setAdapter(mDataAdapter);
            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    boolean selected = mDataAdapter.isItemSelected(position);
                    mAppBarSelectFlag.setSelected(selected);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            mAppBarSelectFlag.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mPager.getCurrentItem();
                    if (position < 0) {
                        return;
                    }
                    if (mPager.getAdapter() == null) {
                        return;
                    }
                    int count = mDataAdapter.getCount();
                    if (position >= count) {
                        return;
                    }
                    ImageInfo imageInfo = mDataAdapter.getItem(position);

                    if (mAppBarSelectFlag.isSelected()) {
                        // 从选中到未选中
                        if (mImagePicker.canSelectImage(mImages, imageInfo, false)) {
                            mImages.selectImage(imageInfo, false);
                            mAppBarSelectFlag.setSelected(false);
                        }
                    } else {
                        // 从未选中到选中
                        if (mImagePicker.canSelectImage(mImages, imageInfo, true)) {
                            mImages.selectImage(imageInfo, true);
                            mAppBarSelectFlag.setSelected(true);
                        }
                    }
                    SubContentPagerView.this.syncBottomBarStatus();
                }
            });
        }

        private void reverseFullscreen() {
            setFullscreen(!mFullscreen);
        }

        private void setFullscreen(boolean fullscreen) {
            mFullscreen = fullscreen;
            if (mFullscreen) {
                requestSystemFullscreen(true);
                hideAppBarAndBottomBar();
            } else {
                requestSystemFullscreen(false);
                showAppBarAndBottomBar();
            }
        }

        private void hideAppBarAndBottomBar() {
            mAppBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
        }

        private void showAppBarAndBottomBar() {
            mAppBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
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
        public void show(boolean bucketChanged) {
            show(bucketChanged, 0);
        }

        public void show(boolean bucketChanged, int position) {
            mPager.setAdapter(mDataAdapter);
            mPager.setCurrentItem(position, false);
            syncBottomBarStatus();
            super.show(bucketChanged);
            setFullscreen(false);
        }

        @Override
        public void hide() {
            mPager.setAdapter(null);
            super.hide();
        }

        public void dismiss() {
            if (isVisible()) {
                hide();
            }
        }

        @Override
        public boolean onInterceptBackPressed() {
            if (isVisible()) {
                dismiss();
                return true;
            }

            return super.onInterceptBackPressed();
        }

        private class DataAdapter extends PagerAdapter {

            @Override
            public int getCount() {
                if (mCurrentBucket == null) {
                    return 0;
                }
                return mCurrentBucket.imageInfos.size();
            }

            public ImageInfo getItem(int position) {
                return mCurrentBucket.imageInfos.get(position);
            }

            public boolean isItemSelected(int position) {
                ImageInfo item = mCurrentBucket.imageInfos.get(position);
                return mImages.isImageSelected(item);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageInfo item = getItem(position);
                Uri uri = Uri.fromFile(new File(item.filePath));

                View view = mLayoutInflater.inflate(R.layout.okandroid_imagepicker_content_pager_item_view, container, false);
                PhotoDraweeView photoDraweeView = ViewUtil.findViewByID(view, R.id.photo_drawee_view);
                photoDraweeView.setPhotoUri(uri);
                photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        reverseFullscreen();
                    }
                });

                container.addView(view);

                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

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

        public boolean isVisible() {
            return mView.getVisibility() == View.VISIBLE;
        }

        public void show(boolean bucketChanged) {
            mView.setVisibility(View.VISIBLE);
        }

        public void hide() {
            mView.setVisibility(View.GONE);
        }

        @Override
        public boolean onInterceptBackPressed() {
            return false;
        }

        public void updateSelf() {
        }

    }

}
