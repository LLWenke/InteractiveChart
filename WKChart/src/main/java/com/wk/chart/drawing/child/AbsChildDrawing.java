

package com.wk.chart.drawing.child;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

import java.util.Arrays;


/**
 * <p>AbsChildDrawing</p>
 */

public abstract class AbsChildDrawing<T extends AbsRender<?, ?>, A extends AbsModule<?>> {
    protected final float[] margin; //边距[left, top, right, bottom]
    protected BaseAttribute attribute;
    protected T render;
    protected A absChartModule;//模块

    protected AbsChildDrawing() {
        this.margin = new float[4];
    }

    /**
     * 初始化
     *
     * @param render render
     */
    public void onInit(T render, A chartModule) {
        this.render = render;
        this.attribute = render.getAttribute();
        this.absChartModule = chartModule;
        Arrays.fill(margin, 0);
    }

    /**
     * 计算子视图
     *
     * @param viewRect         视图矩形
     * @param nonOverlapMargin 非重叠边距
     * @param x                高亮中心坐标 x
     * @param y                高亮中心坐标 y
     * @param markerText       标签文字
     * @param isReverse        是否反转
     */
    public abstract float[] onMeasureChildView(RectF viewRect, float[] nonOverlapMargin, float x, float y,
                                               String markerText, boolean isReverse);

    /**
     * 绘制子视图
     *
     * @param canvas 画布
     */
    public abstract void onChildViewDraw(Canvas canvas, String markerText);

    /**
     * 获取边距
     */
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        return margin;
    }

    /**
     * 设置边距
     */
    public void setMargin(float left, float top, float right, float bottom) {
        this.margin[0] = left;
        this.margin[1] = top;
        this.margin[2] = right;
        this.margin[3] = bottom;
    }
}