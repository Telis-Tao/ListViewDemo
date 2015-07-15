package com.example.xiaoqingtao.listviewdemo.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.example.xiaoqingtao.listviewdemo.interfaces.Cacheable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiskCache implements Cacheable<String, Bitmap> {
    private Context mContext;
    private static final String TAG = "image_tools";
    private static DiskCache mDiskCache;

    private DiskCache(Context context) {
        mContext = context;
    }

    public static DiskCache getInstance(Context context) {
        if (mDiskCache == null) {
            synchronized (DiskCache.class) {
                if (mDiskCache == null) {
                    mDiskCache = new DiskCache(context);
                }
            }
        }
        return mDiskCache;
    }

    private void saveBitmapToDisk(String url, Bitmap bitmap) {
//        context.get
        File path = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        assert path != null;
        if (path.isDirectory()) {
            path.mkdirs();
        }
        File tmp = new File(path + File.separator + url
                .hashCode() +
                "" +
                ".png");
        try {
            tmp.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmp));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromDisk(String url) {
        File path = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return BitmapFactory.decodeFile(path + File
                .separator + url.hashCode() + ".png");
    }

    @Override
    public Bitmap get(String s) {
        return getBitmapFromDisk(s);
    }

    @Override
    public void put(String s, Bitmap bitmap) {
        saveBitmapToDisk(s, bitmap);
    }
}
