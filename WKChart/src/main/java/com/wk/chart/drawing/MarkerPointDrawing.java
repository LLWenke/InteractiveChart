
package com.wk.chart.drawing;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.MarkerPointType;
import com.wk.chart.interfaces.IMarkerPoint;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.CandleRender;

import java.util.HashMap;

/**
 * <p>标记点绘制组件</p>
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
    // 当前标记点区域范围矩形
    private final RectF markerRect = new RectF();
    // 当前标记点文字范围矩形
    private final Rect textRect = new Rect();
    // 用于计算标记点绘制路径的起始位置
    private final float[] pointBuffer = new float[4];
    // 标签下，标签中，标签上三个Y轴可用区域（上）
    private final float[] topAvailableArea = new float[6];
    // 标签下，标签中，标签上三个Y轴可用区域（下）
    private final float[] bottomAvailableArea = new float[6];
    // 标记点位置区域矩形缓存
    private final HashMap<Long, RectF> catchMarkerPointRect = new HashMap<>();
    // 标记点绘制区域宽度的一半/高度/总高度（包含三角形高度）/连接点大小/文字Y轴坐标/三角形高度
    private float pointRectHalfWidth, pointRectHeight, totalRectHeight, jointSize, textY, triangleHeight;
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

        Utils.measureTextArea(markerPointsTextPaint, textRect, "S");
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
                catchMarkerRect(entry);
                break;
            case MarkerPointType.S://卖出
                calculationCoordinate(entry, begin, end, current);
                calculationPath(markerPointsPathS, markerPointsLinePathS, "S");
                catchMarkerRect(entry);
                break;
            case MarkerPointType.T://买入&卖出
                calculationCoordinate(entry, begin, end, current);
                calculationPath(markerPointsPathT, markerPointsLinePathT, "T");
                catchMarkerRect(entry);
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
        render.mapPoints(chartModule.getMatrix(), pointBuffer);
        markerRect.left = pointBuffer[0] - pointRectHalfWidth;//标签左
        markerRect.right = pointBuffer[0] + pointRectHalfWidth;//标签右
        //计算上方可用区域
        calculationAvailableAreas(begin, end, current);
        float topMarkerBottom = topAvailableArea[1] - topAvailableArea[0];//标签下方区域
        float topMarkerMiddle = topAvailableArea[3] - topAvailableArea[2];//标签中间区域
        float topMarkerTop = topAvailableArea[5] - topAvailableArea[4];//标签上方区域
        float high = pointBuffer[1] - attribute.markerPointJointMargin - jointSize;
        float low = pointBuffer[3] + attribute.markerPointJointMargin + jointSize;
        float y;
        if (topMarkerBottom >= totalRectHeight) {
            y = topAvailableArea[1] - triangleHeight;
            y -= (high - y < attribute.markerPointLineDefaultLength ?
                    Math.min(attribute.markerPointLineDefaultLength,
                            topMarkerBottom - totalRectHeight) : 0);
            markerRect.top = y - pointRectHeight;
            markerRect.bottom = y;
            return;
        } else if (topMarkerMiddle >= totalRectHeight) {
            y = topAvailableArea[3] - triangleHeight;
            y -= (high - y < attribute.markerPointLineDefaultLength ?
                    Math.min(attribute.markerPointLineDefaultLength,
                            topMarkerMiddle - totalRectHeight) : 0);
            markerRect.top = y - pointRectHeight;
            markerRect.bottom = y;
            return;
        } else if (topMarkerTop >= totalRectHeight) {
            y = topAvailableArea[5] - triangleHeight;
            y -= (high - y < attribute.markerPointLineDefaultLength ?
                    Math.min(attribute.markerPointLineDefaultLength,
                            topMarkerTop - totalRectHeight) : 0);
            markerRect.top = y - pointRectHeight;
            markerRect.bottom = y;
            return;
        }
        //计算下方可用区域
        float bottomMarkerTop = bottomAvailableArea[0] - bottomAvailableArea[1];//标签上方区域
        float bottomMarkerMiddle = bottomAvailableArea[2] - bottomAvailableArea[3];//标签中间区域
        float bottomMarkerBottom = bottomAvailableArea[4] - bottomAvailableArea[5];//标签下方区域
        if (bottomMarkerTop >= totalRectHeight) {
            y = bottomAvailableArea[1] + triangleHeight;
            y += (y - low < attribute.markerPointLineDefaultLength ?
                    Math.min(attribute.markerPointLineDefaultLength,
                            bottomMarkerTop - totalRectHeight) : 0);
            markerRect.top = y;
            markerRect.bottom = y + pointRectHeight;
        } else if (bottomMarkerMiddle >= totalRectHeight) {
            y = bottomAvailableArea[3] + triangleHeight;
            y += (y - low < attribute.markerPointLineDefaultLength ?
                    Math.min(attribute.markerPointLineDefaultLength,
                            bottomMarkerBottom - totalRectHeight) : 0);
            markerRect.top = y;
            markerRect.bottom = y + pointRectHeight;
        } else if (bottomMarkerBottom >= totalRectHeight) {
            y = bottomAvailableArea[5] + triangleHeight;
            y += (y - low < attribute.markerPointLineDefaultLength ?
                    Math.min(attribute.markerPointLineDefaultLength,
                            bottomMarkerBottom - totalRectHeight) : 0);
            markerRect.top = y;
            markerRect.bottom = y + pointRectHeight;
        } else if (bottomMarkerBottom >= topMarkerTop) {
            y = bottomAvailableArea[5] + triangleHeight;
            markerRect.top = y;
            markerRect.bottom = y + pointRectHeight;
        } else {
            y = topAvailableArea[5] - triangleHeight;
            markerRect.top = y - pointRectHeight;
            markerRect.bottom = y;
        }
    }

    /**
     * 缓存标记点矩形坐标
     */
    private void catchMarkerRect(CandleEntry entry) {
        RectF rect = catchMarkerPointRect.get(entry.getId());
        if (null == rect) {
            catchMarkerPointRect.put(entry.getId(), new RectF(markerRect.left - attribute.markerPointMinMargin,
                    markerRect.top - attribute.markerPointMinMargin,
                    markerRect.right + attribute.markerPointMinMargin,
                    markerRect.bottom + attribute.markerPointMinMargin + triangleHeight));
        } else {
            rect.set(markerRect.left - attribute.markerPointMinMargin,
                    markerRect.top - attribute.markerPointMinMargin,
                    markerRect.right + attribute.markerPointMinMargin,
                    markerRect.bottom + attribute.markerPointMinMargin + triangleHeight);
        }
    }

    /**
     * 计算可用区域
     * 对应         [下，  中，   上]
     * y轴区域矩阵【y1,y2,y3,y4,y5,y6】
     */
    private void calculationAvailableAreas(int begin, int end, int current) {
        topAvailableArea[0] = topAvailableArea[2] = topAvailableArea[4] = viewRect.top;//偶数为y1
        topAvailableArea[1] = topAvailableArea[3] = topAvailableArea[5] = pointBuffer[1] - attribute.markerPointJointMargin - jointSize;//奇数为y2
        bottomAvailableArea[0] = bottomAvailableArea[2] = bottomAvailableArea[4] = viewRect.bottom;//偶数为y2
        bottomAvailableArea[1] = bottomAvailableArea[3] = bottomAvailableArea[5] = pointBuffer[3] + attribute.markerPointJointMargin + jointSize;//奇数为y1
        boolean doLeft = true, doRight = true;
        RectF topPreviousMarkerPointRect = null;
        RectF bottomPreviousMarkerPointRect = null;
        int index = 0;
        while (doLeft || doRight) {
            index++;
            int rightIndex = current + index;
            int leftIndex = current - index;
            //向左修正
            leftCheck:
            if (doLeft) {
                if (leftIndex < begin) {
                    doLeft = false;
                    break leftCheck;
                }
                CandleEntry left = render.getAdapter().getItem(leftIndex);
                RectF markerPointRect = catchMarkerPointRect.get(left.getId());
                float[] buffer = chartModule.getPointRect(render, left, leftIndex);
                float diff = (markerRect.width() - (buffer[2] - buffer[0])) / 2f;
                float right = diff > 0 ? buffer[2] + diff : buffer[2];
                if (markerRect.left >= right) {
                    doLeft = false;
                    break leftCheck;
                }
                //上方可用区域校准
                if (buffer[1] < topAvailableArea[1]) {//标签下方区域
                    topAvailableArea[1] = buffer[1];
                }
                if (buffer[1] < topAvailableArea[3]) {//标签中间区域
                    topAvailableArea[3] = buffer[1];
                }
                if (buffer[1] < topAvailableArea[5]) {//标签上方区域
                    topAvailableArea[5] = buffer[1];
                }
                //下方可用区域校准
                if (buffer[3] > bottomAvailableArea[1]) {//标签上方区域
                    bottomAvailableArea[1] = buffer[3];
                }
                if (buffer[3] > bottomAvailableArea[3]) {//标签中间区域
                    bottomAvailableArea[3] = buffer[3];
                }
                if (buffer[3] > bottomAvailableArea[5]) {//标签下方区域
                    bottomAvailableArea[5] = buffer[3];
                }
                if (null == markerPointRect) break leftCheck;
                if (markerPointRect.bottom <= buffer[1]) {//标签在上
                    if (markerPointRect.top < topAvailableArea[5]) {//标签上方区域
                        topAvailableArea[5] = markerPointRect.top;
                    }
                    if (null == topPreviousMarkerPointRect) {//标签中间区域
                        if (markerPointRect.top < topAvailableArea[3]) {
                            topAvailableArea[3] = markerPointRect.top;
                        }
                    } else {
                        if (topPreviousMarkerPointRect.top - markerPointRect.bottom >= totalRectHeight) {//升
                            topAvailableArea[2] = markerPointRect.bottom;
                            topAvailableArea[3] = Math.min(topAvailableArea[3], topPreviousMarkerPointRect.top);
                        } else if (markerPointRect.top - topPreviousMarkerPointRect.bottom >= totalRectHeight) {//降
                            topAvailableArea[2] = topPreviousMarkerPointRect.bottom;
                            topAvailableArea[3] = Math.min(topAvailableArea[3], markerPointRect.top);
                        } else if (verticalOverlap(markerPointRect, topAvailableArea[2], topAvailableArea[3])) {
                            topAvailableArea[3] = Math.min(topAvailableArea[3], markerPointRect.top);
                        }
                    }
                    if (markerPointRect.bottom > topAvailableArea[0]) {//标签下方区域
                        topAvailableArea[0] = markerPointRect.bottom;
                    }
                    topPreviousMarkerPointRect = markerPointRect;
                } else {//标签在下
                    if (markerPointRect.top < bottomAvailableArea[0]) {//标签上方区域
                        bottomAvailableArea[0] = markerPointRect.top;
                    }
                    if (null == bottomPreviousMarkerPointRect) {//标签中间区域
                        if (markerPointRect.bottom > bottomAvailableArea[3]) {
                            bottomAvailableArea[3] = markerPointRect.bottom;
                        }
                    } else {
                        if (bottomPreviousMarkerPointRect.top - markerPointRect.bottom >= totalRectHeight) {//升
                            bottomAvailableArea[2] = bottomPreviousMarkerPointRect.top;
                            bottomAvailableArea[3] = Math.max(bottomAvailableArea[3], markerPointRect.bottom);
                        } else if (markerPointRect.top - bottomPreviousMarkerPointRect.bottom >= totalRectHeight) {//降
                            bottomAvailableArea[2] = markerPointRect.top;
                            bottomAvailableArea[3] = Math.max(bottomAvailableArea[3], bottomPreviousMarkerPointRect.bottom);
                        } else if (verticalOverlap(markerPointRect, bottomAvailableArea[3], bottomAvailableArea[2])) {
                            bottomAvailableArea[3] = Math.max(bottomAvailableArea[3], markerPointRect.bottom);
                        }
                    }
                    if (markerPointRect.bottom > bottomAvailableArea[5]) {//标签下方区域
                        bottomAvailableArea[5] = markerPointRect.bottom;
                    }
                    bottomPreviousMarkerPointRect = markerPointRect;
                }
            }
            //向右修正
            rightCheck:
            if (doRight) {
                if (rightIndex >= end) {
                    doRight = false;
                    break rightCheck;
                }
                CandleEntry right = render.getAdapter().getItem(rightIndex);
                float[] buffer = chartModule.getPointRect(render, right, rightIndex);
                if (markerRect.right <= buffer[0]) {
                    doRight = false;
                    break rightCheck;
                }
                //上方可用区域校准
                if (buffer[1] < topAvailableArea[1]) {//标签下方区域
                    topAvailableArea[1] = buffer[1];
                }
                if (buffer[1] < topAvailableArea[3]) {//标签中间区域
                    topAvailableArea[3] = buffer[1];
                }
                if (buffer[1] < topAvailableArea[5]) {//标签上方区域
                    topAvailableArea[5] = buffer[1];
                }
                //下方可用区域校准
                if (buffer[3] > bottomAvailableArea[1]) {//标签上方区域
                    bottomAvailableArea[1] = buffer[3];
                }
                if (buffer[3] > bottomAvailableArea[3]) {//标签中间区域
                    bottomAvailableArea[3] = buffer[3];
                }
                if (buffer[3] > bottomAvailableArea[5]) {//标签下方区域
                    bottomAvailableArea[5] = buffer[3];
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

    /**
     * 垂直重叠
     *
     * @param rect   举行区域
     * @param top    垂直区域顶部
     * @param bottom 垂直区域底步
     * @return 是否重叠
     */
    private boolean verticalOverlap(RectF rect, float top, float bottom) {
        return !((top < rect.top && bottom < rect.top) || (top > rect.bottom && bottom > rect.bottom));
    }
}
