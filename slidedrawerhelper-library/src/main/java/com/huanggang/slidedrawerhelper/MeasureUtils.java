package com.huanggang.slidedrawerhelper;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * 测量工具类
 * Created by HuangGang on 2017/6/12.
 * <p>
 * 有问题欢迎联系“huangganghust@qq.com”
 */
public class MeasureUtils {

    public static DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    /**
     * 获取当前屏幕的宽度
     */
    public static int getCurScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 获取当前屏幕的高度
     */
    public static int getCurScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

}
