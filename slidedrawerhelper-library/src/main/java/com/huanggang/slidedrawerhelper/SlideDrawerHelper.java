package com.huanggang.slidedrawerhelper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

/**
 * 上下滑动处理类
 *
 * @author HuangGang
 *         <p>
 *         有问题欢迎联系“huangganghust@qq.com”
 */
public class SlideDrawerHelper {
    private static final String TAG = SlideDrawerHelper.class.getSimpleName();
    /**
     * minHeight：滑动布局最小显示高度；
     * mediumHeight：滑动布局中等显示高度；
     * maxHeight：滑动布局最大显示高度；
     */
    private final int minHeight, mediumHeight, maxHeight;
    private long animDuration = 200;// 动画持续时间
    // 滑动菜单高度状态
    private SlideParentHeight mSlideParentHeight;
    /**
     * 滑动菜单高度状态为中间时，点击滑动布局，对应滑动状态（SlideState.SLIDE_DOWN：向下滑 或 SlideState.SLIDE_UP：向上滑）。
     * 默认为SlideState.SLIDE_DOWN。
     */
    private SlideState mediumClickSlideState = SlideState.SLIDE_DOWN;
    private boolean removeMediumHeightState = false;// 是否移除滑动布局中间高度状态。默认为false
    /**
     * 点击拖动布局是否会自动滑动。默认为true。
     * 若需要布局处于中间时，点击布局也不自动滑动，可设置{@link Builder#mediumClickSlideState(SlideState)}为
     * {@link SlideState#CLICK_UP}或{@link SlideState#CLICK_DOWN}。
     */
    private boolean clickSlidable = true;
    /**
     * 滑动总布局
     */
    private ViewGroup slideParentLayout;
    private ViewGroup.LayoutParams slideParentParams;
    private float downY;
    private float downRawY;
    private SlideDrawerListener mSlideDrawerListener;

