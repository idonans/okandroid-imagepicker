package com.okandroid.imagepicker;

import android.support.annotation.Nullable;

/**
 * Created by idonans on 2017/2/11.
 */

public interface ImageInfoFilter {

    boolean accept(@Nullable ImageInfo info);

}
