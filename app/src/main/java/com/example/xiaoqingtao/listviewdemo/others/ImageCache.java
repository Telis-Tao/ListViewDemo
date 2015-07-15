package com.example.xiaoqingtao.listviewdemo.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.example.xiaoqingtao.listviewdemo.interfaces.Cacheable;

public class ImageCache implements Cacheable<String, Bitmap> {
    private static ImageCache mCache;
    private DiskCache mDiskCache;
    private LruCache<String, Bitmap> mLruCache;

    public static ImageCache getInstance(Context context) {
        if (mCache == null) {
            synchronized (ImageCache.class) {
                if (mCache == null) {
                    mCache = new ImageCache(context);
                }
            }
        }
        return mCache;
    }

    private ImageCache(Context context) {
        mDiskCache = DiskCache.getInstance(context);
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public Bitmap get(String s) {
        Bitmap bitmap = mLruCache.get(s);
        if (bitmap == null) {
            bitmap = mDiskCache.get(s);
        }
        return bitmap;
    }

    @Override
    public void put(String s, Bitmap bitmap) {
        if (mLruCache.get(s) == null) {
            mLruCache.put(s, bitmap);
        }
        if (mDiskCache.get(s) == null) {
            mDiskCache.put(s, bitmap);
        }
    }
}
