package com.example.xiaoqingtao.listviewdemo.interfaces;

import android.graphics.Bitmap;

/**
 * Created by xiaoqing.tao on 2015/7/9.
 */
public interface Request {
    void onFinish(Bitmap bitmap);
    String getUrl();
}
