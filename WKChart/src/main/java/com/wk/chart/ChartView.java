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
import com.wk.chart.enumeration.DelayedTaskID;
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
    private final int STATE_LEFT_LOADING = 1; // 加载中（左）
    private final int STATE_RIGHT_LOADING = 2; // 加载中（右）
    // 视图区域
    private BaseAttribute attribute = null;
    private final RectF viewRect = new RectF();
    private final ViewSizeEntry viewSizeEntry = new ViewSizeEntry();
    private final GestureMoveActionCompat gestureCompat = new GestureMoveActionCompat();
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
    private boolean leftHasLoadMore = false;
    private boolean rightHasLoadMore = false;
    private boolean leftEnableLoadMore = true;
    private boolean rightEnableLoadMore = true;
    private boolean scrollIdle = true;
    private float lastFlingX = 0;
    private long lastFlingTime = 0L;
    private int loadState = 0;//加载状态
    private int lastHighlightIndex = -1; // 上一次高亮的 entry 索引，用于减少回调

    //数据监视器
    private final Observer dataSetObserver = (o, arg) -> {
        switch ((ObserverArg) arg) {
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
    private final GestureDetector gestureDetector = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {
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
                        postOnlyDelayedWork(DelayedTaskID.ID_CANCEL_HIGHLIGHT, 10000);
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
                        } else if (render.canDragging(distanceX)) {
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
                    int flingX = (int) -velocityX / 2;
                    if (!onLongPress && !onDoubleFingerPress && render.canScroll(flingX)) {
                        lastFlingTime = System.currentTimeMillis();
                        scroller.fling(0, 0, flingX, 0, Integer.MIN_VALUE,
                                Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
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


    public ChartView(Context context) {
        this(context, null, 0);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        DelayedHandler.getInstance().setListener(this);
    }

    /**
     * 设置数据适配器
     */
    public void setAdapter(@NonNull AbsAdapter<? extends AbsEntry, ? extends AbsBuildConfig> adapter) {
        adapter.registerDataSetObserver(dataSetObserver);
        ((AbsRender) render).setAdapter(adapter);
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
        float restriction = viewRect.width() / 2f;
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
            int heightSize = render.measureViewHeight();
            heightSize = heightSize + getPaddingTop() + getPaddingBottom();
            setMeasuredDimension(widthSize, heightSize);
        }
    }

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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        Log.e("高度(onSizeChanged)：", "left:" + left + "   top:" + top + "   right:" + right + "   bottom:" + bottom);
        if (changed) {
            this.onViewInit();
        } else {
            this.viewSizeEntry.onRequestLayoutComplete();
        }
    }

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
            } else if (render.canScroll(dx)) {
                scroll(dx);
                return;
            } else {
                int draggingDX = (int) (dx / 10f);
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
                if (leftCanLoadMore() && overScrollOffset > 0) {
                    overScrollOffset -= eventThreshold;
                    if (loadState != STATE_LEFT_LOADING) {
                        loadState = STATE_LEFT_LOADING;
                        interactiveHandler.onLeftRefresh(adapter.getItem(0));
                    }
                } else if (rightCanLoadMore() && overScrollOffset < 0) {
                    overScrollOffset += eventThreshold;
                    if (loadState != STATE_RIGHT_LOADING) {
                        loadState = STATE_RIGHT_LOADING;
                        interactiveHandler.onRightRefresh(adapter.getItem(adapter.getLastPosition()));
                    }
                }
            }
            scroller.startScroll(0, 0, (int) overScrollOffset, 0, OVER_SCROLL_DURATION);
            postInvalidateOnAnimation();
        } else {
            scrollIdle = true;
        }
    }

    /**
     * 加载完成
     */
    public void loadingComplete(boolean hasMore) {
        int overScrollOffset = (int) render.getOverScrollOffset();
        int state = loadState;
        this.loadState = 0;
        this.lastFlingX = 0;
        this.scrollIdle = false;
        if (state == STATE_LEFT_LOADING) {
            leftHasLoadMore = hasMore;
        } else if (state == STATE_RIGHT_LOADING) {
            rightHasLoadMore = hasMore;
        }
        if (overScrollOffset != 0) {
            scroller.startScroll(0, 0, overScrollOffset, 0, OVER_SCROLL_DURATION);
            postInvalidateOnAnimation();
        }
    }

    public boolean isRefreshing() {
        return loadState == STATE_LEFT_LOADING || loadState == STATE_RIGHT_LOADING;
    }

    public void setLeftEnableLoadMore(boolean leftEnableLoadMore) {
        this.leftEnableLoadMore = leftEnableLoadMore;
    }

    public void setRightEnableLoadMore(boolean rightEnableLoadMore) {
        this.rightEnableLoadMore = rightEnableLoadMore;
    }

    public boolean leftCanLoadMore() {
        return leftEnableLoadMore && leftHasLoadMore;
    }

    public boolean rightCanLoadMore() {
        return rightEnableLoadMore && rightHasLoadMore;
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
        boolean state = gestureDetector.onTouchEvent(e) | scaleDetector.onTouchEvent(e);
        switch (e.getAction()) {
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
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouch = false;
                onDoubleFingerPress = false;
                if (onLongPress) {
                    onLongPress = false;
                    postOnlyDelayedWork(DelayedTaskID.ID_CANCEL_HIGHLIGHT, 10000);
                } else if (onDragging) {
                    onDragging = false;
                    computeScroll();
                }
                break;
            default:
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
        this.leftHasLoadMore = true;
        this.rightHasLoadMore = true;
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