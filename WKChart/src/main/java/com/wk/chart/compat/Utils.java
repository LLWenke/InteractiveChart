package com.wk.chart.compat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.wk.chart.entry.IndexConfigEntry;

import java.util.Collection;

public class Utils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 检查集合是否为null或者Empty
     */
    public static boolean listIsEmpty(Collection<?> data) {
        return null == data || data.size() == 0;
    }

    /**
     * 检查x y是否包含在opt数组中
     * (opt.length 必须为4)
     */
    public static boolean contains(@NonNull @Size(value = 4) float[] opt, float x, float y) {
        if (opt.length < 4) {
            return false;
        }
        return opt[0] < opt[2] && opt[1] < opt[3]  // check for empty first
                && x >= opt[0] && x < opt[2] && y >= opt[1] && y < opt[3];
    }

    /**
     * 计算文字实际占用区域
     *
     * @param measurePaint 计算用文字的画笔
     * @param rect         计算后的矩形（此矩形位文字的实际占用区域）
     */
    public static void measureTextArea(TextPaint measurePaint, Rect rect) {
        measureTextArea(measurePaint, rect, "9");
    }

    /**
     * 计算文字实际占用区域
     *
     * @param measurePaint 计算用文字的画笔
     * @param rect         计算后的矩形（此矩形位文字的实际占用区域）
     * @param text         文字
     */
    public static void measureTextArea(TextPaint measurePaint, Rect rect, String text) {
        if (TextUtils.isEmpty(text)) {
            rect.setEmpty();
        } else {
            measurePaint.getTextBounds(text, 0, text.length(), rect);
        }
    }

    /**
     * 对rgb色彩加入透明度
     *
     * @param baseColor 基色
     * @param alpha     透明度，取值范围 0.0f -- 1.0f.
     * @return a color with alpha made from base color
     */
    public static int getColorWithAlpha(int baseColor, float alpha) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }

    /**
     * 替换占位符
     *
     * @param str         源字符串
     * @param flagEntries flag数组，用于将其中flag替换到对应的占位符上
     */
    public static String replacePlaceholder(@NonNull String str, IndexConfigEntry.FlagEntry... flagEntries) {
        if (null == flagEntries) {
            return str;
        }
        //占位符
        String placeholder = "#";
        if (!str.contains(placeholder)) {
            return str;
        }
        for (IndexConfigEntry.FlagEntry value : flagEntries) {
            str = str.replaceFirst(placeholder, String.valueOf(value.getFlag()));
        }
        return str;
    }

    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return Bitmap
     */
    public static @Nullable
    Bitmap drawableToBitmap(Drawable drawable) {
        if (null == drawable) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
