package com.example.xiaoqingtao.listviewdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.xiaoqingtao.listviewdemo.R;
import com.example.xiaoqingtao.listviewdemo.interfaces.Request;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xiaoqing.tao on 2015/7/9.
 */
public class NetworkImageView extends ImageView implements Request {
    private static LruCache<String, Bitmap> cache;
    private static ExecutorService mThreadPool;
    private static Queue<Request> mTaskQueue;
    private static HashSet<String> mRunningRequest;
    private String mUrl;

    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (cache == null) {
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int mCacheSize = maxMemory / 8;
            cache = new LruCache<String, Bitmap>(mCacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }
        if (mThreadPool == null) {
            mThreadPool = Executors.newFixedThreadPool(3);
        }
        if (mTaskQueue == null) {
            mTaskQueue = new LinkedBlockingQueue<>();
        }
        if (mRunningRequest == null) {
            mRunningRequest = new HashSet<>();
        }
    }

    public String getImageUrl() {
        return mUrl;
    }

    public void setImageUrl(String url) {
        mUrl = url;
        Bitmap bitmap = cache.get(mUrl);
        if (bitmap == null) {
//            if (mRunningRequest.contains(mUrl)) {
            synchronized (this) {
                mTaskQueue.add(this);
                if (!mRunningRequest.contains(mUrl)) {
                    mThreadPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.i("running", "log for runnable");
                                URL url = new URL(mUrl);
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.connect();
                                // get size
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeStream(conn.getInputStream(), null,
                                        options);
                                int width = options.outWidth;
                                int height = options.outHeight;
                                int min = Math.min(width, height);
                                int scale = Math.max(1, min / getWidth());
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = scale;
                                conn = (HttpURLConnection) url.openConnection();
                                conn.connect();
                                Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream(),
                                        null,
                                        options);
                                callFinished(bitmap, mUrl);
//                        mTaskQueue.remove(this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } else {
            setImageBitmap(bitmap);
        }
    }

    public static void callFinished(Bitmap bitmap, String url) {
        if (bitmap != null) {
            cache.put(url, bitmap);
        }
        synchronized (mRunningRequest) {
            mRunningRequest.remove(url);
        }
        Iterator<Request> iterator = mTaskQueue.iterator();
        while (iterator.hasNext()) {
            Request req = iterator.next();
            if (cache.get(req.getUrl()) != null) {
                req.onFinish(cache.get(req.getUrl()));
                iterator.remove();
            }
        }
    }

    @Override
    public void onFinish(final Bitmap bitmap) {
        if (bitmap != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    setImageBitmap(bitmap);
                }
            });
        }
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    public void clearImage() {
        setImageResource(R.drawable.error);
    }

//    @Override
//    public boolean onFinish() {
//        Bitmap bitmap = cache.get(mUrl);
//        if (bitmap != null) {
//            setImageBitmap(bitmap);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public String getUrl() {
//        return this.mUrl;
//    }
}
