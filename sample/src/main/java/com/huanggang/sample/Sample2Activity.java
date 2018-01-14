package com.huanggang.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huanggang.slidedrawerhelper.SlideDrawerHelper;

/**
 * 示例2：低、中、高三段上下滑抽屉效果
 * Created by HuangGang on 2017/6/13.
 */
public class Sample2Activity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_1);
        init();
    }

    private void init() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        LinearLayout llSlideParent = (LinearLayout) findViewById(R.id.ll_slide_parent);
//        FrameLayout flDrag = (FrameLayout) findViewById(R.id.fl_drag);

        // 低、中、高三段上下滑抽屉效果
        // 可设置拖动布局为整个底部抽屉的父布局，则对于整个底部抽屉的子控件未消费触摸事件的区域，均可拖动或点击
        new SlideDrawerHelper.Builder(llSlideParent, llSlideParent)
                // 滑动总布局处于中间高度时，点击滑动触发布局，设置滑动状态为上滑（默认为下滑）。
                .mediumClickSlideState(SlideDrawerHelper.SlideState.SLIDE_UP)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

            default:
                break;
        }
    }// end onClick

}
