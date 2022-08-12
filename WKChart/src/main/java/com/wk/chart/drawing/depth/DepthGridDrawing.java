
package com.wk.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.DepthGridStyle;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.DepthRender;

/**
 * >Grid轴绘制组件
 * <p>DepthGridDrawing</p>
 */

public class DepthGridDrawing extends AbsDrawing<DepthRender, AbsModule<AbsEntry>> {
    private static final String TAG = "DepthGridDrawing";
    //配置文件
    private DepthAttribute attribute;
    // Grid 轴标签的画笔
    private final TextPaint gridLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    // Grid 轴网格线画笔
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //用于测量文字的实际占用区域
    private final Rect rect = new Rect();
    //坐标点
    private final float[] pointCache = new float[2];
    //深度盘口的两条买卖的价格数据
    private final ValueEntry[] depthGap = new ValueEntry[2];
    //当前entry的类型
    private int previousType = -1;
    //区域宽度,gridLabel的 Grid 轴坐标
    private float regionWidth, gridLabelY;

    @Override
    public void onInit(DepthRender render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        gridLabelPaint.setTypeface(FontStyle.typeFace);
        gridLabelPaint.setTextSize(attribute.labelSize);
        gridLabelPaint.setColor(attribute.labelColor);
        gridLabelPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(attribute.lineWidth);
        gridPaint.setColor(attribute.labelColor);

        Utils.measureTextArea(gridLabelPaint, rect);
    }

    @Override
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        margin[3] = (float) Math.ceil(attribute.axisLabelMarginVertical * 2f + rect.height());
        return margin;
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
                render.mapPoints(render.getMainModule().getMatrix(), pointCache);
                float textCentre = gridLabelPaint.measureText(depthGap[i].text) / 2f;
                float left = viewRect.left + textCentre;
                float right = viewRect.right - textCentre;
                pointCache[0] += ((i & 1) == 0 ? -textCentre - render.getMainModule().getXOffset()
                        : textCentre + render.getMainModule().getXOffset());
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
                pointCache[0] = x - (i > (attribute.gridCount / 2) ? absChartModule.getXOffset() :
                        -absChartModule.getXOffset());
                render.invertMapPoints(render.getMainModule().getMatrix(), pointCache);
                value = render.getAdapter().rateConversion(pointCache[0], render.getAdapter().getScale().getQuoteScale(), false, false);
                pointCache[0] = x;
                canvas.drawText(value, pointCache[0], gridLabelY, gridLabelPaint);
            }
        }
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        //非重叠边距
        float[] drawingNonOverlapMargin = absChartModule.getDrawingNonOverlapMargin();
        regionWidth = viewRect.width() / (float) (attribute.gridCount - 1);
        gridLabelY = viewRect.bottom + rect.height() + drawingNonOverlapMargin[3] + attribute.axisLabelMarginVertical;
    }

}
