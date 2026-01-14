package com.wk.chart.module;

import androidx.annotation.NonNull;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.MarkerPointType;
import com.wk.chart.enumeration.ModuleGroup;
import com.wk.chart.interfaces.IMarkerPoint;
import com.wk.chart.render.AbsRender;

/**
 * <p>蜡烛图模块</p>
 */

public class CandleModule extends AbsModule<CandleEntry> implements IMarkerPoint {
    private int markerPointCount;//标签数量

    public CandleModule() {
        super(ModuleGroup.MAIN, IndexType.CANDLE);
    }

    @Override
    public void computeMinMax(CandleEntry entry) {
        //计算最小值
        setMinY(entry.getLow());
        //计算最大值
        setMaxY(entry.getHigh());
        //计算指标最大最小值
        for (Integer index : getAttachIndexSet()) {
            computeIndexMinMax(entry.getIndex(index));
            computeIndexMinMax(entry.getLineIndex(index));
        }
        //计算标签数量
        if (entry.getMarkerPointType() != MarkerPointType.NORMAL) {
            this.markerPointCount++;
        }
    }

    private void computeIndexMinMax(ValueEntry[] values) {
        if (null == values) return;
        for (ValueEntry item : values) {
            if (null == item) continue;
            //计算最小值
            setMinY(item);
            //计算最大值
            setMaxY(item);
        }
    }

    @Override
    public void resetMinMax() {
        super.resetMinMax();
        this.markerPointCount = 0;
    }

    @Override
    public float getHighPoint(@NonNull CandleEntry entry) {
        return (float) entry.getHigh().value;
    }

    @Override
    public float getLowPoint(@NonNull CandleEntry entry) {
        return (float) entry.getLow().value;
    }

    @Override
    public int getMarkerPointCount() {
        return markerPointCount;
    }

    /**
     * 获取数据点的矩形坐标点
     *
     * @param current 当前数据点位置下标
     * @param entry   当前数据点位置下标的数据Bean
     * @return 矩形坐标点（0-3:影线和矩形的区域,不包含间隔，4-7：间隔加矩形的区域，不包含影线）
     */
    @Override
    public float[] getPointRect(AbsRender<?, ?> render, CandleEntry entry, int current) {
        if (null == entry) {
            return rectBuffer;
        }
        //x轴坐标
        rectBuffer[0] = current + render.pointsSpace;
        rectBuffer[2] = current + 1 - render.pointsSpace;
        rectBuffer[4] = current;
        rectBuffer[6] = current + 1;
        //y轴坐标
        rectBuffer[1] = (float) entry.getHigh().value;
        rectBuffer[3] = (float) entry.getLow().value;
        if (entry.getOpen().value > entry.getClose().value) {
            rectBuffer[5] = (float) entry.getOpen().value;
            rectBuffer[7] = (float) entry.getClose().value;
        } else {
            rectBuffer[5] = (float) entry.getClose().value;
            rectBuffer[7] = (float) entry.getOpen().value;
        }
        render.mapPoints(getMatrix(), rectBuffer);
        return rectBuffer;
    }
}
