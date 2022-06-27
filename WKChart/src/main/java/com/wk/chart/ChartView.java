package com.wk.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.NonNull;

import com.wk.chart.adapter.AbsAdapter;
import com.wk.chart.compat.GestureMoveActionCompat;
import com.wk.chart.compat.attribute.AttributeRead;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.compat.config.AbsBuildConfig;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ViewSizeEntry;
import com.wk.chart.enumeration.ClickDrawingID;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.ObserverArg;
import com.wk.chart.enumeration.RenderModel;
import com.wk.chart.enumeration.TouchMoveType;
import com.wk.chart.handler.DelayedHandler;
import com.wk.chart.handler.InteractiveHandler;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;
import com.wk.chart.render.CandleRender;
import com.wk.chart.render.DepthRender;

import java.util.Observer;

/**
 * <p>交互式 K 线图</p>
 */

public class ChartView extends View implements DelayedHandler.DelayedWorkListener {
    private static final String TAG = "Chart";
    // 与滚动控制、滑动加载数据相关的属性
    private final int OVER_SCROLL_DURATION = 500; // dragging 松手之后回中的时间，单位：毫秒
    private final int OVER_SCROLL_THRESHOLD = 220; // dragging 的偏移量大于此值时即是一个有效的滑动加载
    private final int IDLE = 0; // 空闲
    private final int RELEASE_BACK = 1; // 放手，回弹到 loading 位置
    private final int LOADING = 2; // 加载中
    private final int SPRING_BACK = 3; // 加载结束，回弹到初始位置
    private final int DELAYED_CANCEL_HIGHLIGHT = 301;//延时取消高亮标识
    // 视图区域
    private BaseAttribute attribute = null;
    private final RectF viewRect = new RectF();
    private final ViewSizeEntry viewSizeEntry = new ViewSizeEntry();
    // 渲染相关的属性
    private AbsRender<? extends AbsAdapter<?, ?>, ? extends BaseAttribute> render;
    private InteractiveHandler interactiveHandler;
    private OverScroller scroller;
    private RenderModel renderModel;
    // 与手势控制相关的属性
    private boolean onTouch = false;
    private boolean onLongPress = false;
    private boolean onDoubleFingerPress = false;
    private boolean onDragging = false;
    private boolean lockRefresh = false;
    private boolean enableLeftRefresh = true;
    private boolean enableRightRefresh = true;
    private boolean loadingComplete = true;
    private float lastFlingX = 0;
    private float lastScrollDx = 0;
    private int chartState = IDLE;//图表状态
    private int lastHighlightIndex = -1; // 上一次高亮的 entry 索引，用于减少回调
    private int orientation;//屏幕方向

    //数据监视器
    private final Observer dataSetObserver = (o, arg) -> {
        switch ((ObserverArg) arg) {
            case NORMAL:
                loadingComplete();
                lockRefresh = true;
                break;
            case ATTR_UPDATE:
                callHighlight();
                onAttributeUpdate();
                break;
            case FORMAT_UPDATE:
                callHighlight();
                onViewInit();
                break;
            case INIT:
                callHighlight();
                onViewInit();
            case RESET:
                resetChartState();
                lockRefresh = false;
            case ADD:
                loadingComplete();
            case UPDATE:
                onDataUpdate();
                break;
            case REFRESH:
                postInvalidateOnAnimation();
                break;
        }
    };

