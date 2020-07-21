
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
import com.wk.chart.module.DepthChartModule;
import com.wk.chart.render.DepthRender;

/**
 * <p>DepthDrawing</p>
 */

public class DepthDrawing extends AbsDrawing<DepthRender, DepthChartModule> {
    private static final String TAG = "DepthDrawing";
    private DepthAttribute attribute;//配置文件
    // 边框线画笔
    private Paint borderPaint = new Paint();
    // 买单折线画笔(绘制path 尽量不开抗锯齿)
    private Paint bidPolylinePaint = new Paint();
    // 卖单折线画笔(绘制path 尽量不开抗锯齿)
    private Paint askPolylinePaint = new Paint();
    // 买单阴影画笔(绘制path 尽量不开抗锯齿)
    private Paint bidShaderPaint = new Paint();
    // 卖单阴影画笔(绘制path 尽量不开抗锯齿)
    private Paint askShaderPaint = new Paint();
    // 买单绘制路径
    private Path bidPath = new Path();
    // 卖单绘制路径
    private Path askPath = new Path();
    // 边框线绘制路径
    private Path borderPath = new Path();
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
    public void onInit(DepthRender render, DepthChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(attribute.borderWidth);
        borderPaint.setColor(attribute.borderColor);

        bidPolylinePaint.setStrokeWidth(attribute.polylineWidth);
        bidPolylinePaint.setColor(attribute.increasingColor);
        bidPolylinePaint.setStyle(Paint.Style.STROKE);

        askPolylinePaint.setStrokeWidth(attribute.polylineWidth);
        askPolylinePaint.setColor(attribute.decreasingColor);
        askPolylinePaint.setStyle(Paint.Style.STROKE);

        offset = chartModule.getxOffset();
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
        bidPath.reset();
        askPath.reset();
        canvas.restore();
    }

    @Override
    public void drawOver(Canvas canvas) {
        // 绘制外层边框线
        if (attribute.borderWidth > 0) {
            borderPath.moveTo(borderPts[0], borderPts[1]);
            borderPath.lineTo(borderPts[0], borderPts[3]);
            borderPath.lineTo(borderPts[2], borderPts[3]);
            borderPath.lineTo(borderPts[2], borderPts[1]);
//          borderPath.close();
            canvas.drawPath(borderPath, borderPaint);
            borderPath.reset();
        }
    }

    @Override
    public void onViewChange() {
        super.onViewChange();
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
