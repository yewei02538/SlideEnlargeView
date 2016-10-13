package me.weyye.slideenlargelayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class SlideEnlargeLayout extends FrameLayout {
    public final static String TAG = "slidelayout";
    private RectF mTargetRectf;
    private ViewGroup rootView;
    private float mDownY;
    private float mDownX;
    private float mScaleValue;
    private ValueAnimator valueAnimator;

    public SlideEnlargeLayout(Context context) {
        this(context, null);
    }

    public SlideEnlargeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideEnlargeLayout(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView = (ViewGroup) View.inflate(context, R.layout.view_slidelayout, null);
        LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ll);
        final TextView tvTime = (TextView) rootView.findViewById(R.id.tvTime);
        ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //可像外面暴露接口
                Toast.makeText(context,"当前时间:"+tvTime.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        addView(rootView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }


    private View findTargetView(ViewGroup viewGroup, float x, float y) {
        int childCount = viewGroup.getChildCount();
        // 迭代查找被点击的目标视图
        for (int i = 0; i < childCount; i++) {
            Log.i(TAG, "viewGroup:" + viewGroup.toString());
            View childView = viewGroup.getChildAt(i);
            if (childView instanceof ViewGroup) {
                return findTargetView((ViewGroup) childView, x, y);
            } else if (isInFrame(childView, x, y)) { // 否则判断该点是否在该View的frame内
                return childView;
            }
        }

        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View targetView = findTargetView(rootView, event.getX(), event.getY());
                if (targetView instanceof ImageView) {
                    //触摸的是图片 拦截
                    mDownY = event.getY();
                    mDownX = event.getX();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float  diffXValue = Math.abs(event.getX() - mDownX);
                float  diffYValue =Math.abs( event.getY() - mDownY);

                //对比移动的距离，判断移动的是x还是y
                mScaleValue = 1+Math.abs( Math.max(diffXValue,diffYValue)* 0.0005f);
                rootView.setScaleX(mScaleValue);
                rootView.setScaleY(mScaleValue);
                break;
            case MotionEvent.ACTION_UP:
                //up时候还原原始大小
                valueAnimator = ValueAnimator.ofFloat(mScaleValue, 1);
                valueAnimator.setDuration(300);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        rootView.setScaleX((Float) animation.getAnimatedValue());
                        rootView.setScaleY((Float) animation.getAnimatedValue());
                    }
                });
                valueAnimator.start();

                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 点击的某个坐标点是否在View的内部
     *
     * @param touchView
     * @param x         被点击的x坐标
     * @param y         被点击的y坐标
     * @return 如果点击的坐标在该view内则返回true, 否则返回false
     */
    private boolean isInFrame(View touchView, float x, float y) {
        initViewRect(touchView);
        return mTargetRectf.contains(x, y);
    }

    /**
     * 获取点中的区域,屏幕绝对坐标值,这个高度值也包含了状态栏和标题栏高度
     *
     * @param touchView
     */
    private void initViewRect(View touchView) {
        int[] location = new int[2];
        touchView.getLocationOnScreen(location);
        // 视图的区域
        mTargetRectf = new RectF(location[0], location[1], location[0]
                + touchView.getWidth(), location[1] + touchView.getHeight());

    }

}
