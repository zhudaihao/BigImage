package com.example.administrator.lsn_8_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;

public class MyView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {
    private Rect rect;
    private BitmapFactory.Options option;
    private GestureDetector gesture;
    private Scroller scroll;
    private int imageW;
    private int imageH;
    private BitmapRegionDecoder brd;
    private int viewW;
    private int viewH;
    private float scale;
    private Bitmap bitmap;

    public MyView(Context context) {
        this(context, null, 0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        rect = new Rect();
        option = new BitmapFactory.Options();
        gesture = new GestureDetector(context, this);
        setOnTouchListener(this);//触摸事件
        scroll = new Scroller(context);//辅助 手势检测器

    }

    //创建方法 调用者传图片 图片以流形式（流形式兼容网络的图片项目的图片）
    public void setImage(InputStream inputStream) {
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, option);//只解码图片信息

        imageW = option.outWidth;
        imageH = option.outHeight;

        option.inMutable = true;
        option.inPreferredConfig = Bitmap.Config.RGB_565;

        //上面设置只解码图片信息 下面就要设置回来，要不就获取不到图片
        option.inJustDecodeBounds = false;
        //解码图片部分
        try {
            brd = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        requestLayout();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewW = getMeasuredWidth();
        viewH = getMeasuredHeight();

        //设置矩形坐标点
        rect.left = 0;
        rect.top = 0;

        rect.right = imageW;
        //获取缩放值
        scale = viewW / (float) imageW;
        rect.bottom = (int) (viewH / scale);

        Log.e("aaron", "----onMeasure---rect" + "---right:" + rect.right + "---bottom:" + rect.bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //图片部分解码为空说明 使用者每月设置图片
        if (null == brd) {
            return;
        }

        //获取bitmap
        //现在设置图片复用内存卡
        option.inBitmap = bitmap;
        Log.e("aaron", "-------屏幕" + "---viewW:" + viewW + "---viewH:" + viewH);
        Log.e("aaron", "-------画rect" + "---top:" + rect.top + "---left:" + rect.left + "---right:" + rect.right + "---bottom:" + rect.bottom);
        bitmap = brd.decodeRegion(rect, option);
        //设置缩放值 的是图片 不是rect
        Matrix matrix = new Matrix();

        matrix.setScale(scale, scale);
        Log.e("aaron", "-------缩放值scale" + scale);

        canvas.drawBitmap(bitmap, matrix, null);

        Log.e("aaron", "-------缩放后rect" + "---w:" + bitmap.getWidth() + "---h:" + bitmap.getHeight());
    }

    //触摸交由手势处理
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gesture.onTouchEvent(event);
    }


    //手指刚按下
    @Override
    public boolean onDown(MotionEvent e) {
        //图片没暂停 手动暂停
        if (scroll.isFinished()) {
            scroll.forceFinished(true);
        }

        return true;
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //滑动矩形显示对应图片的内容
        // 该矩阵在x轴和y轴分别发生的偏移量（很有用，可以上下移动矩阵）
        rect.offset(0, (int) distanceY);

        Log.e("aaron", "-------distanceY" + distanceY);
        //处理两种极端情况 滑到图片顶部 和底部
        Log.e("aaron", "-------top" + rect.top);

        Log.e("aaron", "-------left" + rect.left);

        Log.e("aaron", "-------right" + rect.right);

        Log.e("aaron", "-------bottom" + rect.bottom);
        Log.e("aaron", "------图片-image" + imageH);
        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = (int) (viewH / scale);
        }


        //设置 rect 的top  和bottom 和滑动的距离保存一致
        //通过打印 top 就是图片超出屏幕的距离 ，bottom就是 top的距离+图片高的距离
        if (rect.bottom > imageH) {
            rect.top = (int) (imageH - (viewH / scale));
            rect.bottom = imageH;
        }

        //刷新
        invalidate();
        return false;
    }


    //处理 滑动惯性

    /**
     * velocityY 每秒移动Y像素点
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

/**
 *开始滚动基于一个投掷手势。旅行的距离将
 取决于投掷的初始速度。
 *
 * @param startX滚动的起始点(X)
 * @param startY滚动的起始点(Y)
 * @param velocityX投掷的初始速度(X)，单位为像素/
 *第二。
 *弹丸的初始速度(Y)，单位为像素/
 *第二
 * @param minX最小X值。滚动器不会滚动到此位置
 *点。
 * @param maxX最大值X。滚动器不会滚动到此位置
 *点。
 * @param最小Y值。滚动器不会滚动到此位置
 *点。
 * @param最大Y值。滚动器不会滚动到此位置
 *点。
 */
//调用scroll有个计算惯性滑动方法
        scroll.fling(
                0, rect.top,
                0, (int)- velocityY,
                0, 0,
                0, (int) (imageH-(viewH/scale)));

        return false;
    }


    /**
     * fling方法执行完的 回调
     */
    @Override
    public void computeScroll() {
        //设置rect 的top 和bottom

        //如果滑动结束了就不处理了
        if (scroll.isFinished()) {
            return;
        }

        //true表示还在滑动
        if (scroll.computeScrollOffset()) {
            rect.top = scroll.getCurrY();
            rect.bottom = (int) (rect.top + (viewH / scale));
            invalidate();
        }

    }


    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }


    @Override
    public void onLongPress(MotionEvent e) {

    }


}