    /**
     * @param builder SlideDrawerHelper建造者
     */
    private SlideDrawerHelper(@NonNull Builder builder) {
        ViewGroup dragLayout = builder.dragLayout;// 滑动触发布局，可拖动或点击
        this.slideParentLayout = builder.slideParentLayout;
        this.minHeight = builder.minHeight;
        this.mediumHeight = builder.mediumHeight;
        this.maxHeight = builder.maxHeight;
        this.animDuration = builder.animDuration;
        this.removeMediumHeightState = builder.removeMediumHeightState;
        this.clickSlidable = builder.clickSlidable;
        this.mediumClickSlideState = builder.mediumClickSlideState;
        dragLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleSlide(event);
                return true;
            }
        });
    }

    /**
     * 初始化滑动总布局高度。
     * <p>
     * 若已设置移除滑动布局中间高度状态{@link Builder#removeMediumHeightState(boolean true)}，
     * 仍设置初始化高度状态为SlideParentHeight.MEDIUM_HEIGHT，则抛出IllegalArgumentException异常
     *
     * @param initHeightState 初始化高度状态。
     *                        <p>
     *                        MIN_HEIGHT：{@link #getMinHeight()}；
     *                        MEDIUM_HEIGHT：{@link #getMediumHeight()}；
     *                        MAX_HEIGHT：{@link #getMaxHeight()}。
     */
    private SlideDrawerHelper initLayoutHeight(@NonNull SlideParentHeight initHeightState) {
        if (removeMediumHeightState && initHeightState == SlideParentHeight.MEDIUM_HEIGHT) {
            throw new IllegalArgumentException("You can't set SlideParentHeight.MEDIUM_HEIGHT after calling removeMediumHeightState(true).");
        }
        mSlideParentHeight = initHeightState;
//        int initHeight = getInitHeight(initHeightState);

        // 滑动总布局高度赋值给panlHeight
        slideParentParams = slideParentLayout.getLayoutParams();
        slideParentParams.height = getInitHeight(initHeightState);
        slideParentLayout.setLayoutParams(slideParentParams);

        if (null != mSlideDrawerListener) {
            mSlideDrawerListener.init(initHeightState);
        }
        return this;
    }

    /**
     * 获取滑动标题栏和滑动总布局初始化高度
     *
     * @param initHeightState 初始化高度状态
     */
    private int getInitHeight(SlideParentHeight initHeightState) {
        if (initHeightState == SlideParentHeight.MAX_HEIGHT) {
            return getMaxHeight();
        }

        if (initHeightState == SlideParentHeight.MEDIUM_HEIGHT) {
            return getMediumHeight();
        }

        return getMinHeight();
    }

    /**
     * 滑动处理
     */
    private void handleSlide(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 单点触摸
                downRawY = event.getRawY();
                downY = event.getY();
                float moveY;
                break;

            case MotionEvent.ACTION_MOVE:// 触摸点移动
                moveY = event.getY();// 获取屏幕坐标
                // 当前触摸点在Y方向移动距离
                int moveDistanceY = Float.valueOf(downY - moveY).intValue();
                // 拖动布局滑动过程中动画效果
                dragAnim(moveDistanceY);
                break;

            case MotionEvent.ACTION_UP:// 手指离开屏幕
                // 获取屏幕坐标
                float upRawY = event.getRawY();
                // 手离开屏幕时，滑动状态判断
                SlideState currentSlideState = slideStateJudge(downRawY, upRawY);
                slideAnim(currentSlideState);
                break;

            default:
                break;
        }
    }

    /**
     * 拖动布局滑动过程中动画：采用动态设置布局属性实现。
     *
     * @param moveDistanceY 当前触摸点在Y方向移动距离
     */
    private void dragAnim(int moveDistanceY) {
        slideParentParams = getSlideParentParams();
        int currentHeight = slideParentParams.height + moveDistanceY;

        // 超出阈值处理
        if (currentHeight < getMinHeight()) {
            slideParentParams.height = getMinHeight();
        } else if (currentHeight > getMaxHeight()) {
            slideParentParams.height = getMaxHeight();
        } else {
            slideParentParams.height = currentHeight;
        }
        setSlideParentParams(slideParentParams);

        // 拖动动画对外回调
        if (null != mSlideDrawerListener) {
            mSlideDrawerListener.onDragUpdate(slideParentParams.height, moveDistanceY);
        }
    }

    /**
     * 布局滑动状态判断
     *
     * @param downRawY 手指按下点的屏幕坐标
     * @param upRawY   手指离开点的屏幕坐标
     */
    private SlideState slideStateJudge(float downRawY, float upRawY) {
        if (upRawY - downRawY > 5) {// 下滑
            return SlideState.SLIDE_DOWN;
        }

        if (upRawY - downRawY < -5) {// 上滑
            return SlideState.SLIDE_UP;
        }

        // 若手离开屏幕时，y坐标基本没改变（变动绝对值小于5px），说明没有滑动，只是点击
        if (mSlideParentHeight == SlideParentHeight.MIN_HEIGHT) {// 点击时，滑动布局处于底部，则上滑
            return SlideState.CLICK_UP;
        }

        if (mSlideParentHeight == SlideParentHeight.MAX_HEIGHT) {// 点击时，滑动布局处于顶部，则下滑
            return SlideState.CLICK_DOWN;
        }

        // 点击时，滑动布局处于中间，对应滑动状态
        return mediumClickSlideState;
    }

    /**
     * 滑动动画
     *
     * @param currentSlideState 当前滑动状态
     */
    private void slideAnim(SlideState currentSlideState) {
        if (currentSlideState == SlideState.SLIDE_UP || (clickSlidable && currentSlideState == SlideState.CLICK_UP)) {
            // 上滑
//            handleUpSlide(upRawY);
            handleUpSlide(slideParentParams.height);
            return;
        }

        if (currentSlideState == SlideState.SLIDE_DOWN || (clickSlidable && currentSlideState == SlideState.CLICK_DOWN)) {
            // 下滑
//            handleDownSlide(upRawY);
            handleDownSlide(slideParentParams.height);
        }
    }

    /**
     * 处理上滑
     *
     * @param rawY 屏幕点Y坐标
     */
    private void handleUpSlide(float rawY) {
        // 已取消滑动布局中间高度状态
        if (removeMediumHeightState) {
//            if (rawY > screenHeight - getMaxHeight() && rawY < screenHeight) {
            if (rawY > getMinHeight() && rawY < getMaxHeight()) {
                slideAnimation(getMaxHeight());
                mSlideParentHeight = SlideParentHeight.MAX_HEIGHT;
            }
            return;
        }

//        if (rawY > screenHeight - getMaxHeight() && rawY <= screenHeight - getMediumHeight() + getMinHeight()) {
        if (rawY >= getMediumHeight() && rawY < getMaxHeight()) {
            slideAnimation(getMaxHeight());
            mSlideParentHeight = SlideParentHeight.MAX_HEIGHT;
            return;
        }

        if (rawY > getMinHeight() && rawY < getMediumHeight()) {
            slideAnimation(getMediumHeight());
            mSlideParentHeight = SlideParentHeight.MEDIUM_HEIGHT;
        }
    }

    /**
     * 处理下滑
     *
     * @param rawY 屏幕点Y坐标
     */
    private void handleDownSlide(float rawY) {
        if (removeMediumHeightState) {// 已取消滑动布局中间高度状态
//            if (rawY >= screenHeight - getMaxHeight() && rawY < screenHeight) {
            if (rawY > getMinHeight() && rawY < getMaxHeight()) {
                slideAnimation(getMinHeight());
                mSlideParentHeight = SlideParentHeight.MIN_HEIGHT;
            }
            return;
        }

//        if (rawY >= screenHeight - getMaxHeight() && rawY < screenHeight - getMediumHeight()) {
        if (rawY > getMediumHeight() && rawY <= getMaxHeight()) {
            slideAnimation(getMediumHeight());
            mSlideParentHeight = SlideParentHeight.MEDIUM_HEIGHT;
            return;
        }

//        if (rawY >= screenHeight - getMediumHeight() && rawY < screenHeight) {
        if (rawY > getMinHeight() && rawY <= getMediumHeight()) {
            slideAnimation(getMinHeight());
            mSlideParentHeight = SlideParentHeight.MIN_HEIGHT;
        }
    }

    /**
     * 菜单滑动动画，滑动至高度为targetHeight
     *
     * @param targetHeight 目标高度
     */
    private void slideAnimation(final float targetHeight) {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(animDuration);
        animSet.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return 1.0f - (1.0f - input) * (1.0f - input);
            }
        });

        ValueAnimator animator = ValueAnimator.ofFloat(slideParentParams.height, targetHeight);
        animator.setTarget(slideParentParams.height);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                slideParentParams.height = ((Float) animation.getAnimatedValue()).intValue();
                setSlideParentParams(slideParentParams);
                // 自动滑动动画对外回调
                if (null != mSlideDrawerListener) {
                    mSlideDrawerListener.onSlideUpdate(slideParentParams.height, targetHeight, animation);
                }
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (null != mSlideDrawerListener) {
                    mSlideDrawerListener.onSlideStart(slideParentParams.height, targetHeight, animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != mSlideDrawerListener) {
                    mSlideDrawerListener.onSlideEnd(slideParentParams.height, targetHeight, animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        Animator anim;
        if (null != mSlideDrawerListener &&
                null != (anim = mSlideDrawerListener.slideAttachAnim(slideParentParams.height, targetHeight, animDuration))) {
            animSet.play(animator).with(anim);
        } else {
            animSet.play(animator);
        }
        animSet.start();
    }

    public SlideParentHeight getSlideParentHeight() {
        return mSlideParentHeight;
    }

    /**
     * 设置滑动父布局高度状态
     *
     * @param targetHeight 目标高度
     */
    public void setSlideParentHeightState(@NonNull SlideParentHeight targetHeight) {
        this.mSlideParentHeight = targetHeight;

        if (targetHeight == SlideParentHeight.MIN_HEIGHT) {
            slideAnimation(getMinHeight());
            return;
        }

        if (targetHeight == SlideParentHeight.MAX_HEIGHT) {
            slideAnimation(getMaxHeight());
        }

        if (targetHeight == SlideParentHeight.MEDIUM_HEIGHT) {
            if (removeMediumHeightState) {
                Log.e(TAG, "You can't call setSlideParentHeight(MEDIUM_HEIGHT) after calling removeMediumHeightState(true).");
                return;
            }
            slideAnimation(getMediumHeight());
        }
    }

    /**
     * 获取滑动布局最小显示高度
     */
    public int getMinHeight() {
        return minHeight;
    }

    /**
     * 获取滑动布局中等显示高度
     */
    public int getMediumHeight() {
        return mediumHeight;
    }

    /**
     * 获取滑动布局最大显示高度
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * 获取滑动菜单总布局属性
     */
    private ViewGroup.LayoutParams getSlideParentParams() {
        return slideParentLayout.getLayoutParams();
    }

    /**
     * 设置滑动菜单总布局属性
     */
    private void setSlideParentParams(ViewGroup.LayoutParams slideParentParams) {
        slideParentLayout.setLayoutParams(slideParentParams);
    }

    public void setSlideDrawerListener(SlideDrawerListener slideDrawerListener) {
        this.mSlideDrawerListener = slideDrawerListener;
    }

    /**
     * 滑动状态枚举类。
     * SLIDE_UP：上滑；SLIDE_DOWN：下滑
     */
    public enum SlideState {
        SLIDE_UP, SLIDE_DOWN, CLICK_UP, CLICK_DOWN
    }

    /**
     * 滑动父布局高度枚举类
     * MIN_HEIGHT：最小高度（底部）；
     * MEDIUM_HEIGHT：中等高度（中间）；
     * MAX_HEIGHT：最大高度（顶部）
     */
    public enum SlideParentHeight {
        MIN_HEIGHT, MEDIUM_HEIGHT, MAX_HEIGHT
    }

    /**
     * SlideDrawerHelper的建造者
     */
    public static class Builder {
        private static final int screenHeight = MeasureUtils.getCurScreenHeight();// 屏幕高度
        private final ViewGroup dragLayout, slideParentLayout;
        private long animDuration = 200;// 动画持续时间（ms）
        //  初始化高度状态。默认为：MIN_HEIGHT
        private SlideParentHeight initHeightState = SlideParentHeight.MIN_HEIGHT;
        /**
         * minHeight：滑动布局最小显示高度（默认值为屏幕高度的1/10）；
         * mediumHeight：滑动布局中等显示高度（默认值为屏幕高度的1/2）；
         * maxHeight：滑动布局最大显示高度（默认值为屏幕高度的9/10）
         */
        private int minHeight = screenHeight / 12, mediumHeight = screenHeight / 2, maxHeight = 9 * screenHeight / 10;
        private boolean removeMediumHeightState = false;// 是否移除滑动布局中间高度状态。默认为false
        private boolean clickSlidable = true;// 点击拖动布局是否会自动滑动。默认为true
        /**
         * 滑动菜单高度状态为中间时，点击滑动布局，对应滑动状态（SlideState.SLIDE_DOWN：向下滑 或 SlideState.SLIDE_UP：向上滑）。
         * 默认为SlideState.SLIDE_DOWN。
         */
        private SlideState mediumClickSlideState = SlideState.SLIDE_DOWN;

        /**
         * @param dragLayout        滑动触发布局，可拖动或点击。可设置与slideParentLayout为同一个布局
         * @param slideParentLayout 滑动总布局
         */
        public Builder(@NonNull ViewGroup dragLayout, @NonNull ViewGroup slideParentLayout) {
            this.dragLayout = dragLayout;
            this.slideParentLayout = slideParentLayout;
        }

        /**
         * 设置滑动阈值，单位px。也可使用{@link #slidePercentThreshold(Float, Float, Float)}来设置阈值
         *
         * @param minHeight    滑动布局最小显示高度，默认值为屏幕高度的1/10。若不为null且大于0，则修改对应值；
         * @param mediumHeight 滑动布局中等显示高度，默认值为屏幕高度的1/2。若不为null且大于0，则修改对应值；
         * @param maxHeight    滑动布局最大显示高度，默认值为屏幕高度的9/10。若不为null且大于0，则修改对应值；
         */
        public Builder slideThreshold(@Nullable @IntRange(from = 0, to = Long.MAX_VALUE) Integer minHeight,
                                      @Nullable @IntRange(from = 0, to = Long.MAX_VALUE) Integer mediumHeight,
                                      @Nullable @IntRange(from = 0, to = Long.MAX_VALUE) Integer maxHeight) {
            if (null != minHeight && minHeight > 0) {
                this.minHeight = minHeight;
            }

            if (null != mediumHeight && mediumHeight > 0) {
                this.mediumHeight = mediumHeight;
            }

            if (null != maxHeight && maxHeight > 0) {
                this.maxHeight = maxHeight;
            }
            return this;
        }

        /**
         * 设置滑动阈值占屏幕高度的比例。也可使用{@link #slideThreshold(Integer, Integer, Integer)}来设置阈值
         *
         * @param minHeightPercent    滑动布局最小显示高度占屏幕高度的比例，默认值为1/10。若不为null且大于0，则修改对应值。
         * @param mediumHeightPercent 滑动布局中等显示高度占屏幕高度的比例，默认值为1/2。若不为null且大于0，则修改对应值。
         * @param maxHeightPercent    滑动布局最大显示高度占屏幕高度的比例，默认值为9/10。若不为null且大于0，则修改对应值。
         */
        public Builder slidePercentThreshold(@Nullable @FloatRange(from = 0.0, to = 1.0) Float minHeightPercent,
                                             @Nullable @FloatRange(from = 0.0, to = 1.0) Float mediumHeightPercent,
                                             @Nullable @FloatRange(from = 0.0, to = 1.0) Float maxHeightPercent) {
            setMinHeightPercent(minHeightPercent);
            setMediumHeightPercent(mediumHeightPercent);
            setMaxHeightPercent(maxHeightPercent);
            return this;
        }

        private void setMinHeightPercent(Float minHeightPercent) {
            if (null == minHeightPercent || minHeightPercent <= 0) {
                return;
            }
            this.minHeight = minHeightPercent >= 1 ? screenHeight : (int) (screenHeight * minHeightPercent);
        }

        private void setMediumHeightPercent(Float mediumHeightPercent) {
            if (null == mediumHeightPercent || mediumHeightPercent <= 0) {
                return;
            }
            this.mediumHeight = mediumHeightPercent >= 1 ? screenHeight : (int) (screenHeight * mediumHeightPercent);
        }

        private void setMaxHeightPercent(Float maxHeightPercent) {
            if (null == maxHeightPercent || maxHeightPercent <= 0) {
                return;
            }
            this.maxHeight = maxHeightPercent >= 1 ? screenHeight : (int) (screenHeight * maxHeightPercent);
        }

        /**
         * 移除滑动布局中间高度状态
         *
         * @param removeMediumHeightState 是否移除滑动布局中间高度状态。默认为false
         */
        public Builder removeMediumHeightState(boolean removeMediumHeightState) {
            this.removeMediumHeightState = removeMediumHeightState;
            return this;
        }

        /**
         * 是否允许点击拖动布局，自动滑动。
         *
         * @param clickSlidable 点击拖动布局是否会自动滑动。默认为true。
         *                      若需要布局处于中间时，点击布局也不自动滑动，可设置{@link Builder#mediumClickSlideState(SlideState)}为
         *                      {@link SlideState#CLICK_UP}或{@link SlideState#CLICK_DOWN}。
         */
        public Builder clickSlidable(boolean clickSlidable) {
            this.clickSlidable = clickSlidable;
            return this;
        }

        /**
         * 设置自动滑动动画的执行时间
         *
         * @param animDuration 动画执行时间（ms）
         */
        public Builder animDuration(long animDuration) {
            this.animDuration = animDuration;
            return this;
        }

        /**
         * 滑动总布局处于中间高度时，点击滑动触发布局，对应滑动状态。
         * 默认为SlideState.SLIDE_DOWN
         *
         * @param slideState 目标滑动状态。
         */
        public Builder mediumClickSlideState(@NonNull SlideState slideState) {
            this.mediumClickSlideState = slideState;
            return this;
        }

        /**
         * 滑动总布局初始化高度状态。
         * <p>
         * 若已设置移除滑动布局中间高度状态{@link #removeMediumHeightState(boolean true)}，
         * 仍设置初始化高度状态为SlideParentHeight.MEDIUM_HEIGHT，则抛出IllegalArgumentException异常
         *
         * @param initHeightState 初始化高度状态
         */
        public Builder initHeightState(@NonNull SlideParentHeight initHeightState) {
            if (removeMediumHeightState && initHeightState == SlideParentHeight.MEDIUM_HEIGHT) {
                throw new IllegalArgumentException("You can't set SlideParentHeight.MEDIUM_HEIGHT after calling removeMediumHeightState(true).");
            }
            this.initHeightState = initHeightState;
            return this;
        }

        public SlideDrawerHelper build() {
            return new SlideDrawerHelper(this)
                    .initLayoutHeight(initHeightState);
        }
    }

}
