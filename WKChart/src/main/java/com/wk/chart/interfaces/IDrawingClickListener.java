package com.wk.chart.interfaces;

public interface IDrawingClickListener {
    /**
     * Drawing的Click
     *
     * @return 是否响应该事件
     */
    boolean onDrawingClick(float x, float y);
}