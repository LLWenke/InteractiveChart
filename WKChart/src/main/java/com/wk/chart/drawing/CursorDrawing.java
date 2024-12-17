
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
import com.wk.chart.interfaces.IDrawingClickListener;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.CandleRender;


/**
 * <p>CursorDrawing</p>
 * 游标指示器组件
 */

public class CursorDrawing extends AbsDrawing<CandleRender, AbsModule<AbsEntry>> implements IDrawingClickListener {
    private CandleAttribute attribute;//配置文件
    private final Paint foldedCursorLinePaint = new Paint();//（折叠时）游标线画笔
    private final TextPaint foldedCursorTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//（折叠时）游标值画笔
    private final Paint spreadCursorLinePaint = new Paint();//（展开时）游标线画笔
    private final TextPaint spreadCursorTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//展开时）游标值画笔
    private final Paint spreadCursorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//展开时）游标值容器边框画笔
    private final Paint cursorBackgroundPaint = new Paint();//游标值容器背景画笔
    private final Path cursorPath = new Path();//游标绘制路径
    private final RectF cursorRect = new RectF();//游标显示区域(不包含游标线)
    private final Path path = new Path();//游标线路径
    private final float[] cursorPoint = new float[2];//存放游标坐标
    private boolean clickable = false;//是否可以点击
    private float foldedTextHeight;//（折叠时）游标单文字高度
    private float spreadTextHeight;//（展开时）游标单文字高度
    private float triangleHalfHeight = 0;//（展开时）三角半高

    public CursorDrawing(int id) {
        super(id);
    }

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

        triangleHalfHeight = attribute.spreadTriangleHeight / 2f;
        foldedTextHeight = Utils.measureTextArea(foldedCursorTextPaint, new Rect()).height();
        spreadTextHeight = Utils.measureTextArea(spreadCursorTextPaint, new Rect()).height();
    }

    @Override
    public void drawOver(Canvas canvas) {
        CandleEntry last = render.getAdapter().getItem(render.getAdapter().getLastPosition());
        cursorPoint[0] = render.getAdapter().getLastPosition() + 1;
        cursorPoint[1] = last.getClose().value;
        render.mapPoints(chartModule.getMatrix(), cursorPoint);
        String value = render.getAdapter().rateConversion(last.getClose(), false, false);
        //防止文字抖动现象
        float textWidth = foldedCursorTextPaint.measureText(value);
        float cursorRight = viewRect.right - attribute.axisLabelMarginHorizontal;
        float textLeft = cursorRight - textWidth;
        float textY;
        if (cursorPoint[0] < textLeft) {//折叠
            clickable = false;
            textY = cursorPoint[1] + foldedTextHeight / 2f;
            cursorRect.left = textLeft;
            cursorRect.top = textY - foldedTextHeight;
            cursorRect.right = cursorRight;
            cursorRect.bottom = textY;
            //绘制游标值区域背景
            canvas.drawRect(cursorRect, cursorBackgroundPaint);
            //绘制游标值
            canvas.drawText(value, cursorRect.right, textY, foldedCursorTextPaint);
            //绘制游标线
            path.moveTo(cursorPoint[0], cursorPoint[1]);
            path.lineTo(textLeft, cursorPoint[1]);
            canvas.drawPath(path, foldedCursorLinePaint);
        } else {//展开
            clickable = true;
            //防止文字抖动现象
            textWidth = spreadCursorTextPaint.measureText(value);
            float spreadTextHalfHeight = spreadTextHeight / 2f;
            float spreadCursorHalfHeight = spreadTextHalfHeight + attribute.spreadCursorPaddingVertical + attribute.spreadCursorBorderWidth;
            float topLimit = viewRect.top + spreadCursorHalfHeight;
            float bottomLimit = viewRect.bottom - spreadCursorHalfHeight;
            if (cursorPoint[1] < topLimit) {
                cursorPoint[1] = topLimit;
            } else if (cursorPoint[1] > bottomLimit) {
                cursorPoint[1] = bottomLimit;
            }
            textY = cursorPoint[1] + spreadTextHalfHeight;
            cursorRight -= attribute.rightScrollOffset;
            textLeft = cursorRight - textWidth - attribute.spreadCursorBorderWidth - attribute.spreadCursorPaddingHorizontal - attribute.spreadTriangleWidth;
            cursorRect.left = textLeft - attribute.spreadCursorBorderWidth - attribute.spreadCursorPaddingHorizontal;
            cursorRect.top = cursorPoint[1] - spreadCursorHalfHeight;
            cursorRect.right = cursorRight - attribute.spreadTriangleWidth;
            cursorRect.bottom = cursorPoint[1] + spreadCursorHalfHeight;
            //绘制游标值区域背景
            cursorPath.rewind();
            cursorPath.moveTo(cursorRect.left + attribute.spreadCursorRadius, cursorRect.top);
            cursorPath.lineTo(cursorRect.right - attribute.spreadCursorRadius, cursorRect.top);
            cursorPath.quadTo(cursorRect.right, cursorRect.top, cursorRect.right, cursorRect.top + attribute.spreadCursorRadius);
            cursorPath.lineTo(cursorRect.right, cursorRect.top + spreadCursorHalfHeight - triangleHalfHeight);
            cursorPath.lineTo(cursorRect.right + attribute.spreadTriangleWidth, cursorRect.top + spreadCursorHalfHeight);
            cursorPath.lineTo(cursorRect.right, cursorRect.top + spreadCursorHalfHeight + triangleHalfHeight);
            cursorPath.lineTo(cursorRect.right, cursorRect.bottom - attribute.spreadCursorRadius);
            cursorPath.quadTo(cursorRect.right, cursorRect.bottom, cursorRect.right - attribute.spreadCursorRadius, cursorRect.bottom);
            cursorPath.lineTo(cursorRect.left + attribute.spreadCursorRadius, cursorRect.bottom);
            cursorPath.quadTo(cursorRect.left, cursorRect.bottom, cursorRect.left, cursorRect.bottom - attribute.spreadCursorRadius);
            cursorPath.lineTo(cursorRect.left, cursorRect.top + attribute.spreadCursorRadius);
            cursorPath.quadTo(cursorRect.left, cursorRect.top, cursorRect.left + attribute.spreadCursorRadius, cursorRect.top);
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
