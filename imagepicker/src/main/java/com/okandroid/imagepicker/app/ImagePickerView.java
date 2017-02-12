package com.okandroid.imagepicker.app;

import android.support.annotation.NonNull;

import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;

import java.util.List;

/**
 * Created by idonans on 2017/2/12.
 */

public interface ImagePickerView {

    void showLoadingView();

    void hideLoadingView();

    ImagePicker createImagePickerInstance();

    void showBucket(@NonNull ImagePicker imagePicker, @NonNull Images images, @NonNull Images.Bucket bucket);

    void showBucketsSelector(@NonNull ImagePicker imagePicker, @NonNull Images images, @NonNull List<Images.Bucket> buckets);

    void hideBucketsSelector();

}
