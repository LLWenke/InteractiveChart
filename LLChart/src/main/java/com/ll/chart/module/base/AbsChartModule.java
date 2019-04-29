
package com.ll.chart.module.base;

import android.graphics.Matrix;
import android.graphics.RectF;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.entry.ValueEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.render.AbsRender;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>组件base类</p>
 */

public abstract class AbsChartModule<T extends AbsEntry> {

  private final Matrix matrix = new Matrix(); // 把值映射到屏幕像素的矩阵

  private final List<AbsDrawing<? extends AbsRender>> drawingList = new ArrayList<>();

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

  private ValueEntry maxY;// Y 轴上指标的最大值

  private ValueEntry minY;// Y 轴上指标的最小值

  private ValueEntry maxX;// X 轴上指标的最大值

  private ValueEntry minX;// X 轴上指标的最小值

  private float xScale = 0;// X 轴缩放因子

  private float yScale = 0.1f;// Y 轴缩放因子

  private float xCorrectedValue, yCorrectedValue;// X,Y 轴校正值（这里多用于line的宽度修正）
  private float xOffset, yOffset;// X,Y 轴实际偏移数值（用于修正折线偏移后被影响的数值）

  public AbsChartModule(ModuleType moduleType) {
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

  public List<AbsDrawing<? extends AbsRender>> getDrawingList() {
    return drawingList;
  }

  public void addDrawing(AbsDrawing<? extends AbsRender> drawing) {
    drawingList.add(drawing);
  }

  public void removeDrawing(Class<? extends AbsDrawing> drawing) {
    for (AbsDrawing item : drawingList) {
      if (item.getClass().isInstance(drawing)) {
        drawingList.remove(item);
        break;
      }
    }
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

  public float getxCorrectedValue() {
    return xCorrectedValue;
  }

  public void setxCorrectedValue(float xCorrectedValue, float Multiple) {
    this.xCorrectedValue = xCorrectedValue * Multiple;
    this.xOffset = xCorrectedValue / 2f;
  }

  public float getyCorrectedValue() {
    return yCorrectedValue;
  }

  public void setyCorrectedValue(float yCorrectedValue, float Multiple) {
    this.yCorrectedValue = yCorrectedValue * Multiple;
    this.yOffset = yCorrectedValue / 2f;
  }

  public float getxOffset() {
    return xOffset;
  }

  public float getyOffset() {
    return yOffset;
  }
}
