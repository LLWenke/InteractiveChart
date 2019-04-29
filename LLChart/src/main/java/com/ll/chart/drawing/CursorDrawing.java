
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import com.ll.chart.compat.FontConfig;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.render.CandleRender;

/**
 * <p>CursorDrawing</p>
 * 游标指示器组件
 */

public class CursorDrawing extends AbsDrawing<CandleRender> {
  private CandleAttribute attribute;//配置文件
  private Paint increasingLinePaint = new Paint();//上涨游标线画笔
  private TextPaint increasingTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//上涨游标值画笔
  private Paint increasingBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//上涨游标值容器边框画笔

  private Paint decreasingLinePaint = new Paint();//下跌游标线画笔
  private TextPaint decreasingTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//下跌游标值画笔
  private Paint decreasingBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//下跌游标值容器边框画笔

  private Paint cursorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//下跌游标值容器背景画笔

  private Rect textRect = new Rect();//游标文字显示区域
  private RectF cursorRect = new RectF();//游标显示区域(不包含游标线)
  private Path path = new Path();//游标线路径
  private float[] cursorPoint = new float[2];//存放游标坐标
  private boolean clickable = false;//是否可以点击

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    this.attribute = render.getAttribute();
    DashPathEffect dashPathEffect = new DashPathEffect(new float[] { 15, 8 }, 0);
    increasingLinePaint.setStrokeWidth(attribute.lineWidth);
    increasingLinePaint.setStyle(Paint.Style.STROKE);
    increasingLinePaint.setColor(attribute.increasingColor);
    increasingLinePaint.setPathEffect(dashPathEffect);

    decreasingLinePaint.setStrokeWidth(attribute.lineWidth);
    decreasingLinePaint.setStyle(Paint.Style.STROKE);
    decreasingLinePaint.setColor(attribute.decreasingColor);
    decreasingLinePaint.setPathEffect(dashPathEffect);

    increasingTextPaint.setStrokeWidth(attribute.lineWidth);
    increasingTextPaint.setTextSize(attribute.labelSize);
    increasingTextPaint.setColor(attribute.increasingColor);
    increasingTextPaint.setTypeface(FontConfig.typeFace);

    decreasingTextPaint.setStrokeWidth(attribute.lineWidth);
    decreasingTextPaint.setTextSize(attribute.labelSize);
    decreasingTextPaint.setColor(attribute.decreasingColor);
    decreasingTextPaint.setTypeface(FontConfig.typeFace);

    increasingBorderPaint.setStrokeWidth(attribute.cursorBorderWidth);
    increasingBorderPaint.setStyle(Paint.Style.STROKE);
    increasingBorderPaint.setColor(attribute.increasingColor);

    decreasingBorderPaint.setStrokeWidth(attribute.cursorBorderWidth);
    decreasingBorderPaint.setStyle(Paint.Style.STROKE);
    decreasingBorderPaint.setColor(attribute.decreasingColor);

    cursorBackgroundPaint.setStyle(Paint.Style.FILL);
    cursorBackgroundPaint.setColor(attribute.cursorBackgroundColor);
  }

  @Override
  public void computePoint(int begin, int end, int current) {

  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
  }

  @Override
  public void onDrawOver(Canvas canvas) {
    Paint linePaint;
    Paint textPaint;
    Paint borderPaint;
    CandleEntry last = render.getAdapter().getItem(render.getAdapter().getLastPosition());
    if (last.getClose().value < last.getOpen().value) {//下跌
      linePaint = decreasingLinePaint;
      textPaint = decreasingTextPaint;
      borderPaint = decreasingBorderPaint;
    } else {//上涨或者不涨不跌
      linePaint = increasingLinePaint;
      textPaint = increasingTextPaint;
      borderPaint = increasingBorderPaint;
    }
    textPaint.getTextBounds(last.getClose().text, 0, last.getClose().text.length(), textRect);
    cursorPoint[0] = render.getAdapter().getLastPosition() + 1;
    cursorPoint[1] = last.getClose().value;
    render.mapPoints(cursorPoint);
    //textRect.width()太精准，会造成文字抖动现象，所以这里用 textPaint.measureText(）算大概值
    float textWidth = textPaint.measureText(last.getClose().text);
    float textHalfHeight = textRect.height() / 2f;
    float textX = viewRect.right - textWidth - attribute.axisLabelLRMargin;
    float textY;
    float lineRight = textX - attribute.axisLabelLRMargin;
    if (cursorPoint[0] < lineRight) {
      clickable = false;
      textY = cursorPoint[1] + textHalfHeight;
      cursorRect.left = textX;
      cursorRect.top = textY - textRect.height();
      cursorRect.right = cursorRect.left + textWidth;
      cursorRect.bottom = textY;
      //绘制游标值区域背景
      canvas.drawRect(cursorRect, cursorBackgroundPaint);
      //绘制游标值
      canvas.drawText(last.getClose().text, textX, textY, textPaint);
      //绘制游标线
      path.moveTo(cursorPoint[0], cursorPoint[1]);
      path.lineTo(lineRight, cursorPoint[1]);
      canvas.drawPath(path, linePaint);
    } else {
      clickable = true;
      //修正坐标，防止出界
      float cursorHalfHeight = textHalfHeight + attribute.axisLabelTBMargin;//游标的一半高度
      float topLimit = viewRect.top + cursorHalfHeight;
      float bottomLimit = viewRect.bottom - cursorHalfHeight;
      if (cursorPoint[1] < topLimit) {
        cursorPoint[1] = topLimit;
      } else if (cursorPoint[1] > bottomLimit) {
        cursorPoint[1] = bottomLimit;
      }
      textY = cursorPoint[1] + textHalfHeight;
      String additional = "  >";
      textX -= attribute.rightScrollOffset;
      cursorRect.left = textX - attribute.axisLabelLRMargin;
      cursorRect.top = cursorPoint[1] - cursorHalfHeight;
      cursorRect.right = textX + textWidth + textPaint.measureText(additional)
          + attribute.axisLabelLRMargin;
      cursorRect.bottom = cursorPoint[1] + cursorHalfHeight;
      //绘制游标值区域背景
      canvas.drawRoundRect(cursorRect, attribute.cursorRadius, attribute.cursorRadius,
          cursorBackgroundPaint);
      //绘制游标值区域背景圆角边框线
      canvas.drawRoundRect(cursorRect, attribute.cursorRadius, attribute.cursorRadius, borderPaint);
      //绘制游标值
      canvas.drawText(last.getClose().text.concat(additional), textX, textY, textPaint);
      //绘制游标线
      path.moveTo(viewRect.left, cursorPoint[1]);
      path.lineTo(cursorRect.left, cursorPoint[1]);
      path.moveTo(cursorRect.right, cursorPoint[1]);
      path.lineTo(viewRect.right, cursorPoint[1]);
      canvas.drawPath(path, linePaint);
    }
    path.reset();
  }

  @Override public boolean onDrawingClick(float x, float y) {
    return clickable && cursorRect.contains(x, y);
  }

  @Override public void onViewChange() {

  }
}
