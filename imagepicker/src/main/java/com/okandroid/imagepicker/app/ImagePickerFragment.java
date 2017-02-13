package com.okandroid.imagepicker.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.OKAndroidFragment;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.AvailableUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.imagepicker.BackPressedHost;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;
import com.okandroid.imagepicker.OnBackPressedInterceptor;
import com.okandroid.imagepicker.R;
import com.okandroid.imagepicker.widget.ImagePickerContentView;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerFragment extends OKAndroidFragment implements ImagePickerView, OnBackPressedInterceptor, BackPressedHost {

    private static final String TAG = "ImagePickerFragment";

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

    private ImagePickerContentView mImagePickerContentView;

    @Override
    public void showImages(@NonNull ImagePicker imagePicker, @NonNull Images images) {
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(TAG + " showBucket but activity is null");
            return;
        }

        if (!AvailableUtil.isAvailable(activity)) {
            Log.e(TAG + " showBucket but activity is not available");
            return;
        }

        if (activity instanceof ImagePickerActivity) {
            ((ImagePickerActivity) activity).setOnBackPressedInterceptor(this);
        }

        if (mImagePickerContentView != null) {
            mImagePickerContent.removeView(mImagePickerContentView);
            mImagePickerContentView = null;
        }

        mImagePickerContentView = new ImagePickerContentView(activity, imagePicker, images);
        mImagePickerContentView.setBackPressedHost(this);
        mImagePickerContentView.setOnImagesSelectCompleteListener(new ImagePickerContentView.OnImagesSelectCompleteListener() {
            @Override
            public void onImageSelectComplete(Images images) {
                ImagePickerFragment.this.onImageSelectComplete(images);
            }
        });
        mImagePickerContent.addView(mImagePickerContentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onBackPressed() {
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(TAG + " onImageSelectComplete but activity is null");
            return;
        }

        if (!AvailableUtil.isAvailable(activity)) {
            Log.e(TAG + " onImageSelectComplete but activity is not available");
            return;
        }

        activity.onBackPressed();
    }

    protected void onImageSelectComplete(Images images) {
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(TAG + " onImageSelectComplete but activity is null");
            return;
        }

        if (!AvailableUtil.isAvailable(activity)) {
            Log.e(TAG + " onImageSelectComplete but activity is not available");
            return;
        }

        if (activity instanceof ImagePickerActivity) {
            Intent data = new Intent();
            data.putStringArrayListExtra(ImagePicker.Params.EXTRA_OUT_IMAGES, images.getSelectedImagesPath());
            activity.setResult(Activity.RESULT_OK, data);
            activity.finish();
        }
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

    @Override
    public boolean onInterceptBackPressed() {
        if (!AvailableUtil.isAvailable(mViewProxy)) {
            return false;
        }

        if (mImagePickerContentView == null) {
            return false;
        }

        if (mImagePickerContentView.onInterceptBackPressed()) {
            return true;
        }

        // need exit tip?
        return false;
    }

}
