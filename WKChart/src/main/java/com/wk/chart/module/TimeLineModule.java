package com.wk.chart.module;

import androidx.annotation.NonNull;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.MarkerPointType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.interfaces.IMarkerPoint;
import com.wk.chart.module.base.MainModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>分时图模块</p>
 */

public class TimeLineModule extends MainModule<CandleEntry> implements IMarkerPoint {
    private int markerPointCount;//标签数量

    public TimeLineModule() {
        super(ModuleType.TIME);
    }

    @Override
    public void computeMinMax(CandleEntry entry) {
        //计算最小值
        setMinY(entry.getClose());
        //计算最大值
        setMaxY(entry.getClose());
        //计算标签数量
        if (entry.getMarkerPointType() != MarkerPointType.NORMAL) {
            this.markerPointCount++;
        }
    }

    @Override
    public void resetMinMax() {
        super.resetMinMax();
        this.markerPointCount = 0;
    }

    @Override
    public float getHighPoint(@NonNull CandleEntry entry) {
        return entry.getClose().value;
    }

    @Override
    public float getLowPoint(@NonNull CandleEntry entry) {
        return entry.getClose().value;
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
        //y轴坐标
        rectBuffer[1] = rectBuffer[3] = entry.getClose().value;
        render.mapPoints(getMatrix(), rectBuffer);
        return rectBuffer;
    }
}
