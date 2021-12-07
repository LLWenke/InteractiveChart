

package com.wk.chart.render;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wk.chart.adapter.AbsAdapter;
import com.wk.chart.compat.MeasureUtils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.compat.config.AbsBuildConfig;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.module.base.MainModule;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>AbsRender</p>
 */

public abstract class AbsRender<T extends AbsAdapter<? extends AbsEntry, ? extends AbsBuildConfig>, A extends BaseAttribute> {
    private static final String TAG = "AbsRender";
    private final MeasureUtils measureUtils;//计算工具类
    private final LinkedHashMap<Integer, List<AbsModule<AbsEntry>>> chartModules; //图表指标列表
    private final Matrix matrixValue = new Matrix(); // 把值映射到屏幕像素的矩阵
    private final Matrix matrixTouch = new Matrix(); // 缩放和平移矩阵
    private final Matrix matrixOffset = new Matrix(); // 偏移矩阵
    private final Matrix matrixInvert = new Matrix(); // 用于缓存反转矩阵
    private final float[] points = new float[2];//储存某个点的坐标系信息
    private final float[] touchPts = new float[2];//存储交互位置
    private final float[] touchValues = new float[9]; // 存储缩放和平移信息
    private final float[] extremum = new float[4];//极值[x0, y0, x1, y1]
    private final float[] highlightPoint = new float[2];//长按位置
    private float[] moduleCoordinatePoint;//已经启用的module的坐标点[x0 y0 x1 y1 x2 y2 ...]
    protected final RectF viewRect; // 整个的视图区域
    protected final A attribute; // 配置信息
    protected T adapter; // 数据适配器
    private MainModule<AbsEntry> mainModule;//主图
    private AbsModule<AbsEntry> topModule;//最顶部的图表模型
    private AbsModule<AbsEntry> bottomModule;//最底部的图表模型
    private AbsModule<AbsEntry> focusModuleCache = null;//焦点区域的ChartModule
    private boolean highlight = false;//长按事件状态
    private boolean firstLoad = true;//首次加载
    private int firstLoadPosition = -1;//首次加载下标
    private boolean isProrate = true;//是否按比例计算module大小
    private float borderCorrection;//边框修正数值
    private float minScrollOffset = 0; // 最小滚动量
    private float maxScrollOffset = 0; // 最大滚动量
    private float lastMaxScrollOffset = 0; // 上一次的最大滚动量
    private float overScrollOffset = 0; // 超出边界的滚动量
    private int interval = 1;//x轴label间隔
    public float pointsMinWidth, pointsWidth, pointsSpace = 0;//数据点最小宽度（包含两边的间隔）|数据点默认宽度|数据点间隔
    int begin;//开始位置索引
    int end;//结束位置索引

    public AbsRender(A attribute, RectF viewRect) {
        this.attribute = attribute;
        this.viewRect = viewRect;
        this.measureUtils = new MeasureUtils(this);
        this.chartModules = new LinkedHashMap<>();
        resetChartModules();
        init();
    }

    private void init() {
        this.borderCorrection = attribute.borderWidth / 2f;
        this.pointsSpace = (attribute.pointSpace / attribute.pointWidth) / 2f;
        this.pointsMinWidth = attribute.pointBorderWidth +
                attribute.pointBorderWidth * (attribute.pointSpace / attribute.pointWidth * 2f);
    }

    /**
     * 初始化图标
     */
    public void resetChart() {
        firstLoadPosition = -1;
        firstLoad = true;
        setOverScrollOffset(0);
        setCurrentTransX(0);
    }

    /**
     * 刷新缩放后的Item信息
     */
    protected void resetPointsWidth() {
        if (canScroll()) {
            pointsWidth = attribute.pointWidth * attribute.currentScale;
            if (pointsWidth < pointsMinWidth) {
                pointsWidth = pointsMinWidth;
                float minScale = pointsWidth / attribute.pointWidth;
                attribute.currentScale = minScale;
                attribute.minScale = minScale;
            }
            attribute.visibleCount = getMainModule().getRect().width() / pointsWidth;
        } else {
            pointsMinWidth = pointsWidth = pointsSpace = 0;
        }
    }

