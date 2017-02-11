package com.okandroid.imagepicker;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by idonans on 2017/2/11.
 */

public class ImagePicker {

    public static final class MimeType {
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

        private MimeType(){
        }
    }

    public static final class Query {

        public static final Uri CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        public static final class Columns {

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

}
