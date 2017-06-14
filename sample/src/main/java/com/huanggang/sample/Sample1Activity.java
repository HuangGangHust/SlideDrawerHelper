package com.huanggang.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huanggang.slidedrawerhelper.SlideDrawerHelper;

/**
 * 示例1：简单上下滑抽屉效果
 * Created by HuangGang on 2017/6/13.
 */
public class Sample1Activity extends AppCompatActivity implements View.OnClickListener {
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
        FrameLayout flDrag = (FrameLayout) findViewById(R.id.fl_drag);

        // 简单上下滑抽屉效果
        new SlideDrawerHelper.Builder(flDrag, llSlideParent)
                .removeMediumHeightState(true)// 移除中间高度状态
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
