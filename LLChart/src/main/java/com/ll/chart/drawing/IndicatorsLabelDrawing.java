
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import com.ll.chart.compat.FontConfig;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.render.CandleRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>IndicatorsLabelDrawing</p>
 */

public class IndicatorsLabelDrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "IndicatorsLabelDrawing";
  private CandleAttribute attribute;//配置文件

  private TextPaint[] labelPaint; // 标签画笔
  private String[] labelText; //标签文字
  private float x;
  private float y;

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    switch (chartModule.getModuleType()) {
      case CANDLE:
        labelPaint = new TextPaint[3];
        labelText = new String[3];
        TextPaint ma5Paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextPaint ma10Paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextPaint ma20Paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        ma5Paint.setTextSize(attribute.indicatorsTextSize);
        ma5Paint.setColor(attribute.line1Color);

        ma10Paint.setTextSize(attribute.indicatorsTextSize);
        ma10Paint.setColor(attribute.line2Color);

        ma20Paint.setTextSize(attribute.indicatorsTextSize);
        ma20Paint.setColor(attribute.line3Color);

        labelPaint[0] = ma5Paint;
        labelPaint[1] = ma10Paint;
        labelPaint[2] = ma20Paint;
        break;

      case VOLUME:
        labelPaint = new TextPaint[2];
        labelText = new String[2];
        TextPaint volumeMa5Paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextPaint volumeMa10Paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        volumeMa5Paint.setTextSize(attribute.indicatorsTextSize);
        volumeMa5Paint.setColor(attribute.line1Color);

        volumeMa10Paint.setTextSize(attribute.indicatorsTextSize);
        volumeMa10Paint.setColor(attribute.line2Color);

        labelPaint[0] = volumeMa5Paint;
        labelPaint[1] = volumeMa10Paint;
        break;

      default:
        labelPaint = new TextPaint[0];
        labelText = new String[0];
        break;
    }
    switch (attribute.indicatorsLabelLocation) {
      case RIGHT_TOP:
      case RIGHT_BOTTOM:
      case RIGHT_TOP_INSIDE:
      case RIGHT_BOTTOM_INSIDE:
        for (Paint paint : labelPaint) {
          paint.setTextAlign(Paint.Align.RIGHT);
        }
        break;
    }
  }

  @Override
  public void computePoint(int begin, int end, int current) {
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    CandleEntry entry = render.getAdapter().getItem(render.isHighlight() ?
        render.getAdapter().getHighlightIndex() : begin);
    switch (absChartModule.getModuleType()) {
      case CANDLE:
        labelText[0] = "MA5:".concat(entry.getMa5().text);
        labelText[1] = "MA10:".concat(entry.getMa10().text);
        labelText[2] = "MA20:".concat(entry.getMa20().text);
        break;

      case VOLUME:
        labelText[0] = "MA5:".concat(entry.getVolumeMa5().text);
        labelText[1] = "MA10:".concat(entry.getVolumeMa10().text);
        break;
    }
    float currentX = x;
    for (int i = 0; i < labelPaint.length; i++) {
      canvas.drawText(labelText[i], currentX, y, labelPaint[i]);
      switch (attribute.indicatorsLabelLocation) {
        case LEFT_TOP_INSIDE://左上（内部）
        case LEFT_TOP://左上
        case LEFT_BOTTOM_INSIDE://左下（内部）
        case LEFT_BOTTOM://左下
          currentX += (labelPaint[i].measureText(labelText[i]) + attribute.indicatorsTextInterval);
          break;
        case RIGHT_TOP_INSIDE://右上（内部）
        case RIGHT_TOP://右上
        case RIGHT_BOTTOM_INSIDE://右下（内部）
        case RIGHT_BOTTOM://右下
          int z = labelPaint.length - 1 - i;
          currentX -= (labelPaint[z].measureText(labelText[z]) + attribute.indicatorsTextInterval);
          break;
      }
    }
  }

  @Override
  public void onDrawOver(Canvas canvas) {

  }

  @Override public void onViewChange() {
    switch (attribute.indicatorsLabelLocation) {
      case LEFT_TOP_INSIDE://左上（内部）
        x = viewRect.left + attribute.indicatorsTextMarginX;
        y = viewRect.top
            + attribute.indicatorsTextMarginY
            + attribute.indicatorsTextSize
            + attribute.borderWidth;
        break;
      case RIGHT_TOP_INSIDE://右上（内部）
        x = viewRect.right - attribute.indicatorsTextMarginX;
        y = viewRect.top
            + attribute.indicatorsTextMarginY
            + attribute.indicatorsTextSize
            + attribute.borderWidth;
        break;
      case LEFT_TOP://左上
        x = viewRect.left + attribute.indicatorsTextMarginX;
        y = viewRect.top - attribute.indicatorsTextMarginY - attribute.borderWidth;
        break;
      case RIGHT_TOP://右上
        x = viewRect.right - attribute.indicatorsTextMarginX;
        y = viewRect.top - attribute.indicatorsTextMarginY - attribute.borderWidth;
        break;
      case LEFT_BOTTOM_INSIDE://左下（内部）
        x = viewRect.left + attribute.indicatorsTextMarginX;
        y = viewRect.bottom - attribute.indicatorsTextMarginY - attribute.borderWidth;
        break;
      case RIGHT_BOTTOM_INSIDE://右下（内部）
        x = viewRect.right - attribute.indicatorsTextMarginX;
        y = viewRect.bottom - attribute.indicatorsTextMarginY - attribute.borderWidth;
        break;
      case LEFT_BOTTOM://左下
        x = viewRect.left + attribute.indicatorsTextMarginX;
        y = viewRect.bottom
            + attribute.indicatorsTextMarginY
            + attribute.indicatorsTextSize
            + attribute.borderWidth;
        break;
      case RIGHT_BOTTOM://右下
        x = viewRect.right - attribute.indicatorsTextMarginX;
        y = viewRect.bottom
            + attribute.indicatorsTextMarginY
            + attribute.indicatorsTextSize
            + attribute.borderWidth;
        break;
    }
  }
}
