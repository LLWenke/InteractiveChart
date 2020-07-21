
package com.wk.chart.render;

import android.graphics.RectF;
import androidx.annotation.NonNull;

import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.module.base.AbsChartModule;

import java.util.List;

/**
 * <p>DepthRender 深度图渲染器</p>
 */

public class DepthRender extends AbsRender<DepthAdapter, DepthAttribute> {
    private static final String TAG = "DepthRender";
    private float[] highlightPoints = new float[2];//高亮线坐标点

    public DepthRender(DepthAttribute attribute, RectF viewRect) {
        super(attribute, viewRect);
    }

    @Override
    void initViewRect(RectF viewRect, List<AbsChartModule<? extends AbsEntry>> modules) {
        super.initViewRect(viewRect, modules);
        if (null == getMainChartModule()) {
            return;
        }
        //这里要修正X轴2倍的折线宽度（因为X轴分买单，买单2个折线图，所以为2倍）
        getMainChartModule().setxCorrectedValue(attribute.polylineWidth, 2f);
        //这里要修正Y轴1倍的折线宽度（因为Y轴买单，买单2个折线图都基于View的Bottom，所以为1倍）
        getMainChartModule().setyCorrectedValue(attribute.polylineWidth, 1f);
    }

    /**
     * 数据更新回调
     */
    @Override
    protected void resetMatrix() {
        RectF rectF = getMainChartModule().getRect();
        initMatrixValue(rectF);
        postMatrixOffset(rectF.left + getMainChartModule().getxOffset() * 2f,
                viewRect.top - getMainChartModule().getyOffset());
        postMatrixTouch(rectF.width() - getMainChartModule().getxCorrectedValue(),
                getAdapter().getCount());
    }

    /**
     * 缩放
     *
     * @param x 在点(x, y)上缩放
     * @param y 在点(x, y)上缩放
     */
    @Override
    public void onZoom(float x, float y) {
    }

    @Override
    public boolean canScroll(float dx) {
        return false;
    }

    @Override
    public boolean canDragging() {
        return false;
    }

    /**
     * 扩大显示区域内 X,Y 轴的范围
     */
    @Override
    protected void computeExtremumValue(float[] extremumY, AbsChartModule chartModule) {
        final float deltaYScale = chartModule.getDeltaY() * chartModule.getyScale();
        //X轴
        extremumY[0] = chartModule.getMinX().value;
        extremumY[2] = chartModule.getMaxX().value;
        //Y轴
        extremumY[1] = chartModule.getMinY().value;
        if (deltaYScale > 0) {
            extremumY[3] = chartModule.getMaxY().value + deltaYScale;
        } else {
            extremumY[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
                    * chartModule.getyScale();
        }

        if (extremumY[1] == 0 && extremumY[3] == 0) {
            extremumY[3] = 1;
        }
    }

    /**
     * 获取高亮线当前位置对应的数值（数据的值，并非屏幕像素点）
     */
    public String getHighlightXValue(@NonNull DepthEntry highlightEntry) {
        float highlightX = getPointX(highlightEntry.getPrice().value, getMainChartModule().getMatrix());
        highlightPoints[0] = getHighlightPoint()[0] + getMainChartModule().getxOffset();
        String value;
        if (highlightX - highlightPoints[0] < 5) {
            value = exchangeRateConversion(highlightEntry.getPrice().text,
                    getAdapter().getScale().getQuoteScale());
        } else {
            invertMapPoints(highlightPoints);
            value = exchangeRateConversion(highlightPoints[0],
                    getAdapter().getScale().getQuoteScale());
        }
        return value;
    }
}
