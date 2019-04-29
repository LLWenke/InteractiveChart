

package com.ll.chart.render;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ll.chart.adapter.AbsAdapter;
import com.ll.chart.compat.MeasureUtils;
import com.ll.chart.compat.attribute.BaseAttribute;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.module.FloatChartModule;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.module.base.MainChartModule;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>AbsRender</p>
 */

public abstract class AbsRender<T extends AbsAdapter, A extends BaseAttribute> {
  private static final String TAG = "AbsRender";
  private final RectF viewRect; // 整个的视图区域
  private final MeasureUtils measureUtils;//计算工具类
  private final FloatChartModule floatChartModule;//跨视图组件
  private final List<AbsChartModule<? super AbsEntry>> chartModules = new ArrayList<>(); //图表指标列表
  private final Matrix matrixValue = new Matrix(); // 把值映射到屏幕像素的矩阵
  private final Matrix matrixTouch = new Matrix(); // 缩放和平移矩阵
  private final Matrix matrixOffset = new Matrix(); // 偏移矩阵
  private final Matrix matrixInvert = new Matrix(); // 用于缓存反转矩阵
  private final float[] points = new float[2];//储存某个点的坐标系信息
  private final float[] touchPts = new float[2];//存储交互位置
  private final float[] touchValues = new float[9]; // 存储缩放和平移信息
  private final float[] extremum = new float[4];//[x0, y0, x1, y1]
  private final float[] highlightPoint = new float[2];//长按位置

  protected T adapter; // 数据适配器
  protected A attribute; // 配置信息
  private @NonNull MainChartModule mainChartModule;//主图
  private AbsChartModule focusAreaChartModule = null;//焦点区域的ChartModule

  private boolean highlight = false;//长按事件状态
  private boolean firstLoad = true;//首次加载
  private float borderCorrection;//边框修正数值
  private float minScrollOffset = 0; // 最小滚动量
  private float maxScrollOffset = 0; // 最大滚动量
  private float lastMaxScrollOffset = 0; // 上一次的最大滚动量
  private float overScrollOffset = 0; // 超出边界的滚动量
  private int interval = 1;//x轴label间隔
  int begin;//开始位置索引
  int end;//结束位置索引

  public AbsRender(A attribute, RectF viewRect) {
    this.attribute = attribute;
    this.viewRect = viewRect;
    this.measureUtils = new MeasureUtils(attribute);
    this.borderCorrection = attribute.borderWidth / 2;
    this.floatChartModule = new FloatChartModule(viewRect);
  }

  /**
   * 初始化
   */
  public void init() {
    firstLoad = true;
    setOverScrollOffset(0);
    setCurrentTransX(0);
  }

  /**
   * 刷新缩放后的Item信息
   */
  protected abstract void resetItemInfo();

  /**
   * 缩放
   *
   * @param x 在点(x, y)上缩放
   * @param y 在点(x, y)上缩放
   */
  public abstract void onZoom(float x, float y);

  /**
   * 绘制
   */
  protected abstract void onDraw(Canvas canvas);

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

  /**
   * 获取配置信息
   */
  public A getAttribute() {
    return attribute;
  }

  /**
   * 获取计算工具类
   */
  public MeasureUtils getMeasureUtils() {
    return measureUtils;
  }

  /**
   * 边框修正数值
   */
  public float getBorderCorrection() {
    return borderCorrection;
  }

  /**
   * 设置跨子视图显示的浮动组件
   */
  public void addFloatDrawing(AbsDrawing floatDrawing) {
    this.floatChartModule.addDrawing(floatDrawing);
  }

  /**
   * 根据位置获取长按区域内的信息
   */
  public void onHighlight(float x, float y) {
    highlight = false;
    focusAreaChartModule = null;
    if (adapter.getCount() > 0) {
      for (AbsChartModule item : getChartModules()) {
        if (item.isEnable() && y >= item.getRect().top && y <= item.getRect().bottom) {
          //如果存在结果，则对x轴坐标进行修正，使之不超过焦点区域内的ChartModule的left和right
          if (x < item.getRect().left) {
            x = item.getRect().left;
          } else if (x > item.getRect().right) {
            x = item.getRect().right;
          }
          highlightPoint[0] = x;
          highlightPoint[1] = y;
          focusAreaChartModule = item;
          highlight = true;
          break;
        }
      }
    }
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
    return touchValues[Matrix.MTRANS_X];
  }

