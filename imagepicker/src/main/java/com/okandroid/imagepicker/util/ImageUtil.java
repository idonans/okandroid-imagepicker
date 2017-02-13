package com.okandroid.imagepicker.util;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by idonans on 2017/2/13.
 */

public class ImageUtil {

    private ImageUtil() {
    }

    public static void showImage(SimpleDraweeView draweeView, Uri uri, int width, int height) {
        if (uri == null) {
            draweeView.setImageURI(uri);
            return;
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setRotationOptions(RotationOptions.autoRotateAtRenderTime())
                .setResizeOptions(new ResizeOptions(width, height))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setCallerContext(null)
                .setImageRequest(imageRequest)
                .setOldController(draweeView.getController())
                .build();
        draweeView.setController(controller);
    }

}