    /**
     * 修正显示显示数量
     */
    private void correctVisibleCount() {
        attribute.visibleCount = canScroll() ? attribute.visibleCount : getAdapter().getCount();
    }

    /**
     * 获取缩放后并且减去CandleSpace的CandleWidth
     */
    public float getSubtractSpacePointWidth() {
        return (attribute.pointWidth - attribute.pointSpace) * attribute.currentScale;
    }

    /**
     * 缩放
     *
     * @param x 在点(x, y)上缩放
     * @param y 在点(x, y)上缩放
     */
    public abstract void onZoom(float x, float y);

    /**
     * 获取数据适配器
     */
    public T getAdapter() {
        return adapter;
    }

    /**
     * 设置数据适配器
     */
    public void setAdapter(@NonNull T adapter) {
        this.adapter = adapter;
    }

    public boolean canScroll() {
        return attribute.canScroll;
    }

    /**
     * 获取配置信息
     */
    public A getAttribute() {
        return attribute;
    }

    /**
     * 获取校准后边框坐标点
     */
    public @NonNull
    float[] getBorderPoints(float left, float top, float right, float bottom) {
        final float[] borderPts = new float[4];
        borderPts[0] = left - borderCorrection;
        borderPts[1] = top - borderCorrection;
        borderPts[2] = right + borderCorrection;
        borderPts[3] = bottom + borderCorrection;
        return borderPts;
    }

    /**
     * 根据位置获取长按区域内的信息
     */
    public void onHighlight(float x, float y) {
        highlight = false;
        focusModuleCache = mainModule;
        if (adapter.getCount() == 0) {
            return;
        }
        AbsModule<AbsEntry> focusModule = getFocusModule(x, y);
        this.focusModuleCache = focusModule.getModuleType() == ModuleType.FLOAT ? this.focusModuleCache : focusModule;
        //如果存在结果，则对x轴坐标进行修正，使之不超过焦点区域内的ChartModule的left和right
        if (x < this.focusModuleCache.getRect().left) {
            x = this.focusModuleCache.getRect().left;
        } else if (x > this.focusModuleCache.getRect().right) {
            x = this.focusModuleCache.getRect().right;
        }
        highlightPoint[0] = x;
        highlightPoint[1] = y;
        highlight = true;
    }

