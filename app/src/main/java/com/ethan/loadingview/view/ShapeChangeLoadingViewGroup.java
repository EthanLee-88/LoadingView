package com.ethan.loadingview.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/*******
 * 仿58同城加载缓冲控件
 *
 * created by Ethan Lee
 * on 2021/2/17
 *******/
public class ShapeChangeLoadingViewGroup extends ViewGroup {
    private static final String TAG = "ShapeChangeLoadingViewGroup";
    private ShapeView mShapeView;
    private TextView mLoadTextView;
    private ObjectAnimator translationObjectAnimator, rotationObjectAnimator;

    public ShapeChangeLoadingViewGroup(Context context) {
        this(context, null);
    }

    public ShapeChangeLoadingViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeChangeLoadingViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    private void initData(Context context) {
        removeAllViews();
        mShapeView = new ShapeView(context);
        mLoadTextView = new TextView(context);

        mLoadTextView.setText("拼命加载中...");
        mLoadTextView.setTextColor(Color.BLACK);
        mLoadTextView.setTextSize(spToPx(5));
        mLoadTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        mLoadTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LayoutParams shapeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mShapeView.setLayoutParams(shapeParams);
        addView(mShapeView);
        addView(mLoadTextView);
    }

    public void setText(String text){
        if (mLoadTextView != null) mLoadTextView.setText(text);
    }

    public void setAnimator(){
        if ((translationObjectAnimator != null) || (rotationObjectAnimator != null)) return;
        mShapeView.setRepeat(true);
        float distance = mShapeView.getMeasuredHeight() * 3;
        translationObjectAnimator = ObjectAnimator.ofFloat(mShapeView,
                "translationY", -distance, 0, -distance);
        translationObjectAnimator.setDuration(1500);
        translationObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translationObjectAnimator.setRepeatCount(-1);  // -1 无限循环

        rotationObjectAnimator = ObjectAnimator.ofFloat(mShapeView,
                "rotation", 0f, 360f);
        rotationObjectAnimator.setDuration(1500);
        rotationObjectAnimator.setRepeatCount(-1);  // -1 无限循环

        rotationObjectAnimator.start();
        translationObjectAnimator.start();
    }

    public void release(){  //优化，释放属性动画资源
        if (translationObjectAnimator != null){
            translationObjectAnimator.end();
            translationObjectAnimator.removeAllListeners();
            translationObjectAnimator.cancel();
            translationObjectAnimator = null;
        }
        if (rotationObjectAnimator != null){
            rotationObjectAnimator.end();
            rotationObjectAnimator.removeAllListeners();
            rotationObjectAnimator.cancel();
            rotationObjectAnimator = null;
        }
        if (mShapeView != null) mShapeView.setRepeat(false);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE){
            setAnimator();
        }else if ((visibility == INVISIBLE) || (visibility == GONE)){
            release();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int height = mShapeView.getMeasuredHeight() * 5 + mLoadTextView.getMeasuredHeight();
        int width = mLoadTextView.getMeasuredWidth();
        Log.d(TAG, "height=" + height + "-width=" + width);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int shapeLeft = getWidth() / 2 - mShapeView.getMeasuredWidth() / 2;
        int shapeTop = (int) (mShapeView.getMeasuredHeight() * 3.5);
        int shapeRight = shapeLeft + mShapeView.getMeasuredWidth();
        int shapeBottom = shapeTop + mShapeView.getMeasuredHeight();
        mShapeView.layout(shapeLeft, shapeTop, shapeRight, shapeBottom);
        int textLeft = 0;
        int textTop = getHeight() - mLoadTextView.getMeasuredHeight();
        int textRight = getWidth();
        int textBottom = getHeight();
        mLoadTextView.layout(textLeft, textTop, textRight, textBottom);
        setAnimator();
    }

    private int spToPx(int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
    private int dpToPx(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
