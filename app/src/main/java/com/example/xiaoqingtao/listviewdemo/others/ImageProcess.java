package com.example.xiaoqingtao.listviewdemo.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.example.xiaoqingtao.listviewdemo.interfaces.Cacheable;
import com.example.xiaoqingtao.listviewdemo.interfaces.Request;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ImageProcess {
    private static final String TAG = "ImageProcess";
    private static Cacheable<String, Bitmap> sCache;
    private static ExecutorService sThreadPool;
    private static Queue<Request> sTaskQueue;
    private static HashSet<String> sRunningRequest;
    private static ImageProcess mImageProcess;
    private static Context mContext;
    private boolean isPermitPost = true;

    private synchronized static void callFinished() {
        new Handler(mContext.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Iterator<Request> iterator = sTaskQueue.iterator();
                while (iterator.hasNext()) {
                    Request req = iterator.next();
                    Bitmap bitmap = sCache.get(req.getUrl());
                    if (bitmap != null) {
                        req.onFinish(bitmap);
                        iterator.remove();
                    }
                }
            }
        });
    }

    public synchronized void post(Request request) {
        if (!isPermitPost) {
            return;
        }
//        Log.d(TAG, "post ");
        sTaskQueue.offer(request);
        String url = request.getUrl();
        Bitmap bitmap = sCache.get(url);
//        Bitmap bitmap = null;
        if (bitmap != null) {
            callFinished();
            return;
        }
        if (!sRunningRequest.contains(url)) {
            sRunningRequest.add(url);
        }

//        if (sThreadPool == empty || sThreadPool.isShutdown()) {
//            synchronized (ImageProcess.class) {
//                if (sThreadPool == empty || sThreadPool.isShutdown()) {
//                    sThreadPool = Executors.newFixedThreadPool(3);
//                }
//            }
//        }
        sThreadPool.submit(new NetworkRunnable(url, request));
    }

    public static ImageProcess getInstance(Context context) {
        if (mImageProcess == null) {
            synchronized (ImageProcess.class) {
                if (mImageProcess == null)
                    mImageProcess = new ImageProcess(context);
            }
        }
        return mImageProcess;
    }

    public ImageProcess(Context context) {
        mContext = context;
        if (sCache == null) {
            sCache = ImageCache.getInstance(context);
        }
        if (sThreadPool == null) {
            sThreadPool = Executors.newFixedThreadPool(3);
        }
        if (sTaskQueue == null) {
            sTaskQueue = new LinkedBlockingQueue<>();
        }
        if (sRunningRequest == null) {
            sRunningRequest = new HashSet<>();
        }
    }

    private class NetworkRunnable implements Runnable {
        private String mUrl;
        private Request mRequest;

        public NetworkRunnable(String url, Request request) {
            mUrl = url;
            mRequest = request;
        }

        @Override
        public void run() {
            Bitmap bitmap = null;
//            Log.d(TAG, "run " + mUrl);
            try {
                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(1500);
                conn.setRequestMethod("GET");
//                conn.setRequestMethod("get");
                conn.connect();
                // get size
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(conn.getInputStream(), null,
                        options);
                int width = options.outWidth;
                int height = options.outHeight;
                int min = Math.min(width, height);
                int scale = Math.max(1, min / mRequest.getNeededImageWidth());
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(1500);
                conn.setRequestMethod("GET");
                conn.connect();
                bitmap = BitmapFactory.decodeStream(conn.getInputStream(),
                        null,
                        options);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "run error");
                mRequest.onError();
            }
            if (bitmap == null) {
                Log.d(TAG, "run bitmap is null!!");
            }
            sCache.put(mUrl, bitmap);
            sRunningRequest.remove(mUrl);
            callFinished();
        }
    }

    public synchronized void clearTasks() {
        sTaskQueue.clear();
        sRunningRequest.clear();
    }

    public void shutDown() {
        if (sThreadPool != null) {
            sThreadPool.shutdown();
            sThreadPool = null;
            mImageProcess = null;
        }
    }

    public void permitSubmit() {
        synchronized (ImageProcess.class) {
            isPermitPost = true;
        }
    }

    public void forbidSubmit() {
        synchronized (ImageProcess.class) {
            isPermitPost = false;
        }
    }

    public boolean isPermit() {
        return isPermitPost;
    }
}
