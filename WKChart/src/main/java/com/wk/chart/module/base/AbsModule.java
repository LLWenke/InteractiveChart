
package com.wk.chart.module.base;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.render.AbsRender;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>组件base类</p>
 */

public abstract class AbsModule<T extends AbsEntry> {
    private final Matrix matrix = new Matrix(); // 把值映射到屏幕像素的矩阵

    private final ArrayList<AbsDrawing<AbsRender<?, ?>, AbsModule<?>>> drawingList = new ArrayList<>();

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

    private float width;//宽度

    private float height;//高度

    private boolean enable = false;

    private @ModuleType
    final int moduleType;//模型类型

    private @IndexType
    int attachIndexType;//附加指标类型

    private @ModuleGroupType
    int moduleGroup;//指标类型分组

    private ValueEntry maxY;// Y 轴上指标的最大值

    private ValueEntry minY;// Y 轴上指标的最小值

    private ValueEntry maxX;// X 轴上指标的最大值

    private ValueEntry minX;// X 轴上指标的最小值

    private float xScale = 0;// X 轴缩放因子

    private float yScale = 0.04f;// Y 轴缩放因子

    private float xCorrectedValue, yCorrectedValue;// X,Y 轴校正值（这里多用于line的宽度修正）
    private float xOffset, yOffset;// X,Y 轴实际偏移数值（用于修正折线偏移后被影响的数值）

    public AbsModule(@ModuleType int moduleType, @ModuleGroupType int moduleGroupType) {
        this.moduleType = moduleType;
        this.moduleGroup = moduleGroupType;
        this.attachIndexType = IndexType.NONE;
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
        for (AbsDrawing<?, ?> drawing : getDrawingList()) {
            drawing.resetInit();
        }
    }

    /**
     * 初始化组件
     *
     * @param render 渲染工厂
     */
    public void initDrawing(AbsRender<?, ?> render) {
        for (AbsDrawing<AbsRender<?, ?>, AbsModule<?>> drawing : getDrawingList()) {
            if (!drawing.isInit()) {
                drawing.onInit(render, this);
            }
            drawing.onViewChange();
        }
    }

    /**
     * 初始化组件的边距
     *
     * @return 修改数量
     */
    public int initMargin() {
        int updateCount = 0;
        Arrays.fill(margin, 0);
        for (AbsDrawing<AbsRender<?, ?>, AbsModule<?>> drawing : getDrawingList()) {
            if (!drawing.isInit()) {
                continue;
            }
            float[] margins = drawing.onInitMargin();
            if (margins[0] > margin[0]) {
                margin[0] = margins[0];
                updateCount++;
            }
            if (margins[1] > margin[1]) {
                margin[1] = margins[1];
                updateCount++;
            }
            if (margins[2] > margin[2]) {
                margin[2] = margins[2];
                updateCount++;
            }
            if (margins[3] > margin[3]) {
                margin[3] = margins[3];
                updateCount++;
            }
        }
        return updateCount;
    }

    public ArrayList<AbsDrawing<AbsRender<?, ?>, AbsModule<?>>> getDrawingList() {
        return drawingList;
    }

    public void addDrawing(AbsDrawing<? extends AbsRender<?, ?>, ? extends AbsModule<?>> drawing) {
        drawingList.add((AbsDrawing<AbsRender<?, ?>, AbsModule<?>>) drawing);
    }

    public void removeDrawing(Class<? extends AbsDrawing<?, ?>> drawing) {
        for (int i = 0; i < drawingList.size(); i++) {
            AbsDrawing<AbsRender<?, ?>, AbsModule<?>> item = drawingList.get(i);
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

    public boolean isAttach() {
        return enable && (moduleType != ModuleType.MUTATION || attachIndexType != IndexType.NONE);
    }

    public @IndexType
    int getAttachIndexType() {
        return attachIndexType;
    }

    public void setAttachIndexType(@IndexType int attachIndexType) {
        this.attachIndexType = attachIndexType;
    }

    public @ModuleType
    int getModuleType() {
        return moduleType;
    }

    public @ModuleGroupType
    int getModuleGroup() {
        return moduleGroup;
    }

    public void setModuleGroup(@ModuleGroupType int moduleGroup) {
        this.moduleGroup = moduleGroup;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
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

    public float getYScale() {
        return yScale;
    }

    public float getXScale() {
        return xScale;
    }

    public float getDeltaX() {
        return maxX.value - minX.value;
    }

    public float getDeltaY() {
        return maxY.value - minY.value;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getXCorrectedValue() {
        return xCorrectedValue;
    }

    public void setXCorrectedValue(float xCorrectedValue, float Multiple) {
        this.xCorrectedValue = xCorrectedValue * Multiple;
        this.xOffset = xCorrectedValue / 2f;
    }

    public float getYCorrectedValue() {
        return yCorrectedValue;
    }

    public void setYCorrectedValue(float yCorrectedValue, float Multiple) {
        this.yCorrectedValue = yCorrectedValue * Multiple;
        this.yOffset = yCorrectedValue / 2f;
    }

    public float getXOffset() {
        return xOffset;
    }

    public float getYOffset() {
        return yOffset;
    }

    public float[] getMargin() {
        return margin;
    }

    public float[] getPointRect(AbsRender<?, ?> render, T entry, int current) {
        return rectBuffer;
    }
}
