package com.okandroid.imagepicker.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.preload.PreloadFragment;
import com.okandroid.boot.app.ext.preload.PreloadViewProxy;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.AvailableUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;
import com.okandroid.imagepicker.R;
import com.okandroid.imagepicker.widget.ImagePickerContentView;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerFragment extends PreloadFragment implements ImagePickerView {

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

    private Content mContent;

    @Override
    protected void showPreloadContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        IOUtil.closeQuietly(mContent);
        mContent = new Content(activity, inflater, contentView);
    }

    private class Content extends PreloadSubViewHelper {

        private final ViewGroup mImagePickerContent;

        private ImagePickerContentView mImagePickerContentView;

        public Content(Activity activity, LayoutInflater inflater, ViewGroup parentView) {
            super(activity, inflater, parentView, R.layout.okandroid_imagepicker_view);
            mImagePickerContent = ViewUtil.findViewByID(mRootView, R.id.imagepicker_content);
        }

        public void showImages(@NonNull ImagePicker imagePicker, @NonNull Images images) {
            Activity activity = getActivity();
            if (activity == null) {
                Log.e(TAG + " showImages but activity is null");
                return;
            }

            if (!AvailableUtil.isAvailable(activity)) {
                Log.e(TAG + " showImages but activity is not available");
                return;
            }

            if (mImagePickerContentView != null) {
                mImagePickerContent.removeView(mImagePickerContentView);
                mImagePickerContentView = null;
            }

            mImagePickerContentView = new ImagePickerContentView(activity, imagePicker, images);
            mImagePickerContentView.setOnBackPressedHost(new ImagePickerContentView.OnBackPressedHost() {
                @Override
                public void callActivityOnBackPressed() {
                    ImagePickerFragment.this.requestBackPressed();
                }
            });
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

        public boolean onBackPressed() {
            if (mImagePickerContentView != null) {
                return mImagePickerContentView.onBackPressed();
            }
            return false;
        }
    }


    @Override
    public void showImages(@NonNull ImagePicker imagePicker, @NonNull Images images) {
        if (AvailableUtil.isAvailable(mContent)) {
            mContent.showImages(imagePicker, images);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (AvailableUtil.isAvailable(mContent)) {
            if (mContent.onBackPressed()) {
                return true;
            }
        }

        return super.onBackPressed();
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
    protected PreloadViewProxy newDefaultViewProxy() {
        return new ImagePickerViewProxy(this);
    }

}
