package com.okandroid.imagepicker;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.os.CancellationSignal;
import android.text.TextUtils;

import com.okandroid.boot.lang.Available;
import com.okandroid.boot.lang.NotAvailableException;
import com.okandroid.boot.util.AvailableUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idonans on 2017/2/11.
 */

public class ImagePicker {

    public interface Params {
        String EXTRA_IMAGE_PICKER_CLASS = "extra_image_picker_class";
    }

    public static class MimeType {
        /**
         * png
         */
        public static final String PNG = "image/png";

        /**
         * jpeg jpg jpe
         */
        public static final String JPEG = "image/jpeg";

        /**
         * gif
         */
        public static final String GIF = "image/gif";

        /**
         * webp
         */
        public static final String WEBP = "image/webp";

        private MimeType() {
        }
    }

    public static class Query {

        public static final Uri CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        public static class Columns {

            /**
             * long
             */
            public static final String ID = MediaStore.Images.ImageColumns._ID;
            /**
             * text 相册 id
             */
            public static final String BUCKET_ID = MediaStore.Images.ImageColumns.BUCKET_ID;
            /**
             * text 相册名
             */
            public static final String BUCKET_DISPLAY_NAME = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
            /**
             * long s 加入相册的时间(这是一个秒值)
             */
            public static final String DATE_ADDED = MediaStore.Images.ImageColumns.DATE_ADDED;
            /**
             * long s 文件的最后修改时间(这是一个秒值)
             */
            public static final String DATE_MODIFIED = MediaStore.Images.ImageColumns.DATE_MODIFIED;
            /**
             * 图片路径 (直接文件读取可能没有权限，建议使用 ContentResolver#openFileDescriptor(Uri, String))
             */
            public static final String FILE_PATH = MediaStore.Images.ImageColumns.DATA;
            /**
             * double 图片拍照时的 latitude
             */
            public static final String LATITUDE = MediaStore.Images.ImageColumns.LATITUDE;
            /**
             * double 图片拍照时的 longitude
             */
            public static final String LONGITUDE = MediaStore.Images.ImageColumns.LONGITUDE;
            /**
             * long ms 拍照时间
             */
            public static final String DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN;
            /**
             * text 图片的描述
             */
            public static final String DESC = MediaStore.Images.ImageColumns.DESCRIPTION;
            /**
             * text 图片的 picasa id
             */
            public static final String PICASA_ID = MediaStore.Images.ImageColumns.PICASA_ID;
            /**
             * int 图片的旋转方向 0, 90, 180, 270
             */
            public static final String ORIENTATION = MediaStore.Images.ImageColumns.ORIENTATION;
            /**
             * long 文件长度
             */
            public static final String FILE_SIZE = MediaStore.Images.ImageColumns.SIZE;
            /**
             * text mime 类型
             */
            public static final String MIME_TYPE = MediaStore.Images.ImageColumns.MIME_TYPE;
            /**
             * int 图片的像素宽度
             */
            public static final String IMAGE_WIDTH = MediaStore.Images.ImageColumns.WIDTH;
            /**
             * int 图片的像素高度
             */
            public static final String IMAGE_HEIGHT = MediaStore.Images.ImageColumns.HEIGHT;

            public static final String[] ALL = {
                    ID,
                    BUCKET_ID,
                    BUCKET_DISPLAY_NAME,
                    DATE_ADDED,
                    DATE_MODIFIED,
                    FILE_PATH,
                    LATITUDE,
                    LONGITUDE,
                    DATE_TAKEN,
                    DESC,
                    PICASA_ID,
                    ORIENTATION,
                    FILE_SIZE,
                    MIME_TYPE,
                    IMAGE_WIDTH,
                    IMAGE_HEIGHT
            };

        }

    }

