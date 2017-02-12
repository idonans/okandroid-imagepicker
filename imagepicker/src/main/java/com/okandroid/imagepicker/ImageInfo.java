package com.okandroid.imagepicker;

/**
 * Created by idonans on 2017/2/11.
 */

public class ImageInfo {

    /**
     * long
     */
    public long id;

    /**
     * text 相册 id
     */
    public String bucketId;

    /**
     * text 相册名
     */
    public String bucketName;

    /**
     * long s 加入相册的时间(这是一个秒值)
     */
    public long dateAdd;

    /**
     * long s 文件的最后修改时间(这是一个秒值)
     */
    public long dateLastModified;

    /**
     * 图片路径 (直接文件读取可能没有权限，建议使用 ContentResolver#openFileDescriptor(Uri, String))
     */
    public String filePath;

    /**
     * double 图片拍照时的 latitude
     */
    public double latitude;

    /**
     * double 图片拍照时的 longitude
     */
    public double longitude;

    /**
     * long ms 拍照时间
     */
    public long dateTaken;

    /**
     * text 图片的描述
     */
    public String desc;

    /**
     * text 图片的 picasa id
     */
    public String picasaId;

    /**
     * int 图片的旋转方向 0, 90, 180, 270
     */
    public int orientation;

    /**
     * long 文件长度
     */
    public long fileLength;

    /**
     * text mime 类型
     */
    public String mimeType;

    /**
     * int 图片的像素宽度
     */
    public int width;

    /**
     * int 图片的像素高度
     */
    public int height;

}
