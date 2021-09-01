
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.CandleRender;


/**
 * <p>CursorDrawing</p>
 * 游标指示器组件
 */

public class CursorDrawing extends AbsDrawing<CandleRender, AbsModule<AbsEntry>> {
    private CandleAttribute attribute;//配置文件
    private final Paint foldedCursorLinePaint = new Paint();//（折叠时）游标线画笔
    private final TextPaint foldedCursorTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//（折叠时）游标值画笔
    private final Paint spreadCursorLinePaint = new Paint();//（展开时）游标线画笔
    private final TextPaint spreadCursorTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//展开时）游标值画笔
    private final Paint spreadCursorBorderPaint = new Paint();//展开时）游标值容器边框画笔
    private final Paint cursorBackgroundPaint = new Paint();//游标值容器背景画笔
    private final Path cursorPath = new Path();//游标绘制路径
    private final Rect foldedTextRect = new Rect();//（折叠时）游标文字显示区域
    private final Rect spreadTextRect = new Rect();//（展开时）游标文字显示区域
    private final RectF cursorRect = new RectF();//游标显示区域(不包含游标线)
    private final Path path = new Path();//游标线路径
    private final float[] cursorPoint = new float[2];//存放游标坐标
    private boolean clickable = false;//是否可以点击
    private float foldedCharsWidth, foldedTextHalfHeight = 0;//（折叠时）用于计算的文字宽度和半高
    private float spreadCharsWidth, spreadTextHalfHeight = 0;//（展开时）用于计算的文字宽度和半高
    private float triangleHalfHeight = 0;//（展开时）三角半高