  /**
   * 获取缩放后并且减去CandleSpace的CandleWidth -->kChart 需Override
   */
  public float getSubtractSpaceCandleWidth() {
    return 0;
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
    int value = (int) (attribute.visibleCount / attribute.gridCount);
    if (value > 0) {
      this.interval = value;
    }
  }

  /**
   * 当ViewRect发生改变时，修改viewRectChange状态，用于重新定位和更新所需参数
   */
  public void onViewRectChange() {
    initViewRect(viewRect, chartModules);
    if (isReady()) {
      resetItemInfo();
      resetInterval();
      resetMatrix();
      measureUtils.initViewCoordinates();
      //初始化绘制组件
      initDrawing(chartModules);
      //初始化浮动绘制组件
      initDrawing(floatChartModule);
    }
  }

  /**
   * 初始化各个组件的绘制区域
   */
  void initViewRect(RectF viewRect, List<AbsChartModule<? super AbsEntry>> modules) {
    float left = viewRect.left + attribute.borderWidth;
    float top = viewRect.top + attribute.borderWidth;
    float right = viewRect.right - attribute.borderWidth;
    float bottom;
    for (AbsChartModule<? super AbsEntry> module : modules) {
      if (!module.isEnable()) {
        continue;
      }
      //更新主图模型
      if (module instanceof MainChartModule) {
        this.mainChartModule = (MainChartModule) module;
      }
      //分配图表大小和位置
      bottom = top + module.getViewHeight();
      module.setRect(
          left + module.getPaddingLeft(),
          top + module.getPaddingTop(),
          right - module.getPaddingRight(),
          bottom - module.getPaddingBottom());
      top = bottom + attribute.viewInterval + attribute.borderWidth * 2f;
    }
  }

  /**
   * 刷新Matrix
   */
  void resetMatrix() {
    initMatrixValue(mainChartModule.getRect());
    postMatrixOffset(mainChartModule.getRect().left, mainChartModule.getRect().top);
    postMatrixTouch(mainChartModule.getRect().width(), attribute.visibleCount);
  }

  /**
   * 是初始化Drawing
   */
  private void initDrawing(List<AbsChartModule<? super AbsEntry>> modules) {
    for (AbsChartModule<? super AbsEntry> module : modules) {
      if (module.isEnable()) {
        initDrawing(module);
      }
    }
  }

