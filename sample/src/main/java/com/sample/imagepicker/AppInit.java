package com.sample.imagepicker;

import android.content.Context;
import android.util.Log;

import com.okandroid.boot.App;

/**
 * Created by idonans on 2017/2/12.
 */

public class AppInit {

    private static boolean sInit;

    public synchronized static void init(Context context) {
        if (sInit) {
            return;
        }

        context = context.getApplicationContext();
        new App.Config.Builder()
                .setContext(context)
                .setBuildConfigAdapter(new BuildConfigAdapterImpl())
                .build()
                .init();
    }

    public static class BuildConfigAdapterImpl implements App.BuildConfigAdapter {

        @Override
        public int getVersionCode() {
            return BuildConfig.VERSION_CODE;
        }

        @Override
        public String getVersionName() {
            return BuildConfig.VERSION_NAME;
        }

        @Override
        public String getLogTag() {
            return BuildConfig.APPLICATION_ID;
        }

        @Override
        public String getPublicSubDirName() {
            return BuildConfig.APPLICATION_ID;
        }

        @Override
        public String getChannel() {
            return "okandroid";
        }

        @Override
        public int getLogLevel() {
            return Log.VERBOSE;
        }

        @Override
        public boolean isDebug() {
            return BuildConfig.DEBUG;
        }
    }

}
