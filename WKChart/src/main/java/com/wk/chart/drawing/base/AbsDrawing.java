
package com.wk.chart.drawing.base;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>AbsDrawing</p>
 */

public abstract class AbsDrawing<T extends AbsRender, A extends AbsChartModule> {
    private boolean initState = false;
    protected RectF viewRect; // 绘制区域
    protected T render;//渲染工厂
    protected A absChartModule;//组件
    protected float[] borderPts; //边框线坐标点[x0, y0, x1, y1]
    private float[] margin; //边距[left, top, right, bottom]

    /**
     * 初始化
     *
     * @param render      渲染工厂
     * @param chartModule 组件
     */
    public void onInit(T render, A chartModule) {
        this.initState = true;
        this.render = render;
        this.absChartModule = chartModule;
        this.viewRect = chartModule.getRect();
        this.margin = new float[4];
    }

    /**
     * 准备计算
     *
     * @param begin    开始位置索引
     * @param end      结束位置索引
     * @param extremum 当前视图区域的 X,Y 轴的极值[x0, y0, x1, y1]
     */
    public abstract void readyComputation(Canvas canvas, int begin, int end, float[] extremum);

    /**
     * 坐标计算
     *
     * @param begin   开始位置索引
     * @param end     结束位置索引
     * @param current 当前循环中的 entry 索引
     */
    public abstract void onComputation(int begin, int end, int current, float[] extremum);

    /**
     * 计算结束，开始绘制
     *
     * @param canvas   canvas
     * @param begin    开始位置索引
     * @param end      结束位置索引
     * @param extremum 当前视图区域的 X,Y 轴的极值[x0, y0, x1, y1]
     */
    public abstract void onDraw(Canvas canvas, int begin, int end, float[] extremum);

    /**
     * 绘制结束
     *
     * @param canvas canvas
     */
    public abstract void drawOver(Canvas canvas);

    /**
     * 当视图改变时调用此方法，用于一些属性的重制和运算
     */
    public void onViewChange() {
        this.borderPts = render.getBorderPoints(viewRect);
    }

    /**
     * 是否初始化完成
     */
    public boolean isInit() {
        return initState;
    }

    /**
     * 重置初始化状态
     */
    public void resetInit() {
        this.initState = false;
    }

    /**
     * Drawing的Click
     *
     * @return 是否响应该事件
     */
    public boolean onDrawingClick(float x, float y) {
        return false;
    }

    /**
     * 获取边距
     */
    public final float[] getMargin() {
        return margin;
    }

    /**
     * 获取绘制区域
     *
     * @return 绘制区域
     */
    public RectF getViewRect() {
        return viewRect;
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
