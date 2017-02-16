package com.okandroid.imagepicker;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by idonans on 2017/2/12.
 */

public class Images {

    protected Bucket mAllBucket;
    protected HashMap<String, Bucket> mSubBucketsMap; // 以 bucket id 为 key
    protected List<Bucket> mSubBuckets;
    protected List<ImageInfo> mSelectedImageInfos = new ArrayList<>();

    public Images(List<ImageInfo> imageInfos) {
        mAllBucket = new Bucket();
        mAllBucket.isAll = true;
        mAllBucket.imageInfos = imageInfos;
        mAllBucket.cover = imageInfos.get(0);

        mSubBucketsMap = new HashMap<>();
        for (ImageInfo imageInfo : imageInfos) {
            Bucket old = mSubBucketsMap.get(imageInfo.bucketId);
            if (old != null) {
                old.imageInfos.add(imageInfo);
            } else {
                old = new Bucket();
                old.imageInfos = new ArrayList<>();
                old.imageInfos.add(imageInfo);
                old.cover = imageInfo;
                old.bucketId = imageInfo.bucketId;
                old.bucketName = imageInfo.bucketName;
                mSubBucketsMap.put(imageInfo.bucketId, old);
            }
        }

        mSubBuckets = new ArrayList<>(mSubBucketsMap.values());
        Collections.sort(mSubBuckets, new Comparator<Bucket>() {
            @Override
            public int compare(Bucket o1, Bucket o2) {
                if (o1.bucketName == null) {
                    return -1;
                }
                return o1.bucketName.compareToIgnoreCase(o2.bucketName);
            }
        });
    }

    public boolean isImageSelected(ImageInfo imageInfo) {
        synchronized (mSelectedImageInfos) {
            return mSelectedImageInfos.contains(imageInfo);
        }
    }

    public void selectImage(ImageInfo imageInfo, boolean selected) {
        synchronized (mSelectedImageInfos) {
            if (mSelectedImageInfos.contains(imageInfo) == selected) {
                return;
            }

            if (selected) {
                mSelectedImageInfos.add(imageInfo);
            } else {
                mSelectedImageInfos.remove(imageInfo);
            }
        }
    }

    public List<ImageInfo> getSelectedImages() {
        synchronized (mSelectedImageInfos) {
            return new ArrayList<>(mSelectedImageInfos);
        }
    }

    public ArrayList<String> getSelectedImagesPath() {
        synchronized (mSelectedImageInfos) {
            ArrayList<String> paths = new ArrayList<>(mSelectedImageInfos.size());
            for (ImageInfo info : mSelectedImageInfos) {
                paths.add(info.filePath);
            }
            return paths;
        }
    }

    public int getSelectedImagesSize() {
        synchronized (mSelectedImageInfos) {
            return mSelectedImageInfos.size();
        }
    }

    @NonNull
    public Bucket getAllBucket() {
        return mAllBucket;
    }

    @NonNull
    public List<Bucket> getSubBuckets() {
        return mSubBuckets;
    }

    public static class Bucket {

        public boolean isAll;
        public String bucketId;
        public String bucketName;
        @NonNull
        public ImageInfo cover;
        @NonNull
        public List<ImageInfo> imageInfos;

    }

}
