

package com.ll.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
import com.ll.chart.adapter.AbsAdapter;
import com.ll.chart.compat.GestureMoveActionCompat;
import com.ll.chart.compat.attribute.AttributeRead;
import com.ll.chart.compat.attribute.BaseAttribute;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.compat.attribute.DepthAttribute;
import com.ll.chart.drawing.CursorDrawing;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.enumeration.ObserverArg;
import com.ll.chart.enumeration.RenderModel;
import com.ll.chart.handler.InteractiveHandler;
import com.ll.chart.render.AbsRender;
import com.ll.chart.render.CandleRender;
import com.ll.chart.render.DepthRender;
import java.util.Observable;
import java.util.Observer;

/**
 * <p>交互式 K 线图</p>
 */

public class Chart extends View {
  private static final String TAG = "Chart";
  // 与滚动控制、滑动加载数据相关的属性
  private static final int OVER_SCROLL_DURATION = 500; // dragging 松手之后回中的时间，单位：毫秒
  private static final int OVER_SCROLL_THRESHOLD = 220; // dragging 的偏移量大于此值时即是一个有效的滑动加载
  private static final int IDLE = 0; // 空闲
  private static final int RELEASE_BACK = 1; // 放手，回弹到 loading 位置
  private static final int LOADING = 2; // 加载中
  private static final int SPRING_BACK = 3; // 加载结束，回弹到初始位置
  // 视图区域
  private BaseAttribute attribute = null;
  private final RectF viewRect = new RectF();
  // 渲染相关的属性
  private AbsRender render;
  private InteractiveHandler handler;
  private OverScroller scroller;
  private RenderModel renderModel;
  // 与手势控制相关的属性
  private boolean onTouch = false;
  private boolean onLongPress = false;
  private boolean cancelHighlight = false;
  private boolean onDoubleFingerPress = false;
  private boolean onVerticalMove = false;
  private boolean onDragging = false;
  private boolean enableLeftRefresh = true;
  private boolean enableRightRefresh = true;
  private boolean loadingComplete = true;
  private boolean viewChangeState = true;
  private boolean measureComplete = false;//测量是否完成
  private float lastFlingX = 0;
  private float lastScrollDx = 0;
  private int chartState = IDLE;//图表状态
  private int lastEntrySize = 0; // 上一次的 entry 列表大小，用于判断是否成功加载了数据
  private int lastHighlightIndex = -1; // 上一次高亮的 entry 索引，用于减少回调
  private int orientation;//屏幕方向

  //数据监视器
  private Observer dataSetObserver = new Observer() {
    @Override public void update(Observable o, Object arg) {
      switch ((ObserverArg) arg) {
        case normal:
          loadingComplete(false);
          break;
        case init:
          initChartState();
        case add:
          loadingComplete(false);
        case push:
          onDataChange();
          lastEntrySize = render.getAdapter().getCount();
          break;
      }
    }
  };

  public Chart(Context context) {
    this(context, null, 0);
  }

  public Chart(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public Chart(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    final TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs, R.styleable.ChartAttr, defStyleAttr, defStyleAttr);
    try {
      int type = a.getInteger(R.styleable.ChartAttr_renderModel, RenderModel.CANDLE.ordinal());
      init(a, RenderModel.values()[type]);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      a.recycle();
    }
  }

  /**
   * 图表初始化
   *
   * @param renderModel 渲染类型
   * 根据渲染类型来构建相应的渲染工厂和配置文件
   */
  public void init(TypedArray array, RenderModel renderModel) {
    AttributeRead attributeRead = new AttributeRead();
    switch (renderModel) {
      case CANDLE://蜡烛图
        CandleAttribute candleAttribute = new CandleAttribute(getContext());
        attributeRead.initAttribute(array, candleAttribute);
        render = new CandleRender(candleAttribute, viewRect);
        break;
      case DEPTH://深度图
        DepthAttribute depthAttribute = new DepthAttribute(getContext());
        attributeRead.initAttribute(array, depthAttribute);
        render = new DepthRender(depthAttribute, viewRect);
        break;
    }
    this.renderModel = renderModel;
    this.attribute = render.getAttribute();
    this.gestureDetector.setIsLongpressEnabled(true);
    int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    this.gestureCompat.setTouchSlop(touchSlop);
    this.scroller = new OverScroller(getContext());
    this.scaleDetector.setQuickScaleEnabled(false);
    this.orientation = getResources().getConfiguration().orientation;
  }

