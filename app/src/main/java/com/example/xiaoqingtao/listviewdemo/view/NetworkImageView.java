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
import com.example.xiaoqingtao.listviewdemo.tools.ImageTools;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NetworkImageView extends ImageView implements Request {
    private static final String TAG = "network image view";
    private static LruCache<String, Bitmap> sCache;

    private static ExecutorService sThreadPool;
    private static Queue<Request> sTaskQueue;
    private static HashSet<String> sRunningRequest;

    public static void setThreadPool(ExecutorService threadPool) {
        sThreadPool = threadPool;
    }

    public static void setCache(LruCache<String, Bitmap> cache) {
        sCache = cache;
    }

    private String mUrl;
    private Subscription mSubscription;

    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (sCache == null) {
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int mCacheSize = maxMemory / 8;
            sCache = new LruCache<String, Bitmap>(mCacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }
//        if (mImages == null) {
//            mImages = new NetworkImageBean();
//        }
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

    public String getImageUrl() {
        return mUrl;
    }

    private void clearSubscribe() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    public void setImageUrl(String url) {
        mUrl = url;
        sTaskQueue.offer(this);
        mSubscription = Observable.just(url)
                .filter(new Func1<String, Boolean>() {//是否已经在running
                    @Override
                    public Boolean call(String url) {
                        if (!sRunningRequest.contains(url)) {
                            sRunningRequest.add(url);
                            return true;
                        }
                        return false;
                    }
                }).filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String url) {//缓存命中，则到这里为止
                        return sCache.get(url) == null;
                    }
                }).filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String url) {//硬盘命中
                        Bitmap bitmap = ImageTools.getBitmapFromDisk(url, getContext());
                        if (url != null) {
                            sCache.put(mUrl, bitmap);
                        }
                        return bitmap == null;
                    }
                }).map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String url) {
                        Bitmap bitmap = getBitmap(url);//从网络上获取
                        sRunningRequest.remove(url);
                        if (bitmap != null) {
                            sCache.put(url, bitmap);//存入内存
                            ImageTools.saveBitmapToDisk(url, bitmap, getContext());//存入硬盘
                        }
                        return bitmap;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
//                        Log.d(TAG, "onCompleted ");
                        callFinished();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "onError " + throwable);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        callFinished();
                    }
                });
    }

    //普通写法：
//    NetworkImageBean bitmap = sCache.get(mUrl);
    //        if (bitmap == null) {
//            //            if (sRunningRequest.contains(mUrl)) {
//            sTaskQueue.add(this);
//            synchronized (sRunningRequest) {
//                if (!sRunningRequest.contains(mUrl)) {
//                    mThreadPool.submit(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Log.i("running", "log for runnable");
//                                URL url = new URL(mUrl);
//                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                                conn.connect();
//                                // get size
//                                BitmapFactory.Options options = new BitmapFactory.Options();
//                                options.inJustDecodeBounds = true;
//                                BitmapFactory.decodeStream(conn.getInputStream(), null,
//                                        options);
//                                int width = options.outWidth;
//                                int height = options.outHeight;
//                                int min = Math.min(width, height);
//                                int scale = Math.max(1, min / getWidth());
//                                options.inJustDecodeBounds = false;
//                                options.inSampleSize = scale;
//                                conn = (HttpURLConnection) url.openConnection();
//                                conn.connect();
//                                NetworkImageBean bitmap = BitmapFactory.decodeStream(conn
// .getInputStream(),
//                                        null,
//                                        options);
//                                callFinished(bitmap, mUrl);
//                                //                        sTaskQueue.remove(this);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//        } else {
//            setImageBitmap(bitmap);
//        }
    private Bitmap getBitmap(String URL) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(URL);
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
            bitmap = BitmapFactory.decodeStream(conn.getInputStream(),
                    null,
                    options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public synchronized static void callFinished() {
        Iterator<Request> iterator = sTaskQueue.iterator();
        while (iterator.hasNext()) {
            Request req = iterator.next();
            if (sCache.get(req.getUrl()) != null) {
                req.onFinish(sCache.get(req.getUrl()));
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
//            bitmap.recycle();
        }
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    public void clear() {
        setImageResource(R.drawable.error);
        clearSubscribe();
    }

}
