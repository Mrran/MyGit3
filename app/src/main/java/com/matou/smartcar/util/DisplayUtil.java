package com.matou.smartcar.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtil {


    /**
     * 将px值转换为ps值
     *
     * @return Int
     */
    public static float spToPx(Context context, float px) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (px * fontScale + 0.5f);
    }


    /**
     * 将 dp 单位转换为像素单位，确保尺寸大小不变。
     *
     * @param dp 要转换的 dp 值。
     * @param context 上下文用于访问当前显示指标。
     * @return 对应的像素值。
     */
    public static int dpToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dp * metrics.density + 0.5f); // 使用四舍五入的方法来避免精度损失
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


}
