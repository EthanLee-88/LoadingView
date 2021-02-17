package com.ethan.loadingview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

/*******
 * 画一个小圆圈
 *
 * created by Ethan Lee
 * on 2021/2/16
 *******/
public class CircleView extends View {
    private Paint mPaint;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRes();
    }

    private void initRes(){
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);
    }

    public void changeColor(int color){
        mPaint.setColor(color);
        invalidate();
    }

    public int getPaintColor(){
        if (mPaint != null) return mPaint.getColor();
        return Color.RED;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 老套路 ，宽高一致
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if ((widthMode == MeasureSpec.AT_MOST) && (heightMode == MeasureSpec.AT_MOST)){
            // 宽高都自适应
            setMeasuredDimension(dipToPx(12), dipToPx(12));
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(height, height);
        }else if (heightMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(width, width);
        }else {
            int maxOne = Math.max(width, height);
            setMeasuredDimension(maxOne, maxOne);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getHeight() / 2, getHeight() / 2, getHeight() / 2, mPaint);
    }

    private int dipToPx(int dip){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                getResources().getDisplayMetrics());
    }
}
