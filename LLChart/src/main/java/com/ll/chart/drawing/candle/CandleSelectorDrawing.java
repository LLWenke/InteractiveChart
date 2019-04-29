
package com.ll.chart.drawing.candle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import com.ll.chart.R;
import com.ll.chart.compat.DisplayTypeUtils;
import com.ll.chart.compat.FontConfig;
import com.ll.chart.compat.Utils;
import com.ll.chart.compat.ValueUtils;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.entry.SelectorItemEntry;
import com.ll.chart.render.CandleRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>CandleSelectorDrawing</p>
 */

public class CandleSelectorDrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "CandleSelectorDrawing";

  private CandleAttribute attribute;//配置文件

  private Paint selectorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//选择器边框画笔
  private Paint selectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//选择器背景画笔
  private TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//label画笔
  private TextPaint valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//value画笔(默认)
  private TextPaint increasingValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//上涨value画笔
  private TextPaint decreasingValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//下跌value画笔

  private Paint.FontMetrics metrics;
  private float[] viewRectBuffer = new float[4]; // 计算选择器矩形坐标用的

  private float selectedWidth;//信息选择框的宽度
  private float selectedHeight;//信息选择框的高度
  private SelectorItemEntry[] selectorInfo;//选择器信息集合
  private float borderOffset;//边框偏移量
  private int itemCount = 8;//选择器中的条目数

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    selectorBorderPaint.setStyle(Paint.Style.STROKE);
    selectorBorderPaint.setStrokeWidth(attribute.selectorBorderWidth);
    selectorBorderPaint.setColor(attribute.selectorBorderColor);

    selectorBackgroundPaint.setStyle(Paint.Style.FILL);
    selectorBackgroundPaint.setColor(attribute.selectorBackgroundColor);

    labelPaint.setTextSize(attribute.selectorLabelSize);
    labelPaint.setColor(attribute.selectorLabelColor);
    labelPaint.setTypeface(FontConfig.typeFace);

    valuePaint.setTextSize(attribute.selectorValueSize);
    valuePaint.setColor(attribute.selectorValueColor);
    valuePaint.setTypeface(FontConfig.typeFace);

    increasingValuePaint.setTextSize(attribute.selectorValueSize);
    increasingValuePaint.setColor(attribute.increasingColor);
    increasingValuePaint.setTypeface(FontConfig.typeFace);

    decreasingValuePaint.setTextSize(attribute.selectorValueSize);
    decreasingValuePaint.setColor(attribute.decreasingColor);
    decreasingValuePaint.setTypeface(FontConfig.typeFace);

    metrics = attribute.selectorLabelSize > attribute.selectorValueSize ?
        labelPaint.getFontMetrics() : valuePaint.getFontMetrics();

    borderOffset = attribute.selectorBorderWidth / 2;
    selectorInfo = new SelectorItemEntry[itemCount];
    for (int i = 0; i < itemCount; i++) {
      selectorInfo[i] = new SelectorItemEntry();
    }
  }

  @Override
  public void computePoint(int begin, int end, int current) {
  }

  @Override public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {

  }

  @Override
  public void onDrawOver(Canvas canvas) {
    if (!render.isHighlight()) {
      return;
    }
    //初始信息
    float textHeight = metrics.descent - metrics.ascent;
    float left;
    float top = viewRect.top + attribute.selectorMarginY + attribute.borderWidth;

    //添加选择器内容
    loadSelectorInfo();

    //动态计算选择器宽度和高度
    SelectorItemEntry firstItem = selectorInfo[0];
    float width = firstItem.getLabelPaint().measureText(firstItem.getLabel())
        + firstItem.getValuePaint().measureText(firstItem.getValue())
        + attribute.selectorPadding * 2
        + attribute.selectorIntervalX;
    this.selectedWidth = selectedWidth < width ? width : selectedWidth;
    this.selectedHeight = selectedHeight > 0 ? selectedHeight : attribute.selectorIntervalY *
        (selectorInfo.length + 1) + textHeight * selectorInfo.length;

    //负责选择器左右漂浮
    float x = render.getHighlightPoint()[0];
    if (x > viewRect.width() / 2) {
      left = viewRect.left + attribute.selectorMarginX + attribute.borderWidth;
    } else {
      left = viewRect.right - selectedWidth - attribute.selectorMarginX - attribute.borderWidth;
    }

    //计算选择器坐标位置
    viewRectBuffer[0] = left;
    viewRectBuffer[1] = top;
    viewRectBuffer[2] = left + selectedWidth;
    viewRectBuffer[3] = top + selectedHeight;

    //绘制选择器外边框
    canvas.drawRoundRect(viewRectBuffer[0], viewRectBuffer[1], viewRectBuffer[2],
        viewRectBuffer[3], attribute.selectorRadius, attribute.selectorRadius,
        selectorBorderPaint);

    //绘制选择器填充背景
    canvas.drawRoundRect(viewRectBuffer[0] + borderOffset, viewRectBuffer[1] + borderOffset,
        viewRectBuffer[2] - borderOffset, viewRectBuffer[3] - borderOffset,
        attribute.selectorRadius, attribute.selectorRadius, selectorBackgroundPaint);

    //绘制选择器内容信息
    float y = top + attribute.selectorIntervalY + (textHeight - metrics.bottom - metrics.top) / 2;
    for (SelectorItemEntry item : selectorInfo) {
      //绘制label
      canvas.drawText(item.getLabel(), viewRectBuffer[0] + attribute.selectorPadding, y,
          item.getLabelPaint());
      //绘制value
      canvas.drawText(item.getValue(),
          viewRectBuffer[2]
              - item.getValuePaint().measureText(item.getValue())
              - attribute.selectorPadding, y, item.getValuePaint());
      //计算Y轴位置
      y += textHeight + attribute.selectorIntervalY;
    }
  }

  @Override public void onViewChange() {

  }

  /**
   * 装载选择器的内容信息
   */
  private void loadSelectorInfo() {
    CandleEntry point = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
    //时间
    selectorInfo[0]
        .setLabel(attribute.context.getString(R.string.wk_time_value))
        .setLabelPaint(labelPaint)
        .setValue(DisplayTypeUtils.selectorFormat(point.getTime(),
            render.getAdapter().getDisplayType()))
        .setValuePaint(valuePaint);
    //开
    selectorInfo[1]
        .setLabel(attribute.context.getString(R.string.wk_open))
        .setLabelPaint(labelPaint)
        .setValue(point.getOpen().text)
        .setValuePaint(valuePaint);
    //高
    selectorInfo[2]
        .setLabel(attribute.context.getString(R.string.wk_high))
        .setLabelPaint(labelPaint)
        .setValue(point.getHigh().text)
        .setValuePaint(valuePaint);
    //低
    selectorInfo[3]
        .setLabel(attribute.context.getString(R.string.wk_low))
        .setLabelPaint(labelPaint)
        .setValue(point.getLow().text)
        .setValuePaint(valuePaint);
    //收
    selectorInfo[4]
        .setLabel(attribute.context.getString(R.string.wk_close))
        .setLabelPaint(labelPaint)
        .setValue(point.getClose().text)
        .setValuePaint(valuePaint);
    //涨跌额
    String symbol;
    TextPaint paint;
    if (point.getClose().value < point.getOpen().value) {
      paint = decreasingValuePaint;//下跌
      symbol = "";
    } else {
      paint = increasingValuePaint;//上涨或者不涨不跌
      symbol = "+";
    }
    selectorInfo[5]
        .setLabel(attribute.context.getString(R.string.wk_change_amount))
        .setLabelPaint(labelPaint)
        .setValue(symbol.concat(point.getChangeAmount().text))
        .setValuePaint(paint);
    //涨跌幅
    selectorInfo[6]
        .setLabel(attribute.context.getString(R.string.wk_change_proportion))
        .setLabelPaint(labelPaint)
        .setValue(symbol.concat(point.getChangeProportion().text).concat("%"))
        .setValuePaint(paint);
    //成交量
    selectorInfo[7]
        .setLabel(attribute.context.getString(R.string.wk_volume))
        .setLabelPaint(labelPaint)
        .setValue(ValueUtils.formatBig(point.getVolume().value))
        .setValuePaint(valuePaint);
  }

  @Override public boolean onDrawingClick(float x, float y) {
    return render.isHighlight() && Utils.contains(viewRectBuffer, x, y);
  }
}
