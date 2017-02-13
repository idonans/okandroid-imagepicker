package com.sample.imagepicker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.okandroid.boot.app.OKAndroidActivity;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.imagepicker.ImagePicker;
import com.okandroid.imagepicker.app.ImagePickerActivity;
import com.sample.imagepicker.R;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends OKAndroidActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_IMAGE_PICK = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isAvailable()) {
            return;
        }

        setContentView(R.layout.sample_main_view);
        View test = ViewUtil.findViewByID(this, R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    private void test() {
        startActivityForResult(ImagePickerActivity.start(this, null), REQUEST_CODE_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE_PICK) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> mSelectedImages = data.getStringArrayListExtra(ImagePicker.Params.EXTRA_OUT_IMAGES);
                Log.d(TAG + " selected images " + Arrays.deepToString(mSelectedImages.toArray()));
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
