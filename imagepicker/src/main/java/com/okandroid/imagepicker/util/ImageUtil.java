package com.okandroid.imagepicker.util;

import android.graphics.drawable.Animatable;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import me.relex.photodraweeview.PhotoDraweeView;

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

    public static void showImage(final PhotoDraweeView photoDraweeView, Uri uri, int width, int height) {
        if (uri == null) {
            photoDraweeView.setPhotoUri(uri);
            return;
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setRotationOptions(RotationOptions.autoRotateAtRenderTime())
                .setResizeOptions(new ResizeOptions(width, height))
                .build();

        photoDraweeView.setEnableDraweeMatrix(false);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setCallerContext(null)
                .setImageRequest(imageRequest)
                .setOldController(photoDraweeView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        photoDraweeView.setEnableDraweeMatrix(false);
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        photoDraweeView.setEnableDraweeMatrix(true);
                        if (imageInfo != null) {
                            photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                        photoDraweeView.setEnableDraweeMatrix(false);
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);
                        photoDraweeView.setEnableDraweeMatrix(true);
                        if (imageInfo != null) {
                            photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }
                })
                .build();
        photoDraweeView.setController(controller);
    }

}
