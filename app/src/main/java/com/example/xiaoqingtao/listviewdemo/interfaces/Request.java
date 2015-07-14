package com.example.xiaoqingtao.listviewdemo.interfaces;

import android.graphics.Bitmap;

public interface Request {
    void onFinish(Bitmap bitmap);

    String getUrl();

    int getNeededImageWidth();

    void onError();
}
