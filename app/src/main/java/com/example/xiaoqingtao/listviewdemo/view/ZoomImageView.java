package com.example.xiaoqingtao.listviewdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.xiaoqingtao.listviewdemo.R;

public class ZoomImageView extends ImageView {
    //    for debug
//    private static final String TAG = "ZoomImageView";
    // 设置
    private static final float mDisplayDensity = 25;

    private static final int SINGLE_FINGER = 0;
    private static final int DOUBLE_FINGER = 1;

    private Bitmap mBitmap;
    private boolean isInit = true;
    private GestureDetector mGestureDetector;
    private float mOriginalScale;
    private Matrix savedMatrix = new Matrix();
    private Matrix newMatrix = new Matrix();

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error);
        setOnTouchListener(new ZoomTouchListener());
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;
        mOriginalScale = getWidth() / (float) mBitmap.getWidth();
        resetImage();
    }

    private float getFingersDistance(MotionEvent event) {
        float firstX = event.getX(0);
        float firstY = event.getY(0);
        float lastX = event.getX(1);
        float lastY = event.getY(1);
        return (float) Math.sqrt(Math.pow(lastX - firstX, 2) + Math.pow(lastY - firstY, 2));
    }

    private void resetImage() {
        newMatrix.reset();
        savedMatrix.reset();
        newMatrix.postScale(mOriginalScale, mOriginalScale);
        newMatrix.postTranslate(0, (getHeight() - mBitmap.getHeight() * mOriginalScale) / 2);
        setImageMatrix(newMatrix);
        savedMatrix.set(newMatrix);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isInit) {
            int width = mBitmap.getWidth();
            mOriginalScale = getWidth() / (float) width;
            resetImage();
            isInit = false;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawBitmap(mBitmap, newMatrix, null);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            //捕获Down事件
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //触发双击事件
            resetImage();
            return true;
        }
    }

    private class ZoomTouchListener implements OnTouchListener {
        float oldX;
        float oldY;
        private int touchMode;
        private float oldDistance;
        private float oldCenterX;
        private float oldCenterY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
//                        Log.d(TAG, "onTouch action down");
                    oldX = event.getX();
                    oldY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    savedMatrix.set(newMatrix);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
//                        Log.d(TAG, "onTouch pointer down");
                    oldDistance = getFingersDistance(event);
                    oldCenterX = (event.getX(1) + event.getX(0)) / 2;
                    oldCenterY = (event.getY(1) + event.getY(0)) / 2;
                    touchMode = DOUBLE_FINGER;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
//                        Log.d(TAG, "onTouch pointer up");
                    touchMode = SINGLE_FINGER;
                    savedMatrix.set(newMatrix);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (touchMode == DOUBLE_FINGER) {
                        float distance = getFingersDistance(event);
                        float scale = (distance - oldDistance) /
                                mDisplayDensity + 1;
//                            Log.d(TAG, "onTouch " + scale);
                        if (scale > 0.1 && scale < mOriginalScale * 3) {
                            float centerX = (event.getX(1) + event.getX(0)) / 2;
                            float centerY = (event.getY(1) + event.getY(0)) / 2;
                            newMatrix.set(savedMatrix);
                            newMatrix.postTranslate(centerX - oldCenterX, centerY - oldCenterY);
                            newMatrix.postScale(scale, scale, centerX, centerY);
                            setImageMatrix(newMatrix);
                        }
                    } else if (touchMode == SINGLE_FINGER) {
//                            Log.d(TAG, "onTouch single move");
                        newMatrix.set(savedMatrix);
                        newMatrix.postTranslate(event.getX() - oldX, event.getY() - oldY);
                        setImageMatrix(newMatrix);
                    }
                    break;
            }
            return mGestureDetector.onTouchEvent(event);
        }
    }
}
