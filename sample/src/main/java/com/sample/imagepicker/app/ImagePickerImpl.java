package com.sample.imagepicker.app;

import com.okandroid.imagepicker.ImageInfo;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;

import java.util.List;

/**
 * Created by idonans on 2017/2/19.
 */

public class ImagePickerImpl extends ImagePicker {

    @Override
    public Images createImages(List<ImageInfo> imageInfos) {
        return new ImagesImpl(imageInfos);
    }

    public class ImagesImpl extends Images {

        public ImagesImpl(List<ImageInfo> imageInfos) {
            super(imageInfos);
        }

        @Override
        public void selectImage(ImageInfo imageInfo, boolean selected) {
            super.selectImage(imageInfo, selected);
            synchronized (mSelectedImageInfos) {
                int size = mSelectedImageInfos.size();
                if (size > 50) {
                    mSelectedImageInfos.remove(0);
                }
            }
        }

    }

}
