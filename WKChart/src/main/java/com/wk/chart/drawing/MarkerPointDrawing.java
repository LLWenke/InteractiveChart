
package com.wk.chart.drawing;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.MarkerPointType;
import com.wk.chart.interfaces.IMarkerPoint;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>MarkerPointDrawing</p>
 * 标记点绘制组件
 */

public class MarkerPointDrawing extends AbsDrawing<CandleRender, AbsModule<AbsEntry>> {
    private static final String TAG = "MarkerPointDrawing";
    //配置文件
    private CandleAttribute attribute;
    //标记点绘制组件接口
    private IMarkerPoint iMarkerPoint;
    // 标记点画笔
    private final Paint markerPointsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 标记点连接线画笔
    private final Paint markerPointsLinePaint = new Paint();
    // 标记点文字画笔
    private final TextPaint markerPointsTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    // B标记点绘制路径
    private final Path markerPointsPathB = new Path();
    // S标记点绘制路径
    private final Path markerPointsPathS = new Path();
    // T标记点绘制路径
    private final Path markerPointsPathT = new Path();
    // B标记点连接线绘制路径
    private final Path markerPointsLinePathB = new Path();
    // S标记点连接线绘制路径
    private final Path markerPointsLinePathS = new Path();
    // T标记点连接线绘制路径
    private final Path markerPointsLinePathT = new Path();
    // 用于计算标记点绘制路径的起始位置
    private final float[] pointBuffer = new float[4];
    //可用区域，上下都分为标签上，标签下两个可用区域，共4个可用区域
    private final float[] availableAreas = new float[16];
    // 标记点绘制区域宽度的一半/高度/总高度（包含三角形高度）/连接点大小/文字Y轴坐标/三角形高度
    private float pointRectHalfWidth, pointRectHeight, totalRectHeight, jointSize, textY, triangleHeight;
    // 当前标记点区域范围矩形
    private final Rect markerRect = new Rect();
    // 当前标记点文字范围矩形
    private final Rect textRect = new Rect();
    // 标记点下标
    private int position = 0;
    // 标记点文字数组
    private String[] texts;
    // 标记点文字坐标数组
    private float[] textPointBuffer;

