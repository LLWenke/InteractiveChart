
package com.ll.chart.drawing.candle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import com.ll.chart.compat.FontConfig;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.render.CandleRender;

/**
 * <p>CandleDrawing</p>
 */

public class CandleDrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "CandleDrawing";
  private CandleAttribute attribute;//配置文件

  // 上涨蜡烛图画笔
  private Paint increasingCandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 下跌蜡烛图画笔
  private Paint decreasingCandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 上涨蜡烛图画笔(用于高度或宽度小于边框宽高度时使用)
  private Paint increasingCandleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 上涨蜡烛图画笔(用于高度或宽度小于边框宽高度时使用)
  private Paint decreasingCandleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // k线图边框线画笔
  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 当前可见区域内的极值画笔
  private TextPaint extremumPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
  private final Rect extremumRect = new Rect(); // 用于计算极值文字位置

  private float[] candleLineBuffer = new float[8]; // 计算 2 根线坐标用的
  private float[] candleRectBuffer = new float[4]; // 计算 1 个矩形坐标用的

  private float extremumToRight;
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

    extremumPaint.setStyle(Paint.Style.FILL);
    extremumPaint.setTypeface(FontConfig.typeFace);
    extremumPaint.setTextSize(attribute.candleExtremumLabelSize);
    extremumPaint.setColor(attribute.candleExtremumLableColor);

    space = (attribute.candleSpace / attribute.candleWidth) / 2;
    borderOffset = attribute.candleBorderWidth / 2;
    minSize = attribute.candleBorderWidth * 2;
    extremumToRight = viewRect.right - 150;
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
      // 绘制 影线
      float offset = i + 0.5f;
      candleLineBuffer[0] = offset;
      candleLineBuffer[2] = offset;
      candleLineBuffer[4] = offset;
      candleLineBuffer[6] = offset;
      if (entry.getOpen().value > entry.getClose().value) {
        candleLineBuffer[1] = entry.getHigh().value;
        candleLineBuffer[3] = entry.getOpen().value;
        candleLineBuffer[5] = entry.getClose().value;
        candleLineBuffer[7] = entry.getLow().value;
      } else {
        candleLineBuffer[1] = entry.getHigh().value;
        candleLineBuffer[3] = entry.getClose().value;
        candleLineBuffer[5] = entry.getOpen().value;
        candleLineBuffer[7] = entry.getLow().value;
      }
      render.mapPoints(candleLineBuffer);
      canvas.drawLines(candleLineBuffer, candlePaint);

      // 绘制 当前显示区域的"最小"与"最大"两个值
      String text;
      if (i == render.getAdapter().getMinYIndex()) {
        if (candleLineBuffer[6] > extremumToRight) {
          extremumPaint.setTextAlign(Paint.Align.RIGHT);
          text = entry.getLow().text.concat(" →");
        } else {
          extremumPaint.setTextAlign(Paint.Align.LEFT);
          text = "← ".concat(entry.getLow().text);
        }
        extremumPaint.getTextBounds(text, 0, text.length(), extremumRect);
        canvas.drawText(text, candleLineBuffer[6],
            candleLineBuffer[7] + extremumRect.height(), extremumPaint);
      }
      if (i == render.getAdapter().getMaxYIndex()) {
        if (candleLineBuffer[0] > extremumToRight) {
          extremumPaint.setTextAlign(Paint.Align.RIGHT);
          text = entry.getHigh().text.concat(" →");
        } else {
          extremumPaint.setTextAlign(Paint.Align.LEFT);
          text = "← ".concat(entry.getHigh().text);
        }
        canvas.drawText(text, candleLineBuffer[0], candleLineBuffer[1], extremumPaint);
      }
      // 绘制 蜡烛图的矩形
      candleRectBuffer[0] = i + space;
      candleRectBuffer[2] = i + 1 - space;
      if (entry.getOpen().value > entry.getClose().value) {
        candleRectBuffer[1] = entry.getOpen().value;
        candleRectBuffer[3] = entry.getClose().value;
      } else {
        candleRectBuffer[1] = entry.getClose().value;
        candleRectBuffer[3] = entry.getOpen().value;
      }
      render.mapPoints(candleRectBuffer);
      //Log.e(TAG, "left " + candleRectBuffer[0]
      //    + "right " + candleRectBuffer[2]
      //    + "top " + candleRectBuffer[1]
      //    + "bottom " + candleRectBuffer[3]
      //    + "candleRectHeight " + (candleRectBuffer[1] - candleRectBuffer[3]));

      float width = candleRectBuffer[2] - candleRectBuffer[0];
      float height = candleRectBuffer[3] - candleRectBuffer[1];

      //边框偏移量修正
      if (candlePaint.getStyle() == Paint.Style.STROKE) {
        if (width > minSize && height > minSize) {
          candleRectBuffer[0] += borderOffset;
          candleRectBuffer[2] -= borderOffset;
          candleRectBuffer[1] += borderOffset;
          candleRectBuffer[3] -= borderOffset;
        } else {
          candlePaint = candleFillPaint;
        }
      }
      if (height < 2) {// 涨停、跌停、或不涨不跌的一字板
        candleRectBuffer[1] -= 1;
        candleRectBuffer[3] += 1;
      }
      canvas.drawRect(candleRectBuffer[0], candleRectBuffer[1], candleRectBuffer[2],
          candleRectBuffer[3], candlePaint);
      // 计算高亮坐标
      if (render.isHighlight()) {
        final float[] highlightPoint = render.getHighlightPoint();
        if (candleRectBuffer[0] <= highlightPoint[0] && highlightPoint[0] <= candleRectBuffer[2]) {
          highlightPoint[0] = candleLineBuffer[0];
          render.getAdapter().setHighlightIndex(i);
        }
      }
    }

    canvas.restore();
  }

  @Override
  public void onDrawOver(Canvas canvas) {
    // 绘制外层边框线
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
