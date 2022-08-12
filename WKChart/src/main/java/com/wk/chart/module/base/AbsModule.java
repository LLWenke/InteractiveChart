
package com.wk.chart.module.base;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.ClickDrawingID;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.interfaces.IDrawingClickListener;
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

    private final RectF rect = new RectF();

    protected final float[] drawingMargin; //边距[left, top, right, bottom]

    protected final float[] drawingNonOverlapMargin; //非重叠边距[left, top, right, bottom]

    protected float[] rectBuffer;//数据点的矩形坐标点

    private boolean enable = false;

    private @ModuleType
    final int moduleType;//模块类型

    private @IndexType
    int attachIndexType;//附加指标类型

    private @ModuleGroupType
    int moduleGroup;//指标类型分组

    private ValueEntry maxY;// Y 轴上指标的最大值

    private ValueEntry minY;// Y 轴上指标的最小值

    private ValueEntry maxX;// X 轴上指标的最大值

    private ValueEntry minX;// X 轴上指标的最小值

    private final float xScale = 0;// X 轴缩放因子

    private final float yScale = 0.04f;// Y 轴缩放因子

    private float xCorrectedValue, yCorrectedValue;// X,Y 轴校正值（这里多用于line的宽度修正）
    private float xOffset, yOffset;// X,Y 轴实际偏移数值（用于修正折线偏移后被影响的数值）
    private float width = 0f, height = 0f;// 宽，高

    public AbsModule(@ModuleType int moduleType, @ModuleGroupType int moduleGroupType) {
        this.moduleType = moduleType;
        this.moduleGroup = moduleGroupType;
        this.attachIndexType = IndexType.NONE;
        this.maxEntry = new ValueEntry();
        this.minEntry = new ValueEntry();
        this.zeroEntry = new ValueEntry();
        this.maxEntry.result = -Long.MAX_VALUE;
        this.minEntry.result = Long.MAX_VALUE;
        this.rectBuffer = new float[8];
        this.drawingMargin = new float[4];
        this.drawingNonOverlapMargin = new float[4];
    }

    public abstract void computeMinMax(T entry);

    public void onSizeChanged(float width, float height) {
        this.width = width;
        this.height = height;
    }

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
        Arrays.fill(drawingMargin, 0);
        Arrays.fill(drawingNonOverlapMargin, 0);
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
            drawing.onInitConfig();
        }
    }

    /**
     * 初始化组件的边距
     */
    public void initDrawingMargin(float viewWidth, float viewHeight) {
        Arrays.fill(drawingMargin, 0);
        Arrays.fill(drawingNonOverlapMargin, 0);
        for (AbsDrawing<?, ?> drawing : getDrawingList()) {
            if (!drawing.isInit()) {
                continue;
            }
            float[] margins = drawing.onInitMargin(viewWidth, viewHeight);
            if (drawing.marginOverlap()) {
                if (margins[0] > drawingMargin[0]) {
                    drawingMargin[0] = margins[0];
                }
                if (margins[1] > drawingMargin[1]) {
                    drawingMargin[1] = margins[1];
                }
                if (margins[2] > drawingMargin[2]) {
                    drawingMargin[2] = margins[2];
                }
                if (margins[3] > drawingMargin[3]) {
                    drawingMargin[3] = margins[3];
                }
            } else {
                drawingNonOverlapMargin[0] += margins[0];
                drawingNonOverlapMargin[1] += margins[1];
                drawingNonOverlapMargin[2] += margins[2];
                drawingNonOverlapMargin[3] += margins[3];
            }
        }
        drawingMargin[0] += drawingNonOverlapMargin[0];
        drawingMargin[1] += drawingNonOverlapMargin[1];
        drawingMargin[2] += drawingNonOverlapMargin[2];
        drawingMargin[3] += drawingNonOverlapMargin[3];
    }

    public ArrayList<AbsDrawing<AbsRender<?, ?>, AbsModule<?>>> getDrawingList() {
        return drawingList;
    }

    public void addDrawing(AbsDrawing<? extends AbsRender<?, ?>, ? extends AbsModule<?>> drawing) {
        drawingList.add((AbsDrawing<AbsRender<?, ?>, AbsModule<?>>) drawing);
    }

    public boolean removeDrawing(Class<? extends AbsDrawing<?, ?>> drawing) {
        for (int i = 0; i < drawingList.size(); i++) {
            AbsDrawing<AbsRender<?, ?>, AbsModule<?>> item = drawingList.get(i);
            if (item.getClass().isAssignableFrom(drawing)) {
                drawingList.remove(i);
                return true;
            }
        }
        return false;
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float[] getDrawingMargin() {
        return drawingMargin;
    }

    public float[] getDrawingNonOverlapMargin() {
        return drawingNonOverlapMargin;
    }

    public float[] getPointRect(AbsRender<?, ?> render, T entry, int current) {
        return rectBuffer;
    }

    /**
     * 布局完成
     */
    public void onLayoutComplete() {
        for (AbsDrawing<AbsRender<?, ?>, AbsModule<?>> drawing : getDrawingList()) {
            if (drawing.isInit()) {
                drawing.onLayoutComplete();
            }
        }
    }

    /**
     * Click事件判定
     *
     * @return (返回响应事件的元素ID)
     */
    public int onClick(float x, float y) {
        if (!isAttach() || !getRect().contains(x, y)) {
            return ClickDrawingID.ID_NONE;
        }
        for (AbsDrawing<AbsRender<?, ?>, AbsModule<?>> drawing : getDrawingList()) {
            if (drawing instanceof IDrawingClickListener) {
                if (((IDrawingClickListener) drawing).onDrawingClick(x, y)) {
                    return drawing.getId();
                }
            }
        }
        return ClickDrawingID.ID_NONE;
    }
}
