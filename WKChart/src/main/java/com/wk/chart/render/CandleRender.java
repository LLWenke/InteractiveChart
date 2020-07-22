

package com.wk.chart.render;

import android.graphics.RectF;

import com.wk.chart.adapter.CandleAdapter;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.module.base.AbsChartModule;

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
        resetInterval();
        zoom(getMainChartModule().getRect(), attribute.visibleCount, x, y);
    }

    /**
     * 计算显示区域内 X,Y 轴的范围(此处重写目的是扩大 X,Y 轴的范围)
     */
    @Override
    protected void computeExtremumValue(float[] extremum, AbsChartModule chartModule) {
        final float deltaYScale = chartModule.getDeltaY() * chartModule.getyScale();
        switch (chartModule.getModuleType()) {
            case VOLUME://交易量需要底部对齐，所以不做Y轴最小值的缩放,只缩放Y轴最大值
                extremum[1] = chartModule.getMinY().value;
                if (deltaYScale > 0) {
                    extremum[3] = chartModule.getMaxY().value + deltaYScale;
                } else {
                    extremum[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
                            * chartModule.getyScale();
                }
                break;
            default://默认Y轴最大值和最小值全部进行比例缩放
                if (deltaYScale > 0) {
                    extremum[1] = chartModule.getMinY().value - deltaYScale;
                    extremum[3] = chartModule.getMaxY().value + deltaYScale;
                } else {
                    extremum[1] = chartModule.getMinY().value - chartModule.getMinY().value
                            * chartModule.getyScale();
                    extremum[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
                            * chartModule.getyScale();
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
        contentPts[0] = getMainChartModule().getRect().left;
        invertMapPoints(contentPts);
        begin = Math.max((int) contentPts[0], 0);
        //根据maxVisibleIndex的显示位置修正maxVisibleIndex值
        end = (int) (begin + Math.ceil(attribute.visibleCount) + 1);
        if (Math.ceil(getPointX(end, null) - pointsWidth) >= getMainChartModule().getRect().width()) {
            end--;
            //Log.e(TAG, "getTransX---b: " +
            //    Math.ceil(getPointX(end) - candleWidth))
        }
        end = Math.min(end, getAdapter().getCount());
        begin = Math.min(begin, end);
        // 计算当前显示区域内 entry 在 Y 轴上的最小值和最大值
        getAdapter().computeMinAndMax(begin, end, getChartModules());
    }
}
