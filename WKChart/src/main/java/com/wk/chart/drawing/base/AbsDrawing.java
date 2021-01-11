
package com.wk.chart.drawing.base;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>AbsDrawing</p>
 */

public abstract class AbsDrawing<T extends AbsRender<?, ?>, A extends AbsModule<?>> {
    protected final float[] margin; //边距[left, top, right, bottom]
    private boolean initState = false;//是否初始化
    protected RectF viewRect; // 绘制区域
    protected T render;//渲染工厂
    protected A absChartModule;//组件


    public AbsDrawing() {
        this.margin = new float[4];
    }

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
    public abstract void onViewChange();

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
     * 初始化边距
     */
    public float[] onInitMargin() {
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
}
