
package com.wk.chart.drawing.base;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.wk.chart.enumeration.ClickDrawingID;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

import java.util.Arrays;

/**
 * <p>AbsDrawing</p>
 * 绘制组件基类
 */

public abstract class AbsDrawing<T extends AbsRender<?, ?>, A extends AbsModule<?>> {
    protected final float[] margin; //边距[left, top, right, bottom]
    protected final int id;
    private boolean initState = false;//是否初始化
    protected RectF viewRect; // 绘制区域
    protected T render;//渲染工厂
    protected A absChartModule;//组件

    public AbsDrawing() {
        this(ClickDrawingID.ID_NONE);
    }

    public AbsDrawing(int id) {
        this.id = id;
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
        Arrays.fill(margin, 0);
    }

    /**
     * 准备计算
     *
     * @param begin    开始位置索引
     * @param end      结束位置索引
     * @param extremum 当前视图区域的 X,Y 轴的极值[x0, y0, x1, y1]
     */
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {
    }

    /**
     * 坐标计算
     *
     * @param begin   开始位置索引
     * @param end     结束位置索引
     * @param current 当前循环中的 entry 索引
     */
    public void onComputation(int begin, int end, int current, float[] extremum) {
    }

    /**
     * 计算结束，开始绘制
     *
     * @param canvas   canvas
     * @param begin    开始位置索引
     * @param end      结束位置索引
     * @param extremum 当前视图区域的 X,Y 轴的极值[x0, y0, x1, y1]
     */
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
    }

    /**
     * 绘制结束
     *
     * @param canvas canvas
     */
    public void drawOver(Canvas canvas) {
    }

    /**
     * 初始化配置信息
     */
    public void onInitConfig() {
    }

    /**
     * 当视图布局完成后调用此方法，将依赖于视图的大小和位置的属进行重置和运算
     */
    public void onLayoutComplete() {
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

    /**
     * 获取id
     *
     * @return id值
     */
    public int getId() {
        return id;
    }

}