    /**
     * 获取当前焦点区域内的Module
     */
    public AbsModule<AbsEntry> getFocusModule(float x, float y) {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isAttach() && module.getRect().contains(x, y)) {
                    return module;
                }
            }
        }
        return mainModule;
    }

    /**
     * 长按事件取消
     */
    public void onCancelHighlight() {
        highlight = false;
        highlightPoint[0] = -1;
        highlightPoint[1] = -1;
    }

    /**
     * 是否是长按状态
     */
    public boolean isHighlight() {
        return highlight;
    }

    /**
     * 获取长按的位置
     */
    public float[] getHighlightPoint() {
        return highlightPoint;
    }

    /**
     * 是否按比例计算module大小
     */
    public void setProrate(boolean prorate) {
        this.isProrate = prorate;
    }

    /**
     * 设置第一次加载应滚动到的下标位置
     */
    public void setFirstLoadPosition(int firstLoadPosition) {
        this.firstLoadPosition = firstLoadPosition;
    }

    /**
     * 是否可以滚动
     */
    public boolean canScroll(float dx) {
        final float offset = touchValues[Matrix.MTRANS_X] - dx;
        return (!(offset < -maxScrollOffset) || !(touchValues[Matrix.MTRANS_X] <= -maxScrollOffset))
                && (!(offset > minScrollOffset) || !(touchValues[Matrix.MTRANS_X] >= minScrollOffset));
    }

    /**
     * 是否可以拖动
     */
    public boolean canDragging() {
        return true;
    }

    /**
     * 获取超出边界滚动的距离
     */
    public float getOverScrollOffset() {
        return overScrollOffset;
    }

    /**
     * 设置超出边界滚动的距离
     */
    public void setOverScrollOffset(float overScrollOffset) {
        this.overScrollOffset = overScrollOffset;
    }

    /**
     * 更新超出边界滚动的距离
     *
     * @param dx 变化量
     */
    public void updateOverScrollOffset(float dx) {
        overScrollOffset += -dx;
    }

    /**
     * 获取最大滚动量
     */
    public float getMaxScrollOffset() {
        return -maxScrollOffset;
    }

    /**
     * 获取当前滚动量
     */
    public float getCurrentTransX() {
        matrixTouch.getValues(touchValues);
        return touchValues[Matrix.MTRANS_X];
    }

    /**
     * 获取x轴label间隔
     */
    public int getInterval() {
        return interval;
    }

    /**
     * 更新x轴label间隔
     */
    public void resetInterval() {
        int value = Math.round(attribute.visibleCount / attribute.gridCount);
        if (value > 0) {
            this.interval = value;
        }
    }

    /**
     * 初始化模块
     * tips:当View高度模式为WRAP_CONTENT时，可能会出现view的高度与模块实际所需的高度不相符，
     * 这时就需要返回false，通知view调用requestLayout()重新计算view的高度
     *
     * @return true:高度相符 false：高度不符
     */
    public boolean onModuleInit() {
        if (null == adapter) {
            return true;
        }
        measureModuleSize();
        initModuleDrawing();
        if (isProrate) {
            measureModuleSize();
            return true;
        }
        int height = measureActualOccupyHeight();
//        if (this instanceof CandleRender) {
//            Log.e(TAG, "实际高度：" + height + "    view高度：" + ((int) viewRect.height()));
//        }
        return height == (int) viewRect.height();
    }

    /**
     * 当ViewRect发生改变时，修改viewRectChange状态，用于重新定位和更新所需参数
     */
    public void onViewInit() {
        layoutModule();
        if (isReady()) {
            initViewCoordinates();
            resetPointsWidth();
            correctVisibleCount();
            resetInterval();
            resetMatrix();
            onReady();
        }
    }

    /**
     * 初始化模块中的绘图组件
     */
    public void initModuleDrawing() {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) {
                    continue;
                }
                module.initDrawing(this);
                module.initDrawingMargin();
            }
        }
    }

    /**
     * 布局组件
     */
    void layoutModule() {
        float left = viewRect.left + attribute.borderWidth;
        float top = viewRect.top + attribute.borderWidth;
        float y;
        float marginTop = 0;
        final List<AbsModule<AbsEntry>> floatModules = chartModules.get(ModuleGroupType.FLOAT);
        if (null != floatModules) {
            for (AbsModule<AbsEntry> module : floatModules) {
                module.setRect(
                        left + module.getPaddingLeft(),
                        top + module.getPaddingTop(),
                        module.getRect().width() - module.getPaddingRight(),
                        module.getRect().height() - module.getPaddingBottom());
                marginTop = Math.max(marginTop, module.getDrawingMargin()[1]);
            }
            top = top + attribute.borderWidth;
        }
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach() || module.getModuleGroup() == ModuleGroupType.FLOAT) {
                    continue;
                }
                //更新主图模型
                if (module.getModuleGroup() == ModuleGroupType.MAIN) {
                    this.mainModule = (MainModule<AbsEntry>) module;
                }
                //更新最顶部的图表模型
                if (null == topModule) {
                    this.topModule = module;
                }
                //更新最底部的图表模型
                this.bottomModule = module;
                //分配图表大小和位置
                marginTop = Math.max(module.getDrawingMargin()[1], marginTop);
                top += marginTop;
//                if (this instanceof CandleRender) {
//                    Log.e(TAG, "top111111:" + top);
//                }
                y = top + module.getRect().height();
                module.setRect(
                        left + module.getPaddingLeft(),
                        top + module.getPaddingTop(),
                        module.getRect().width() - module.getPaddingRight(),
                        y - module.getPaddingBottom());
                top = y + attribute.viewInterval + module.getDrawingMargin()[3] + attribute.borderWidth * 2f;
                marginTop = 0;
