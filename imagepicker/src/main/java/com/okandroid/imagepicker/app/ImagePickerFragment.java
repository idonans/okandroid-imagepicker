package com.okandroid.imagepicker.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.OKAndroidFragment;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.imagepicker.R;

/**
 * Created by idonans on 2017/2/12.
 */

public class ImagePickerFragment extends OKAndroidFragment {

    public static ImagePickerFragment newInstance(Bundle extraParams) {
        Bundle args = new Bundle();
        if (extraParams != null) {
            args.putAll(extraParams);
        }
        ImagePickerFragment fragment = new ImagePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        return inflater.inflate(R.layout.okandroid_imagepicker_view, container, false);
    }

    private ContentLoadingProgressBar mLoadingView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLoadingView = ViewUtil.findViewByID(view, R.id.content_loading_progress_bar);
        if (mLoadingView != null) {
            mLoadingView.show();
        }

        // TODO
    }

}
