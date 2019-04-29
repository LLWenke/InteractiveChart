
package com.ll.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.ll.chart.adapter.DepthAdapter;
import com.ll.chart.compat.attribute.DepthAttribute;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.entry.DepthEntry;
import com.ll.chart.marker.AbsMarker;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.render.DepthRender;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>DepthHighlightDrawing</p>
 */

public class DepthHighlightDrawing extends AbsDrawing<DepthRender> {
  private static final String TAG = "DepthHighlightDrawing";
  private DepthAttribute attribute;//配置文件

  private Paint bidHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 买单高亮线条画笔
  private Paint askHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 卖单高亮线条画笔
  private Paint bidCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//买单圆点画笔
  private Paint askCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//卖单圆点画笔

  private float[] highlightPoint = new float[2];//高亮线条x，y
  private float[] markerViewInfo = new float[4];//markerView的left,top,right,bottom信息
  private String[] markerText = new String[2];//marker中显示的值

  private List<AbsMarker> markerViewList = new ArrayList<>();

  @Override public void onInit(DepthRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    bidHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    bidHighlightPaint.setColor(attribute.bidHighlightColor);
    bidHighlightPaint.setStrokeWidth(attribute.polylineWidth);

    askHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    askHighlightPaint.setColor(attribute.askHighlightColor);
    askHighlightPaint.setStrokeWidth(attribute.polylineWidth);

    bidCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    bidCirclePaint.setColor(attribute.bidLineColor);
    bidCirclePaint.setStrokeWidth(attribute.circleSize);

    askCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    askCirclePaint.setColor(attribute.askLineColor);
    askCirclePaint.setStrokeWidth(attribute.circleSize);

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
    float top, bottom;
    Paint circlePant;//圆点画笔1
    Paint highlightPaint;//高亮线画笔
    DepthEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
    //获取当前焦点区域内的chartModule
    AbsChartModule chartModule = render.getChartModuleInFocusArea();
    if (null == chartModule) {
      return;
    }
    //区分区域
    if (entry.getType() == DepthAdapter.BID) {
      circlePant = bidCirclePaint;
      highlightPaint = bidHighlightPaint;
    } else {
      circlePant = askCirclePaint;
      highlightPaint = askHighlightPaint;
    }

    highlightPoint[0] = render.getHighlightPoint()[0];
    highlightPoint[1] = render.getHighlightPoint()[1];
    markerText[0] = render.getHighlightXValue(entry);
    markerText[1] = entry.getTotalAmount().text;

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
    canvas.drawLines(render.getMeasureUtils().buildViewLRCoordinates(highlightPoint[0],
        highlightPoint[0], top, bottom, chartModule.getRect()), highlightPaint);
    canvas.drawCircle(highlightPoint[0], highlightPoint[1], attribute.circleSize / 2,
        circlePant);

    for (AbsMarker markerView : markerViewList) {
      markerView.onMarkerViewDraw(canvas, markerText);
    }
  }

  @Override public void onViewChange() {

  }

  public void addMarkerView(AbsMarker markerView) {
    markerViewList.add(markerView);
  }
}