    public Cursor createQueryCursor(ContentResolver resolver) {
        Uri uri = Query.CONTENT_URI;
        String[] projection = Query.Columns.ALL;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = Query.Columns.DATE_ADDED + " desc"; // 按添加时间排序
        CancellationSignal cancellationSignal = null;
        return ContentResolverCompat.query(resolver, uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    @Nullable
    public List<ImageInfo> parse(Cursor cursor, Available available) {
        List<ImageInfo> images = new ArrayList<>();
        try {
            AvailableUtil.mustAvailable(available);
            if (cursor.moveToFirst()) {
                ImageInfoFilter imageInfoFilter = createImageInfoFilter();

                int indexId = cursor.getColumnIndex(Query.Columns.ID);
                int indexBucketId = cursor.getColumnIndex(Query.Columns.BUCKET_ID);
                int indexBucketName = cursor.getColumnIndex(Query.Columns.BUCKET_DISPLAY_NAME);
                int indexDateAdd = cursor.getColumnIndex(Query.Columns.DATE_ADDED);
                int indexDateModified = cursor.getColumnIndex(Query.Columns.DATE_MODIFIED);
                int indexFilePath = cursor.getColumnIndex(Query.Columns.FILE_PATH);
                int indexLatitude = cursor.getColumnIndex(Query.Columns.LATITUDE);
                int indexLongitude = cursor.getColumnIndex(Query.Columns.LONGITUDE);
                int indexDateTaken = cursor.getColumnIndex(Query.Columns.DATE_TAKEN);
                int indexDesc = cursor.getColumnIndex(Query.Columns.DESC);
                int indexPicasaId = cursor.getColumnIndex(Query.Columns.PICASA_ID);
                int indexOrientation = cursor.getColumnIndex(Query.Columns.ORIENTATION);
                int indexFileSize = cursor.getColumnIndex(Query.Columns.FILE_SIZE);
                int indexMimeType = cursor.getColumnIndex(Query.Columns.MIME_TYPE);
                int indexImageWidth = cursor.getColumnIndex(Query.Columns.IMAGE_WIDTH);
                int indexImageHeight = cursor.getColumnIndex(Query.Columns.IMAGE_HEIGHT);
                do {
                    ImageInfo imageInfo = new ImageInfo();
                    if (indexId >= 0) {
                        imageInfo.id = cursor.getLong(indexId);
                    }
                    if (indexBucketId >= 0) {
                        imageInfo.bucketId = cursor.getString(indexBucketId);
                    }
                    if (indexBucketName >= 0) {
                        imageInfo.bucketName = cursor.getString(indexBucketName);
                    }
                    if (indexDateAdd >= 0) {
                        imageInfo.dateAdd = cursor.getLong(indexDateAdd);
                    }
                    if (indexDateModified >= 0) {
                        imageInfo.dateLastModified = cursor.getLong(indexDateModified);
                    }
                    if (indexFilePath >= 0) {
                        imageInfo.filePath = cursor.getString(indexFilePath);
                    }
                    if (indexLatitude >= 0) {
                        imageInfo.latitude = cursor.getDouble(indexLatitude);
                    }
                    if (indexLongitude >= 0) {
                        imageInfo.longitude = cursor.getDouble(indexLongitude);
                    }
                    if (indexDateTaken >= 0) {
                        imageInfo.dateTaken = cursor.getLong(indexDateTaken);
                    }
                    if (indexDesc >= 0) {
                        imageInfo.desc = cursor.getString(indexDesc);
                    }
                    if (indexPicasaId >= 0) {
                        imageInfo.picasaId = cursor.getString(indexPicasaId);
                    }
                    if (indexOrientation >= 0) {
                        imageInfo.orientation = cursor.getInt(indexOrientation);
                    }
                    if (indexFileSize >= 0) {
                        imageInfo.fileLength = cursor.getLong(indexFileSize);
                    }
                    if (indexMimeType >= 0) {
                        imageInfo.mimeType = cursor.getString(indexMimeType);
                    }
                    if (indexImageWidth >= 0) {
                        imageInfo.width = cursor.getInt(indexImageWidth);
                    }
                    if (indexImageHeight >= 0) {
                        imageInfo.height = cursor.getInt(indexImageHeight);
                    }

                    AvailableUtil.mustAvailable(available);

                    if (imageInfoFilter == null) {
                        images.add(imageInfo);
                    } else {
                        if (imageInfoFilter.accept(imageInfo)) {
                            images.add(imageInfo);
                        }
                    }
                } while (cursor.moveToNext());
            }
        } catch (Throwable e) {
            if (e instanceof NotAvailableException) {
                // ignore
            } else {
                e.printStackTrace();
            }
        }
        return images;
    }

    public ImageInfoFilter createImageInfoFilter() {
        return new SampleImageInfoFilter(new String[]{MimeType.JPEG, MimeType.PNG});
    }

    public static class SampleImageInfoFilter implements ImageInfoFilter {

        private final String[] mMimeTypes;

        public SampleImageInfoFilter(String[] mMimeTypes) {
            this.mMimeTypes = mMimeTypes;
        }

        @Override
        public boolean accept(@Nullable ImageInfo info) {
            if (info == null
                    || info.fileLength <= 0
                    || info.width <= 0
                    || info.height <= 0) {
                return false;
            }

            if (mMimeTypes == null || mMimeTypes.length <= 0) {
                return true;
            }

            String mimeType = info.mimeType;
            for (String accept : mMimeTypes) {
                if (TextUtils.isEmpty(accept)) {
                    if (TextUtils.isEmpty(mimeType)) {
                        return true;
                    }
                } else {
                    if (accept.equalsIgnoreCase(mimeType)) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

}
