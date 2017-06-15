package com.huanggang.slidedrawerhelper;

import android.animation.Animator;
import android.animation.ValueAnimator;

/**
 * {@link SlideDrawerHelper} 滑动抽屉监听接口
 * Created by HuangGang on 2017/6/13.
 * <p>
 * 有问题欢迎联系“huangganghust@qq.com”
 */
public interface SlideDrawerListener {
    /**
     * {@link SlideDrawerHelper} 初始化回调
     *
     * @param initHeightState 初始化高度状态
     */
    void init(SlideDrawerHelper.SlideParentHeight initHeightState);

    /**
     * 拖动更新回调
     *
     * @param currentHeight 滑动总布局当前高度
     * @param moveDistanceY 当前触摸点在Y方向移动距离
     */
    void onDragUpdate(int currentHeight, int moveDistanceY);

    /**
     * 滑动动画更新回调
     *
     * @param currentHeight 滑动总布局当前高度
     * @param targetHeight  滑动目标高度
     * @param animation     当前滑动动画
     */
    void onSlideUpdate(int currentHeight, float targetHeight, ValueAnimator animation);

    /**
     * 滑动动画执行时需要伴随执行的动画。内部使用{@link android.animation.AnimatorSet.Builder#with(Animator)}实现
     * <p>
     * 不需要伴随执行的动画时，返回 null 即可；需要时实现此方法并将对应动画返回。
     * <p>
     * 注意：若动画执行时间与滑动动画一致，无需另行设置动画时间。
     * <p>
     *
     * @param currentHeight 滑动总布局当前高度
     * @param targetHeight  滑动目标高度
     * @param animDuration  滑动动画时间
     * @return 滑动动画执行时需要伴随执行的动画。不需要时，返回null即可；需要时实现此方法并将对应动画返回。
     */
    Animator slideAttachAnim(int currentHeight, float targetHeight, long animDuration);

    /**
     * 滑动动画更新回调
     *
     * @param currentHeight 滑动总布局当前高度
     * @param targetHeight  滑动目标高度
     * @param animation     当前滑动动画
     */
    void onSlideStart(int currentHeight, float targetHeight, Animator animation);

    /**
     * 滑动动画更新回调
     *
     * @param currentHeight 滑动总布局当前高度
     * @param targetHeight  滑动目标高度
     * @param animation     当前滑动动画
     */
    void onSlideEnd(int currentHeight, float targetHeight, Animator animation);
}