//                if (this instanceof CandleRender) {
//                    Log.e(TAG, "moduleRect:" + module.getRect().toString() + "    top:" + top);
//                }
            }
        }
    }

    /**
     * 刷新Matrix
     */
    void resetMatrix() {
        initMatrixValue(mainModule.getRect());
        postMatrixOffset(mainModule.getRect().left, viewRect.top);
        postMatrixTouch(mainModule.getRect(), mainModule.getRect().width(), attribute.visibleCount);
    }

    /**
     * 视图准备就绪
     */
    private void onReady() {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) {
                    continue;
                }
                module.onLayoutComplete();
            }
        }
    }

    /**
     * 无边界检测地更新当前滚动量
     *
     * @param dx 变化量
     */
    public void updateCurrentTransX(float dx) {
        matrixTouch.getValues(touchValues);
        touchValues[Matrix.MTRANS_X] += -dx;
        matrixTouch.setValues(touchValues);
    }

    /**
     * 无边界检测地直接设置当前滚动量
     *
     * @param transX 当前滚动位置。此值为正时将被程序视为负，因为这里的滚动量用负数表示。
     */
    public void setCurrentTransX(float transX) {
        matrixTouch.getValues(touchValues);
        touchValues[Matrix.MTRANS_X] = transX > 0 ? -transX : transX;
        matrixTouch.setValues(touchValues);
    }

    /**
     * 滚动到给定的 entryIndex 对应的滚动偏移量
     *
     * @param entryIndex entry 索引
     */
    public void toTransX(int entryIndex) {
        if (attribute.rightScrollOffset > 0) {
            setCurrentTransX(-maxScrollOffset + attribute.rightScrollOffset);
        }
        setCurrentTransX(getTransX(getMainModule().getRect(),
                attribute.visibleCount, entryIndex) - 1f);
    }

    /**
     * 获取给定的 entryIndex 对应的滚动偏移量。在调用 {@link #computeScrollRange} 之后才能调用此方法
     *
     * @param visibleCount 当前显示区域的 X 轴方向上需要显示多少个 entry 值
     * @param entryIndex   entry 索引
     */
    protected float getTransX(RectF rect, float visibleCount, float entryIndex) {
        final int entrySetSize = adapter.getCount();
        if (entrySetSize <= visibleCount) {
            return 0;
        }
        float leftOffset = getPointX(0, null) - rect.left;
        float rightOffset = rect.right - getPointX(entrySetSize, null);
        rightOffset = rightOffset > 0 ? Math.min(rightOffset, attribute.rightScrollOffset) : 0;
        leftOffset = leftOffset > 0 ? Math.min(leftOffset, attribute.leftScrollOffset) : 0;

        float scrollOffset = maxScrollOffset - (attribute.rightScrollOffset - rightOffset);
        float result = scrollOffset * entryIndex / (entrySetSize - visibleCount) - leftOffset;
        result = Math.min(scrollOffset, result);
        //Log.e("valuehaha", "scrollOffset"+scrollOffset+"     result:" + (-result));
        return -result;
    }

    /**
     * 计算当前缩放下，X 轴方向的最小滚动值和最大滚动值
     *
     * @param width  当前显示区域的宽
     * @param scaleX X 轴方向的缩放
     */
    protected void computeScrollRange(float width, float scaleX) {
        minScrollOffset = attribute.leftScrollOffset;
        if (scaleX > 1f) {
            maxScrollOffset = width * (scaleX - 1f) + attribute.rightScrollOffset;
        } else {
            lastMaxScrollOffset = 0;
            maxScrollOffset = 0;
        }
    }

    /**
     * 获取给定的 entryIndex 对应的当前试图区域的位置
     *
     * @param entryIndex entry 索引
     */
    public float getPointX(float entryIndex, Matrix matrix) {
        points[0] = entryIndex;
        points[1] = 0;
        if (null == matrix) {
            mapPoints(points);
        } else {
            mapPoints(matrix, points);
        }
        return points[0];
    }

    /**
     * 更新当前滚动量，当滚动到边界时将不能再滚动
     *
     * @param dx 变化量
     */
    public void scroll(float dx) {
        matrixTouch.getValues(touchValues);
        touchValues[Matrix.MTRANS_X] += -dx;
        overScrollOffset = 0;
        if (touchValues[Matrix.MTRANS_X] < -maxScrollOffset) {
            touchValues[Matrix.MTRANS_X] = -maxScrollOffset;
        } else if (touchValues[Matrix.MTRANS_X] > minScrollOffset) {
            touchValues[Matrix.MTRANS_X] = minScrollOffset;
        }
        matrixTouch.setValues(touchValues);
    }

    /**
     * 缩放
     *
     * @param contentRect  当前显示区域
     * @param visibleCount 当前显示区域的 X 轴方向上需要显示多少个 entry 值
     * @param x            在点(x, y)上缩放
     * @param y            在点(x, y)上缩放。由于 K 线图只会进行水平滚动，因此 y 值被忽略
     */
    protected void zoom(RectF contentRect, float visibleCount, float x, float y) {
        if (x < contentRect.left) {
            x = contentRect.left;
        } else if (x > contentRect.right) {
            x = contentRect.right;
        }

        matrixTouch.getValues(touchValues);

        final float minVisibleIndex;
        final float toMinVisibleIndex = visibleCount * (x - contentRect.left) / contentRect.width();

        touchPts[0] = x;
        touchPts[1] = 0;
        invertMapPoints(touchPts);

        if (touchPts[0] <= toMinVisibleIndex) {
            minVisibleIndex = 0;
        } else {
            minVisibleIndex = Math.abs(touchPts[0] - toMinVisibleIndex);
        }
        touchValues[Matrix.MSCALE_X] = adapter.getCount() / visibleCount;

        computeScrollRange(contentRect.width(), touchValues[Matrix.MSCALE_X]);

        touchValues[Matrix.MTRANS_X] = getTransX(contentRect, visibleCount, minVisibleIndex) + (int) overScrollOffset;

        matrixTouch.setValues(touchValues);
    }

    /**
     * 坐标值校准(将矩阵计算出的屏幕像素坐标进行校准)
     *
     * @param pts 浮点数序列 [x0, y0, x1, y1, ...]
     */
    private void calibrationMapPoints(float[] pts, float xOffset, float yOffset) {
        int begin;
        int increase = 1;
        if (xOffset != 0) {
            begin = 0;
        } else if (yOffset != 0) {
            begin = 1;
            increase += 1;
        } else {
            return;
        }
        for (int i = begin; i < pts.length; i += increase) {
            if ((i & 1) == 1) {
                pts[i] += yOffset;
            } else {
                pts[i] += xOffset;
            }
        }
    }

    /**
     * 利用矩阵将 entry 的值映射到屏幕像素上
     *
     * @param pts 浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void mapPoints(float[] pts, float xOffset, float yOffset) {
        matrixValue.mapPoints(pts);
        matrixTouch.mapPoints(pts);
        matrixOffset.mapPoints(pts);
        calibrationMapPoints(pts, xOffset, -yOffset);
    }

    /**
     * 利用矩阵将 entry 的值映射到屏幕像素上
     *
     * @param pts 浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void mapPoints(float[] pts) {
        matrixValue.mapPoints(pts);
        matrixTouch.mapPoints(pts);
        matrixOffset.mapPoints(pts);
    }

    /**
     * 按给定矩阵将 entry 的值映射到屏幕像素上
     *
     * @param matrix 矩阵
     * @param pts    浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void mapPoints(Matrix matrix, float[] pts) {
        if (matrix == null) {
            matrixValue.mapPoints(pts);
        } else {
            matrix.mapPoints(pts);
        }
        matrixTouch.mapPoints(pts);
        matrixOffset.mapPoints(pts);
    }

    /**
     * 按给定矩阵将 entry 的值映射到屏幕像素上
     *
     * @param matrix 矩阵
     * @param pts    浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void mapPoints(Matrix matrix, float[] pts, float xOffset, float yOffset) {
        if (matrix == null) {
            matrixValue.mapPoints(pts);
        } else {
            matrix.mapPoints(pts);
        }
        matrixTouch.mapPoints(pts);
        matrixOffset.mapPoints(pts);
        calibrationMapPoints(pts, xOffset, -yOffset);
    }

    /**
     * 将基于屏幕像素的坐标反转成 entry 的值
     *
     * @param pts 浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void invertMapPoints(float[] pts) {
        matrixInvert.reset();

        matrixOffset.invert(matrixInvert);
        matrixInvert.mapPoints(pts);

        matrixTouch.invert(matrixInvert);
        matrixInvert.mapPoints(pts);

        matrixValue.invert(matrixInvert);
        matrixInvert.mapPoints(pts);
    }

    /**
     * 将基于屏幕像素的坐标按给定矩阵反转到值
     *
     * @param matrix 矩阵
     * @param pts    浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void invertMapPoints(Matrix matrix, float[] pts) {
        if (null == matrix) {
            invertMapPoints(pts);
        } else {
            matrixInvert.reset();
            matrix.invert(matrixInvert);
            matrixInvert.mapPoints(pts);
        }
    }

    /**
     * 值矩阵运算
     */
    protected void postMatrixValue(AbsModule<AbsEntry> chartModule) {
        RectF rect = chartModule.getRect();//视图 rect
        //计算 X,Y 轴的极值
        computeExtremumValue(extremum, chartModule);
        final float deltaX = extremum[2] - extremum[0];
        final float deltaY = extremum[3] - extremum[1];
        if (Float.isInfinite(deltaY) || Float.isInfinite(deltaX)) {
            return;
        }
        final float scaleX = (rect.width() - chartModule.getXCorrectedValue())
                / (deltaX == 0 ? adapter.getCount() : deltaX);
        final float scaleY = (rect.height() - chartModule.getYCorrectedValue()) / deltaY;
        final float translateX = extremum[0] * scaleX;
        final float translateY = rect.top + extremum[3] / deltaY * rect.height();
        matrixValue.reset();
        matrixValue.postScale(scaleX, -scaleY);
        matrixValue.postTranslate(-translateX, translateY);
        //反向赋值
        chartModule.getMatrix().set(matrixValue);
    }

    /**
     * 初始化值矩阵
     */
    protected void initMatrixValue(RectF rect) {
        matrixValue.reset();
        matrixValue.postScale(rect.width() / adapter.getCount(), 1);
    }

    /**
     * 手势滑动缩放矩阵运算
     *
     * @param rect         当前显示区域矩形
     * @param width        当前显示区域宽度
     * @param visibleCount 当前显示区域的 X 轴方向上需要显示多少个 entry 值
     */
    protected void postMatrixTouch(RectF rect, float width, float visibleCount) {
        final float scaleX = adapter.getCount() / visibleCount;
        matrixTouch.reset();
        matrixTouch.postScale(scaleX, 1);
        if (maxScrollOffset != 0) {
            lastMaxScrollOffset = maxScrollOffset;
        }
        computeScrollRange(width, scaleX);
        if (touchValues[Matrix.MTRANS_X] > 0) {
            // 左滑加载完成之后定位到之前滚动的位置
            matrixTouch.postTranslate(
                    touchValues[Matrix.MTRANS_X] - (maxScrollOffset - lastMaxScrollOffset), 0);
        } else if (touchValues[Matrix.MTRANS_X] < 0) {
            if ((int) overScrollOffset == 0) {
                // 转动屏幕方向导致矩形变化，定位到之前相同比例的滚动位置
                touchValues[Matrix.MTRANS_X] =
                        touchValues[Matrix.MTRANS_X] / lastMaxScrollOffset * maxScrollOffset;
            }
            matrixTouch.postTranslate(touchValues[Matrix.MTRANS_X], 0);
//            Log.e(TAG, "##d postMatrixTouch: currentOffset = " + touchValues[Matrix.MTRANS_X]
//                    + ", rightScrollOffset = " + attribute.rightScrollOffset);
        } else if (firstLoad) {
            this.firstLoad = false;
            float firstScrollOffset;
            if (firstLoadPosition < 0) { // 通常首次加载时定位到最末尾
                firstScrollOffset = -maxScrollOffset;
            } else { //如果有需要第一次加载滚动到的下标位置，则滚动到该下标对应位置
                firstScrollOffset = getTransX(rect, attribute.visibleCount, firstLoadPosition) - 1f;
            }
            setCurrentTransX(firstScrollOffset);
        }
        //Log.e(TAG, "##d postMatrixTouch: currentOffset = " + touchValues[Matrix.MTRANS_X]
        //    + ", maxScrollOffset = " + -maxScrollOffset
        //    + ", minScrollOffset = " + minScrollOffset
        //    + ", lastMaxScrollOffset = " + lastMaxScrollOffset
        //    + ", overScrollOffset = " + overScrollOffset);
    }

    /**
     * 偏移矩阵运算
     *
     * @param offsetY 偏移量 Y
     */
    protected void postMatrixOffset(float offsetX, float offsetY) {
        matrixOffset.reset();
        matrixOffset.postTranslate(offsetX, offsetY);
    }

    /**
     * 计算显示区域内 X,Y 轴的范围
     */
    protected void computeExtremumValue(float[] extremum, AbsModule<AbsEntry> chartModule) {
        //X轴
        extremum[0] = chartModule.getMinX().value;
        extremum[2] = chartModule.getMaxX().value;
        //Y轴
        extremum[1] = chartModule.getMinY().value;
        extremum[3] = chartModule.getMaxY().value;
    }

    /**
     * 渲染Drawing
     */
    private void renderDrawing(Canvas canvas, AbsModule<AbsEntry> module) {
        for (AbsDrawing<?, ?> drawing : module.getDrawingList()) {
            if (checkDrawingState(drawing, module)) {
                drawing.readyComputation(canvas, begin, end, extremum);
            }
        }
        for (AbsDrawing<?, ?> drawing : module.getDrawingList()) {
            if (checkDrawingState(drawing, module)) {
                for (int i = begin; i < end; i++) {
                    drawing.onComputation(begin, end, i, extremum);
                }
            }
        }
        for (AbsDrawing<?, ?> drawing : module.getDrawingList()) {
            if (checkDrawingState(drawing, module)) {
                drawing.onDraw(canvas, begin, end, extremum);
            }
        }
        for (AbsDrawing<?, ?> drawing : module.getDrawingList()) {
            if (checkDrawingState(drawing, module)) {
                drawing.drawOver(canvas);
            }
        }
    }

    /**
     * 检查绘制组件状态
     *
     * @return true:绘制  false:不绘制
     */
    private boolean checkDrawingState(AbsDrawing<?, ?> drawing, AbsModule<AbsEntry> module) {
        if (!drawing.isInit()) {
            return false;
        }
        if (drawing instanceof IndexDrawing) {
            return ((IndexDrawing<?, ?>) drawing).getIndexType() == module.getAttachIndexType();
        }
        return true;
    }

    /**
     * 测量模块(预计)占用高度（用于View高度模式为WRAP_CONTENT时）
     */
    public int measureEstimateOccupyHeight() {
        return measureUtils.measureEstimateOccupyHeight();
    }

    /**
     * 测量模块(实际)占用高度（用于View高度模式为WRAP_CONTENT时）
     */
    public int measureActualOccupyHeight() {
        return measureUtils.measureActualOccupyHeight();
    }

    /**
     * 测量模块大小
     */
    public void measureModuleSize() {
        measureUtils.measureModuleSize(viewRect.width(), viewRect.height(), isProrate);
    }

    /**
     * 重置图表组件
     */
    public void resetChartModules() {
        this.chartModules.clear();
        this.chartModules.put(ModuleGroupType.MAIN, new ArrayList<>());
    }

    /**
     * 添加图表组件
     */
    @SuppressLint("SwitchIntDef")
    public void addModule(@NotNull AbsModule module) {
        List<AbsModule<AbsEntry>> modules = chartModules.get(module.getModuleGroup());
        if (null == modules) {
            modules = new ArrayList<>();
            chartModules.put(module.getModuleGroup(), modules);
        }
        modules.add(module);
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
     * 获取最小显示的位置
     */
    public int getBegin() {
        return begin;
    }

    /**
     * 获取最大显示的位置
     */
    public int getEnd() {
        return end;
    }

    /**
     * 指标集合
     */
    public LinkedHashMap<Integer, List<AbsModule<AbsEntry>>> getModules() {
        return chartModules;
    }

    /**
     * 获取启用的分组数量
     */
    public int getModuleGroupCount() {
        int count = 0;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            if (item.getKey() == ModuleGroupType.FLOAT) {
                continue;
            }
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isAttach()) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    /**
     * 获取焦点module缓存
     */
    public @Nullable
    AbsModule<AbsEntry> getFocusModuleCache() {
        return focusModuleCache;
    }

    /**
     * 根据图表ModuleType获取对应模型
     */
    public AbsModule<AbsEntry> getModule(@ModuleType int moduleType,
                                         @ModuleGroupType int moduleGroupType) {
        List<AbsModule<AbsEntry>> modules = chartModules.get(moduleGroupType);
        if (null != modules) {
            for (AbsModule<AbsEntry> item : modules) {
                if (item.getModuleType() == moduleType) {
                    return item;
                }
            }
        }
        return mainModule;
    }

    /**
     * 根据当前可见的主图组件
     */
    public AbsModule<AbsEntry> getMainModule() {
        return mainModule;
    }

    /**
     * Drawing的Click
     *
     * @return (返回响应事件的元素)
     */
    public @Nullable
    AbsDrawing<?, ?> onDrawingClick(float x, float y) {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isAttach() && module.getRect().contains(x, y)) {
                    for (AbsDrawing<?, ?> drawing : module.getDrawingList()) {
                        if (drawing.onDrawingClick(x, y)) {
                            return drawing;
                        }
                    }
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 开始渲染
     */
    public void render(Canvas canvas) {
        if (!isReady()) {
            return;
        }
        computeVisibleIndex();
        computeMinMax();
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) {
                    continue;
                }
                postMatrixValue(module);
                renderDrawing(canvas, module);
            }
        }
    }

    /**
     * 计算当前显示区域内 entry 在 Y 轴上的最小值和最大值
     */
    protected void computeMinMax() {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isAttach()) {
                    module.resetMinMax();
                    for (int i = begin; i < end; i++) {
                        module.computeMinMax(adapter.getItem(i));
                    }
                }
            }
        }
        adapter.computeMinAndMax(begin, end);
    }

    /**
     * 计算当前显示区域内的 X 轴范围
     */
    protected void computeVisibleIndex() {
        begin = 0;
        end = getAdapter().getCount();
    }

    /**
     * 配置文件更改回调
     */
    public void onAttributeChange() {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                module.resetDrawing();
            }
        }
    }

    /**
     * 数据更改
     */
    public void onDataChange() {
        if (isReady()) {
            correctVisibleCount();
            resetMatrix();
        }
    }

    /**
     * 是否已经准备好
     */
    public boolean isReady() {
        return null != mainModule && null != adapter;
    }

    /**
     * 获取当前被启用的最顶部的图表模型
     */
    public AbsModule<AbsEntry> getTopModule() {
        return topModule;
    }

    /**
     * 获取当前被启用的最底部的图表模型
     */
    public AbsModule<AbsEntry> getBottomModule() {
        return bottomModule;
    }

    /**
     * 初始化每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...]
     * 此方法只更新top和bottom(如：y0 y1)
     */
    public void initViewCoordinates() {
        int coordinatesCount = getModuleGroupCount() * 4;
        if (null == moduleCoordinatePoint || coordinatesCount != moduleCoordinatePoint.length) {
            moduleCoordinatePoint = new float[coordinatesCount];
        }
        int point = -1;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (int i = 0, z = item.getValue().size(); i < z && i < moduleCoordinatePoint.length; i++) {
                AbsModule<AbsEntry> module = item.getValue().get(i);
                if (module.isAttach() && module.getModuleType() != ModuleType.FLOAT) {
                    point += 2;
                    moduleCoordinatePoint[point] = module.getRect().top;
                    point += 2;
                    moduleCoordinatePoint[point] = module.getRect().bottom;
                }
            }
        }
    }

    /**
     * 构建每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...]
     * 此方法只更新left和right(如：x0 x1)
     */
    public float[] buildViewLRCoordinates(float x0, float x1) {
        for (int i = 0; i < moduleCoordinatePoint.length; i += 2) {
            moduleCoordinatePoint[i] = x0;
            i += 2;
            moduleCoordinatePoint[i] = x1;
        }
        return moduleCoordinatePoint;
    }
}
