package com.example.xiaoqingtao.listviewdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.xiaoqingtao.listviewdemo.R;
import com.example.xiaoqingtao.listviewdemo.view.ZoomImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZoomImageViewActivity extends Activity {
    public static final String IMAGE_URL = "image_url";
    private ZoomImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_view);
        mImageView = (ZoomImageView) findViewById(R.id.image_view);
        Intent i = getIntent();
        if (i != null) {
            String url = i.getStringExtra(IMAGE_URL);
            new MyAsyncTask().execute(url);
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... paras) {
            try {
                URL url = new URL(paras[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
