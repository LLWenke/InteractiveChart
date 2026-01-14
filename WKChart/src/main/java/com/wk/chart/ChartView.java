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
import com.wk.chart.compat.GestureMoveAction;
import com.wk.chart.compat.attribute.AttributeRead;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.compat.config.AbsBuildConfig;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ViewSizeEntry;
import com.wk.chart.enumeration.ClickDrawingID;
import com.wk.chart.enumeration.DelayedTaskID;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleGroup;
import com.wk.chart.enumeration.ObserverArg;
import com.wk.chart.enumeration.RenderModel;
import com.wk.chart.enumeration.TouchMoveType;
import com.wk.chart.handler.DelayedHandler;
import com.wk.chart.handler.InteractiveHandler;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.AbsRender;
import com.wk.chart.render.CandleRender;
import com.wk.chart.render.DepthRender;

import java.beans.PropertyChangeListener;

/**
 * <p>交互式图标</p>
 */

public class ChartView extends View implements DelayedHandler.DelayedWorkListener {
    private static final String TAG = "ChartView";
    private final int LEFT_LOADING = -1; // 加载中（左）
    // 视图区域
    private BaseAttribute attribute = null;
    private final RectF viewRect = new RectF();
    private final ViewSizeEntry viewSizeEntry = new ViewSizeEntry();
    private final GestureMoveAction gestureCompat = new GestureMoveAction();
    // 渲染相关的属性
    private AbsRender<? extends AbsAdapter<?, ?>, ? extends BaseAttribute> render;
    private InteractiveHandler interactiveHandler;
    private OverScroller scroller;
    private RenderModel renderModel;
    // 与手势控制相关的属性
    private boolean onTouch = false;
    private boolean onDragging = false;
    private boolean onLongPress = false;
    private boolean onDoubleFingerPress = false;
    private boolean hasLeftLoad = false;
    private boolean scrollIdle = true;
    private int loadState = 0;//加载状态
    private float lastFlingX = 0;
    private long lastFlingTime = 0L;
    private int lastHighlightIndex = -1; // 上一次高亮的 entry 索引，用于减少回调

    //数据监视器
    private final PropertyChangeListener propertyChangeListener = evt -> {
        switch ((ObserverArg) evt.getNewValue()) {
            case NORMAL:
                loadingComplete(false);
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
            case ADD:
                loadingComplete(true);
            case UPDATE:
                onDataUpdate();
                break;
            case REFRESH:
                postInvalidateOnAnimation();
                break;
        }
    };

    /**
     * 手势检测器
     */
    private final GestureDetector gestureDetector = new GestureDetector(
            getContext(),
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(@NonNull MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(@NonNull MotionEvent e) {
                    if (onTouch && !onDoubleFingerPress) {
                        onLongPress = true;
                        highlight(e.getX(), e.getY());
                    }
                }

                @Override
                public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
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
                        postOnlyDelayedWork(DelayedTaskID.ID_CANCEL_HIGHLIGHT, 10000);
                        consumed = true;
                    }
                    return consumed;
                }

                @Override
                public boolean onScroll(
                        @NonNull MotionEvent e1,
                        @NonNull MotionEvent e2,
                        float distanceX,
                        float distanceY
                ) {
//                    Log.e(TAG, "onScroll：" + distanceX);
                    if (!onLongPress && !onDoubleFingerPress) {
                        cancelHighlight();
                        if (render.canScroll()) {
                            scroll(distanceX);
                        } else if (render.canDragging(distanceX)) {
                            dragging(distanceX);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public boolean onFling(
                        @NonNull MotionEvent e1,
                        @NonNull MotionEvent e2,
                        float velocityX,
                        float velocityY
                ) {
                    lastFlingX = 0;
                    int flingX = (int) (-velocityX / 1.5f);
                    if (!onLongPress && !onDoubleFingerPress && render.canScroll()) {
                        lastFlingTime = System.currentTimeMillis();
                        scroller.fling(0, 0, flingX, 0, Integer.MIN_VALUE,
                                Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE
                        );
                        return true;
                    } else {
                        return false;
                    }
                }
            }
    );

    /**
     * 缩放手势处理逻辑
     */
    private final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(
            getContext(),
            new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(@NonNull ScaleGestureDetector detector) {
//                    Log.e(TAG, "onScale：" + detector.getScaleFactor());
                    double scalePow = Math.pow(detector.getScaleFactor(), attribute.scalePow);
                    float scale = (float) (attribute.currentScale * scalePow);
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
            }
    );


    public ChartView(Context context) {
        this(context, null, 0);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ChartView, defStyleAttr, defStyleAttr);
        try {
            int type =
                    a.getInteger(R.styleable.ChartView_renderModel, RenderModel.CANDLE.ordinal());
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
                depthAttribute.enableLeftLoadMore = false;
                depthAttribute.enableRightLoadMore = false;
                render = new DepthRender(depthAttribute, viewRect);
                break;
        }
        this.renderModel = renderModel;
        this.attribute = render.getAttribute();
        this.scroller = new OverScroller(getContext());
        DelayedHandler.getInstance().setListener(this);
    }

