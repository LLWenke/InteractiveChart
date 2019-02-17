

package com.wk.chart.render;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.AbsDrawing;
import com.wk.chart.enumeration.ChartLevel;
import com.wk.chart.stock.base.AbsChartModule;

/**
 * <p>DepthRender 深度图渲染器</p>
 */

public class DepthRender extends AbsRender<DepthAdapter, DepthAttribute> {
  private static final String TAG = "DepthRender";
  private RectF mainRectF;//主图绘制区域矩形

  public DepthRender(DepthAttribute attribute, RectF viewRect) {
    super(attribute, viewRect);
    this.mainRectF = viewRect;
  }

  @Override public void resetItemInfo() {

  }

  /**
   * viewRect 位置初始化
   */
  @Override
  protected void onViewRect() {
    float left = viewRect.left + attribute.borderWidth;
    float top = viewRect.top + attribute.borderWidth;
    float right = viewRect.right - attribute.borderWidth;
    float bottom;
    //分配图表大小和位置
    for (AbsChartModule item : getChartModules()) {
      if (!item.isEnable()) {
        continue;
      }
      bottom = top + item.getViewHeight();
      item.setRect(
          left + item.getPaddingLeft(),
          top + item.getPaddingTop(),
          right - item.getPaddingRight(),
          bottom - item.getPaddingBottom());
      top = bottom + attribute.viewInterval + attribute.borderWidth * 2;
      //更新主图绘制区域矩形参数
      if (item.getChartLevel() == ChartLevel.MAIN) {
        this.mainRectF = item.getRect();
      }
    }
  }

  /**
   * 数据更新回调
   */
  @Override
  public void onDataChange() {
    postMatrixTouch(mainRectF.width(), getAdapter().getCount());
    initMatrixValue(mainRectF);
    postMatrixOffset(mainRectF.left, mainRectF.top);
  }

  /**
   * 缩放
   *
   * @param x 在点(x, y)上缩放
   * @param y 在点(x, y)上缩放
   */
  @Override
  public void onZoom(float x, float y) {
  }

  @Override
  public boolean canScroll(float dx) {
    return false;
  }

  @Override public boolean canDragging() {
    return false;
  }

  /**
   * 是初始化Drawing
   *
   * @param chartModule 组件
   */
  @Override protected void initDrawing(AbsChartModule chartModule) {
    for (int i = 0, z = chartModule.getDrawingList().size(); i < z; i++) {
      AbsDrawing drawing = (AbsDrawing) chartModule.getDrawingList().get(i);
      if (!drawing.isInit()) {
        drawing.onInit(chartModule.getRect(), this, chartModule);
      }
      drawing.onViewChange();
    }
  }

  /**
   * 渲染逻辑
   */
  @Override
  public void render(Canvas canvas) {
    computeVisibleIndex();
    //内容渲染之前调用（被覆盖）
    renderDrawingBefore(canvas);
    for (AbsChartModule item : getChartModules()) {
      if (item.isEnable()) {
        postMatrixValue(item);
        renderDrawing(canvas, item.getDrawingList());
      }
    }
    //内容渲染之后调用（覆盖）
    renderDrawingAfter(canvas);
  }

  /**
   * 扩大显示区域内 X,Y 轴的范围
   */
  @Override
  protected void computeExtremumValue(float[] extremumY, AbsChartModule chartModule) {
    final float deltaYScale = chartModule.getDeltaY() * chartModule.getyScale();
    //X轴
    extremumY[0] = chartModule.getMinX().value;
    extremumY[2] = chartModule.getMaxX().value;
    //Y轴
    extremumY[1] = chartModule.getMinY().value;
    if (deltaYScale > 0) {
      extremumY[3] = chartModule.getMaxY().value + deltaYScale;
    } else {
      extremumY[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
          * chartModule.getyScale();
    }

    if (extremumY[1] == 0 && extremumY[3] == 0) {
      extremumY[3] = 1;
    }
  }

  /**
   * 计算当前显示区域内的 X 轴范围
   */
  private void computeVisibleIndex() {
    begin = 0;
    end = getAdapter().getCount();
    // 计算当前显示区域内 entry 在 Y 轴上的最小值和最大值
    getAdapter().computeMinAndMax(begin, end, getChartModules());
  }
}
