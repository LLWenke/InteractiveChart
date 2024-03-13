
package com.wk.chart.render;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.wk.chart.adapter.CandleAdapter;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AbsModule;

/**
 * <p>CandleRender 蜡烛图渲染器</p>
 */

public class CandleRender extends AbsRender<CandleAdapter, CandleAttribute> {
    private static final String TAG = "CandleRender";
    private final float[] contentPts = new float[4];//[x0, y0, x1, y1]

    public CandleRender(CandleAttribute attribute, RectF viewRect) {
        super(attribute, viewRect);
    }

    /**
     * 缩放
     *
     * @param x 在点(x, y)上缩放
     * @param y 在点(x, y)上缩放
     */
    @Override
    public void onZoom(float x, float y) {
        if (adapter.getCount() == 0 || !canScroll()) {
            return;
        }
        resetPointsWidth();
        zoom(mainModule.getMatrix(), mainModule.getRect(), attribute.visibleCount, x, y);
    }

    /**
     * 计算显示区域内 X,Y 轴的范围(此处重写目的是扩大 X,Y 轴的范围)
     */
    @SuppressLint("SwitchIntDef")
    @Override
    protected void computeExtremumValue(float[] extremum, AbsModule<AbsEntry> chartModule) {
        final float deltaYScale = chartModule.getDeltaY() * chartModule.getYScale();
        switch (chartModule.getModuleType()) {
            case ModuleType.VOLUME://交易量需要底部对齐，所以不做Y轴最小值的缩放,只缩放Y轴最大值
                extremum[1] = chartModule.getMinY().value;
                if (deltaYScale > 0) {
                    extremum[3] = chartModule.getMaxY().value + deltaYScale;
                } else {
                    extremum[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
                            * chartModule.getYScale();
                }
                break;
            default://默认Y轴最大值和最小值全部进行比例缩放
                if (deltaYScale > 0) {
                    extremum[1] = chartModule.getMinY().value - deltaYScale;
                    extremum[3] = chartModule.getMaxY().value + deltaYScale;
                } else {
                    extremum[1] = chartModule.getMinY().value - chartModule.getMinY().value
                            * chartModule.getYScale();
                    extremum[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
                            * chartModule.getYScale();
                }
                break;
        }
        if (extremum[1] == 0 && extremum[3] == 0) {
            extremum[3] = 1;
        }
    }

    /**
     * 计算当前显示区域内的 X 轴范围
     */
    @Override
    protected void computeVisibleIndex() {
        RectF rectF = mainModule.getRect();
        Matrix matrix = mainModule.getMatrix();
        contentPts[0] = rectF.left;
        invertMapPoints(matrix, contentPts);
        begin = Math.max((int) contentPts[0], 0);
        end = (int) (begin + Math.ceil(attribute.visibleCount) + 1);
        if (Math.ceil(getPointX(matrix, end) - pointsWidth) >= rectF.width()) end--;
        end = Math.min(end, getAdapter().getCount());
        begin = Math.min(begin, end);
    }
}
