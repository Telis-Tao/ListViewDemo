package com.example.xiaoqingtao.listviewdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.example.xiaoqingtao.listviewdemo.R;
import com.example.xiaoqingtao.listviewdemo.interfaces.Request;
import com.example.xiaoqingtao.listviewdemo.others.ImageProcess;

public class NetworkImageView extends ImageView {
    private static final String TAG = "networkimageview";
    private String mUrl;


    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public void setImageUrl(final String url) {
        mUrl = url;
        ImageProcess.getInstance(getContext().getApplicationContext()).post(new Request() {
            @Override
            public void onFinish(Bitmap bitmap) {
                if (bitmap != null) {
                    setImageBitmap(bitmap);
                } else {
                    setImageResource(R.drawable.error);
                }
            }

            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public int getNeededImageWidth() {
                return getWidth();
            }

            @Override
            public void onError() {
                setImageResource(R.drawable.error);
                Log.d(TAG, "onError :" + "load " + url + " failed");
            }
        });
        /*mUrl = url;
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
                });*/
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
//    private Bitmap getBitmap(String url) {
//
//    }

//    public synchronized static void callFinished() {
//        Iterator<Request> iterator = sTaskQueue.iterator();
//        while (iterator.hasNext()) {
//            Request req = iterator.next();
//            if (sCache.get(req.getUrl()) != null) {
//                req.onFinish(sCache.get(req.getUrl()));
//                iterator.remove();
//            }
//        }
//    }

    public void clear() {
        setImageResource(R.drawable.error);
//        clearSubscribe();
    }

    public String getImageUrl() {
        return mUrl;
    }
}
