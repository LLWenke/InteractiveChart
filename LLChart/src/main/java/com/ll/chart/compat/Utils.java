package com.ll.chart.compat;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.text.TextPaint;
import java.util.Collection;

public class Utils {

  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   */
  public static int dpTopx(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public static int pxTodp(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  /**
   * 检查集合是否为null或者Empty
   */
  public static boolean listIsEmpty(Collection data) {
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
   * @param rect 计算后的矩形（此矩形位文字的实际占用区域）
   */
  public static void measureTextArea(TextPaint measurePaint, Rect rect) {
    String test = "9.Y";
    measurePaint.getTextBounds(test, 0, test.length(), rect);
  }
}