  private void initDrawing(AbsChartModule<? extends AbsEntry> module) {
    for (AbsDrawing drawing : module.getDrawingList()) {
      if (!drawing.isInit()) {
        drawing.onInit(this, module);
      }
      drawing.onViewChange();
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
    setCurrentTransX(getTransX(getMainChartModule().getRect(),
        attribute.visibleCount, entryIndex) - 1f);
  }

  /**
   * 获取给定的 entryIndex 对应的滚动偏移量。在调用 {@link #computeScrollRange} 之后才能调用此方法
   *
   * @param visibleCount 当前显示区域的 X 轴方向上需要显示多少个 entry 值
   * @param entryIndex entry 索引
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
   * @param width 当前显示区域的宽
   * @param scaleX X 轴方向的缩放
   */
  protected void computeScrollRange(float width, float scaleX) {
    minScrollOffset = attribute.leftScrollOffset;
    if (scaleX == 0) {
      lastMaxScrollOffset = 0;
      maxScrollOffset = 0;
    } else {
      maxScrollOffset = width * (scaleX - 1f) + attribute.rightScrollOffset;
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
   * @param contentRect 当前显示区域
   * @param visibleCount 当前显示区域的 X 轴方向上需要显示多少个 entry 值
   * @param x 在点(x, y)上缩放
   * @param y 在点(x, y)上缩放。由于 K 线图只会进行水平滚动，因此 y 值被忽略
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

    touchValues[Matrix.MTRANS_X] = getTransX(contentRect, visibleCount, minVisibleIndex);

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
    calibrationMapPoints(pts, xOffset, -(yOffset + attribute.borderWidth));
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
    calibrationMapPoints(pts, 0, -attribute.borderWidth);
  }

  /**
   * 按给定矩阵将 entry 的值映射到屏幕像素上
   *
   * @param matrix 矩阵
   * @param pts 浮点数序列 [x0, y0, x1, y1, ...]
   */
  public void mapPoints(Matrix matrix, float[] pts) {
    if (matrix == null) {
      matrixValue.mapPoints(pts);
    } else {
      matrix.mapPoints(pts);
    }
    matrixTouch.mapPoints(pts);
    matrixOffset.mapPoints(pts);
    calibrationMapPoints(pts, 0, -attribute.borderWidth);
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
   * @param pts 浮点数序列 [x0, y0, x1, y1, ...]
   */
  public void invertMapPoints(Matrix matrix, float[] pts) {
    if (matrix != null) {
      matrixInvert.reset();
      matrix.invert(matrixInvert);
      matrixInvert.mapPoints(pts);
    }
  }

  /**
   * 值矩阵运算
   */
  protected void postMatrixValue(AbsChartModule chartModule) {
    RectF rect = chartModule.getRect();//视图 rect
    //计算 X,Y 轴的极值
    computeExtremumValue(extremum, chartModule);
    final float deltaX = extremum[2] - extremum[0];
    final float deltaY = extremum[3] - extremum[1];
    final float scaleX = (rect.width() - chartModule.getxCorrectedValue())
        / (deltaX == 0 ? adapter.getCount() : deltaX);
    final float scaleY = (rect.height() - chartModule.getyCorrectedValue()) / deltaY;
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
    final float scaleX = rect.width() / adapter.getCount();
    matrixValue.reset();
    matrixValue.postScale(scaleX, 1);
  }

  /**
   * 手势滑动缩放矩阵运算
   *
   * @param width 当前显示区域的宽
   * @param visibleCount 当前显示区域的 X 轴方向上需要显示多少个 entry 值
   */
  protected void postMatrixTouch(float width, float visibleCount) {
    final float scaleX = adapter.getCount() / visibleCount;
    matrixTouch.reset();
    matrixTouch.postScale(scaleX, 1);

    if (maxScrollOffset != 0) {
      lastMaxScrollOffset = maxScrollOffset;
    }
    computeScrollRange(width, scaleX);
    // 数据不满一屏，不需要滚动定位
    if (scaleX > 1) {
      if (touchValues[Matrix.MTRANS_X] > 0) {
        // 左滑加载完成之后定位到之前滚动的位置
        matrixTouch.postTranslate(
            touchValues[Matrix.MTRANS_X] - (maxScrollOffset - lastMaxScrollOffset), 0);
      } else if (touchValues[Matrix.MTRANS_X] < 0) {
        if ((int) overScrollOffset != 0) {
          // 右滑加载完成之后定位到之前滚动的位置
          matrixTouch.postTranslate(touchValues[Matrix.MTRANS_X], 0);
        } else {
          // TODO 左滑、右滑加载中时转动屏幕方向，定位仍然有 BUG，我将花更多时间找到好的办法解决
          // 转动屏幕方向导致矩形变化，定位到之前相同比例的滚动位置
          touchValues[Matrix.MTRANS_X] =
              touchValues[Matrix.MTRANS_X] / lastMaxScrollOffset * maxScrollOffset;

          matrixTouch.postTranslate(touchValues[Matrix.MTRANS_X], 0);
        }
      } else if (firstLoad) {
        // 通常首次加载时定位到最末尾
        setCurrentTransX(-maxScrollOffset);
        firstLoad = false;
      }
      //Log.e(TAG, "##d postMatrixTouch: currentOffset = " + touchValues[Matrix.MTRANS_X]
      //    + ", maxScrollOffset = " + -maxScrollOffset
      //    + ", minScrollOffset = " + minScrollOffset
      //    + ", lastMaxScrollOffset = " + lastMaxScrollOffset
      //    + ", overScrollOffset = " + overScrollOffset);
    }
  }

  /**
   * 偏移矩阵运算
   *
   * @param offsetX 偏移量 X
   * @param offsetY 偏移量 Y
   */
  protected void postMatrixOffset(float offsetX, float offsetY) {
    matrixOffset.reset();
    matrixOffset.postTranslate(offsetX, offsetY);
  }

  /**
   * 计算显示区域内 X,Y 轴的范围
   */
  protected void computeExtremumValue(float[] extremum, AbsChartModule chartModule) {
    //X轴
    extremum[0] = chartModule.getMinX().value;
    extremum[2] = chartModule.getMaxX().value;
    //Y轴
    extremum[1] = chartModule.getMinY().value;
    extremum[3] = chartModule.getMaxY().value;
  }

  /**
   * 渲染LeapingViewDrawing(renderDrawing之前调用)
   */
  protected void renderDrawingBefore(Canvas canvas) {
    for (AbsDrawing drawing : floatChartModule.getDrawingList()) {
      drawing.onComputeOver(canvas, begin, end, extremum);
    }
  }

  /**
   * 渲染Drawing
   */
  protected void renderDrawing(Canvas canvas, List<AbsDrawing> drawingList) {
    for (int i = begin; i < end; i++) {
      for (AbsDrawing drawing : drawingList) {
        drawing.computePoint(begin, end, i);
      }
    }
    for (AbsDrawing drawing : drawingList) {
      drawing.onComputeOver(canvas, begin, end, extremum);
    }

    for (AbsDrawing drawing : drawingList) {
      drawing.onDrawOver(canvas);
    }
  }

  /**
   * 渲染LeapingViewDrawing(renderDrawing之后调用)
   */
  protected void renderDrawingAfter(Canvas canvas) {
    for (AbsDrawing drawing : floatChartModule.getDrawingList()) {
      drawing.onDrawOver(canvas);
    }
  }

  /**
   * 布局高度计算
   */
  public int measureHeight(int height) {
    return Math.round(measureUtils.childViewHeightMeasure(height));
  }

  /**
   * 添加图表组件
   */
  public void addChartModule(AbsChartModule chartModule) {
    if (null == chartModule) {
      return;
    }
    switch (chartModule.getModuleType()) {
      case CANDLE:
        chartModule.setViewHeight(attribute.candleViewHeight);
        break;
      case VOLUME:
        chartModule.setViewHeight(attribute.volumeViewHeight);
        break;
      case TIME:
        chartModule.setViewHeight(attribute.timeLineViewHeight);
        break;
      case DEPTH:
        chartModule.setViewHeight(attribute.depthViewHeight);
        break;
      default:
        chartModule.setViewHeight(attribute.otherViewHeight);
        break;
    }
    if (chartModule instanceof MainChartModule) {
      chartModules.add(0, chartModule);//主图位居首位
    } else {
      chartModules.add(chartModule);
    }
    measureUtils.chartModuleNotifyDataSetChanged(chartModules);
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
  public List<AbsChartModule<? super AbsEntry>> getChartModules() {
    return chartModules;
  }

  /**
   * 获取当前焦点区域内的chartModule
   */
  public @Nullable AbsChartModule getChartModuleInFocusArea() {
    return focusAreaChartModule;
  }

  /**
   * 根据图表moduleType获取对应组件
   */
  public AbsChartModule getChartModule(ModuleType moduleType) {
    for (AbsChartModule item : getChartModules()) {
      if (item.getModuleType() == moduleType) {
        return item;
      }
    }
    return floatChartModule;
  }

  /**
   * 根据当前可见的主图组件
   */
  public AbsChartModule getMainChartModule() {
    return mainChartModule;
  }

  /**
   * Drawing的Click
   *
   * @return (返回响应事件的元素)
   */
  public @Nullable AbsDrawing onDrawingClick(float x, float y) {
    for (AbsChartModule item : getChartModules()) {
      if (item.isEnable() && item.getRect().contains(x, y)) {
        for (AbsDrawing drawing : (List<AbsDrawing>) item.getDrawingList()) {
          if (drawing.onDrawingClick(x, y)) {
            return drawing;
          }
        }
        return null;
      }
    }
    return null;
  }

  /**
   * 进行绘制
   */
  public void render(Canvas canvas) {
    if (isReady()) {
      onDraw(canvas);
    }
  }

  /**
   * 数据更改
   */
  public void onDataChange() {
    if (isReady()) {
      resetMatrix();
    }
  }

  /**
   * 是否已经准备好
   */
  private boolean isReady() {
    return null != mainChartModule && null != adapter;
  }
}
