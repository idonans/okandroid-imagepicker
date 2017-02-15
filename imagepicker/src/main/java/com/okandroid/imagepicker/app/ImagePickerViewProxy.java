package com.okandroid.imagepicker.app;

import android.database.Cursor;

import com.okandroid.boot.AppContext;
import com.okandroid.boot.app.ext.preload.PreloadViewProxy;
import com.okandroid.imagepicker.ImageInfo;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;

import java.util.List;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerViewProxy extends PreloadViewProxy<ImagePickerView> {

    private ImagePicker mImagePicker;
    private Images mImages;

    public ImagePickerViewProxy(ImagePickerView imagePickerView) {
        super(imagePickerView);
    }

    @Override
    protected void onPreDataLoadBackground() {
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
    public void onPrepared() {
        super.onPrepared();

        if (!isPrepared()) {
            return;
        }

        ImagePickerView view = getView();
        if (view == null) {
            return;
        }

        if (mImages != null) {
            view.showImages(mImagePicker, mImages);
        }
    }

}
