
package com.wk.chart.drawing.timeLine;


import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>分时图组件</p>
 */

public class TimeLineDrawing extends IndexDrawing<CandleRender, AbsModule<?>> {
    private static final String TAG = "TimeLineDrawing";
    private CandleAttribute attribute;//配置文件
    // 分时折线画笔(绘制path 尽量不开抗锯齿)
    private final Paint timelinePaint = new Paint();
    // 分时阴影画笔(绘制path 尽量不开抗锯齿)
    private final Paint timeShaderPaint = new Paint();
    // 分时折线绘制路径
    private final Path timelinePath = new Path();
    // 分时阴影绘制路径
    private final Path timeShaderPath = new Path();
    // 折线路径位置信息
    private final float[] pathPts = new float[2];
    // 计算 1 个矩形坐标用的
    private final float[] candleRectBuffer = new float[8];
    // 高亮状态
    private boolean highlightState;
    // 蜡烛图绘制的实际收首尾X轴坐标点（从首尾两根蜡烛图的中心点算起）
    private float beginX, endX = 0;

    public TimeLineDrawing() {
        super(IndexType.TIME_LINE);
    }

    @Override
    public void onInit(CandleRender render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        timelinePaint.setStrokeWidth(attribute.timeLineWidth);
        timelinePaint.setColor(attribute.timeLineColor);
        timelinePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {
        highlightState = true;
        beginX = render.getPointX(chartModule.getMatrix(), begin + 0.5f);
        endX = render.getPointX(chartModule.getMatrix(), end - 1 + 0.5f);
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        CandleEntry entry = render.getAdapter().getItem(current);
        pathPts[0] = current + 0.5f;
        pathPts[1] = (float) entry.getClose().value;
        render.mapPoints(chartModule.getMatrix(), pathPts);
        if (current == begin) {//开始点
            float left = begin == 0 ? pathPts[0] : viewRect.left;
            timelinePath.moveTo(left, pathPts[1]);
            timeShaderPath.moveTo(left, viewRect.bottom);
            timeShaderPath.lineTo(left, pathPts[1]);
        }
        if (current == end - 1) {//结束点
            float right = end == render.getAdapter().getCount() ? pathPts[0] : viewRect.right;
            timelinePath.lineTo(right, pathPts[1]);
            timeShaderPath.lineTo(right, pathPts[1]);
            timeShaderPath.lineTo(right, viewRect.bottom);
        } else {
            timelinePath.lineTo(pathPts[0], pathPts[1]);
            timeShaderPath.lineTo(pathPts[0], pathPts[1]);
        }
        candleRectBuffer[0] = current + render.pointsSpace;
        candleRectBuffer[2] = current + 1 - render.pointsSpace;
        candleRectBuffer[4] = current;
        candleRectBuffer[6] = current + 1;
        render.mapPoints(chartModule.getMatrix(), candleRectBuffer);
        // 计算高亮坐标
        if (render.isHighlight() && highlightState) {
            final float[] highlightPoint = render.getHighlightPoint();
            if (candleRectBuffer[4] <= highlightPoint[0] && highlightPoint[0] <= candleRectBuffer[6]) {
                highlightPoint[0] = pathPts[0];
                render.getAdapter().setHighlightIndex(current);
                highlightState = false;
            } else if (highlightPoint[0] <= beginX) {
                highlightPoint[0] = beginX;
                render.getAdapter().setHighlightIndex(begin);
                highlightState = false;
            } else if (highlightPoint[0] >= endX) {
                highlightPoint[0] = endX;
                render.getAdapter().setHighlightIndex(end - 1);
                highlightState = false;
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        canvas.save();
        canvas.clipRect(viewRect);
        canvas.drawPath(timelinePath, timelinePaint);
        canvas.drawPath(timeShaderPath, timeShaderPaint);
        timelinePath.rewind();
        timeShaderPath.rewind();
        canvas.restore();
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        timeShaderPaint.setShader(
                new LinearGradient(0, viewRect.top, 0, viewRect.bottom,
                        new int[]{Utils.getColorWithAlpha(
                                attribute.timeLineColor,
                                attribute.shaderBeginColorAlpha
                        )
                                , Utils.getColorWithAlpha(
                                attribute.timeLineColor,
                                attribute.shaderEndColorAlpha
                        )},
                        null, Shader.TileMode.REPEAT
                ));
    }
}
