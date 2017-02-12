package com.okandroid.imagepicker.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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

    public ImagePickerContentView(Context context, @NonNull ImagePicker imagePicker, @NonNull Images images) {
        super(context);
        mImagePicker = imagePicker;
        mImages = images;
        mLayoutInflater = LayoutInflater.from(context);

        mSubContentGridView = new SubContentGridView(mLayoutInflater, this);
    }

    private class SubContentGridView extends SubContentView {
        public SubContentGridView(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, R.layout.okandroid_imagepicker_content_grid_view, parent);
        }
    }

    private class SubContentPagerView extends SubContentView {
        public SubContentPagerView(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, R.layout.okandroid_imagepicker_content_pager_view, parent);
        }
    }

    private class SubContentView {
        public final View mView;

        public SubContentView(LayoutInflater inflater, int layout, ViewGroup parent) {
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
