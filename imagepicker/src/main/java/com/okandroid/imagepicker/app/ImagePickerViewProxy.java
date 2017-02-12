package com.okandroid.imagepicker.app;

import android.database.Cursor;

import com.okandroid.boot.AppContext;
import com.okandroid.boot.viewproxy.ViewProxy;
import com.okandroid.imagepicker.ImageInfo;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;

import java.util.List;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerViewProxy extends ViewProxy<ImagePickerView> {

    public ImagePickerViewProxy(ImagePickerView imagePickerView) {
        super(imagePickerView);
    }

    private ImagePicker mImagePicker;
    private Images mImages;

    @Override
    protected void onInitBackground() {
        super.onInitBackground();

        ImagePickerView view = getView();
        if (view == null) {
            return;
        }

        mImagePicker = view.createImagePickerInstance();
        Cursor cursor = mImagePicker.createQueryCursor(AppContext.getContext().getContentResolver());
        List<ImageInfo> imageInfos = mImagePicker.parse(cursor, this);

        if (!isAvailable()) {
            return;
        }

        if (imageInfos != null && imageInfos.size() > 0) {
            mImages = new Images(imageInfos);
        }
    }

    public ImagePicker getImagePicker() {
        return mImagePicker;
    }

    @Override
    protected void onLoading() {
        ImagePickerView view = getView();
        if (view == null) {
            return;
        }

        view.showLoadingView();
    }

    @Override
    protected void onStart() {
        ImagePickerView view = getView();
        if (view == null) {
            return;
        }

        view.hideLoadingView();

        if (mImages != null) {
            view.showImages(mImagePicker, mImages);
        }
    }

}