    /**
     * 设置数据适配器
     */
    public void setAdapter(@NonNull AbsAdapter<? extends AbsEntry, ? extends AbsBuildConfig> adapter) {
        adapter.addDataChangeSupport(propertyChangeListener);
        ((AbsRender) render).setAdapter(adapter);
    }

    /**
     * 获取渲染器
     */
    public AbsRender<? extends AbsAdapter<?, ?>, ? extends BaseAttribute> getRender() {
        return render;
    }

    /**
     * 设置交互Handler
     */
    public void setInteractiveHandler(InteractiveHandler handler) {
        this.interactiveHandler = handler;
    }

    /**
     * 获取交互Handler
     */
    public InteractiveHandler getInteractiveHandler() {
        return interactiveHandler;
    }

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
                    interactiveHandler.onHighlight(
                            entry,
                            highlightIndex,
                            highlightPoint[0],
                            highlightPoint[1]
                    );
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
        cancelOnlyDelayedWork(DelayedTaskID.ID_CANCEL_HIGHLIGHT);
    }

    /**
     * 滚动到最后
     */
    public void scrollToEnd() {
        lastFlingX = 0;
        float scrollX = render.getMaxScrollOffset() - Math.abs(render.getCurrentTransX());
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
        float rate = overScrollOffset < restriction ? 1f - overScrollOffset / restriction : 0f;
        float draggingDX = dx * rate;
        render.updateCurrentTransX(draggingDX);
        render.updateOverScrollOffset(draggingDX);
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

    /**
     * 视图计算
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (viewSizeEntry.isNotMeasure()) {
            setMeasuredDimension(viewSizeEntry.getWidth(), viewSizeEntry.getHeight());
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            this.render.setProrate(true);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            this.render.setProrate(false);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = render.measureViewHeight();
            heightSize = heightSize + getPaddingTop() + getPaddingBottom();
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    /**
     * 视图大小改变
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float left = getPaddingLeft();
        float top = getPaddingTop();
        float tight = left + w - getPaddingRight();
        float bottom = top + h - getPaddingBottom();
        this.viewSizeEntry.setWidth(w);
        this.viewSizeEntry.setHeight(h);
        this.viewRect.set(left, top, tight, bottom);
    }

    /**
     * 视图布局
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            this.onViewInit();
        } else {
            this.viewSizeEntry.onRequestLayoutComplete();
        }
    }

    /**
     * 滚动回调
     */
    @Override
    public void computeScroll() {
        if (onLongPress) return;
        if (scroller.computeScrollOffset()) {
            final float scrollX = scroller.getCurrX();
            final float dx = scrollX - lastFlingX;
            lastFlingX = scrollX;
            if (onTouch) {
                lastFlingTime = 0;
                scroller.abortAnimation();
                return;
            } else if (!scrollIdle) {
                releaseBack(dx);
                return;
            } else if (render.canScroll()) {
                scroll(dx);
                return;
            } else {
                int draggingDX = (int) (dx / 5f);
                long currentTime = System.currentTimeMillis();
                if (render.canDragging(draggingDX) && currentTime - lastFlingTime < 300) {
                    dragging(draggingDX);
                    return;
                }
                lastFlingTime = 0;
                scroller.abortAnimation();
            }
        }
        AbsAdapter<?, ?> adapter = render.getAdapter();
        float eventThreshold = viewRect.width() / 4f;
        float overScrollOffset = render.getOverScrollOffset();
        if (!onTouch && scrollIdle && (int) overScrollOffset != 0) {
            lastFlingX = 0;
            scrollIdle = false;
            // dragging 的偏移量大于阀值时即是一个有效的滑动加载
            if (null != interactiveHandler && Math.abs(overScrollOffset) > eventThreshold) {
                if (canLeftLoad() && overScrollOffset > 0) {
                    overScrollOffset -= eventThreshold;
                    if (loadState != LEFT_LOADING) {
                        loadState = LEFT_LOADING;
                        interactiveHandler.onLeftLoad(adapter.getItem(0));
                    }
                }
            }
            // 与滚动控制、滑动加载数据相关的属性
            scroller.startScroll(0, 0, (int) overScrollOffset, 0, 500);
            postInvalidateOnAnimation();
        } else {
            scrollIdle = true;
        }
    }

    /**
     * 加载完成
     */
    public void loadingComplete(boolean hasMore) {
        int state = loadState;
        this.loadState = 0;
        this.lastFlingX = 0;
        this.scrollIdle = false;
        this.render.setOverScrollOffset(0);
        if (state == LEFT_LOADING) {
            hasLeftLoad = hasMore;
        }
    }

    /**
     * 是否加载中
     */
    public boolean isLoading() {
        return loadState == LEFT_LOADING;
    }

    /**
     * 是否可以左滑加载
     */
    public boolean canLeftLoad() {
        return null != attribute && attribute.enableLeftLoadMore && hasLeftLoad;
    }

    /**
     * 是否高亮
     */
    public boolean isHighlighting() {
        return render.isHighlight();
    }

    /**
     * 事件分发
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (checkReadyState()) {
            boolean doublePointer = event.getPointerCount() > 1;
            int touchMoveType = gestureCompat.getTouchMoveType(event, event.getX(), event.getY());
            if (onLongPress || onDoubleFingerPress || doublePointer || touchMoveType == TouchMoveType.HORIZONTAL) {
                getParent().requestDisallowInterceptTouchEvent(true);
                return super.dispatchTouchEvent(event);
            }
            return super.dispatchTouchEvent(event);
        }
        return false;
    }

    /**
     * 事件处理
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                onDoubleFingerPress = true;
                break;
            case MotionEvent.ACTION_DOWN:
                onTouch = true;
                onDragging = false;
                onLongPress = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (onLongPress) {
                    highlight(e.getX(), e.getY());
                } else {
                    onDragging = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onDoubleFingerPress = false;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouch = false;
                if (onLongPress) {
                    onLongPress = false;
                    postOnlyDelayedWork(DelayedTaskID.ID_CANCEL_HIGHLIGHT, 10000);
                } else if (onDragging) {
                    onDragging = false;
                    computeScroll();
                }
                break;
        }
        return onDoubleFingerPress ? scaleDetector.onTouchEvent(e) : gestureDetector.onTouchEvent(e);
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

    /**
     * 从窗口分离
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != render.getAdapter()) {
            this.render.getAdapter().onDestroy();
            DelayedHandler.getInstance().onDestroy();
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
        this.lastFlingX = 0;
        this.scrollIdle = true;
        this.hasLeftLoad = true;
        this.render.resetChart();
        this.scroller.forceFinished(true);
    }

    /**
     * 检查图表准备状态
     */
    private boolean checkReadyState() {
        return null != render && render.isReady();
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
     * @param indexType       模块指标类型
     * @param moduleGroupType 模块组
     * @param classes         组件
     */
    @SafeVarargs
    public final int removeDrawing(
            @IndexType int indexType,
            @ModuleGroup int moduleGroupType,
            Class<? extends AbsDrawing<?, ?>>... classes
    ) {
        int removeCount = 0;
        AbsModule<?> module = getRender().getModule(indexType, moduleGroupType);
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
     * 发送唯一性延时任务
     *
     * @param what  延时任务标识
     * @param delay 延时时间（ms）
     */
    public void postOnlyDelayedWork(@DelayedTaskID final int what, final long delay) {
        DelayedHandler.getInstance().postOnlyDelayedWork(what, delay);
    }

    /**
     * 取消唯一性延时任务
     *
     * @param what 延时任务标识
     */
    public void cancelOnlyDelayedWork(@DelayedTaskID final int what) {
        DelayedHandler.getInstance().cancelDelayedWork(what);
    }

    /**
     * 延时执行的任务回调
     *
     * @param what 延时任务唯一标识
     */
    @Override
    public void onDelayedWork(int what) {
        if (what == DelayedTaskID.ID_CANCEL_HIGHLIGHT) {//延时取消高亮标识
            cancelHighlight();
        }
    }
}