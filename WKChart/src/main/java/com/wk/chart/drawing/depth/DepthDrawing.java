
package com.wk.chart.drawing.depth;


import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.module.DepthModule;
import com.wk.chart.render.DepthRender;

/**
 * <p>DepthDrawing</p>
 */

public class DepthDrawing extends AbsDrawing<DepthRender, DepthModule> {
    private static final String TAG = "DepthDrawing";
    private DepthAttribute attribute;//配置文件
    // 买单折线画笔(绘制path 尽量不开抗锯齿)
    private final Paint bidPolylinePaint = new Paint();
    // 卖单折线画笔(绘制path 尽量不开抗锯齿)
    private final Paint askPolylinePaint = new Paint();
    // 买单阴影画笔(绘制path 尽量不开抗锯齿)
    private final Paint bidShaderPaint = new Paint();
    // 卖单阴影画笔(绘制path 尽量不开抗锯齿)
    private final Paint askShaderPaint = new Paint();
    // 买单绘制路径
    private final Path bidPath = new Path();
    // 卖单绘制路径
    private final Path askPath = new Path();
    // 绘制路径
    private Path path;
    // 路径位置信息
    private final float[] pathPts = new float[2];
    // 当前entry的类型
    private int previousType = -1;
    //用于判断是否计算高亮元素
    private float left, right;
    //用于修正折线偏移量
    private float offset;

    @Override
    public void onInit(DepthRender render, DepthModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        bidPolylinePaint.setStrokeWidth(attribute.polylineWidth);
        bidPolylinePaint.setColor(attribute.increasingColor);
        bidPolylinePaint.setStyle(Paint.Style.STROKE);

        askPolylinePaint.setStrokeWidth(attribute.polylineWidth);
        askPolylinePaint.setColor(attribute.decreasingColor);
        askPolylinePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        float x0, x1;
        DepthEntry entry = render.getAdapter().getItem(current);
        pathPts[0] = entry.getPrice().value;
        pathPts[1] = entry.getTotalAmount().value;
        //计算数据点
        if (previousType != entry.getType()) {
            previousType = entry.getType();
            path = previousType == DepthAdapter.BID ? bidPath : askPath;
            offset = -offset;
            render.mapPoints(pathPts, offset, 0);
            path.moveTo(pathPts[0], viewRect.bottom);
        } else {
            render.mapPoints(pathPts, offset, 0);
        }
        path.lineTo(pathPts[0], pathPts[1]);
        //高亮点查找范围
        x0 = pathPts[0];
        //计算补位点
        if (current + 1 < end) {
            //获取补位点信息
            DepthEntry fillEntry = render.getAdapter().getItem(current + 1);
            if (previousType == fillEntry.getType()) {
                pathPts[0] = fillEntry.getPrice().value;
                pathPts[1] = entry.getTotalAmount().value;
                render.mapPoints(pathPts, offset, 0);
                path.lineTo(pathPts[0], pathPts[1]);
                x1 = pathPts[0];
            } else {
                path.lineTo(left, pathPts[1]);
                path.lineTo(left, viewRect.bottom);
                x1 = left;
            }
        } else {
            path.lineTo(right, pathPts[1]);
            path.lineTo(right, viewRect.bottom);
            x1 = right;
        }
        // 计算高亮坐标
        if (render.isHighlight() && previousType == DepthAdapter.BID ?
                (render.getHighlightPoint()[0] >= x1 && render.getHighlightPoint()[0] <= x0)
                : (render.getHighlightPoint()[0] >= x0 && render.getHighlightPoint()[0] <= x1)) {
            render.getHighlightPoint()[1] = pathPts[1];
            render.getAdapter().setHighlightIndex(current);
        }
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        canvas.save();
        canvas.clipRect(viewRect);
        canvas.drawPath(bidPath, bidShaderPaint);
        canvas.drawPath(askPath, askShaderPaint);
        canvas.drawPath(bidPath, bidPolylinePaint);
        canvas.drawPath(askPath, askPolylinePaint);
        bidPath.rewind();
        askPath.rewind();
        canvas.restore();
    }

    @Override
    public void drawOver(Canvas canvas) {
    }

    @Override
    public void onLayoutComplete() {
        offset = absChartModule.getXOffset();
        left = viewRect.left - attribute.polylineWidth;
        right = viewRect.right + attribute.polylineWidth;

        bidShaderPaint.setShader(
                new LinearGradient(0, viewRect.top, 0, viewRect.bottom,
                        new int[]{Utils.getColorWithAlpha(attribute.increasingColor, attribute.shaderBeginColorAlpha)
                                , Utils.getColorWithAlpha(attribute.increasingColor, attribute.shaderEndColorAlpha)},
                        null, Shader.TileMode.REPEAT));
        askShaderPaint.setShader(
                new LinearGradient(0, viewRect.top, 0, viewRect.bottom,
                        new int[]{Utils.getColorWithAlpha(attribute.decreasingColor, attribute.shaderBeginColorAlpha)
                                , Utils.getColorWithAlpha(attribute.decreasingColor, attribute.shaderEndColorAlpha)},
                        null, Shader.TileMode.REPEAT));
    }


}
