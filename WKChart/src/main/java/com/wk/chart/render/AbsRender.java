

package com.wk.chart.render;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.wk.chart.adapter.AbsAdapter;
import com.wk.chart.compat.MeasureUtils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.compat.config.AbsBuildConfig;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ClickDrawingID;
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
    protected final Matrix matrixTouch = new Matrix(); // 缩放和平移矩阵
    protected final Matrix matrixOffset = new Matrix(); // 偏移矩阵
    protected final Matrix matrixInvert = new Matrix(); // 用于缓存反转矩阵
    private final float[] points = new float[2];//储存某个点的坐标系信息
    private final float[] touchPts = new float[2];//存储交互位置
    private final float[] touchValues = new float[9]; // 存储缩放和平移信息
    private final float[] extremum = new float[4];//极值[x0, y0, x1, y1]
    private final float[] highlightPoint = new float[2];//长按位置
    protected final RectF viewRect; // 整个的视图区域
    protected final A attribute; // 配置信息
    protected T adapter; // 数据适配器
    protected MainModule<AbsEntry> mainModule;//主图模块
    protected AbsModule<AbsEntry> focusModule;//焦点图表模型
    private boolean highlight = false;//长按事件状态
    private boolean firstLoad = true;//首次加载
    private boolean isProrate = true;//是否按比例计算module大小
    private float minScrollOffset = 0; // 最小滚动量
    private float maxScrollOffset = 0; // 最大滚动量
    private float cacheMaxScrollOffset = 0; // 缓存最大滚动量
    private float cacheCurrentTransX = 0; // 缓存最大滚动量
    private float overScrollOffset = 0; // 超出边界的滚动量
    public float pointsMinWidth;//数据点最小宽度（包含两边的间隔）
    public float pointsWidth;//数据点默认宽度
    public float pointsSpace;//数据点间隔
    protected int begin;//开始位置索引
    protected int end;//结束位置索引

    public AbsRender(A attribute, RectF viewRect) {
        this.attribute = attribute;
        this.viewRect = viewRect;
        this.measureUtils = new MeasureUtils();
        this.chartModules = new LinkedHashMap<>();
        resetChartModules();
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
     * 根据位置获取长按区域内的信息
     */
    public void onHighlight(float x, float y) {
        if (adapter.getCount() == 0) {
            return;
        }
        resetFocusModule(x, y);
        RectF rect = focusModule.getRect();
        x = Math.max(x, rect.left);
        x = Math.min(x, rect.right);
        y = Math.max(y, rect.top);
        y = Math.min(y, rect.bottom);
        highlightPoint[0] = x;
        highlightPoint[1] = y;
        highlight = true;
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
     * 获取焦点模型
     *
     * @return 焦点模型
     */
    public AbsModule<AbsEntry> getFocusModule() {
        return focusModule;
    }

    /**
     * 是否按比例计算module大小
     */
    public void setProrate(boolean prorate) {
        this.isProrate = prorate;
    }

    /**
     * 设置缓存最大滚动量
     *
     * @param cacheMaxScrollOffset 缓存最大滚动量
     */
    public void setCacheMaxScrollOffset(float cacheMaxScrollOffset) {
        this.cacheMaxScrollOffset = cacheMaxScrollOffset;
    }

    public void setCacheCurrentTransX(float cacheCurrentTransX) {
        this.cacheCurrentTransX = cacheCurrentTransX;
    }

    /**
     * 是否可以滚动
     */
    public boolean canScroll(float dx) {
        return (touchValues[Matrix.MTRANS_X] < minScrollOffset) &&
                (touchValues[Matrix.MTRANS_X] > -maxScrollOffset);
    }

    /**
     * 是否可以拖动
     */
    public boolean canDragging(float dx) {
        return dx != 0;
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
        overScrollOffset -= dx;
    }

    /**
     * 获取最大滚动量
     */
    public float getMaxScrollOffset() {
        return maxScrollOffset;
    }

    /**
     * 获取当前滚动量
     */
    public float getCurrentTransX() {
        matrixTouch.getValues(touchValues);
        return touchValues[Matrix.MTRANS_X];
    }

    /**
     * 初始化模块
     * tips:当View高度模式为WRAP_CONTENT时，可能会出现view的高度与模块实际所需的高度不相符，
     * 这时就需要返回false，通知view调用requestLayout()重新计算view的高度
     *
     * @return true:高度相符 false：高度不符
     */
    public boolean onModuleInit() {
        if (null == adapter) return true;
        float viewWidth = viewRect.width();
        float viewHeight = viewRect.height();
        initModuleDrawing(viewWidth, viewHeight);
        measureModuleSize();
        if (isProrate) return true;
        int measureHeight = measureViewHeight();
//        if (this instanceof CandleRender) {
//            Log.e(TAG, "计算高度：" + measureHeight + "    view高度：" + (int) viewHeight);
//        }
        return (int) viewHeight == measureHeight;
    }

    /**
     * 当ViewRect发生改变时，修改viewRectChange状态，用于重新定位和更新所需参数
     */
    public void onViewInit() {
        initBasicsAttr();
        layoutModule();
        if (isReady()) {
            resetPointsWidth();
            resetMatrix();
            layoutComplete();
        }
    }

    /**
     * 初始化模块中的绘图组件
     */
    public void initModuleDrawing(float viewWidth, float viewHeight) {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) {
                    continue;
                }
                module.initDrawing(this);
                module.initDrawingMargin(viewWidth, viewHeight);
            }
        }
    }

    /**
     * 初始化基础属性
     */
    private void initBasicsAttr() {
        this.pointsSpace = (attribute.pointSpace / attribute.pointWidth) / 2f;
        this.pointsMinWidth = attribute.pointBorderWidth +
                attribute.pointBorderWidth * (attribute.pointSpace / attribute.pointWidth * 2f);
    }

    /**
     * 布局组件
     */
    void layoutModule() {
        float left = viewRect.left, top = viewRect.top, maxMarginLeft = 0f;
        //浮动模块布局
        List<AbsModule<AbsEntry>> viewModules = chartModules.get(ModuleGroupType.FLOAT);
        if (null != viewModules) {
            float[] maxMargin = measureUtils.getModuleMaxMargin(viewModules);
            left += maxMargin[0];
            top += maxMargin[1];
            for (AbsModule<AbsEntry> module : viewModules) {
                if (!module.isAttach()) continue;
                module.setRect(left, top, left + module.getWidth(), top + module.getHeight());
            }
        }
        //指标模块Left位置计算
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            if (item.getKey() == ModuleGroupType.FLOAT) continue;
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) continue;
                float[] margin = module.getDrawingMargin();
                maxMarginLeft = Math.max(maxMarginLeft, margin[0]);
            }
        }
        left += maxMarginLeft;
        //指标模块布局
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            if (item.getKey() == ModuleGroupType.FLOAT) continue;
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) continue;
                //更新主图模块
                if (module.getModuleGroup() == ModuleGroupType.MAIN) {
                    this.mainModule = (MainModule<AbsEntry>) module;
                }
                //分配图表大小和位置
                float[] margin = module.getDrawingMargin();
                top += margin[1];
                module.setRect(left, top, left + module.getWidth(), top + module.getHeight());
                top = module.getRect().bottom + margin[3] + attribute.viewInterval;