    @Override
    public void onInit(CandleRender render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        this.attribute = render.getAttribute();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10f, 5f}, 0);
        foldedCursorLinePaint.setStrokeWidth(attribute.lineWidth);
        foldedCursorLinePaint.setStyle(Paint.Style.STROKE);
        foldedCursorLinePaint.setColor(attribute.foldedCursorLineColor);
        foldedCursorLinePaint.setPathEffect(dashPathEffect);

        foldedCursorTextPaint.setStrokeWidth(attribute.lineWidth);
        foldedCursorTextPaint.setTextSize(attribute.labelSize);
        foldedCursorTextPaint.setColor(attribute.foldedCursorTextColor);
        foldedCursorTextPaint.setTypeface(FontStyle.typeFace);
        foldedCursorTextPaint.setTextAlign(Paint.Align.RIGHT);

        spreadCursorLinePaint.setStrokeWidth(attribute.lineWidth);
        spreadCursorLinePaint.setStyle(Paint.Style.STROKE);
        spreadCursorLinePaint.setColor(attribute.spreadCursorLineColor);
        spreadCursorLinePaint.setPathEffect(dashPathEffect);

        spreadCursorTextPaint.setStrokeWidth(attribute.lineWidth);
        spreadCursorTextPaint.setTextSize(attribute.labelSize);
        spreadCursorTextPaint.setColor(attribute.spreadCursorTextColor);
        spreadCursorTextPaint.setTypeface(FontStyle.typeFace);

        spreadCursorBorderPaint.setStrokeWidth(attribute.spreadCursorBorderWidth);
        spreadCursorBorderPaint.setStyle(Paint.Style.STROKE);
        spreadCursorBorderPaint.setColor(attribute.spreadCursorBorderColor);

        cursorBackgroundPaint.setStyle(Paint.Style.FILL);
        cursorBackgroundPaint.setColor(attribute.cursorBackgroundColor);

        String chars = "0";
        foldedCursorTextPaint.getTextBounds(chars, 0, 1, foldedTextRect);
        foldedCharsWidth = foldedTextRect.width() + Utils.sp2px(attribute.context, 0.5f);
        spreadCursorTextPaint.getTextBounds(chars, 0, 1, spreadTextRect);
        spreadCharsWidth = spreadTextRect.width() + Utils.sp2px(attribute.context, 0.5f);
        foldedTextHalfHeight = foldedTextRect.height() / 2f;
        spreadTextHalfHeight = spreadTextRect.height() / 2f;
        triangleHalfHeight = attribute.spreadTriangleHeight / 2f;
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
        cursorPoint[0] = render.getAdapter().getLastPosition() + 1;
        cursorPoint[1] = last.getClose().value;
        render.mapPoints(cursorPoint);
        String value = render.getAdapter().rateConversion(last.getClose().value,
                render.getAdapter().getScale().getQuoteScale(), true);
        //防止文字抖动现象
        float textWidth = foldedCharsWidth * (float) value.length();
        float textRight = viewRect.right - attribute.axisLabelLRMargin;
        float textLeft = textRight - textWidth;
        float textY;
        if (cursorPoint[0] < textLeft) {
            clickable = false;
            textY = cursorPoint[1] + foldedTextHalfHeight;
            cursorRect.left = textLeft;
            cursorRect.top = textY - foldedTextRect.height();
            cursorRect.right = textRight;
            cursorRect.bottom = textY;
            //绘制游标值区域背景
            canvas.drawRect(cursorRect, cursorBackgroundPaint);
            //绘制游标值
            canvas.drawText(value, cursorRect.right, textY, foldedCursorTextPaint);
            //绘制游标线
            path.moveTo(cursorPoint[0], cursorPoint[1]);
            path.lineTo(textLeft, cursorPoint[1]);
            canvas.drawPath(path, foldedCursorLinePaint);
        } else {
            clickable = true;
            //防止文字抖动现象
            textWidth = spreadCharsWidth * (float) value.length();
            //修正坐标，防止出界
            float cursorHalfHeight = spreadTextHalfHeight + attribute.spreadCursorTextTBMargin;//游标的一半高度
            float topLimit = viewRect.top + cursorHalfHeight;
            float bottomLimit = viewRect.bottom - cursorHalfHeight;
            if (cursorPoint[1] < topLimit) {
                cursorPoint[1] = topLimit;
            } else if (cursorPoint[1] > bottomLimit) {
                cursorPoint[1] = bottomLimit;
            }
            textY = cursorPoint[1] + spreadTextHalfHeight;
            textRight -= (attribute.rightScrollOffset + attribute.spreadCursorTextLRMargin);
            textLeft = textRight - textWidth;
            cursorRect.left = textLeft - attribute.spreadCursorTextLRMargin;
            cursorRect.top = cursorPoint[1] - cursorHalfHeight;
            cursorRect.right = textRight + attribute.spreadCursorTextLRMargin;
            cursorRect.bottom = cursorPoint[1] + cursorHalfHeight;
            //绘制游标值区域背景
            float cursorRight = cursorRect.right + attribute.spreadTriangleWidth;
            cursorPath.rewind();
            cursorPath.moveTo(cursorRect.left, cursorRect.top);
            cursorPath.lineTo(cursorRect.right, cursorRect.top);
            cursorPath.lineTo(cursorRect.right, cursorRect.top + cursorHalfHeight - triangleHalfHeight);
            cursorPath.lineTo(cursorRight, cursorRect.top + cursorHalfHeight);
            cursorPath.lineTo(cursorRect.right, cursorRect.top + cursorHalfHeight + triangleHalfHeight);
            cursorPath.lineTo(cursorRect.right, cursorRect.bottom);
            cursorPath.lineTo(cursorRect.left, cursorRect.bottom);
            cursorPath.lineTo(cursorRect.left, cursorRect.top);
            canvas.drawPath(cursorPath, cursorBackgroundPaint);
            canvas.drawPath(cursorPath, spreadCursorBorderPaint);
            //绘制游标值
            canvas.drawText(value, textLeft, textY, spreadCursorTextPaint);
            //绘制游标线
            path.moveTo(viewRect.left, cursorPoint[1]);
            path.lineTo(cursorRect.left, cursorPoint[1]);
            path.moveTo(cursorRight, cursorPoint[1]);
            path.lineTo(viewRect.right, cursorPoint[1]);
            canvas.drawPath(path, spreadCursorLinePaint);
        }
        path.rewind();
    }

    @Override
    public boolean onDrawingClick(float x, float y) {
        return clickable && cursorRect.contains(x, y);
    }
}