    @Override
    public void onInit(CandleRender render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();
        iMarkerPoint = (IMarkerPoint) chartModule;

        markerPointsLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        markerPointsLinePaint.setStrokeWidth(attribute.markerPointLineWidth);
        markerPointsLinePaint.setPathEffect(new DashPathEffect(
                new float[]{attribute.markerPointLineWidth,
                        attribute.markerPointLineWidth}, 0));

        markerPointsPaint.setStyle(Paint.Style.FILL);

        markerPointsTextPaint.setStyle(Paint.Style.FILL);
        markerPointsTextPaint.setColor(attribute.markerPointTextColor);
        markerPointsTextPaint.setTextSize(attribute.markerPointTextSize);
        markerPointsTextPaint.setTextAlign(Paint.Align.CENTER);
        markerPointsTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        markerPointsTextPaint.getTextBounds("S", 0, 1, textRect);
        pointRectHalfWidth = (int) ((textRect.width() + attribute.markerPointTextMarginHorizontal * 2) / 2);
        pointRectHeight = (textRect.height() + attribute.markerPointTextMarginVertical * 2);
        jointSize = (int) (attribute.markerPointJointRadius * 2);
        textY = (pointRectHeight - textRect.height()) / 2;
        triangleHeight = jointSize - jointSize / 3;
        totalRectHeight = pointRectHeight + triangleHeight;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {
        position = 0;
        if (null == texts || iMarkerPoint.getMarkerPointCount() > texts.length) {
            texts = new String[iMarkerPoint.getMarkerPointCount()];
            textPointBuffer = new float[texts.length * 2];
        }
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        CandleEntry entry = render.getAdapter().getItem(current);
        switch (entry.getMarkerPointType()) {
            case MarkerPointType.B://买入
                calculationCoordinate(entry, begin, end, current);
                calculationPath(markerPointsPathB, markerPointsLinePathB, "B");
                break;
            case MarkerPointType.S://卖出
                calculationCoordinate(entry, begin, end, current);
                calculationPath(markerPointsPathS, markerPointsLinePathS, "S");
                break;
            case MarkerPointType.T://买入&卖出
                calculationCoordinate(entry, begin, end, current);
                calculationPath(markerPointsPathT, markerPointsLinePathT, "T");
                break;
        }
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (iMarkerPoint.getMarkerPointCount() == 0) {
            return;
        }
        canvas.save();
        canvas.clipRect(viewRect);

        markerPointsPaint.setColor(attribute.markerPointColorB);
        canvas.drawPath(markerPointsPathB, markerPointsPaint);
        markerPointsPaint.setColor(attribute.markerPointColorS);
        canvas.drawPath(markerPointsPathS, markerPointsPaint);
        markerPointsPaint.setColor(attribute.markerPointColorT);
        canvas.drawPath(markerPointsPathT, markerPointsPaint);
        markerPointsLinePaint.setColor(attribute.markerPointColorB);
        canvas.drawPath(markerPointsLinePathB, markerPointsLinePaint);
        markerPointsLinePaint.setColor(attribute.markerPointColorS);
        canvas.drawPath(markerPointsLinePathS, markerPointsLinePaint);
        markerPointsLinePaint.setColor(attribute.markerPointColorT);
        canvas.drawPath(markerPointsLinePathT, markerPointsLinePaint);

        for (int i = 0; i < iMarkerPoint.getMarkerPointCount() && i < texts.length; i++) {
            int index = i * 2;
            canvas.drawText(texts[i], textPointBuffer[index], textPointBuffer[index + 1], markerPointsTextPaint);
        }

        markerPointsPathB.rewind();
        markerPointsPathS.rewind();
        markerPointsPathT.rewind();

        markerPointsLinePathB.rewind();
        markerPointsLinePathS.rewind();
        markerPointsLinePathT.rewind();

        canvas.restore();
    }

    /**
     * 计算标记点位置
     */
    private void calculationCoordinate(CandleEntry entry, int begin, int end, int current) {
        pointBuffer[0] = current + 0.5f;
        pointBuffer[1] = iMarkerPoint.getHighPoint(entry);
        pointBuffer[3] = iMarkerPoint.getLowPoint(entry);
        render.mapPoints(pointBuffer);
        //计算顶部可用区域
        calculationAvailableAreas(begin, end, current);
        float topMarkerTop = availableAreas[5] - availableAreas[7];//标签上（方向：上）
        float topMarkerBottom = availableAreas[1] - availableAreas[3];//标签下（方向：上）
        float high = pointBuffer[1] - attribute.markerPointJointMargin - jointSize;
        float low = pointBuffer[3] + attribute.markerPointJointMargin + jointSize;
        float y;
        if (topMarkerBottom >= totalRectHeight) {
            y = availableAreas[1] - triangleHeight;
            y -= (high - y < attribute.markerPointLineDefaultLength ? Math.min(attribute.markerPointLineDefaultLength, topMarkerBottom - totalRectHeight) : 0);
            updateMarkerRect(entry, availableAreas[0], y - pointRectHeight, availableAreas[2], y);
            return;
        } else if (topMarkerTop >= totalRectHeight) {
            y = availableAreas[5] - triangleHeight;
            y -= (high - y < attribute.markerPointLineDefaultLength ? Math.min(attribute.markerPointLineDefaultLength, topMarkerTop - totalRectHeight) : 0);
            updateMarkerRect(entry, availableAreas[0], y - pointRectHeight, availableAreas[2], y);
            return;
        }
        //计算底部可用区域
        float bottomMarkerTop = availableAreas[11] - availableAreas[9];//标签上（方向：下）
        float bottomMarkerBottom = availableAreas[15] - availableAreas[13];//标签下（方向：下）
        if (bottomMarkerTop >= totalRectHeight) {
            y = availableAreas[9] + triangleHeight;
            y += (y - low < attribute.markerPointLineDefaultLength ? Math.min(attribute.markerPointLineDefaultLength, bottomMarkerTop - totalRectHeight) : 0);
            updateMarkerRect(entry, availableAreas[0], y, availableAreas[2], y + pointRectHeight);
        } else if (bottomMarkerBottom >= totalRectHeight) {
            y = availableAreas[13] + triangleHeight;
            y += (y - low < attribute.markerPointLineDefaultLength ? Math.min(attribute.markerPointLineDefaultLength, bottomMarkerBottom - totalRectHeight) : 0);
            updateMarkerRect(entry, availableAreas[0], y, availableAreas[2], y + pointRectHeight);
        } else if (bottomMarkerBottom >= topMarkerTop) {
            y = availableAreas[13] + triangleHeight;
            updateMarkerRect(entry, availableAreas[0], y, availableAreas[2], y + pointRectHeight);
        } else {
            y = availableAreas[5] - triangleHeight;
            updateMarkerRect(entry, availableAreas[0], y - pointRectHeight, availableAreas[2], y);
        }
    }

    /**
     * 更新标记点矩形坐标
     */
    private void updateMarkerRect(CandleEntry entry, float left, float top, float right, float bottom) {
        markerRect.set((int) left, (int) top, (int) right, (int) bottom);
        entry.updateMarkerRect(markerRect.left - attribute.markerPointMinMargin,
                markerRect.top - attribute.markerPointMinMargin,
                markerRect.right + attribute.markerPointMinMargin,
                markerRect.bottom + attribute.markerPointMinMargin + triangleHeight);
    }

    /**
     * 计算可用区域
     */
    private void calculationAvailableAreas(int begin, int end, int current) {
        availableAreas[0] = pointBuffer[0] - pointRectHalfWidth;
        availableAreas[2] = pointBuffer[0] + pointRectHalfWidth;
        availableAreas[1] = availableAreas[5] = pointBuffer[1] - attribute.markerPointJointMargin - jointSize;
        availableAreas[3] = availableAreas[7] = viewRect.top;
        availableAreas[9] = availableAreas[13] = pointBuffer[3] + attribute.markerPointJointMargin + jointSize;
        availableAreas[11] = availableAreas[15] = viewRect.bottom;
        boolean doLeft = true, doRight = true;
        int index = 0;
        while (doLeft || doRight) {
            index++;
            int rightIndex = current + index;
            int leftIndex = current - index;
            //向左修正（方向：上）
            leftCheck:
            if (doLeft) {
                if (leftIndex < begin) {
                    doLeft = false;
                    break leftCheck;
                }
                CandleEntry left = render.getAdapter().getItem(leftIndex);
                Rect leftRect = left.getMarkerPointRect();
                float[] buffer = absChartModule.getPointRect(render, left, leftIndex);
                float right = Math.max(buffer[2], leftRect.right);
                if (availableAreas[0] >= right) {
                    doLeft = false;
                    break leftCheck;
                }
                if (buffer[1] < availableAreas[1]) {
                    availableAreas[1] = buffer[1];
                }
                if (buffer[3] > availableAreas[9]) {
                    availableAreas[9] = buffer[3];
                }
                if (leftRect.bottom <= buffer[1]) {
                    float minTop = leftRect.top < availableAreas[1] ? leftRect.top : availableAreas[1];
                    if (minTop - availableAreas[3] >= totalRectHeight) {
                        availableAreas[1] = minTop;
                    } else if (leftRect.bottom > availableAreas[3]) {
                        availableAreas[3] = leftRect.bottom;
                    }
                    if (leftRect.top < availableAreas[5]) {
                        availableAreas[5] = leftRect.top;
                    }
                }
                if (leftRect.top >= buffer[3]) {
                    float maxTop = leftRect.bottom > availableAreas[9] ? leftRect.bottom : availableAreas[9];
                    if (availableAreas[11] - maxTop >= totalRectHeight) {
                        availableAreas[9] = maxTop;
                    } else if (leftRect.top < availableAreas[11]) {
                        availableAreas[11] = leftRect.top;
                    }
                    if (leftRect.bottom > availableAreas[13]) {
                        availableAreas[13] = leftRect.bottom;
                    }
                }
            }
            //向右修正（方向：上）
            rightCheck:
            if (doRight) {
                if (rightIndex >= end) {
                    doRight = false;
                    break rightCheck;
                }
                CandleEntry right = render.getAdapter().getItem(rightIndex);
                float[] buffer = absChartModule.getPointRect(render, right, rightIndex);
                if (availableAreas[2] <= buffer[0]) {
                    doRight = false;
                    break rightCheck;
                }
                if (buffer[1] < availableAreas[1]) {
                    availableAreas[1] = buffer[1];
                }
                if (buffer[1] < availableAreas[5]) {
                    availableAreas[5] = buffer[1];
                }
                if (buffer[3] > availableAreas[9]) {
                    availableAreas[9] = buffer[3];
                }
                if (buffer[3] > availableAreas[13]) {
                    availableAreas[13] = buffer[3];
                }
            }
        }
    }

    /**
     * 路径计算
     *
     * @param markerPointsPath     标记点绘制路径
     * @param markerPointsLinePath 标记点连接线绘制路径
     */
    private void calculationPath(Path markerPointsPath, Path markerPointsLinePath, String
            markerText) {
        float x = pointBuffer[0];
        float y = pointBuffer[1];
        if (markerRect.bottom <= y) {
            //计算起始坐标
            y -= attribute.markerPointJointMargin;
            //计算圆点坐标
            markerPointsPath.moveTo(x, y);
            markerPointsPath.addCircle(x, y - attribute.markerPointJointRadius, attribute.markerPointJointRadius, Path.Direction.CW);
            //计算连接线坐标
            y -= jointSize;
            markerPointsLinePath.moveTo(x, y);
            y = markerRect.bottom + triangleHeight;
            markerPointsLinePath.lineTo(x, y);
            //计算标签区域坐标
            markerPointsPath.moveTo(x, y);
            markerPointsPath.lineTo(x - attribute.markerPointJointRadius, markerRect.bottom);
            markerPointsPath.lineTo(markerRect.left, markerRect.bottom);
            markerPointsPath.lineTo(markerRect.left, markerRect.top);
            markerPointsPath.lineTo(markerRect.right, markerRect.top);
            markerPointsPath.lineTo(markerRect.right, markerRect.bottom);
            markerPointsPath.lineTo(x + attribute.markerPointJointRadius, markerRect.bottom);
            markerPointsPath.close();
            y = markerRect.bottom - textY;
        } else {
            y = pointBuffer[3];
            //计算起始坐标
            y += attribute.markerPointJointMargin;
            //计算圆点坐标
            markerPointsPath.moveTo(x, y);
            markerPointsPath.addCircle(x, y + attribute.markerPointJointRadius, attribute.markerPointJointRadius, Path.Direction.CW);
            //计算连接线坐标
            y += jointSize;
            markerPointsLinePath.moveTo(x, y);
            y = markerRect.top - triangleHeight;
            markerPointsLinePath.lineTo(x, y);
            //计算三角形坐标
            markerPointsPath.moveTo(x, y);
            markerPointsPath.lineTo(x - attribute.markerPointJointRadius, markerRect.top);
            markerPointsPath.lineTo(markerRect.left, markerRect.top);
            markerPointsPath.lineTo(markerRect.left, markerRect.bottom);
            markerPointsPath.lineTo(markerRect.right, markerRect.bottom);
            markerPointsPath.lineTo(markerRect.right, markerRect.top);
            markerPointsPath.lineTo(x + attribute.markerPointJointRadius, markerRect.top);
            markerPointsPath.close();
            y = markerRect.top + textRect.height() + textY;
        }
        if (position < texts.length) {
            texts[position] = markerText;
            int index = position * 2;
            textPointBuffer[index] = x;
            textPointBuffer[index + 1] = y;
            position++;
        }
    }

}