//                if (this instanceof CandleRender) {
//                    Log.e(TAG, "moduleRect:" + module.getRect().toString());
//                }
            }
        }
    }

    /**
     * 初始化图表
     */
    public void resetChart() {
        firstLoad = true;
        cacheCurrentTransX = 0;
        cacheMaxScrollOffset = 0;
        setOverScrollOffset(0);
        setCurrentTransX(0);
    }

    /**
     * 刷新缩放后的数据点宽度和显示数量
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
            attribute.visibleCount = mainModule.getRect().width() / pointsWidth;
        } else {
            pointsMinWidth = pointsWidth = pointsSpace = 0;
            attribute.visibleCount = getAdapter().getCount();
        }
    }

    /**
     * 获取缩放后并且减去CandleSpace的CandleWidth
     */
    public float getSubtractSpacePointWidth() {
        return (attribute.pointWidth - attribute.pointSpace) * attribute.currentScale;
    }

    /**
     * 刷新Matrix
     */
    void resetMatrix() {
        final float lastMaxScrollOffset = maxScrollOffset;
        final float visibleScale = adapter.getCount() / attribute.visibleCount;
        final float widthScale = mainModule.getRect().width() / adapter.getCount();
        computeScrollRange(mainModule.getRect(), visibleScale);
        postMatrixScale(matrixTouch, visibleScale, 1f);
        postMatrixScale(mainModule.getMatrix(), widthScale, 1f);
        postMatrixOffset(matrixOffset, mainModule.getRect().left, viewRect.top);
        postMatrixTranslate(matrixTouch, lastMaxScrollOffset);
    }

    /**
     * 布局完成
     */
    private void layoutComplete() {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isAttach()) {
                    module.onLayoutComplete();
                }
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
        touchValues[Matrix.MTRANS_X] -= dx;
        matrixTouch.setValues(touchValues);
    }

    /**
     * 无边界检测地直接设置当前滚动量
     *
     * @param transX 当前滚动位置。此值为正时将被程序视为负，因为这里的滚动量用负数表示。
     */
    public void setCurrentTransX(float transX) {
        touchValues[Matrix.MTRANS_X] = transX > 0 ? -transX : transX;
        matrixTouch.setValues(touchValues);
    }

    /**
     * 获取给定的 entryIndex 对应的滚动偏移量。在调用 {@link #computeScrollRange} 之后才能调用此方法
     *
     * @param matrix       矩阵
     * @param rect         显示区域矩形
     * @param visibleCount 当前显示区域的 X 轴方向上需要显示多少个 entry 值
     * @param entryIndex   entry 索引
     */
    protected float getTransX(Matrix matrix, RectF rect, float visibleCount, float entryIndex) {
        final int dataSize = adapter.getCount();
        if (dataSize <= visibleCount) return touchValues[Matrix.MTRANS_X];
        float leftOffset = getPointX(matrix, 0) - rect.left;
        float result = (maxScrollOffset - attribute.rightScrollOffset)
                * entryIndex / (dataSize - visibleCount)
                - Math.max(leftOffset, 0f);
        result = Math.min(maxScrollOffset, result);
        result = Math.max(-minScrollOffset, result);
        return -result + overScrollOffset;
    }

    /**
     * 计算当前缩放下，X 轴方向的最小滚动值和最大滚动值
     *
     * @param rect   显示区域矩形
     * @param scaleX X 轴方向的缩放
     */
    protected void computeScrollRange(RectF rect, float scaleX) {
        minScrollOffset = attribute.leftScrollOffset;
        if (scaleX > 1f) {
            maxScrollOffset = rect.width() * (scaleX - 1f) + attribute.rightScrollOffset;
        } else {
            maxScrollOffset = attribute.rightScrollOffset;
        }
    }

    /**
     * 获取给定的 entryIndex 对应的当前试图区域的位置
     *
     * @param entryIndex entry 索引
     */
    public float getPointX(@NotNull Matrix matrix, float entryIndex) {
        points[0] = entryIndex;
        points[1] = 0;
        mapPoints(matrix, points);
        return points[0];
    }

    /**
     * 更新当前滚动量，当滚动到边界时将不能再滚动
     *
     * @param dx 变化量
     */
    public void scroll(float dx) {
        overScrollOffset = 0;
        matrixTouch.getValues(touchValues);
        touchValues[Matrix.MTRANS_X] -= dx;
        touchValues[Matrix.MTRANS_X] = touchValues[Matrix.MTRANS_X] > minScrollOffset ?
                minScrollOffset : Math.max(touchValues[Matrix.MTRANS_X], -maxScrollOffset);
        matrixTouch.setValues(touchValues);
    }

    /**
     * 缩放
     *
     * @param matrix       当前矩阵
     * @param contentRect  当前显示区域
     * @param visibleCount 当前显示区域的 X 轴方向上需要显示多少个 entry 值
     * @param x            在点(x, y)上缩放
     * @param y            在点(x, y)上缩放。由于 K 线图只会进行水平滚动，因此 y 值被忽略
     */
    protected void zoom(@NotNull Matrix matrix, RectF contentRect, float visibleCount, float x, float y) {
        if (x < contentRect.left) {
            x = contentRect.left;
        } else if (x > contentRect.right) {
            x = contentRect.right;
        }
        final float minVisibleIndex;
        final float toMinVisibleIndex = visibleCount * (x - contentRect.left) / contentRect.width();
        touchPts[0] = x;
        touchPts[1] = 0;
        invertMapPoints(matrix, touchPts);
        if (touchPts[0] <= toMinVisibleIndex) {
            minVisibleIndex = 0;
        } else {
            minVisibleIndex = Math.abs(touchPts[0] - toMinVisibleIndex);
        }
        touchValues[Matrix.MSCALE_X] = adapter.getCount() / visibleCount;
        computeScrollRange(contentRect, touchValues[Matrix.MSCALE_X]);
        touchValues[Matrix.MTRANS_X] = getTransX(matrix, contentRect, visibleCount, minVisibleIndex);
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
     * 按给定矩阵将 entry 的值映射到屏幕像素上
     *
     * @param matrix 矩阵
     * @param pts    浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void mapPoints(@NotNull Matrix matrix, float[] pts) {
        matrix.mapPoints(pts);
        matrixTouch.mapPoints(pts);
        matrixOffset.mapPoints(pts);
    }

    /**
     * 按给定矩阵将 entry 的值映射到屏幕像素上
     *
     * @param matrix 矩阵
     * @param pts    浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void mapPoints(@NotNull Matrix matrix, float[] pts, float xOffset, float yOffset) {
        matrix.mapPoints(pts);
        matrixTouch.mapPoints(pts);
        matrixOffset.mapPoints(pts);
        calibrationMapPoints(pts, xOffset, -yOffset);
    }

    /**
     * 将基于屏幕像素的坐标反转成 entry 的值
     *
     * @param pts 浮点数序列 [x0, y0, x1, y1, ...]
     */
    public void invertMapPoints(@NotNull Matrix matrix, float[] pts) {
        matrixInvert.reset();

        matrixOffset.invert(matrixInvert);
        matrixInvert.mapPoints(pts);

        matrixTouch.invert(matrixInvert);
        matrixInvert.mapPoints(pts);

        matrix.invert(matrixInvert);
        matrixInvert.mapPoints(pts);
    }

    /**
     * 值矩阵运算
     */
    protected void postMatrixValue(AbsModule<AbsEntry> chartModule) {
        RectF rect = chartModule.getRect();//视图 rect
        Matrix matrix = chartModule.getMatrix();//视图 matrix
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
        matrix.reset();
        matrix.postScale(scaleX, -scaleY);
        matrix.postTranslate(-translateX, translateY);
    }

    /**
     * 偏移矩阵运算
     *
     * @param matrix  矩阵
     * @param offsetX 偏移量 X
     * @param offsetY 偏移量 Y
     */
    protected void postMatrixOffset(Matrix matrix, float offsetX, float offsetY) {
        matrix.reset();
        matrix.postTranslate(offsetX, offsetY);
    }

    /**
     * 滑动矩阵运算
     *
     * @param matrix              矩阵
     * @param lastMaxScrollOffset 上一次的最大滚动量
     */
    protected void postMatrixTranslate(Matrix matrix, float lastMaxScrollOffset) {
        float translate = Float.NaN;
        if (firstLoad) {
            this.firstLoad = false;
            if (cacheMaxScrollOffset > 0) { // 通常首次加载时定位到最末尾
                translate = cacheCurrentTransX + (cacheMaxScrollOffset - maxScrollOffset);
            } else { //如果有需要第一次加载滚动到的下标位置，则滚动到该下标对应位置
                translate = -maxScrollOffset;
            }
        } else if (touchValues[Matrix.MTRANS_X] > 0) { // 左滑加载完成之后定位到之前滚动的位置
            translate = touchValues[Matrix.MTRANS_X] - (maxScrollOffset - lastMaxScrollOffset) - attribute.leftScrollOffset;
        } else if (touchValues[Matrix.MTRANS_X] < 0) {// 右滑加载完成之后定位到之前滚动的位置
            if ((int) overScrollOffset == 0) {
                // 转动屏幕方向导致矩形变化，定位到之前相同比例的滚动位置
                translate = touchValues[Matrix.MTRANS_X];
            } else {
                translate = touchValues[Matrix.MTRANS_X] + attribute.rightScrollOffset;
            }
        }
        if (!Float.isNaN(translate)) {
            translate = Math.max(-maxScrollOffset, translate);
            translate = Math.min(minScrollOffset, translate);
            touchValues[Matrix.MTRANS_X] = translate;
            matrix.postTranslate(translate, 0f);
        }
    }

    /**
     * 矩阵缩放运算
     *
     * @param matrix 矩阵
     * @param sx     x轴倍率
     * @param sy     y轴倍率
     */
    protected void postMatrixScale(Matrix matrix, float sx, float sy) {
        matrix.reset();
        matrix.postScale(sx, sy);
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
     * 测量模块占用高度（用于View高度模式为WRAP_CONTENT时）
     */
    public int measureViewHeight() {
        return measureUtils.measureViewHeight(attribute, chartModules);
    }

    /**
     * 测量模块大小
     */
    public void measureModuleSize() {
        measureUtils.measureModuleSize(attribute, viewRect, chartModules, isProrate);
    }

    /**
     * 重置图表组件
     */
    public void resetChartModules() {
        this.chartModules.clear();
    }

    /**
     * 添加图表组件
     */
    public void addModule(@NotNull AbsModule<? extends AbsEntry> module) {
        List<AbsModule<AbsEntry>> modules = chartModules.get(module.getModuleGroup());
        if (null == modules) {
            modules = new ArrayList<>();
            chartModules.put(module.getModuleGroup(), modules);
        }
        modules.add((AbsModule<AbsEntry>) module);
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
     * 根据图表ModuleType获取对应模块
     */
    public AbsModule<AbsEntry> getModule(@ModuleType int moduleType, @ModuleGroupType int moduleGroupType) {
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
     * Click事件判定
     *
     * @return (返回响应事件的元素ID)
     */
    public int onClick(float x, float y) {
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                int clickId = module.onClick(x, y);
                if (clickId == ClickDrawingID.ID_NONE) {
                    continue;
                }
                return clickId;
            }
        }
        return ClickDrawingID.ID_NONE;
    }

    /**
     * 重置x,y焦点的Module
     */
    public void resetFocusModule(float x, float y) {
        if (null != focusModule && focusModule.getRect().contains(x, y)) return;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : chartModules.entrySet()) {
            if (item.getKey() == ModuleGroupType.FLOAT) continue;
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) continue;
                if (module.getRect().contains(x, y)) {
                    focusModule = module;
                    return;
                }
            }
        }
        focusModule = null == focusModule ? mainModule : focusModule;
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
            resetPointsWidth();
            resetMatrix();
        }
    }

    /**
     * 是否已经就绪
     */
    public boolean isReady() {
        return null != mainModule && !viewRect.isEmpty() && null != adapter && adapter.getCount() > 0;
    }
}
