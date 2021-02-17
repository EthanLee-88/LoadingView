package com.ethan.loadingview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

/*******
 * 三个图形定时切换
 *
 * created by Ethan Lee
 * on 2021/2/17
 *******/
public class ShapeView extends View {
    private static final String TAG = "ShapeView";
    // 定义三种颜色的画笔
    private Paint circlePaint, rectPaint, trianglePaint;
    // 定义三角形路径
    private Path trianglePath;
    //
    private Shape mShape;
    // 是否继续循环变换形状
    private boolean setChangeContinue = true;

    // 三种形状
    private enum Shape {
        circle, rect, triangle
    }

    public ShapeView(Context context) {
        this(context, null);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRes();
    }

    public void setRepeat(boolean setChangeContinue) { // 优化，对外接口
        if ((setChangeContinue) && (!this.setChangeContinue)) changeShape();
        this.setChangeContinue = setChangeContinue;
    }

    private void initRes() {
        circlePaint = getPaint(Color.parseColor("#F37146"));
        rectPaint = getPaint(Color.parseColor("#037BFF"));
        trianglePaint = getPaint(Color.parseColor("#69FA5F"));
        mShape = Shape.circle;
        trianglePath = new Path();
        changeShape();
    }

    private Paint getPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        return paint;
    }

    private void setTrianglePath() {  // 初始化三角形路径
        if (trianglePath != null) {
            trianglePath.moveTo(getWidth() / 2f, 0);
            trianglePath.lineTo(0, (float) (Math.sqrt(3) * getHeight() / 2)); // 等边三角形
            trianglePath.lineTo(getWidth(), (float) (Math.sqrt(3) * getHeight() / 2));
            trianglePath.close();
        }
    }

    private void changeShape() {  // 循环改变形状
        Log.d(TAG, "changeShape");
        postDelayed(() -> {
            switch (mShape) {
                case circle:
                    mShape = Shape.rect;
                    break;
                case rect:
                    mShape = Shape.triangle;
                    break;
                case triangle:
                    mShape = Shape.circle;
                    break;
            }
            invalidate();
            if (setChangeContinue) changeShape(); // 循环
        }, 1000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 确保宽和高保持一致
        if ((widthMode == MeasureSpec.AT_MOST) && (heightMode == MeasureSpec.AT_MOST)) {
            width = dipToPx(22);
            height = dipToPx(22);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = height;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = width;
        }
        int dimension = Math.min(width, height);
        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "getHeight()=" + getHeight());
        setTrianglePath();

//        post(this::setTrianglePath); // Lambda can be replaced with method reference
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mShape) {
            case circle:
                canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getHeight() / 2f, circlePaint);
                break;
            case rect:
                canvas.drawRect(0, 0, getWidth(), getHeight(), rectPaint);
                break;
            case triangle:
                canvas.drawPath(trianglePath, trianglePaint);
                break;
        }
    }

    private int dipToPx(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

}
