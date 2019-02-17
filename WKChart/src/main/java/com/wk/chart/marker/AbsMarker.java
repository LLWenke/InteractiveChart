

package com.wk.chart.marker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.render.AbsRender;

/**
 * <p>AbsMarker</p>
 */

public abstract class AbsMarker<T extends AbsRender> {
  public boolean isInit = false;
  protected BaseAttribute attribute;
  protected T render;

  /**
   * 初始化
   *
   * @param render render
   */
  public void onInit(T render) {
    this.isInit = true;
    this.render = render;
    this.attribute = render.getAttribute();
  }

  /**
   * onDrawMarkerView
   * @param canvas canvas
   * @param matrix
   * @param highlightPointX 高亮中心坐标 x
   * @param highlightPointY 高亮中心坐标 y
   * @param markerText
   */
  public abstract void onDrawMarkerView(Canvas canvas, RectF viewRect,
      Matrix matrix, float highlightPointX,
      float highlightPointY,
      String[] markerText, @Size(min = 4) @NonNull float[] markerViewInfo);
}