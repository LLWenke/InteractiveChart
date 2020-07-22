
package com.wk.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.DepthGridStyle;
import com.wk.chart.module.FloatChartModule;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.render.DepthRender;

/**
 * >Grid轴绘制组件
 * <p>DepthGridDrawing</p>
 */

public class DepthGridDrawing extends AbsDrawing<DepthRender, AbsChartModule> {
    private static final String TAG = "DepthGridDrawing";
    //配置文件
    private DepthAttribute attribute;
    // 分割线画笔
    private Paint gridDividingLinePaint = new Paint();
    // Grid 轴标签的画笔
    private TextPaint gridLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    // Grid 轴网格线画笔
    private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //用于测量文字的实际占用区域
    private Rect rect = new Rect();
    // 分割线绘制路径
    private Path dividingLinePath = new Path();
    //坐标点
    private final float[] pointCache = new float[2];
    //深度盘口的两条买卖的价格数据
    private final ValueEntry[] depthGap = new ValueEntry[2];
    //当前entry的类型
    private int previousType = -1;
    //区域宽度,gridLabel的 Grid 轴坐标
    private float regionWidth, gridLabelY;

    @Override
    public void onInit(DepthRender render, AbsChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        gridDividingLinePaint.setStyle(Paint.Style.STROKE);
        gridDividingLinePaint.setStrokeWidth(attribute.gridDividingLineWidth);
        gridDividingLinePaint.setColor(attribute.borderColor);

        gridLabelPaint.setTypeface(FontStyle.typeFace);
        gridLabelPaint.setTextSize(attribute.labelSize);
        gridLabelPaint.setColor(attribute.labelColor);
        gridLabelPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(attribute.lineWidth);
        gridPaint.setColor(attribute.labelColor);

        Utils.measureTextArea(gridLabelPaint, rect);
        setMargin(0, 0, 0,
                attribute.gridLabelMarginTop
                        + attribute.gridLabelMarginBottom
                        + rect.height()
                        + attribute.gridDividingLineWidth);
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        //盘口样式
        if (attribute.depthGridStyle == DepthGridStyle.GAP_STYLE) {
            DepthEntry entry = render.getAdapter().getItem(current);
            if (previousType != entry.getType()) {
                previousType = entry.getType();
                depthGap[previousType] = entry.getPrice();
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        //绘制盘口价格或者刻度值（取决于depthGridStyle）
        if (attribute.depthGridStyle == DepthGridStyle.GAP_STYLE) {
            for (int i = 0; i < depthGap.length; i++) {
                pointCache[0] = depthGap[i].value;
                render.mapPoints(render.getMainChartModule().getMatrix(), pointCache);
                float textCentre = gridLabelPaint.measureText(depthGap[i].text) / 2f;
                float left = viewRect.left + textCentre;
                float right = viewRect.right - textCentre;
                pointCache[0] += ((i & 1) == 0 ? -textCentre - render.getMainChartModule().getxOffset()
                        : textCentre + render.getMainChartModule().getxOffset());
                if (pointCache[0] < left) {
                    pointCache[0] = left;
                } else if (pointCache[0] > right) {
                    pointCache[0] = right;
                }
                canvas.drawText(depthGap[i].text, pointCache[0], gridLabelY, gridLabelPaint);
            }
        } else {
            String value;
            //绘制最小值
            value = absChartModule.getMinX().text;
            pointCache[0] = viewRect.left + gridLabelPaint.measureText(value) / 2f;
            canvas.drawText(value, pointCache[0], gridLabelY, gridLabelPaint);
            //绘制最大值
            value = absChartModule.getMaxX().text;
            pointCache[0] = viewRect.right - gridLabelPaint.measureText(value) / 2f;
            canvas.drawText(value, pointCache[0], gridLabelY, gridLabelPaint);

            for (int i = 1; i < attribute.gridCount; i++) {
                float x = viewRect.left + i * regionWidth;
                pointCache[0] = x - (i > (attribute.gridCount / 2) ? absChartModule.getxOffset() :
                        -absChartModule.getxOffset());
                render.invertMapPoints(render.getMainChartModule().getMatrix(), pointCache);
                value = render.exchangeRateConversion(pointCache[0],
                        render.getAdapter().getScale().getQuoteScale());
                pointCache[0] = x;
                canvas.drawText(value, pointCache[0], gridLabelY, gridLabelPaint);
            }
        }
    }

    @Override
    public void drawOver(Canvas canvas) {
        // 绘制外层边框线
        if (attribute.gridDividingLineWidth > 0) {
            dividingLinePath.moveTo(borderPts[0], borderPts[1]);
            dividingLinePath.lineTo(borderPts[0], borderPts[3]);
            dividingLinePath.lineTo(borderPts[2], borderPts[3]);
            dividingLinePath.lineTo(borderPts[2], borderPts[1]);
//          borderPath.close();
            canvas.drawPath(dividingLinePath, gridDividingLinePaint);
            dividingLinePath.rewind();
        }
    }

    @Override
    public void onViewChange() {
        float labelAreaHeight = rect.height() + attribute.gridLabelMarginTop + attribute.gridLabelMarginBottom;
        float bottom = viewRect.bottom + attribute.borderWidth;
        borderPts = render.getBorderPoints(viewRect.left,
                bottom + attribute.gridDividingLineWidth / 2f,
                viewRect.right,
                bottom + labelAreaHeight);
        regionWidth = viewRect.width() / (attribute.gridCount - 1);
        gridLabelY = bottom + rect.height() + attribute.gridLabelMarginTop;
    }

}