  /**
   * 设置数据适配器
   */
  public void setAdapter(@NonNull AbsAdapter adapter) {
    adapter.registerDataSetObserver(dataSetObserver);
    render.setAdapter(adapter);
  }

  public AbsRender getRender() {
    return render;
  }

  public void setInteractiveHandler(InteractiveHandler handler) {
    this.handler = handler;
  }

  public InteractiveHandler getInteractiveHandler() {
    return handler;
  }

  private final GestureDetector gestureDetector =
      new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
          if (onTouch) {
            onLongPress = true;
            highlight(e.getX(), e.getY());
          }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
          if (handler != null) {
            handler.onDoubleTap(e, e.getX(), e.getY());
          }
          return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
          if (handler != null) {
            handler.onSingleTap(e, e.getX(), e.getY());
          }
          if (getRender().onDrawingClick(e.getX(), e.getY()) instanceof CursorDrawing) {
            scroller.startScroll(0, 0,
                (int) (render.getCurrentTransX() - render.getMaxScrollOffset()), 0, 1000);
            postInvalidateOnAnimation();
          }
          return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
          if (!onLongPress && !onDoubleFingerPress && !onVerticalMove) {
            if (render.canScroll(distanceX)) {
              scroll(distanceX);
            } else if (onDragging && render.canDragging()) {
              dragging(distanceX);
            }
            return true;
          } else {
            return false;
          }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
          lastFlingX = 0;
          if (!onLongPress && !onDoubleFingerPress && !onVerticalMove && render.canScroll(0)) {

            scroller.fling(0, 0, (int) -velocityX, 0,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            return true;
          } else {
            return false;
          }
        }
      });

  /**
   * 缩放手势处理逻辑
   */
  private final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(),
      new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        float scaleFocusX = 0;
        float oldScale = 1;

        @Override public boolean onScaleBegin(ScaleGestureDetector detector) {
          scaleFocusX = detector.getFocusX();
          return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
          //Log.e(TAG, "onScale: " + attribute.currentScale);
          attribute.currentScale *= detector.getScaleFactor();
          if (attribute.currentScale < attribute.minScale) {
            attribute.currentScale = attribute.minScale;
          } else if (attribute.currentScale > attribute.maxScale) {
            attribute.currentScale = attribute.maxScale;
          }
          if (attribute.currentScale != oldScale) {
            render.onZoom(scaleFocusX, detector.getFocusY());
            oldScale = attribute.currentScale;
            postInvalidateOnAnimation();
            return true;
          } else {
            return false;
          }
        }
      });

  private GestureMoveActionCompat gestureCompat = new GestureMoveActionCompat(null);

  /**
   * 长按手势处理逻辑
   */
  private void highlight(float x, float y) {
    render.onHighlight(x, y);
    postInvalidateOnAnimation();
    int highlightIndex = render.getAdapter().getHighlightIndex();
    AbsEntry entry = render.getAdapter().getHighlightEntry();

    if (entry != null && lastHighlightIndex != highlightIndex) {
      if (handler != null) {
        handler.onHighlight(entry, highlightIndex, x, y);
      }
      lastHighlightIndex = highlightIndex;
    }
  }

  private void cancelHighlight() {
    if (render.isHighlight()) {
      render.onCancelHighlight();
      postInvalidateOnAnimation();
      if (handler != null) {
        handler.onCancelHighlight();
      }
      lastHighlightIndex = -1;
    }
  }

  /**
   * 滚动，这里只会进行水平滚动，到达边界时将不能继续滑动
   *
   * @param dx 变化 量
   */
  public void scroll(float dx) {
    render.scroll(dx);
    postInvalidateOnAnimation();
  }

  /**
   * 拖动，不同于滚动，当 K 线图到达边界时，依然可以滑动，用来支持加载更多
   *
   * @param dx 变化量
   */
  private void dragging(float dx) {
    //添加阻尼效果
    float overScrollOffset = Math.abs(render.getOverScrollOffset());
    float restriction = viewRect.width() / 2;
    float value = overScrollOffset < restriction ? 1 - overScrollOffset / restriction : 0;
    //Log.e(TAG, "     value:" + value);
    dx *= value;
    if (render.getMaxScrollOffset() < 0 || dx < 0) {
      render.updateCurrentTransX(dx);
      render.updateOverScrollOffset(dx);
      postInvalidateOnAnimation();
    }
  }

  /**
   * 更新滚动的距离，用于拖动松手后回中
   *
   * @param dx 变化量
   */
  private void releaseBack(float dx) {
    render.updateCurrentTransX(dx);
    render.updateOverScrollOffset(dx);
    postInvalidateOnAnimation();
  }

  /**
   * 更新滚动的距离，用于加载数据完成后滚动或者回中
   *
   * @param dx 变化量
   */
  private void springBack(float dx) {
    if (render.getAdapter().getCount() > lastEntrySize) {
      scroll(dx);
    } else {
      releaseBack(dx);
    }
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //计算View高度，并返回真实高度（注意，此方法在此处必须调用，否则有可能发生视图错位）
    int height = MeasureSpec.getSize(heightMeasureSpec);
    if (height > 0) {
      height = render.measureHeight(height);
      measureComplete = true;
      Log.e(TAG, "height :" + height);
    }
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    this.viewRect.set(0, 0, w, h);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    if ((changed || viewChangeState) && measureComplete) {
      this.render.onViewRectChange();
      this.viewChangeState = false;
    }
  }

  @Override
  public void computeScroll() {
    if (onVerticalMove || onLongPress) {
      return;
    }
    if (scroller.computeScrollOffset()) {
      final float x = scroller.getCurrX();
      final float dx = x - lastFlingX;
      lastFlingX = x;
      if (onTouch) {
        scroller.abortAnimation();
      } else {
        if (chartState == RELEASE_BACK) {
          releaseBack(dx);
        } else if (chartState == SPRING_BACK) {
          springBack(dx);
        } else {
          scroll(dx);
        }
      }
    } else {
      final float overScrollOffset = render.getOverScrollOffset();
      if (!onTouch && (int) overScrollOffset != 0 && chartState == IDLE) {
        lastScrollDx = 0;
        float dx = overScrollOffset;

        if (Math.abs(overScrollOffset) > OVER_SCROLL_THRESHOLD) {
          if (enableLeftRefresh && overScrollOffset > 0) {
            lastScrollDx = overScrollOffset - OVER_SCROLL_THRESHOLD;
            dx = lastScrollDx;
          }

          if (enableRightRefresh && overScrollOffset < 0) {
            lastScrollDx = overScrollOffset + OVER_SCROLL_THRESHOLD;
            dx = lastScrollDx;
          }
        }
        chartState = RELEASE_BACK;
        lastFlingX = 0;
        scroller.startScroll(0, 0, (int) dx, 0, OVER_SCROLL_DURATION);
        postInvalidateOnAnimation();
      } else if (chartState == RELEASE_BACK && loadingComplete) {
        chartState = LOADING;
        if (handler != null) {
          if (lastScrollDx > 0) {
            loadingComplete = false;
            handler.onLeftRefresh(render.getAdapter().getItem(0));
          } else if (lastScrollDx < 0) {
            loadingComplete = false;
            handler.onRightRefresh(render.getAdapter().getItem(lastEntrySize - 1));
          }
        } else {
          loadingComplete(false);
        }
      } else {
        chartState = IDLE;
      }
    }
  }

  /**
   * 加载完成
   *
   * @param reverse 是否反转滚动的方向
   */
  public void loadingComplete(boolean reverse) {
    loadingComplete = true;
    final int overScrollOffset = (int) render.getOverScrollOffset();
    if (overScrollOffset != 0) {
      //Log.e("滚动", "加载完成 overScrollOffset不为0" + (int)render.getOverScrollOffset());
      chartState = SPRING_BACK;
      lastFlingX = 0;
      scroller.startScroll(0, 0, reverse ? -overScrollOffset : overScrollOffset, 0,
          OVER_SCROLL_DURATION);
      postInvalidateOnAnimation();
    }
  }

  public boolean isRefreshing() {
    return chartState == LOADING;
  }

  public void setEnableLeftRefresh(boolean enableLeftRefresh) {
    this.enableLeftRefresh = enableLeftRefresh;
  }

  public void setEnableRightRefresh(boolean enableRightRefresh) {
    this.enableRightRefresh = enableRightRefresh;
  }

  public boolean isHighlighting() {
    return render.isHighlight();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    if (checkReadyState()) {
      boolean onHorizontalMove = gestureCompat.onTouchEvent(event, event.getX(), event.getY());
      final int action = event.getAction();
      onVerticalMove = false;
      if (action == MotionEvent.ACTION_MOVE) {
        if (!onHorizontalMove
            && !onLongPress
            && !onDoubleFingerPress
            && gestureCompat.isDragging()) {
          onTouch = false;
          onVerticalMove = true;
        }
      }
      getParent().requestDisallowInterceptTouchEvent(!onVerticalMove);
      return super.dispatchTouchEvent(event);
    }
    return false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    final int action = e.getAction();
    gestureDetector.onTouchEvent(e);
    if (e.getPointerCount() == 2 || onLongPress) {
      getParent().requestDisallowInterceptTouchEvent(true);
    }
    scaleDetector.onTouchEvent(e);

    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        onTouch = true;
        onDragging = false;
        cancelHighlight = true;
        onLongPress = false;

        break;
      }

      case MotionEvent.ACTION_POINTER_DOWN: {
        onDoubleFingerPress = true;
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        if (onLongPress) {
          highlight(e.getX(), e.getY());
          cancelHighlight = false;
        } else {
          if (cancelHighlight) {
            cancelHighlight();
            cancelHighlight = false;
          }
          onDragging = true;
        }
        break;
      }

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL: {
        onDoubleFingerPress = false;
        onTouch = false;
        if (cancelHighlight) {
          onDragging = false;
          cancelHighlight();
          onLongPress = false;
        } else if (onDragging) {
          onDragging = false;
          computeScroll();
        }
        break;
      }
    }

    return true;
  }

  /**
   * 视图重绘
   */
  @Override
  protected void onDraw(Canvas canvas) {
    if (checkReadyState()) {
      render.render(canvas);
    }
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (null != render.getAdapter()
        && getResources().getConfiguration().orientation == orientation) {
      this.render.getAdapter().unRegisterListener();
      this.render.getAdapter().onDestroy();
    } else {
      this.orientation = getResources().getConfiguration().orientation;
    }
  }

  /**
   * 视图更新
   */
  public void onViewChanged() {
    if (null != render.getAdapter()) {
      this.viewChangeState = true;
      this.requestLayout();
    }
  }

  /**
   * 数据更新回调
   */
  public void onDataChange() {
    this.render.onDataChange();
    postInvalidateOnAnimation();
  }

  /**
   * 初始化图表状态
   */
  private void initChartState() {
    chartState = IDLE;
    lastFlingX = 0;
    render.init();
  }

  /**
   * 检查图表准备状态
   */
  private boolean checkReadyState() {
    return null != render.getAdapter() && render.getAdapter().getCount() > 0 && measureComplete;
  }

  /**
   * 获取图表类型
   *
   * @return 图表类型
   */
  public RenderModel getRenderModel() {
    return renderModel;
  }
}
