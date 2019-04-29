
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.render.CandleRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>MACDDrawing</p>
 */

public class MACDDrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "MACDDrawing";
  private CandleAttribute attribute;//配置文件

  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //边框画笔
  private Paint centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //中心线画笔

  private Paint deaPaint = new Paint();
  private Paint diffPaint = new Paint();
  // 上涨蜡烛图画笔
  private Paint increasingCandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 下跌蜡烛图画笔
  private Paint decreasingCandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 上涨蜡烛图画笔(用于高度或宽度小于边框宽高度时使用)
  private Paint increasingCandleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 上涨蜡烛图画笔(用于高度或宽度小于边框宽高度时使用)
  private Paint decreasingCandleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  // dea绘制路径
  private Path deaPath = new Path();
  // diff绘制路径
  private Path diffPath = new Path();
  // 折线路径位置信息
  private final float[] pathPts = new float[4];

  private float[] gridBuffer = new float[2];

  private float[] xRectBuffer = new float[4];
  private float[] macdBuffer = new float[4];

  private float space = 0;//间隔
  private float borderOffset;//边框偏移量
  private float minSize;//最小宽度或高度

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    centerLinePaint.setColor(attribute.centerLineColor);
    centerLinePaint.setStrokeWidth(attribute.lineWidth);
    centerLinePaint.setStyle(Paint.Style.FILL);

    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(attribute.borderWidth);
    borderPaint.setColor(attribute.borderColor);

    deaPaint.setStyle(Paint.Style.STROKE);
    deaPaint.setStrokeWidth(attribute.lineWidth);
    deaPaint.setColor(attribute.deaLineColor);

    diffPaint.setStyle(Paint.Style.STROKE);
    diffPaint.setStrokeWidth(attribute.lineWidth);
    diffPaint.setColor(attribute.diffLineColor);

    increasingCandlePaint.setStyle(attribute.increasingStyle);
    increasingCandlePaint.setStrokeWidth(attribute.candleBorderWidth);
    increasingCandlePaint.setColor(attribute.increasingColor);

    increasingCandleFillPaint.setStyle(Paint.Style.FILL);
    increasingCandleFillPaint.setColor(attribute.increasingColor);

    decreasingCandlePaint.setStyle(attribute.decreasingStyle);
    decreasingCandlePaint.setStrokeWidth(attribute.candleBorderWidth);
    decreasingCandlePaint.setColor(attribute.decreasingColor);

    decreasingCandleFillPaint.setStyle(Paint.Style.FILL);
    decreasingCandleFillPaint.setColor(attribute.decreasingColor);

    space = (attribute.candleSpace / attribute.candleWidth) / 2;
    borderOffset = attribute.candleBorderWidth / 2;
    minSize = attribute.candleBorderWidth * 2;
  }

  @Override
  public void computePoint(int begin, int end, int current) {
    final CandleEntry entry = render.getAdapter().getItem(current);
    pathPts[0] = pathPts[2] = current + 0.5f;
    pathPts[1] = entry.getDea().value;
    pathPts[3] = entry.getDiff().value;
    render.mapPoints(pathPts);
    if (begin == current) {
      deaPath.moveTo(pathPts[0], pathPts[1]);
      diffPath.moveTo(pathPts[2], pathPts[3]);
    } else {
      deaPath.lineTo(pathPts[0], pathPts[1]);
      diffPath.lineTo(pathPts[2], pathPts[3]);
    }
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    canvas.save();
    canvas.clipRect(viewRect);

    Paint candlePaint;
    Paint candleFillPaint;
    gridBuffer[0] = 0;
    gridBuffer[1] = 0;
    render.mapPoints(gridBuffer);

    canvas.drawLine(viewRect.left, gridBuffer[1], viewRect.right, gridBuffer[1], centerLinePaint);

    for (int i = begin; i < end; i++) {
      CandleEntry entry = render.getAdapter().getItem(i);

      xRectBuffer[0] = i + space;
      xRectBuffer[1] = 0;
      xRectBuffer[2] = i + 1 - space;
      xRectBuffer[3] = 0;
      render.mapPoints(xRectBuffer);

      macdBuffer[0] = 0;
      macdBuffer[2] = 0;

      if (entry.getMacd().value >= 0) {
        macdBuffer[1] = entry.getMacd().value;
        macdBuffer[3] = 0;
      } else {
        macdBuffer[1] = 0;
        macdBuffer[3] = entry.getMacd().value;
      }
      render.mapPoints(macdBuffer);

      if (macdBuffer[3] <= gridBuffer[1]) {
        candlePaint = increasingCandlePaint;//上涨或者不涨不跌
        candleFillPaint = increasingCandleFillPaint;//上涨或者不涨不跌fill
      } else {
        candlePaint = decreasingCandlePaint;//下跌
        candleFillPaint = decreasingCandleFillPaint;//下跌fill
      }

      float width = xRectBuffer[2] - xRectBuffer[0];
      float height = macdBuffer[3] - macdBuffer[1];

      //边框偏移量修正
      if (candlePaint.getStyle() == Paint.Style.STROKE) {
        if (width > minSize && height > minSize) {
          xRectBuffer[0] += borderOffset;
          xRectBuffer[2] -= borderOffset;
          macdBuffer[1] += borderOffset;
          macdBuffer[3] -= borderOffset;
        } else {
          candlePaint = candleFillPaint;
        }
      }
      if (height < 2) {// 涨停、跌停、或不涨不跌的一字板
        macdBuffer[1] -= 2;
      }
      canvas.drawRect(xRectBuffer[0], macdBuffer[1], xRectBuffer[2],
          macdBuffer[3], candlePaint);
    }
    canvas.restore();

    canvas.save();
    canvas.clipRect(viewRect);

    canvas.drawPath(deaPath, deaPaint);
    canvas.drawPath(diffPath, diffPaint);
    deaPath.reset();
    diffPath.reset();

    canvas.restore();
  }

  @Override
  public void onDrawOver(Canvas canvas) {
    if (attribute.borderWidth > 0) {
      canvas.drawRect(viewRect.left - render.getBorderCorrection(),
          viewRect.top - render.getBorderCorrection(),
          viewRect.right + render.getBorderCorrection(),
          viewRect.bottom + render.getBorderCorrection(),
          borderPaint);
    }
  }

  @Override public void onViewChange() {

  }
}
