package com.sample.imagepicker;

import android.app.Application;

/**
 * Created by idonans on 2017/2/12.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppInit.init(this);
    }

}
