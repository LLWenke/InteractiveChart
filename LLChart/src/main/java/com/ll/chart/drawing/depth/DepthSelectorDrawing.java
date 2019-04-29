
package com.ll.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import com.ll.chart.R;
import com.ll.chart.compat.FontConfig;
import com.ll.chart.compat.Utils;
import com.ll.chart.compat.attribute.DepthAttribute;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.entry.DepthEntry;
import com.ll.chart.entry.SelectorItemEntry;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.render.DepthRender;

/**
 * <p>DepthSelectorDrawing</p>
 */

public class DepthSelectorDrawing extends AbsDrawing<DepthRender> {
  private static final String TAG = "DepthSelectorDrawing";
  private DepthAttribute attribute;//配置文件

  private Paint selectorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//选择器边框画笔
  private Paint selectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//选择器背景画笔
  private TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//label画笔
  private TextPaint valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//value画笔
  private TextPaint unitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//unit画笔

  private Paint.FontMetrics metrics;
  private float[] viewRectBuffer = new float[4]; // 计算选择器矩形坐标用的

  private float selectedWidth;//信息选择框的宽度
  private float selectedHeight;//信息选择框的高度
  private SelectorItemEntry[] selectorInfo;//选择器信息集合
  private float borderOffset;//边框偏移量
  private int itemCount = 3;//选择器中的条目数

  @Override public void onInit(DepthRender render, AbsChartModule chartModule) {
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

    unitPaint.setTextSize(attribute.selectorValueSize);
    unitPaint.setColor(attribute.selectorLabelColor);
    unitPaint.setTypeface(FontConfig.boldTypeFace);

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

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
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
    float width = 0;
    for (SelectorItemEntry item : selectorInfo) {
      float textWidth = item.getLabelPaint().measureText(item.getLabel())
          + item.getValuePaint().measureText(item.getValue())
          + item.getUnitPaint().measureText(item.getUnit());
      width = width < textWidth ? textWidth : width;
    }
    width += (attribute.selectorPadding * 2 + attribute.selectorIntervalX);
    this.selectedWidth = selectedWidth < width ? width : selectedWidth;
    this.selectedHeight = selectedHeight > 0 ? selectedHeight : attribute.selectorIntervalY *
        (selectorInfo.length + 1) + textHeight * selectorInfo.length;

    //负责选择器显示位置
    left = viewRect.width() / 2 - selectedWidth / 2 - attribute.borderWidth;

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
      float x = viewRectBuffer[0] + attribute.selectorPadding;
      canvas.drawText(item.getLabel(), x, y, item.getLabelPaint());
      //绘制unit
      x = viewRectBuffer[2] - item.getUnitPaint().measureText(item.getUnit())
          - attribute.selectorPadding;
      canvas.drawText(item.getUnit(), x, y, item.getUnitPaint());
      //绘制value
      x -= item.getValuePaint().measureText(item.getValue());
      canvas.drawText(item.getValue(), x, y, item.getValuePaint());
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
    DepthEntry point = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
    //价格
    selectorInfo[0]
        .setLabel(attribute.context.getString(R.string.wk_price))
        .setLabelPaint(labelPaint)
        .setValue(render.getHighlightXValue(point))
        .setValuePaint(valuePaint)
        .setUnit(" ".concat(render.getAdapter().getQuoteSymbol()))
        .setUnitPaint(unitPaint);

    //总量
    selectorInfo[1]
        .setLabel(attribute.context.getString(R.string.wk_total_amount))
        .setLabelPaint(labelPaint)
        .setValue(point.getTotalAmount().text)
        .setValuePaint(valuePaint)
        .setUnit(" ".concat(render.getAdapter().getBaseSymbol()))
        .setUnitPaint(unitPaint);
    //总成本
    selectorInfo[2]
        .setLabel(attribute.context.getString(R.string.wk_total_cost))
        .setLabelPaint(labelPaint)
        .setValue(point.getTotalPrice().text)
        .setValuePaint(valuePaint)
        .setUnit(" ".concat(render.getAdapter().getQuoteSymbol()))
        .setUnitPaint(unitPaint);
  }

  @Override public boolean onDrawingClick(float x, float y) {
    return render.isHighlight() && Utils.contains(viewRectBuffer, x, y);
  }
}
