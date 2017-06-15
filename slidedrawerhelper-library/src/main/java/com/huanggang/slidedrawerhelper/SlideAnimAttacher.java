package com.huanggang.slidedrawerhelper;

import android.animation.Animator;
import android.animation.ValueAnimator;

/**
 * 抽屉滑动动画附加类
 * Created by HuangGang on 2017/6/15.
 */
public abstract class SlideAnimAttacher implements SlideDrawerListener {

    @Override
    public void init(SlideDrawerHelper.SlideParentHeight initHeightState) {

    }

    @Override
    public void onDragUpdate(int currentHeight, int moveDistanceY) {

    }

    @Override
    public void onSlideUpdate(int currentHeight, float targetHeight, ValueAnimator animation) {

    }

    @Override
    public void onSlideStart(int currentHeight, float targetHeight, Animator animation) {

    }

    @Override
    public void onSlideEnd(int currentHeight, float targetHeight, Animator animation) {

    }
}
