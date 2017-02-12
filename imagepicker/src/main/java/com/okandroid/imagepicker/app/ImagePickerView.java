package com.okandroid.imagepicker.app;

import android.support.annotation.NonNull;

import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.Images;

/**
 * Created by idonans on 2017/2/12.
 */

public interface ImagePickerView {

    void showLoadingView();

    void hideLoadingView();

    ImagePicker createImagePickerInstance();

    void showImages(@NonNull ImagePicker imagePicker, @NonNull Images images);

}
