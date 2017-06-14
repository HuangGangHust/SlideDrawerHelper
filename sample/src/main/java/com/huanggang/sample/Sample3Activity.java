package com.huanggang.sample;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huanggang.slidedrawerhelper.SlideDrawerHelper;
import com.huanggang.slidedrawerhelper.SlideDrawerListener;

/**
 * 示例3：各项配置及增加上下滑联动动画
 * Created by HuangGang on 2017/6/13.
 */
public class Sample3Activity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = Sample3Activity.class.getSimpleName();
    private SlideDrawerHelper mSlideDrawerHelper;
    private TextView tvComeOnBaby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_3);
        init();
    }

    private void init() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        Button btnToMin = (Button) findViewById(R.id.btn_to_min);
        btnToMin.setOnClickListener(this);
        Button btnToMedium = (Button) findViewById(R.id.btn_to_medium);
        btnToMedium.setOnClickListener(this);
        Button btnToMax = (Button) findViewById(R.id.btn_to_max);
        btnToMax.setOnClickListener(this);
        LinearLayout llSlideParent = (LinearLayout) findViewById(R.id.ll_slide_parent);
        FrameLayout flDrag = (FrameLayout) findViewById(R.id.fl_drag);
        tvComeOnBaby = (TextView) findViewById(R.id.tv_come_on_baby);

        // 低、中、高三段上下滑抽屉效果
        mSlideDrawerHelper = new SlideDrawerHelper.Builder(flDrag, llSlideParent)
                // 设置滑动总布局初始化高度状态为最大高度
                .initHeightState(SlideDrawerHelper.SlideParentHeight.MAX_HEIGHT)
                // 设置滑动低、中、高三段的阈值
                .slideThreshold(160, 960, 1920)
                // 设置滑动动画的执行时间
                .animDuration(200)
                .build();

        mSlideDrawerHelper.setmSlideDrawerListener(new SlideDrawerListener() {
            @Override
            public void init(SlideDrawerHelper.SlideParentHeight initHeightState) {

            }

            @Override
            public void onDragUpdate(int currentHeight, int moveDistanceY) {
                if (currentHeight > mSlideDrawerHelper.getMinHeight()) {
                    tvComeOnBaby.setAlpha((float) currentHeight / mSlideDrawerHelper.getMaxHeight());
                } else {
                    tvComeOnBaby.setAlpha(0.1f);
                }
            }

            @Override
            public void onSlideUpdate(int currentHeight, float targetHeight, ValueAnimator animation) {
                long currentPlayTime = animation.getCurrentPlayTime();
                long duration = animation.getDuration();
                Log.d(TAG, "滑动布局目标高度：" + targetHeight
                        + "，滑动布局当前高度：" + currentHeight
                        + "，动画总时间(ms)：" + duration
                        + "，已执行时间(ms)：" + currentPlayTime
                        + "，动画执行进度：" + (float) currentPlayTime / duration);
            }

            @Override
            public Animator slideAttachAnim(int currentHeight, float targetHeight, long animDuration) {
                if (targetHeight > mSlideDrawerHelper.getMediumHeight()) {
                    return ObjectAnimator.ofFloat(tvComeOnBaby, "alpha", 1f);
                }
                if (targetHeight > mSlideDrawerHelper.getMinHeight()) {
                    return ObjectAnimator.ofFloat(tvComeOnBaby, "alpha", 0.5f);
                }
                return ObjectAnimator.ofFloat(tvComeOnBaby, "alpha", 0.1f);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.btn_to_min:
                // 滑至底部
                mSlideDrawerHelper.setSlideParentHeightState(SlideDrawerHelper.SlideParentHeight.MIN_HEIGHT);
                break;

            case R.id.btn_to_medium:
                // 滑至中间
                mSlideDrawerHelper.setSlideParentHeightState(SlideDrawerHelper.SlideParentHeight.MEDIUM_HEIGHT);
                break;

            case R.id.btn_to_max:
                // 滑至顶部
                mSlideDrawerHelper.setSlideParentHeightState(SlideDrawerHelper.SlideParentHeight.MAX_HEIGHT);
                break;

            default:
                break;
        }
    }// end onClick

}