    public ChartView(Context context) {
        this(context, null, 0);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(0, 0, 0, 0);
        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ChartView, defStyleAttr, defStyleAttr);
        try {
            int type = a.getInteger(R.styleable.ChartView_renderModel, RenderModel.CANDLE.ordinal());
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
     *                    根据渲染类型来构建相应的渲染工厂和配置文件
     */
    private void init(TypedArray array, RenderModel renderModel) {
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
        this.scroller = new OverScroller(getContext());
        this.scaleDetector.setQuickScaleEnabled(false);
        this.orientation = getResources().getConfiguration().orientation;
        DelayedHandler.getInstance().setListener(this);
    }

    /**
     * 设置数据适配器
     */
    public void setAdapter(@NonNull AbsAdapter<? extends AbsEntry, ? extends AbsBuildConfig> adapter) {
        adapter.registerDataSetObserver(dataSetObserver);
        ((AbsRender<AbsAdapter<?, ?>, BaseAttribute>) render).setAdapter(adapter);
    }

    public AbsRender<? extends AbsAdapter<?, ?>, ? extends BaseAttribute> getRender() {
        return render;
    }

    public void setInteractiveHandler(InteractiveHandler handler) {
        this.interactiveHandler = handler;
    }

    public InteractiveHandler getInteractiveHandler() {
        return interactiveHandler;
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
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    boolean consumed = false;
                    int clickId = getRender().onClick(e.getX(), e.getY());
                    if (null != interactiveHandler && clickId != ClickDrawingID.ID_NONE) {
                        consumed = interactiveHandler.onSingleClick(clickId, e.getX(), e.getY());
                    }
                    if (!consumed && clickId == ClickDrawingID.ID_CURSOR) {
                        scrollToEnd();
                        consumed = true;
                    }
                    if (!consumed && attribute.onSingleClickSelected) {
                        highlight(e.getX(), e.getY());
                        DelayedHandler.getInstance().posOnlyDelayedWork(DELAYED_CANCEL_HIGHLIGHT, 10000);
                        consumed = true;
                    }
                    return consumed;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (!onLongPress && !onDoubleFingerPress) {
                        cancelHighlight();
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
                    if (!onLongPress && !onDoubleFingerPress && render.canScroll(0)) {
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
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    float scale = attribute.currentScale * detector.getScaleFactor();
                    if (scale < attribute.minScale) {
                        scale = attribute.minScale;
                    } else if (scale > attribute.maxScale) {
                        scale = attribute.maxScale;
                    }
                    if (scale != attribute.currentScale) {
                        attribute.currentScale = scale;
                        render.onZoom(detector.getFocusX(), detector.getFocusY());
                        postInvalidateOnAnimation();
                        return true;
                    } else {
                        return false;
                    }
                }
            });

    private final GestureMoveActionCompat gestureCompat = new GestureMoveActionCompat();

    /**
     * 回调高亮监听，用于图表初始化后，通知外部监听重新调整高亮选择器中的显示信息
     */
    private void callHighlight() {
        if (checkReadyState() && render.isHighlight()) {
            int highlightIndex = render.getAdapter().getHighlightIndex();
            AbsEntry entry = render.getAdapter().getHighlightEntry();
            float[] highlightPoint = render.getHighlightPoint();
            if (entry != null) {
                if (interactiveHandler != null) {
                    interactiveHandler.onHighlight(entry, highlightIndex, highlightPoint[0], highlightPoint[1]);
                }
                lastHighlightIndex = highlightIndex;
            }
        }
    }

    /**
     * 高亮处理逻辑（选中了某个item）
     */
    private void highlight(float x, float y) {
        render.onHighlight(x, y);
        postInvalidateOnAnimation();
        int highlightIndex = render.getAdapter().getHighlightIndex();
        AbsEntry entry = render.getAdapter().getHighlightEntry();
        if (entry != null && lastHighlightIndex != highlightIndex) {
            if (interactiveHandler != null) {
                interactiveHandler.onHighlight(entry, highlightIndex, x, y);
            }
            lastHighlightIndex = highlightIndex;
        }
    }

    /**
     * 取消高亮处理逻辑
     */
    private void cancelHighlight() {
        if (!render.isHighlight()) {
            return;
        }
        render.onCancelHighlight();
        postInvalidateOnAnimation();
        if (interactiveHandler != null) {
            interactiveHandler.onCancelHighlight();
        }
        lastHighlightIndex = -1;
        DelayedHandler.getInstance().cancelDelayedWork(DELAYED_CANCEL_HIGHLIGHT);
    }

    /**
     * 滚动到最后
     */
    public void scrollToEnd() {
        lastFlingX = 0;
        float scrollX = Math.abs(render.getMaxScrollOffset()) - Math.abs(render.getCurrentTransX());
        scroller.startScroll(0, 0, (int) scrollX, 0, 2000);
        postInvalidateOnAnimation();
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
        if (dx == 0) {
            return;
        }
        render.updateCurrentTransX(dx);
        render.updateOverScrollOffset(dx);
        postInvalidateOnAnimation();
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.e("height(onMeasure)：", MeasureSpec.getSize(heightMeasureSpec) + "");
        if (viewSizeEntry.isNotMeasure()) {
            setMeasuredDimension(viewSizeEntry.getWidth(), viewSizeEntry.getHeight());
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            this.render.setProrate(true);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            this.render.setProrate(false);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = render.measureEstimateOccupyHeight();
            heightSize = heightSize + getPaddingTop() + getPaddingBottom();
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        Log.e("高度(onMeasure)：", h+"(onSizeChanged)");
        this.viewSizeEntry.setWidth(w);
        this.viewSizeEntry.setHeight(h);
        this.viewRect.set(getPaddingLeft(),
                getPaddingTop(),
                w - getPaddingRight(),
                h - getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            this.onViewInit();
        } else {
            this.viewSizeEntry.onRequestLayoutComplete();
        }
    }

    @Override
    public void computeScroll() {
        if (onLongPress) {
            return;
        }
        if (scroller.computeScrollOffset()) {
            final float x = scroller.getCurrX();
            final float dx = x - lastFlingX;
            lastFlingX = x;
            if (onTouch) {
                scroller.abortAnimation();
            } else if (chartState == RELEASE_BACK || chartState == SPRING_BACK) {
                releaseBack(dx);
            } else {
                scroll(dx);
            }
        } else {
            final float overScrollOffset = render.getOverScrollOffset();
            if (!onTouch && (int) overScrollOffset != 0 && chartState == IDLE) {
                lastScrollDx = 0;
                float dx = overScrollOffset;

                if (Math.abs(overScrollOffset) > OVER_SCROLL_THRESHOLD) {
                    if (enableLeftRefresh && !lockRefresh && overScrollOffset > 0) {
                        lastScrollDx = overScrollOffset - OVER_SCROLL_THRESHOLD;
                        dx = lastScrollDx;
                    }

                    if (enableRightRefresh && !lockRefresh && overScrollOffset < 0) {
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
                if (interactiveHandler == null) {
                    loadingComplete();
                    return;
                }
                if (lastScrollDx > 0) {
                    loadingComplete = false;
                    interactiveHandler.onLeftRefresh(render.getAdapter().getItem(0));
                } else if (lastScrollDx < 0) {
                    loadingComplete = false;
                    interactiveHandler.onRightRefresh(render.getAdapter().getItem(render.getAdapter().getLastPosition()));
                }
            } else {
                chartState = IDLE;
            }
        }
    }

    /**
     * 加载完成
     */
    public void loadingComplete() {
        loadingComplete = true;
        chartState = SPRING_BACK;
        lastFlingX = 0;
        final int overScrollOffset = (int) render.getOverScrollOffset();
        if (overScrollOffset != 0) {
            scroller.startScroll(0, 0, overScrollOffset, 0, OVER_SCROLL_DURATION);
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

    public boolean isEnableLeftRefresh() {
        return enableLeftRefresh;
    }

    public boolean isEnableRightRefresh() {
        return enableRightRefresh;
    }

    public boolean isHighlighting() {
        return render.isHighlight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (checkReadyState()) {
            int touchMoveType = gestureCompat.getTouchMoveType(event, event.getX(), event.getY());
            if (onLongPress || onDoubleFingerPress || touchMoveType == TouchMoveType.HORIZONTAL) {
                getParent().requestDisallowInterceptTouchEvent(true);
                return super.dispatchTouchEvent(event);
            }
            getParent().requestDisallowInterceptTouchEvent(false);
            return super.dispatchTouchEvent(event);
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final boolean state = gestureDetector.onTouchEvent(e) | scaleDetector.onTouchEvent(e);
        final int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onTouch = true;
                onDragging = false;
                onLongPress = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onDoubleFingerPress = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (onLongPress) {
                    highlight(e.getX(), e.getY());
                } else {
                    onDragging = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onDoubleFingerPress = false;
                onTouch = false;
                if (onLongPress) {
                    onLongPress = false;
                    DelayedHandler.getInstance().posOnlyDelayedWork(DELAYED_CANCEL_HIGHLIGHT, 10000);
                } else if (onDragging) {
                    onDragging = false;
                    computeScroll();
                }
                break;
        }
        return state;
    }

    /**
     * 视图重绘
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (checkReadyState()) {
            render.render(canvas);
        } else {
            canvas.clipRect(viewRect);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != render.getAdapter() && getResources().getConfiguration().orientation == orientation) {
            this.render.getAdapter().onDestroy();
            DelayedHandler.getInstance().onDestroy();
        } else {
            this.orientation = getResources().getConfiguration().orientation;
        }
    }

    /**
     * 视图初始化
     */
    public void onViewInit() {
        if (null == render || null == render.getAdapter()) {
            return;
        }
        if (render.onModuleInit()) {
            this.render.onViewInit();
            this.postInvalidateOnAnimation();
        } else {
            this.viewSizeEntry.onRequestLayout();
            this.requestLayout();
        }
    }

    /**
     * 配置文件更改回调
     */
    public void onAttributeUpdate() {
        this.render.onAttributeChange();
        this.onViewInit();
    }

    /**
     * 数据更新回调
     */
    public void onDataUpdate() {
        this.render.onDataChange();
        this.postInvalidateOnAnimation();
    }

    /**
     * 重置图表状态
     */
    private void resetChartState() {
        this.scroller.forceFinished(true);
        this.chartState = IDLE;
        this.lastFlingX = 0;
        this.render.resetChart();
    }

    /**
     * 检查图表准备状态
     */
    private boolean checkReadyState() {
        return null != render
                && null != render.getAdapter()
                && null != render.getMainModule()
                && render.getAdapter().getCount() > 0;
    }

    /**
     * 获取图表类型
     *
     * @return 图表类型
     */
    public RenderModel getRenderModel() {
        return renderModel;
    }

    /**
     * 删除绘制组件
     *
     * @param moduleType      模块类型
     * @param moduleGroupType 模块组
     * @param classes         组件
     */
    public int removeDrawing(@ModuleType int moduleType, @ModuleGroupType int moduleGroupType, Class<? extends AbsDrawing<?, ?>>... classes) {
        int removeCount = 0;
        AbsModule<?> module = getRender().getModule(moduleType, moduleGroupType);
        if (null == module) {
            return removeCount;
        }
        for (Class<? extends AbsDrawing<?, ?>> cl : classes) {
            if (module.removeDrawing(cl)) {
                removeCount++;
            }
        }
        return removeCount;
    }

    /**
     * 延时执行的任务回调
     *
     * @param what 延时任务唯一标识
     */
    @Override
    public void onDelayedWork(int what) {
        if (what == DELAYED_CANCEL_HIGHLIGHT) {//延时取消高亮标识
            cancelHighlight();
        }
    }
}