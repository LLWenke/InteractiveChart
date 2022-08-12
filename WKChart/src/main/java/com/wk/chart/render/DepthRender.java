
package com.wk.chart.render;

import android.graphics.RectF;

import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.module.base.AbsModule;

/**
 * <p>DepthRender 深度图渲染器</p>
 */

public class DepthRender extends AbsRender<DepthAdapter, DepthAttribute> {
    private static final String TAG = "DepthRender";

    public DepthRender(DepthAttribute attribute, RectF viewRect) {
        super(attribute, viewRect);
    }

    @Override
    void layoutModule() {
        super.layoutModule();
        if (null == mainModule) return;
        //这里要修正X轴2倍的折线宽度（因为X轴分买单，卖单2个折线图，所以为2倍）
        mainModule.setXCorrectedValue(attribute.polylineWidth, 2f);
        //这里要修正Y轴1倍的折线宽度（因为Y轴买单，卖单2个折线图都基于View的Bottom，所以为1倍）
        mainModule.setYCorrectedValue(attribute.polylineWidth, 1f);
    }

    /**
     * 数据更新回调
     */
    @Override
    protected void resetMatrix() {
        computeScrollRange(mainModule.getRect(), 1f);
        postMatrixScale(mainModule.getMatrix(), 1f, 1f);
        postMatrixScale(matrixTouch, 1f, 1f);
        postMatrixOffset(matrixOffset,
                mainModule.getRect().left + mainModule.getXOffset() * 2f,
                viewRect.top - mainModule.getYOffset());
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
    protected void computeExtremumValue(float[] extremumY, AbsModule<AbsEntry> chartModule) {
        final float deltaYScale = chartModule.getDeltaY() * chartModule.getYScale();
        //X轴
        extremumY[0] = chartModule.getMinX().value;
        extremumY[2] = chartModule.getMaxX().value;
        //Y轴
        extremumY[1] = chartModule.getMinY().value;
        if (deltaYScale > 0) {
            extremumY[3] = chartModule.getMaxY().value + deltaYScale;
        } else {
            extremumY[3] = chartModule.getMaxY().value + chartModule.getMaxY().value
                    * chartModule.getYScale();
        }

        if (extremumY[1] == 0 && extremumY[3] == 0) {
            extremumY[3] = 1;
        }
    }
}
