
package com.wk.chart.stock.base;

import android.graphics.Matrix;
import android.graphics.RectF;
import com.wk.chart.drawing.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.ChartLevel;
import com.wk.chart.enumeration.ModuleType;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>组件base类</p>
 */

public abstract class AbsChartModule<T extends AbsEntry> {

  private final Matrix matrix = new Matrix(); // 把值映射到屏幕像素的矩阵

  private final List<AbsDrawing> drawingList = new ArrayList<>();

  private final ValueEntry maxEntry; //最大值

  private final ValueEntry minEntry; //最小值

  private RectF rect = new RectF();

  private int paddingTop = 0;

  private int paddingBottom = 0;

  private int paddingLeft = 0;

  private int paddingRight = 0;

  private float proportion;//高度比例

  private float viewHeight;//高度

  private boolean enable = true;

  private ModuleType moduleType;//图表类型

  private ChartLevel chartLevel;//图表级别

  private ValueEntry maxY;// Y 轴上指标的最大值

  private ValueEntry minY;// Y 轴上指标的最小值

  private ValueEntry maxX;// X 轴上指标的最大值

  private ValueEntry minX;// X 轴上指标的最小值

  private float xScale = 0;// X 轴缩放因子

  private float yScale = 0.1f;// Y 轴缩放因子

  public AbsChartModule(ModuleType moduleType, ChartLevel chartLevel) {
    this.chartLevel = chartLevel;
    this.moduleType = moduleType;
    this.maxEntry = new ValueEntry();
    this.minEntry = new ValueEntry();
    this.maxEntry.value = -Float.MAX_VALUE;
    this.minEntry.value = Float.MAX_VALUE;
  }

  public abstract void computeMinMax(int currentIndex, T entry);

  public void resetMinMax() {
    this.minX = minEntry;
    this.minY = minEntry;
    this.maxX = maxEntry;
    this.maxY = maxEntry;
  }

  public Matrix getMatrix() {
    return matrix;
  }

  public RectF getRect() {
    return rect;
  }

  public void setRect(float left, float top, float right, float bottom) {
    rect.set(left, top, right, bottom);
  }

  public void setRect(RectF rect) {
    this.rect = rect;
  }

  public List<AbsDrawing> getDrawingList() {
    return drawingList;
  }

  public void addDrawing(AbsDrawing drawing) {
    drawingList.add(drawing);
  }

  public void removeDrawing(AbsDrawing drawing) {
    drawingList.remove(drawing);
  }

  public int getPaddingTop() {
    return paddingTop;
  }

  public void setPaddingTop(int paddingTop) {
    this.paddingTop = paddingTop;
  }

  public int getPaddingBottom() {
    return paddingBottom;
  }

  public void setPaddingBottom(int paddingBottom) {
    this.paddingBottom = paddingBottom;
  }

  public int getPaddingLeft() {
    return paddingLeft;
  }

  public void setPaddingLeft(int paddingLeft) {
    this.paddingLeft = paddingLeft;
  }

  public int getPaddingRight() {
    return paddingRight;
  }

  public void setPaddingRight(int paddingRight) {
    this.paddingRight = paddingRight;
  }

  public void setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
    this.paddingLeft = paddingLeft;
    this.paddingTop = paddingTop;
    this.paddingRight = paddingRight;
    this.paddingBottom = paddingBottom;
  }

  public boolean isEnable() {
    return enable;
  }

  public ModuleType getModuleType() {
    return moduleType;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public ChartLevel getChartLevel() {
    return chartLevel;
  }

  public void setChartLevel(ChartLevel chartLevel) {
    this.chartLevel = chartLevel;
  }

  public void setModuleType(ModuleType moduleType) {
    this.moduleType = moduleType;
  }

  public ValueEntry getMaxY() {
    return maxY;
  }

  protected void setMaxY(ValueEntry maxY) {
    this.maxY = maxY;
  }

  public ValueEntry getMinY() {
    return minY;
  }

  protected void setMinY(ValueEntry minY) {
    this.minY = minY;
  }

  public ValueEntry getMaxX() {
    return maxX;
  }

  protected void setMaxX(ValueEntry maxX) {
    this.maxX = maxX;
  }

  public ValueEntry getMinX() {
    return minX;
  }

  protected void setMinX(ValueEntry minX) {
    this.minX = minX;
  }

  public float getyScale() {
    return yScale;
  }

  public float getxScale() {
    return xScale;
  }

  public float getDeltaX() {
    return maxX.value - minX.value;
  }

  public float getDeltaY() {
    return maxY.value - minY.value;
  }

  public float getProportion() {
    return proportion;
  }

  public void setProportion(float proportion) {
    this.proportion = proportion;
  }

  public Float getViewHeight() {
    return viewHeight;
  }

  public void setViewHeight(float viewHeight) {
    this.viewHeight = viewHeight;
  }
}
