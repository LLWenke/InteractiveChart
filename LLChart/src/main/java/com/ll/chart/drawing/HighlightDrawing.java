
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.ll.chart.compat.DisplayTypeUtils;
import com.ll.chart.compat.ValueUtils;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.marker.AbsMarker;
import com.ll.chart.render.CandleRender;
import com.ll.chart.module.base.AbsChartModule;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>HighlightDrawing</p>
 */

public class HighlightDrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "HighlightDrawing";
  private CandleAttribute attribute;//配置文件

  private Paint xHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // X高亮线条画笔
  private Paint yHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Y高亮线条画笔

  private float[] markerViewInfo = new float[4];//markerView的left,top,right,bottom信息
  private float[] highlightPoint = new float[2];//高亮线条x，y
  private float[] highlightInvertPoint = new float[2];//高亮线条坐标反转后的x，y
  private String[] markerText = new String[2];//marker中显示的值
  private AbsChartModule mainChartModule = null;//主图组件

  private List<AbsMarker> markerViewList = new ArrayList<>();

  public void addMarkerView(AbsMarker markerView) {
    markerViewList.add(markerView);
  }

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    xHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    xHighlightPaint.setColor(attribute.xHighlightColor);

    yHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    yHighlightPaint.setColor(attribute.yHighlightColor);

    if (markerViewList.size() > 0) {
      for (AbsMarker markerView : markerViewList) {
        markerView.onInit(render);
      }
    }
  }

  @Override
  public void computePoint(int begin, int end, int current) {
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
  }

  @Override
  public void onDrawOver(Canvas canvas) {
    if (!render.isHighlight()) {
      return;
    }
    // 绘制高亮
    float left, top, right, bottom;

    CandleEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
    //获取当前焦点区域内的chartModule
    AbsChartModule chartModule = render.getChartModuleInFocusArea();
    if (null == chartModule) {
      return;
    }
    markerText[0] = DisplayTypeUtils.selectorFormat(entry.getTime(),
        render.getAdapter().getDisplayType());
    switch (chartModule.getModuleType()) {
      case VOLUME://交易量 指标
        chartModule = mainChartModule;
      case CANDLE://k线图 指标
      case TIME://分时图 指标
        highlightPoint[1] = entry.getClose().value;
        render.mapPoints(chartModule.getMatrix(), highlightPoint);
        highlightPoint[0] = render.getHighlightPoint()[0];
        markerText[1] = entry.getClose().text;
        break;
      case MACD:  //MACD 指标
      case KDJ:  //KDJ 指标
      case RSI:  //RSI 指标
      case BOLL:  //BOLL 指标
      default:
        highlightPoint[0] = render.getHighlightPoint()[0];
        highlightPoint[1] = render.getHighlightPoint()[1];
        markerText[1] = getInvertPoint(chartModule);
        break;
    }
    for (AbsMarker markerView : markerViewList) {
      markerView.onMarkerViewMeasure(chartModule.getRect(),
          chartModule.getMatrix(),
          highlightPoint[0],
          highlightPoint[1],
          markerText,
          markerViewInfo);
    }
    if (markerViewInfo[1] < chartModule.getRect().top + chartModule.getRect().height() / 2) {
      top = markerViewInfo[3] > 0 ? markerViewInfo[3] : chartModule.getRect().top;
      bottom = chartModule.getRect().bottom;
    } else {
      top = chartModule.getRect().top;
      bottom = markerViewInfo[1] > 0 ? markerViewInfo[1] : chartModule.getRect().bottom;
    }

    if (markerViewInfo[0] < chartModule.getRect().left + chartModule.getRect().width() / 2) {
      left = markerViewInfo[2] > 0 ? markerViewInfo[2] : chartModule.getRect().left;
      right = chartModule.getRect().right;
    } else {
      left = chartModule.getRect().left;
      right = markerViewInfo[0] > 0 ? markerViewInfo[0] : chartModule.getRect().right;
    }

    xHighlightPaint.setStrokeWidth(
        attribute.xHighlightAutoWidth ? render.getSubtractSpaceCandleWidth()
            : attribute.lineWidth);

    canvas.drawLines(render.getMeasureUtils().buildViewLRCoordinates(highlightPoint[0],
        highlightPoint[0], top, bottom, chartModule.getRect()), xHighlightPaint);

    yHighlightPaint.setStrokeWidth(
        attribute.yHighlightAutoWidth ? render.getSubtractSpaceCandleWidth()
            : attribute.lineWidth);
    canvas.drawLine(left, highlightPoint[1], right, highlightPoint[1], yHighlightPaint);

    for (AbsMarker markerView : markerViewList) {
      markerView.onMarkerViewDraw(canvas, markerText);
    }
  }

  @Override public void onViewChange() {
    mainChartModule = render.getMainChartModule();
  }

  /**
   * 获取坐标反转后的值（此处已做精度控制）
   */
  private String getInvertPoint(AbsChartModule chartModule) {
    highlightInvertPoint[1] = highlightPoint[1];
    render.invertMapPoints(chartModule.getMatrix(), highlightInvertPoint);
    return ValueUtils.format(highlightInvertPoint[1],
        render.getAdapter().getScale());
  }
}
