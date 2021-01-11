

package com.wk.chart.marker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.render.AbsRender;


/**
 * <p>AbsMarker</p>
 */

public abstract class AbsMarker<T extends AbsRender<?, ?>> {
    public boolean isInit = false;
    protected BaseAttribute attribute;
    protected T render;
    private float[] margin; //边距[left, top, right, bottom]

    /**
     * 初始化
     *
     * @param render render
     */
    public void onInit(T render) {
        this.isInit = true;
        this.render = render;
        this.attribute = render.getAttribute();
        this.margin = new float[4];
    }

    /**
     * onMarkerViewMeasure
     *
     * @param highlightPointX 高亮中心坐标 x
     * @param highlightPointY 高亮中心坐标 y
     */
    public abstract void onMarkerViewMeasure(RectF viewRect, Matrix matrix, float highlightPointX,
                                             float highlightPointY, String[] markerText, @Size(min = 4)
                                             @NonNull float[] markerViewInfo);

    /**
     * onMarkerViewDraw
     *
     * @param canvas canvas
     */
    public abstract void onMarkerViewDraw(Canvas canvas, String[] markerText);

    /**
     * 获取边距
     */
    public final float[] getMargin() {
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