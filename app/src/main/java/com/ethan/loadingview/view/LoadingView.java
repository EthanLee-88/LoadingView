package com.ethan.loadingview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*******
 * 加载缓冲控件
 *
 * created by Ethan Lee
 * on 2021/2/16
 *******/
public class LoadingView extends ViewGroup {
    private static final String TAG = "LoadingView";
    //  左中右三个圆
    private CircleView leftView, middleView, rightView;
    // 属性动画
    private ObjectAnimator leftAnimator, rightAnimator;
    private AnimatorSet mAnimatorSet;

    public LoadingView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRes(context);
    }

    private void initRes(Context context){
        removeAllViews();
        setPadding(8, 8, 8, 8);//px
        leftView = new CircleView(context);
        middleView = new CircleView(context);
        rightView = new CircleView(context);
        addView(leftView);
        addView(rightView);
        addView(middleView);

        // 初始化颜色
        leftView.changeColor(Color.parseColor("#F37146"));
        rightView.changeColor(Color.parseColor("#499FFA"));
        middleView.changeColor(Color.parseColor("#5EFA52"));

        leftView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        middleView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        rightView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        // 布局加载时（onResume），控件还没开始测量，无法拿到View的正确属性，这里延时获取
        post(() -> {
            startAnimator();
        });
    }

    //释放资源
    public void release(){
        Log.d(TAG, "release");
        if (mAnimatorSet != null) {
            mAnimatorSet.end();  // 取消前恢复到原来位置
            mAnimatorSet.cancel();
            mAnimatorSet.removeAllListeners();
            mAnimatorSet = null;
        }
        if (leftAnimator != null){
            leftAnimator.end();
            leftAnimator.removeAllListeners();
            leftAnimator.cancel();
            leftAnimator = null;
        }
        if (rightAnimator != null){
            rightAnimator.end();
            rightAnimator.removeAllListeners();
            rightAnimator.cancel();
            rightAnimator = null;
        }
    }

    // 重写此方法，释放资源
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if ((visibility == GONE) || (visibility == INVISIBLE)){
            release();
        }else if (visibility == VISIBLE){
            startAnimator();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        Log.d(TAG, "onVisibilityChanged=" + changedView.getClass().getSimpleName());
        Log.d(TAG, "onVisibilityChanged=" + (changedView == this.getRootView()) + "-visibility" + visibility);
    }

    private void startAnimator(){
        if ((leftAnimator != null) || (rightAnimator != null)) return;
        float distance = getMeasuredWidth() / 2f - leftView.getMeasuredWidth();
        leftAnimator = ObjectAnimator.ofFloat(leftView,
                "translationX", -distance, 0, -distance);
        leftAnimator.setDuration(1000);
        leftAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        leftAnimator.setRepeatCount(-1);  // -1 无限循环

        rightAnimator = ObjectAnimator.ofFloat(rightView,
                "translationX", distance, 0, distance);
        rightAnimator.setDuration(1000);
        rightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rightAnimator.setRepeatCount(-1);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(leftAnimator, rightAnimator);
        mAnimatorSet.start();
        rightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                // 一轮动画后交换颜色
                int middleColor = middleView.getPaintColor();
                middleView.changeColor(leftView.getPaintColor());
                leftView.changeColor(rightView.getPaintColor());
                rightView.changeColor(middleColor);
                Log.d(TAG, "onAnimationRepeat");
            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = getPaddingLeft() + getPaddingRight() + leftView.getMeasuredWidth() * 9;
        int height = getPaddingTop() + getPaddingBottom() + leftView.getMeasuredHeight();

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 依次摆放
        int left = getMeasuredWidth() / 2 - middleView.getMeasuredWidth() / 2;
        int top = getPaddingTop();
        int right = left + middleView.getMeasuredWidth();
        int bottom = top + middleView.getMeasuredHeight();

        // 初始状态改为全部居中
        middleView.layout(left, top, right, bottom);
        leftView.layout(left, top, right, bottom);
        rightView.layout(left, top, right, bottom);

//        leftView.layout(getPaddingLeft(), getPaddingTop(),
//                getPaddingLeft() + leftView.getMeasuredWidth(), getPaddingTop() + leftView.getMeasuredHeight());
//
//        rightView.layout(getMeasuredWidth() - rightView.getMeasuredWidth() - getPaddingRight(), getPaddingTop(),
//                getMeasuredWidth() - rightView.getPaddingRight(), getPaddingTop() + rightView.getMeasuredHeight());
    }
}
