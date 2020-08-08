
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.module.base.MainChartModule;
import com.wk.chart.render.CandleRender;


/**
 * <p>CursorDrawing</p>
 * 游标指示器组件
 */

public class CursorDrawing extends AbsDrawing<CandleRender, MainChartModule> {
    private CandleAttribute attribute;//配置文件
    private Paint cursorLinePaint = new Paint();//游标线画笔
    private TextPaint cursorTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//游标值画笔
    private Paint cursorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//游标值容器边框画笔
    private Paint cursorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//游标值容器背景画笔

    private Path cursorIconPath = new Path();//游标的图标绘制路径
    private Rect textRect = new Rect();//游标文字显示区域
    private RectF cursorRect = new RectF();//游标显示区域(不包含游标线)
    private Path path = new Path();//游标线路径
    private float[] cursorPoint = new float[2];//存放游标坐标
    private boolean clickable = false;//是否可以点击
    private float charsWidth, textHalfHeight = 0;//用于计算的文字宽度和半高
    private float iconWidth, iconHeight = 0;//游标的图标的宽高（这里以游标中"一个字符"的宽高来定义）

    @Override
    public void onInit(CandleRender render, MainChartModule chartModule) {
        super.onInit(render, chartModule);
        this.attribute = render.getAttribute();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10f, 5f}, 0);
        cursorLinePaint.setStrokeWidth(attribute.lineWidth);
        cursorLinePaint.setStyle(Paint.Style.STROKE);
        cursorLinePaint.setColor(attribute.cursorLineColor);
        cursorLinePaint.setPathEffect(dashPathEffect);

        cursorTextPaint.setStrokeWidth(attribute.lineWidth);
        cursorTextPaint.setTextSize(attribute.labelSize);
        cursorTextPaint.setColor(attribute.cursorTextColor);
        cursorTextPaint.setTypeface(FontStyle.typeFace);

        cursorBorderPaint.setStrokeWidth(attribute.cursorBorderWidth);
        cursorBorderPaint.setStyle(Paint.Style.STROKE);
        cursorBorderPaint.setColor(attribute.cursorBorderColor);

        cursorBackgroundPaint.setStyle(Paint.Style.FILL);
        cursorBackgroundPaint.setColor(attribute.cursorBackgroundColor);

        String chars = "0";
        cursorTextPaint.getTextBounds(chars, 0, 1, textRect);
        iconWidth = textRect.width();
        iconHeight = textRect.height() - 2;
        charsWidth = cursorTextPaint.measureText(chars);
        textHalfHeight = textRect.height() / 2f;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
    }

    @Override
    public void drawOver(Canvas canvas) {
        CandleEntry last = render.getAdapter().getItem(render.getAdapter().getLastPosition());
        cursorTextPaint.getTextBounds(last.getClose().text, 0, last.getClose().text.length(), textRect);
        cursorPoint[0] = render.getAdapter().getLastPosition() + 1;
        cursorPoint[1] = last.getClose().value;
        render.mapPoints(cursorPoint);
        //防止文字抖动现象
        float textWidth = charsWidth * last.getClose().text.length();
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
            canvas.drawText(last.getClose().text, textX, textY, cursorTextPaint);
            //绘制游标线
            path.moveTo(cursorPoint[0], cursorPoint[1]);
            path.lineTo(lineRight, cursorPoint[1]);
            canvas.drawPath(path, cursorLinePaint);
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
            textX -= attribute.rightScrollOffset;
            cursorRect.left = textX - attribute.axisLabelLRMargin;
            cursorRect.top = cursorPoint[1] - cursorHalfHeight;
            cursorRect.right = textX + textWidth + iconWidth + attribute.axisLabelLRMargin * 2f;
            cursorRect.bottom = cursorPoint[1] + cursorHalfHeight;
            //绘制游标值区域背景
            canvas.drawRoundRect(cursorRect, attribute.cursorRadius, attribute.cursorRadius,
                    cursorBackgroundPaint);
            //绘制游标值区域背景圆角边框线
            canvas.drawRoundRect(cursorRect, attribute.cursorRadius, attribute.cursorRadius, cursorBorderPaint);
            //绘制游标值
            canvas.drawText(last.getClose().text, textX, textY, cursorTextPaint);
            //绘制游标icon
            cursorIconPath.rewind();
            float iconX = cursorRect.right - attribute.axisLabelLRMargin - iconWidth;
            cursorIconPath.moveTo(iconX, textY - iconHeight);
            cursorIconPath.lineTo(iconX + iconWidth, textY - iconHeight / 2f);
            cursorIconPath.lineTo(iconX, textY);
            canvas.drawPath(cursorIconPath, cursorBorderPaint);
            //绘制游标线
            path.moveTo(viewRect.left, cursorPoint[1]);
            path.lineTo(cursorRect.left, cursorPoint[1]);
            path.moveTo(cursorRect.right, cursorPoint[1]);
            path.lineTo(viewRect.right, cursorPoint[1]);
            canvas.drawPath(path, cursorLinePaint);
        }
        path.rewind();
    }

    @Override
    public boolean onDrawingClick(float x, float y) {
        return clickable && cursorRect.contains(x, y);
    }

    @Override
    public void onViewChange() {
    }
}
