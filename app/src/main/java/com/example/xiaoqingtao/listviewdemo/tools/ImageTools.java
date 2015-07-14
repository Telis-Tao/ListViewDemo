package com.example.xiaoqingtao.listviewdemo.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageTools {
    private static final String TAG = "image_tools";

    public static void saveBitmapToDisk(String url, Bitmap bitmap, Context context) {
//        context.get
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    public static Bitmap getBitmapFromDisk(String url, Context context) {
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return BitmapFactory.decodeFile(path + File
                .separator + url.hashCode() + ".png");
    }
}
