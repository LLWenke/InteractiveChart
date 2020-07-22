
package com.wk.chart.module.base;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndicatorType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.render.AbsRender;

import java.util.ArrayList;

/**
 * <p>组件base类</p>
 */

public abstract class AbsChartModule<T extends AbsEntry> {
    private final Matrix matrix = new Matrix(); // 把值映射到屏幕像素的矩阵

    private final ArrayList<AbsDrawing> drawingList = new ArrayList<>();

    protected final ValueEntry maxEntry; //最大值

    protected final ValueEntry minEntry; //最小值

    protected final ValueEntry zeroEntry; //0

    private RectF rect = new RectF();

    private float[] margin; //边距[left, top, right, bottom]

    protected float[] rectBuffer;//数据点的矩形坐标点

    private int paddingTop = 0;

    private int paddingBottom = 0;

    private int paddingLeft = 0;

    private int paddingRight = 0;

    private float proportion;//高度比例

    private float viewHeight;//高度

    private boolean enable = false;

    private ModuleType moduleType;//图表类型

    private ValueEntry maxY;// Y 轴上指标的最大值

    private ValueEntry minY;// Y 轴上指标的最小值

    private ValueEntry maxX;// X 轴上指标的最大值

    private ValueEntry minX;// X 轴上指标的最小值

    private float xScale = 0;// X 轴缩放因子

    private float yScale = 0.1f;// Y 轴缩放因子

    private int indicatorType;//

    private float xCorrectedValue, yCorrectedValue;// X,Y 轴校正值（这里多用于line的宽度修正）
    private float xOffset, yOffset;// X,Y 轴实际偏移数值（用于修正折线偏移后被影响的数值）

    public AbsChartModule(ModuleType moduleType) {
        this.moduleType = moduleType;
        this.maxEntry = new ValueEntry(0);
        this.minEntry = new ValueEntry(0);
        this.zeroEntry = new ValueEntry(0);
        this.maxEntry.result = -Long.MAX_VALUE;
        this.minEntry.result = Long.MAX_VALUE;
        this.margin = new float[4];
        this.rectBuffer = new float[8];
    }

    public abstract void computeMinMax(T entry);

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
        this.rect.set(rect);
    }

    /**
     * 重置组件
     */
    public void resetDrawing() {
        for (AbsDrawing drawing : getDrawingList()) {
            drawing.resetInit();
        }
    }

    /**
     * 初始化组件
     *
     * @param render 渲染工厂
     * @return 初始化数量
     */
    public int initDrawing(AbsRender render) {
        int initCount = 0;
        for (AbsDrawing drawing : getDrawingList()) {
            if (!drawing.isInit()) {
                initCount++;
                drawing.onInit(render, this);
            }
            drawing.onViewChange();
            margin[0] = Math.max(margin[0], drawing.getMargin()[0]);
            margin[1] = Math.max(margin[1], drawing.getMargin()[1]);
            margin[2] = Math.max(margin[2], drawing.getMargin()[2]);
            margin[3] = Math.max(margin[3], drawing.getMargin()[3]);
        }
        return initCount;
    }

    public ArrayList<AbsDrawing> getDrawingList() {
        return drawingList;
    }

    public void addDrawing(AbsDrawing drawing) {
        drawingList.add(drawing);
    }

    public void removeDrawing(Class<? super AbsDrawing> drawing) {
        for (int i = 0; i < drawingList.size(); i++) {
            AbsDrawing item = drawingList.get(i);
            if (item.getClass().isInstance(drawing)) {
                drawingList.remove(i);
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
        return maxY == maxEntry ? zeroEntry : maxY;
    }

    protected void setMaxY(ValueEntry maxY) {
        if (maxY.result > this.maxY.result) {
            this.maxY = maxY;
        }
    }

    public ValueEntry getMinY() {
        return minY == minEntry ? zeroEntry : minY;
    }

    protected void setMinY(ValueEntry minY) {
        if (minY.result < this.minY.result) {
            this.minY = minY;
        }
    }

    public ValueEntry getMaxX() {
        return maxX == maxEntry ? zeroEntry : maxX;
    }

    protected void setMaxX(ValueEntry maxX) {
        if (maxX.result > this.maxX.result) {
            this.maxX = maxX;
        }
    }

    public ValueEntry getMinX() {
        return minX == minEntry ? zeroEntry : minX;
    }

    protected void setMinX(ValueEntry minX) {
        if (minX.result < this.minX.result) {
            this.minX = minX;
        }
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

    public float[] getMargin() {
        return margin;
    }

    public @IndicatorType
    int getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(@IndicatorType int indicatorType) {
        this.indicatorType = indicatorType;
    }

    public float[] getPointRect(AbsRender render, CandleEntry entry, int current) {
        return rectBuffer;
    }
}
