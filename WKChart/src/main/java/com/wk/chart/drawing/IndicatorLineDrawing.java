
package com.wk.chart.drawing;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.IndicatorTagEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndicatorType;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>IndicatorLineDrawing</p>
 * 指标线组件
 */

public class IndicatorLineDrawing extends AbsDrawing<CandleRender, AbsChartModule> {
    private CandleAttribute attribute;//配置文件

    private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //边框画笔
    private Paint centerLinePaint = new Paint(); //中心线画笔
    private Paint[] paints;//画笔数组
    private Path[] paths;//绘制路径数组
    private final float[] pathPts = new float[2]; // 折线路径位置信息
    private float[] gridBuffer = new float[2]; //grid轴坐标

    @Override
    public void onInit(CandleRender render, AbsChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        centerLinePaint.setColor(attribute.centerLineColor);
        centerLinePaint.setStrokeWidth(attribute.lineWidth);
        centerLinePaint.setStyle(Paint.Style.FILL);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(attribute.borderWidth);
        borderPaint.setColor(attribute.borderColor);
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        ValueEntry[] values = render.getAdapter().getItem(current).getIndicator(absChartModule.getIndicatorType());
        if (null == values) {
            return;
        }
        //取最小单位绘制指标线路径
        int count = Math.min(values.length, paths.length);
        for (int i = 0; i < count; i++) {
            ValueEntry entry = values[i];
            if (null == entry) {
                continue;
            }
            pathPts[0] = current + 0.5f;
            pathPts[1] = entry.value;
            render.mapPoints(pathPts);
            Path path = paths[i];
//            Log.e("极致：" + entry.value+" vvv", pathPts[0] + "  " + pathPts[1]+"rect"+viewRect.toShortString()+"extremum："+extremum[1]+"  "+extremum[3]);
            if (path.isEmpty()) {
                path.moveTo(pathPts[0], pathPts[1]);
            } else {
                path.lineTo(pathPts[0], pathPts[1]);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        canvas.save();
        canvas.clipRect(viewRect);
        //为指定的指标绘制中心线
        switch (absChartModule.getIndicatorType()) {
            case IndicatorType.BOLL:
            case IndicatorType.MACD:
            case IndicatorType.WR:
            case IndicatorType.RSI:
            case IndicatorType.KDJ:
                gridBuffer[0] = 0;
                gridBuffer[1] = (extremum[3] + extremum[1]) / 2;
                render.mapPoints(gridBuffer);
                canvas.drawLine(viewRect.left, gridBuffer[1], viewRect.right, gridBuffer[1], centerLinePaint);
                break;
            case IndicatorType.CANDLE_MA:
            case IndicatorType.DMI:
            case IndicatorType.EMA:
            case IndicatorType.VOLUME_MA:
                break;
        }
        //绘制指标线
        for (int i = 0; i < paints.length; i++) {
            canvas.drawPath(paths[i], paints[i]);
            paths[i].reset();
        }
        canvas.restore();
    }

    @Override
    public void drawOver(Canvas canvas) {
        switch (absChartModule.getIndicatorType()) {
            case IndicatorType.BOLL:
            case IndicatorType.MACD:
            case IndicatorType.WR:
            case IndicatorType.RSI:
            case IndicatorType.KDJ:
                if (attribute.borderWidth > 0) {
                    canvas.drawRect(borderPts[0], borderPts[1], borderPts[2], borderPts[3], borderPaint);
                }
                break;
            case IndicatorType.CANDLE_MA:
            case IndicatorType.DMI:
            case IndicatorType.EMA:
            case IndicatorType.VOLUME_MA:
                break;
        }

    }

    @Override
    public void onViewChange() {
        super.onViewChange();
        IndicatorTagEntry indicatorTag = render.getAdapter().getBuildConfig().getIndicatorTags(absChartModule.getIndicatorType());
        if (null == indicatorTag) {
            return;
        }
        //重置指标线画笔/路径等资源
        if (null == paints || paints.length != indicatorTag.getFlagEntries().length) {
            paints = new Paint[indicatorTag.getFlagEntries().length];
            paths = new Path[indicatorTag.getFlagEntries().length];
            for (int i = 0; i < indicatorTag.getFlagEntries().length; i++) {
                Paint linePaint = new Paint();
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(attribute.lineWidth);
                linePaint.setColor(indicatorTag.getFlagEntries()[i].getColor());
                paints[i] = linePaint;
                paths[i] = new Path();
            }
        }
    }
}