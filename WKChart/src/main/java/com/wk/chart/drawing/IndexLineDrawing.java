
package com.wk.chart.drawing;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.IndexConfigEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>指标线组件</p>
 */

public class IndexLineDrawing extends IndexDrawing<CandleRender, AbsModule<?>> {
    private CandleAttribute attribute;//配置文件
    private final Paint centerLinePaint = new Paint(); //中心线画笔
    private final float[] pathPts = new float[2]; // 折线路径位置信息
    private final float[] gridBuffer = new float[2]; //grid轴坐标
    private Paint[] paints;//画笔数组
    private Path[] paths;//绘制路径数组

    public IndexLineDrawing(int indexType) {
        super(indexType);
    }

    @Override
    public void onInit(CandleRender render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        centerLinePaint.setColor(attribute.centerLineColor);
        centerLinePaint.setStrokeWidth(attribute.lineWidth);
        centerLinePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onInitConfig() {
        IndexConfigEntry indexTag = render.getAdapter().getBuildConfig().getIndexTags(indexType);
        if (null == indexTag) {
            return;
        }
        //重置指标线画笔/路径等资源
        if (null == paints || paints.length != indexTag.getFlagEntries().length) {
            paints = new Paint[indexTag.getFlagEntries().length];
            paths = new Path[indexTag.getFlagEntries().length];
            for (int i = 0; i < indexTag.getFlagEntries().length; i++) {
                Paint linePaint = new Paint();
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(attribute.lineWidth);
                linePaint.setColor(indexTag.getFlagEntries()[i].getColor());
                paints[i] = linePaint;
                paths[i] = new Path();
            }
        }
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        ValueEntry[] values = render.getAdapter().getItem(current).getLineIndex(indexType);
        if (null == values || null == paths) {
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
            render.mapPoints(chartModule.getMatrix(), pathPts);
            Path path = paths[i];
            if (path.isEmpty()) {
                path.moveTo(pathPts[0], pathPts[1]);
            } else {
                path.lineTo(pathPts[0], pathPts[1]);
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (null == paints) {
            return;
        }
        canvas.save();
        canvas.clipRect(viewRect);
        //为指定的指标绘制中心线
        switch (indexType) {
            case IndexType.WR:
            case IndexType.RSI:
            case IndexType.KDJ:
                gridBuffer[0] = 0;
                gridBuffer[1] = (extremum[3] + extremum[1]) / 2;
                render.mapPoints(chartModule.getMatrix(), gridBuffer);
                canvas.drawLine(viewRect.left, gridBuffer[1], viewRect.right, gridBuffer[1], centerLinePaint);
                break;
            case IndexType.BOLL:
            case IndexType.CANDLE_MA:
            case IndexType.DMI:
            case IndexType.EMA:
            case IndexType.VOLUME_MA:
                break;
        }
        //绘制指标线
        for (int i = 0; i < paints.length; i++) {
            canvas.drawPath(paths[i], paints[i]);
            paths[i].rewind();
        }
        canvas.restore();
    }
}