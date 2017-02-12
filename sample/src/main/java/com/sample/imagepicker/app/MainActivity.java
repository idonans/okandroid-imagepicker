package com.sample.imagepicker.app;

import android.os.Bundle;
import android.view.View;

import com.okandroid.boot.app.OKAndroidActivity;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.imagepicker.app.ImagePickerActivity;
import com.sample.imagepicker.R;

public class MainActivity extends OKAndroidActivity {

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
        startActivity(ImagePickerActivity.start(this, null));
    }

}
