

package com.ll.chart.render;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.ll.chart.adapter.CandleAdapter;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>CandleRender 蜡烛图渲染器</p>
 */

public class CandleRender extends AbsRender<CandleAdapter, CandleAttribute> {
  private static final String TAG = "CandleRender";
  private final float[] contentPts = new float[4];//[x0, y0, x1, y1]

  private float candleWidth = 0;//蜡烛宽度
  private float minWidth;//蜡烛最小宽度（包含两边的间隔）

  public CandleRender(CandleAttribute attribute, RectF viewRect) {
    super(attribute, viewRect);
    this.minWidth = attribute.candleBorderWidth +
        attribute.candleBorderWidth * (attribute.candleSpace / attribute.candleWidth * 2);
  }

  /**
   * 缩放
   *
   * @param x 在点(x, y)上缩放
   * @param y 在点(x, y)上缩放
   */
  @Override
  public void onZoom(float x, float y) {
    if (adapter.getCount() == 0) {
      return;
    }
    resetItemInfo();
    resetInterval();
    zoom(getMainChartModule().getRect(), attribute.visibleCount, x, y);
  }

  /**
   * 刷新缩放后的蜡烛图信息（CandleWidth 和 visibleCount）
   */
  @Override public void resetItemInfo() {
    candleWidth = getAttribute().candleWidth * attribute.currentScale;
    if (candleWidth < minWidth) {
      candleWidth = minWidth;
      float minScale = candleWidth / attribute.candleWidth;
      attribute.currentScale = minScale;
      attribute.minScale = minScale;
    }
    attribute.visibleCount = getMainChartModule().getRect().width() / candleWidth;
    //Log.e(TAG, "candleWidth:" + candleWidth);
  }

  /**
   * 获取缩放后并且减去CandleSpace的CandleWidth
   */
  @Override public float getSubtractSpaceCandleWidth() {
    return (getAttribute().candleWidth - getAttribute().candleSpace) * attribute.currentScale;
  }

  /**
   * 渲染逻辑
   */
  @Override
  public void onDraw(Canvas canvas) {
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
   * 计算显示区域内 X,Y 轴的范围(此处重写目的是扩大 X,Y 轴的范围)
   */
  @Override
  protected void computeExtremumValue(float[] extremum, AbsChartModule chartModule) {
    final float deltaYScale = chartModule.getDeltaY() * chartModule.getyScale();
    switch (chartModule.getModuleType()) {
      case VOLUME://交易量需要底部对齐，所以不做Y轴最小值的缩放,只缩放Y轴最大值
        extremum[1] = chartModule.getMinY().value;
        if (deltaYScale > 0) {
          extremum[3] = chartModule.getMaxY().value + deltaYScale;
        } else {
          extremum[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
              * chartModule.getyScale();
        }
        break;

      default://默认Y轴最大值和最小值全部进行比例缩放
        if (deltaYScale > 0) {
          extremum[1] = chartModule.getMinY().value - deltaYScale;
          extremum[3] = chartModule.getMaxY().value + deltaYScale;
        } else {
          extremum[1] = chartModule.getMinY().value - chartModule.getMinY().value
              * chartModule.getyScale();
          extremum[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
              * chartModule.getyScale();
        }
        break;
    }
    if (extremum[1] == 0 && extremum[3] == 0) {
      extremum[3] = 1;
    }
  }

  /**
   * 计算当前显示区域内的 X 轴范围
   */
  private void computeVisibleIndex() {
    contentPts[0] = getMainChartModule().getRect().left;
    invertMapPoints(contentPts);
    begin = contentPts[0] > 0 ? (int) contentPts[0] : 0;
    //根据maxVisibleIndex的显示位置修正maxVisibleIndex值
    end = (int) (begin + Math.ceil(attribute.visibleCount) + 1);
    if (Math.ceil(getPointX(end, null) - candleWidth) >= getMainChartModule().getRect().width()) {
      end--;
      //Log.e(TAG, "getTransX---b: " +
      //    Math.ceil(getPointX(end) - candleWidth));
    }
    int max = getAdapter().getCount();
    if (end > max) {
      end = max;
    }
    begin = begin > end ? end : begin;
    // 计算当前显示区域内 entry 在 Y 轴上的最小值和最大值
    getAdapter().computeMinAndMax(begin, end, getChartModules());
  }
}
