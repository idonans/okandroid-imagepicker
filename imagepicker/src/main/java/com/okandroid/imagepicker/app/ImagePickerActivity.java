package com.okandroid.imagepicker.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.okandroid.boot.app.ext.preload.PreloadActivity;
import com.okandroid.boot.app.ext.preload.PreloadFragment;
import com.okandroid.boot.widget.ContentView;
import com.okandroid.imagepicker.R;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerActivity extends PreloadActivity {

    public static Intent start(Context context, @Nullable Bundle args) {
        Intent starter = new Intent(context, ImagePickerActivity.class);
        if (args != null) {
            starter.putExtras(args);
        }
        return starter;
    }

    private static final String TAG_IMAGE_PICKER_FRAGMENT = "image_picker_fragment";
    private static final int REQUEST_CODE_PERMISSION_IMAGE_PICKER = 1;

    @Override
    protected void initContent() {
        setContentView(new ContentView(this));

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_IMAGE_PICKER_FRAGMENT);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commitNowAllowingStateLoss();
        }

        checkPermissionAndContinue();
    }

    private void checkPermissionAndContinue() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            onRequestPermissionsResult(REQUEST_CODE_PERMISSION_IMAGE_PICKER,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    new int[]{PackageManager.PERMISSION_GRANTED});
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_IMAGE_PICKER);
        }
    }

    @Override
    protected PreloadFragment createPreloadFragment() {
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_IMAGE_PICKER) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    showFailMessage();
                    finish();
                    return;
                }
            }

            addImagePickerFragment();
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void showFailMessage() {
        Toast.makeText(this, "权限被禁止", Toast.LENGTH_LONG).show();
    }

    protected void addImagePickerFragment() {
        Fragment fragment = createImagePickerFragment();
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.okandroid_content, fragment, TAG_IMAGE_PICKER_FRAGMENT)
                    .commitNowAllowingStateLoss();
        }
    }

    protected Fragment createImagePickerFragment() {
        return ImagePickerFragment.newInstance(getIntent().getExtras());
    }

}
