package com.okandroid.imagepicker.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.OKAndroidFragment;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;
import com.okandroid.imagepicker.R;

import java.util.List;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerFragment extends OKAndroidFragment implements ImagePickerView {

    public static ImagePickerFragment newInstance(Bundle extraParams) {
        Bundle args = new Bundle();
        if (extraParams != null) {
            args.putAll(extraParams);
        }
        ImagePickerFragment fragment = new ImagePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        return inflater.inflate(R.layout.okandroid_imagepicker_view, container, false);
    }

    private ViewGroup mImagePickerContent;
    private ImagePickerViewProxy mViewProxy;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mImagePickerContent = ViewUtil.findViewByID(view, R.id.imagepicker_content);

        mViewProxy = new ImagePickerViewProxy(this);
        mViewProxy.start();
    }

    @Override
    public void showBucket(@NonNull ImagePicker imagePicker, @NonNull Images images, @NonNull Images.Bucket bucket) {
        // TODO
    }

    @Override
    public void showBucketsSelector(@NonNull ImagePicker imagePicker, @NonNull Images images, @NonNull List<Images.Bucket> buckets) {
        // TODO
    }

    @Override
    public void hideBucketsSelector() {
        // TODO
    }

    @Override
    public ImagePicker createImagePickerInstance() {
        ImagePicker imagePicker = null;

        Bundle args = getArguments();
        if (args != null) {
            String clazz = args.getString(ImagePicker.Params.EXTRA_IMAGE_PICKER_CLASS);
            if (!TextUtils.isEmpty(clazz)) {
                try {
                    imagePicker = (ImagePicker) Class.forName(clazz).newInstance();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        if (imagePicker == null) {
            imagePicker = new ImagePicker();
        }

        return imagePicker;
    }

    @Override
    public void showLoadingView() {
        ContentLoadingProgressBar loadingView = ViewUtil.findViewByID(mImagePickerContent, R.id.content_loading_progress_bar);
        if (loadingView != null) {
            loadingView.show();
        }
    }

    @Override
    public void hideLoadingView() {
        ContentLoadingProgressBar loadingView = ViewUtil.findViewByID(mImagePickerContent, R.id.content_loading_progress_bar);
        if (loadingView != null) {
            loadingView.hide();
        }
    }

    @Override
    public void onDestroyView() {
        IOUtil.closeQuietly(mViewProxy);
        super.onDestroyView();
    }

}
