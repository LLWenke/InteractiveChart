package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.render.CandleRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>VolumeDrawing K线成交量的绘制</p>
 */

public class VolumeDrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "VolumeDrawing";
  private CandleAttribute attribute;//配置文件
  // X 轴和 Y 轴的画笔
  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 上涨蜡烛图画笔
  private Paint increasingCandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 下跌蜡烛图画笔
  private Paint decreasingCandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 上涨蜡烛图画笔(用于高度或宽度小于边框宽高度时使用)
  private Paint increasingCandleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 上涨蜡烛图画笔(用于高度或宽度小于边框宽高度时使用)
  private Paint decreasingCandleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private float[] xRectBuffer = new float[4];
  private float[] candleBuffer = new float[4];

  private float space = 0;//间隔
  private float borderOffset;//边框偏移量
  private float minSize;//最小宽度或高度

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(attribute.borderWidth);
    borderPaint.setColor(attribute.borderColor);

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
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    canvas.save();
    canvas.clipRect(viewRect);

    Paint candlePaint;
    Paint candleFillPaint;
    for (int i = begin; i < end; i++) {
      // 设置画笔颜色
      CandleEntry entry = render.getAdapter().getItem(i);

      if (entry.getClose().value < entry.getOpen().value) {
        candlePaint = decreasingCandlePaint;//下跌
        candleFillPaint = decreasingCandleFillPaint;//下跌fill
      } else {
        candlePaint = increasingCandlePaint;//上涨或者不涨不跌
        candleFillPaint = increasingCandleFillPaint;//上涨或者不涨不跌fill
      }

      // 计算 成交量的矩形卓坐标
      // 绘制 蜡烛图的矩形
      xRectBuffer[0] = i + space;
      xRectBuffer[1] = 0;
      xRectBuffer[2] = i + 1 - space;
      xRectBuffer[3] = 0;
      render.mapPoints(xRectBuffer);

      candleBuffer[0] = 0;
      candleBuffer[1] = entry.getVolume().value;
      candleBuffer[2] = 0;
      candleBuffer[3] = extremum[1];

      render.mapPoints(candleBuffer);

      float width = xRectBuffer[2] - xRectBuffer[0];
      float height = candleBuffer[3] - candleBuffer[1];

      //边框偏移量修正
      if (candlePaint.getStyle() == Paint.Style.STROKE) {
        if (width > minSize && height > minSize) {
          xRectBuffer[0] += borderOffset;
          xRectBuffer[2] -= borderOffset;
          candleBuffer[1] += borderOffset;
          candleBuffer[3] -= borderOffset;
        } else {
          candlePaint = candleFillPaint;
        }
      }
      if (height < 2) {// 涨停、跌停、或不涨不跌的一字板
        candleBuffer[1] -= 2;
      }
      canvas.drawRect(xRectBuffer[0], candleBuffer[1], xRectBuffer[2],
          candleBuffer[3], candlePaint);
    }
    canvas.restore();
  }

  @Override
  public void onDrawOver(Canvas canvas) {
    //绘制外层边框线
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
