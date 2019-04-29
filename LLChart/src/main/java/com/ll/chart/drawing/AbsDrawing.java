
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.ll.chart.render.AbsRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>AbsDrawing</p>
 */

public abstract class AbsDrawing<T extends AbsRender> {
  private boolean isInit = false;
  protected RectF viewRect; // 绘制区域
  protected T render;//渲染工厂
  protected AbsChartModule absChartModule;//组件

  /**
   * 初始化
   *
   * @param render 渲染工厂
   * @param chartModule 组件
   */
  public void onInit(T render, AbsChartModule chartModule) {
    this.isInit = true;
    this.render = render;
    this.absChartModule = chartModule;
    this.viewRect = chartModule.getRect();
  }

  /**
   * 计算预绘制的坐标
   *
   * @param begin 开始位置索引
   * @param end 结束位置索引
   * @param current 当前循环中的 entry 索引
   */
  public abstract void computePoint(int begin, int end, int current);

  /**
   * 计算结束，开始绘制
   *
   * @param canvas canvas
   * @param begin 开始位置索引
   * @param end 结束位置索引
   * @param extremum 当前视图区域的 X,Y 轴的极值[x0, y0, x1, y1]
   */
  public abstract void onComputeOver(Canvas canvas, int begin, int end, float[] extremum);

  /**
   * 绘制结束
   *
   * @param canvas canvas
   */
  public abstract void onDrawOver(Canvas canvas);

  /**
   * 当视图改变时调用此方法，用于一些属性的重制和运算
   */
  public abstract void onViewChange();

  /**
   * 是否初始化完成
   */
  public boolean isInit() {
    return isInit;
  }

  /**
   * Drawing的Click
   *
   * @return 是否响应该事件
   */
  public boolean onDrawingClick(float x, float y) {
    return false;
  }
}
