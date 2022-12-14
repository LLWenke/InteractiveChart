package com.wk.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.wk.chart.adapter.AbsAdapter;
import com.wk.chart.adapter.CandleAdapter;
import com.wk.chart.compat.Utils;
import com.wk.chart.drawing.AxisDrawing;
import com.wk.chart.drawing.BorderDrawing;
import com.wk.chart.drawing.BreathingLampDrawing;
import com.wk.chart.drawing.CursorDrawing;
import com.wk.chart.drawing.ExtremumLabelDrawing;
import com.wk.chart.drawing.ExtremumTagDrawing;
import com.wk.chart.drawing.GridLabelDrawing;
import com.wk.chart.drawing.GridLineDrawing;
import com.wk.chart.drawing.HighlightDrawing;
import com.wk.chart.drawing.IndexLabelDrawing;
import com.wk.chart.drawing.IndexLineDrawing;
import com.wk.chart.drawing.MACDDrawing;
import com.wk.chart.drawing.MarkerPointDrawing;
import com.wk.chart.drawing.VolumeDrawing;
import com.wk.chart.drawing.WaterMarkingDrawing;
import com.wk.chart.drawing.candle.CandleDrawing;
import com.wk.chart.drawing.candle.CandleSelectorDrawing;
import com.wk.chart.drawing.child.AxisTextMarker;
import com.wk.chart.drawing.child.GridTextMarker;
import com.wk.chart.drawing.depth.DepthDrawing;
import com.wk.chart.drawing.depth.DepthGridDrawing;
import com.wk.chart.drawing.depth.DepthHighlightDrawing;
import com.wk.chart.drawing.depth.DepthSelectorDrawing;
import com.wk.chart.drawing.timeLine.TimeLineDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ChartCache;
import com.wk.chart.enumeration.ClickDrawingID;
import com.wk.chart.enumeration.DataType;
import com.wk.chart.enumeration.ExtremumVisible;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.LoadingType;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.enumeration.RenderModel;
import com.wk.chart.interfaces.ICacheLoadListener;
import com.wk.chart.module.CandleIndexModule;
import com.wk.chart.module.CandleModule;
import com.wk.chart.module.DepthModule;
import com.wk.chart.module.FloatModule;
import com.wk.chart.module.TimeLineModule;
import com.wk.chart.module.VolumeModule;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ChartLayout extends ConstraintLayout {
    private static final String TAG = "ChartLayout";
    private final ConstraintSet constraintSet;
    private DataType dataDisplayType;
    private AbsRender<?, ?> candleRender;
    private ChartView candleChartView;
    private ICacheLoadListener iCacheLoadListener;

    public ChartLayout(Context context) {
        this(context, null);
    }

    public ChartLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.constraintSet = new ConstraintSet();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.constraintSet.clone(this);
        initChart();
    }

    /**
     * 构建蜡烛图组件
     */
    protected void initCandleChartModules(AbsRender<?, ?> render) {
        render.resetChartModules();
        CandleModule candleModule = new CandleModule();
        candleModule.addDrawing(new WaterMarkingDrawing());//水印组件
        candleModule.addDrawing(new AxisDrawing(5, false));//axis轴组件
        candleModule.addDrawing(new GridLineDrawing());//grid轴组件
        candleModule.addDrawing(new CandleDrawing());//蜡烛图组件
        candleModule.addDrawing(new IndexLineDrawing(IndexType.CANDLE_MA));//MA组件
        candleModule.addDrawing(new IndexLabelDrawing(IndexType.CANDLE_MA));//MA指标文字标签组件
        candleModule.addDrawing(new IndexLineDrawing(IndexType.BOLL));//BOLL平均线组件
        candleModule.addDrawing(new IndexLabelDrawing(IndexType.BOLL));//BOLL指标文字标签组件
        candleModule.addDrawing(new MarkerPointDrawing());//标记点绘制组件
        candleModule.addDrawing(new ExtremumTagDrawing(ClickDrawingID.ID_EXTREMUM_TAG));//极值标签组件
        candleModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        candleModule.setAttachIndexType(IndexType.CANDLE_MA);
        candleModule.setEnable(true);
        render.addModule(candleModule);

        TimeLineModule timeLineModule = new TimeLineModule();
        timeLineModule.addDrawing(new WaterMarkingDrawing());//水印组件
        timeLineModule.addDrawing(new AxisDrawing(5, false));//axis轴组件
        timeLineModule.addDrawing(new GridLineDrawing());//grid轴组件
        timeLineModule.addDrawing(new TimeLineDrawing());//分时图组件
        timeLineModule.addDrawing(new BreathingLampDrawing());//呼吸灯组件
        timeLineModule.addDrawing(new MarkerPointDrawing());//标记点绘制组件
        timeLineModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        render.addModule(timeLineModule);

        VolumeModule volumeModule = new VolumeModule();
        volumeModule.addDrawing(new GridLineDrawing());//grid轴组件
        volumeModule.addDrawing(new VolumeDrawing());//交易量组件
        volumeModule.addDrawing(new IndexLineDrawing(IndexType.VOLUME_MA));//MA组件
        volumeModule.addDrawing(new IndexLabelDrawing(IndexType.VOLUME_MA));//MA指标文字标签组件
        volumeModule.addDrawing(new ExtremumLabelDrawing(ExtremumVisible.MAX_VISIBLE, true, false));//x轴标签组件
        volumeModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        volumeModule.setAttachIndexType(IndexType.VOLUME_MA);
        volumeModule.setEnable(true);
        render.addModule(volumeModule);

        CandleIndexModule indexModule = new CandleIndexModule();
        indexModule.addDrawing(new GridLineDrawing());//grid轴组件
        indexModule.addDrawing(new MACDDrawing());//MACD 指标组件
        indexModule.addDrawing(new IndexLabelDrawing(IndexType.MACD));//MACD 指标文字标签组件
        indexModule.addDrawing(new IndexLineDrawing(IndexType.KDJ));//KDJ 指标线组件
        indexModule.addDrawing(new IndexLabelDrawing(IndexType.KDJ));//KDJ 指标文字标签组件
        indexModule.addDrawing(new IndexLineDrawing(IndexType.RSI));//RSI 指标线组件
        indexModule.addDrawing(new IndexLabelDrawing(IndexType.RSI));//RSI 指标文字标签组件
        indexModule.addDrawing(new IndexLineDrawing(IndexType.WR));//WR 指标线组件
        indexModule.addDrawing(new IndexLabelDrawing(IndexType.WR));//WR 指标文字标签组件
        indexModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        indexModule.setEnable(true);
        render.addModule(indexModule);

        FloatModule floatModule = new FloatModule();
        floatModule.addDrawing(new GridLabelDrawing());
        floatModule.addDrawing(new HighlightDrawing(new AxisTextMarker(), new GridTextMarker()));
        floatModule.addDrawing(new CandleSelectorDrawing());
        render.addModule(floatModule);
    }

    /**
     * 构建深度图组件
     */
    protected void initDepthChartModules(AbsRender<?, ?> render) {
        DepthModule depthModule = new DepthModule();
        depthModule.addDrawing(new AxisDrawing(5, true));//axis轴组件
        depthModule.addDrawing(new DepthGridDrawing());//grid轴组件
        depthModule.addDrawing(new DepthDrawing());//深度图组件
        depthModule.addDrawing(new DepthHighlightDrawing(new AxisTextMarker(), new GridTextMarker()));//高亮组件
        depthModule.addDrawing(new DepthSelectorDrawing());//选择器组件
        depthModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        depthModule.setEnable(true);
        render.addModule(depthModule);

        FloatModule floatModule = new FloatModule();
        floatModule.addDrawing(new BorderDrawing(PositionType.START | PositionType.BOTTOM | PositionType.END));//边框组件
        render.addModule(floatModule);
    }

    /**
     * 初始化 图表
     */
    private void initChart() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            View view = getChildAt(i);
            if (!(view instanceof ChartView)) {
                continue;
            }
            ChartView chartView = (ChartView) view;
            AbsRender<?, ?> render = chartView.getRender();
            switch (chartView.getRenderModel()) {
                case CANDLE://蜡烛图
                    this.candleChartView = chartView;
                    this.candleRender = render;
                    initCandleChartModules(render);
                    break;
                case DEPTH://深度图
                    initDepthChartModules(render);
                    chartView.setEnableRightLoad(false);
                    chartView.setEnableLeftLoad(false);
                    break;
            }
        }
    }

    /**
     * 切换图表组件
     *
     * @param moduleType      模块类型
     * @param moduleGroupType 模块分组
     */
    public boolean switchModuleType(@ModuleType int moduleType, @ModuleGroupType int moduleGroupType) {
        if (null == candleRender) {
            return false;
        }
        List<AbsModule<AbsEntry>> modules = candleRender.getModules().get(moduleGroupType);
        if (null == modules || modules.isEmpty()) {
            return false;
        }
        boolean state = false;
        for (AbsModule<AbsEntry> item : modules) {
            if (moduleType == item.getModuleType()) {
                if (item.isEnable()) {
                    state = false;
                } else {
                    item.setEnable(true);
                    state = true;
                }
            } else {
                item.setEnable(false);
            }
        }
        return state;
    }

    /**
     * 切换图表指标
     *
     * @param indexType       指标类型
     * @param moduleGroupType 模块分组
     */
    public boolean switchIndexType(@IndexType int indexType, @ModuleGroupType int moduleGroupType) {
        if (null == candleRender) {
            return false;
        }
        List<AbsModule<AbsEntry>> modules = candleRender.getModules().get(moduleGroupType);
        if (null == modules || modules.isEmpty()) {
            return false;
        }
        for (AbsModule<AbsEntry> item : modules) {
            if (item.isEnable()) {
                if (item.getAttachIndexType() == indexType) {
                    return false;
                }
                item.setAttachIndexType(indexType);
                return true;
            }
        }
        return false;
    }

    public @IndexType
    int getNowIndexType(@ModuleGroupType int moduleGroupType) {
        List<AbsModule<AbsEntry>> modules = candleRender.getModules().get(moduleGroupType);
        if (null == modules || modules.isEmpty()) {
            return IndexType.NONE;
        }
        for (AbsModule<AbsEntry> item : modules) {
            if (item.isEnable()) {
                return item.getAttachIndexType();
            }
        }
        return IndexType.NONE;
    }

    /**
     * 缓存图表信息
     *
     * @return 缓存信息
     */
    public @Nullable
    ChartCache chartCache() {
        if (null == candleRender || null == candleChartView) {
            return null;
        }
        ChartCache chartCache = new ChartCache();
        chartCache.scale = candleRender.getAttribute().currentScale;
        chartCache.cacheMaxScrollOffset = candleRender.getMaxScrollOffset();
        chartCache.cacheCurrentTransX = candleRender.getCurrentTransX();
        AbsAdapter<?, ?> adapter = candleRender.getAdapter();
        if (adapter instanceof CandleAdapter) {
            chartCache.timeType = ((CandleAdapter) adapter).getTimeType();
        }
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : candleRender.getModules().entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isEnable()) {
                    chartCache.getTypes().put(module.getModuleGroup(), new ChartCache.TypeEntry(
                            module.getModuleType(), module.getAttachIndexType()));
                    break;
                }
            }
        }
        return chartCache;
    }

    /**
     * 加载图表缓存信息
     */
    public void loadChartCache(@NotNull final ChartCache chartCache) {
        if (null == candleRender || null == candleChartView) {
            return;
        }
        boolean isNeedLoadData = false;
        candleRender.getAttribute().currentScale = chartCache.scale;
        candleRender.setCacheMaxScrollOffset(chartCache.cacheMaxScrollOffset);
        candleRender.setCacheCurrentTransX(chartCache.cacheCurrentTransX);
        AbsAdapter<?, ?> adapter = candleRender.getAdapter();
        if (adapter instanceof CandleAdapter) {
            CandleAdapter candleAdapter = (CandleAdapter) adapter;
            isNeedLoadData = candleAdapter.isNeedLoadData(chartCache.timeType);
        }
        for (Map.Entry<Integer, ChartCache.TypeEntry> types : chartCache.getTypes().entrySet()) {
            switchModuleType(types.getValue().getModuleType(), types.getKey());
            switchIndexType(types.getValue().getIndexType(), types.getKey());
        }
        this.candleChartView.post(() -> candleChartView.onViewInit());
        if (null != iCacheLoadListener) {
            this.iCacheLoadListener.onLoadCacheTypes(chartCache.timeType, isNeedLoadData, chartCache.getTypes());
        }
    }

    /**
     * 设置图表数据显示模式（1:分页模式，2:实时模式）
     */
    public void setDataDisplayType(DataType type) {
        if (type == dataDisplayType) {
            return;
        }
        this.dataDisplayType = type;
        for (int i = 0, z = getChildCount(); i < z; i++) {
            View view = getChildAt(i);
            if (view instanceof ChartView) {
                ChartView chart = (ChartView) view;
                if (chart.getRenderModel() == RenderModel.CANDLE) {
                    AbsRender<?, ?> reader = chart.getRender();
                    if (type == DataType.REAL_TIME) {
                        chart.setEnableRightLoad(false);//禁用右滑
                        //添加右固定偏移量
                        if (reader.getAttribute().rightScrollOffset == 0) {
                            reader.getAttribute().rightScrollOffset = 250;
                        }
                        chart.getRender().getModule(ModuleType.CANDLE, ModuleGroupType.MAIN).addDrawing(new CursorDrawing(ClickDrawingID.ID_CURSOR));
                        chart.getRender().getModule(ModuleType.TIME, ModuleGroupType.MAIN).addDrawing(new CursorDrawing(ClickDrawingID.ID_CURSOR));
                    } else {
                        chart.setEnableRightLoad(true);//启用右滑
                        reader.getAttribute().rightScrollOffset = 0;  //消除右固定偏移量
                        chart.getRender().getModule(ModuleType.CANDLE, ModuleGroupType.MAIN).removeDrawing(CursorDrawing.class);
                        chart.getRender().getModule(ModuleType.TIME, ModuleGroupType.MAIN).removeDrawing(CursorDrawing.class);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 数据开始加载
     *
     * @param loadingType 加载框出现类型
     */
    public void loadBegin(LoadingType loadingType, ProgressBar bar, ChartView chart) {
        this.constraintSet.setVisibility(bar.getId(), VISIBLE);
        this.constraintSet.connect(bar.getId(), ConstraintSet.START, chart.getId(),
                ConstraintSet.START, Utils.dp2px(getContext(), 30));
        this.constraintSet.connect(bar.getId(), ConstraintSet.END, chart.getId(),
                ConstraintSet.END, Utils.dp2px(getContext(), 30));
        switch (loadingType) {
            case LEFT_LOADING:
                this.constraintSet.clear(bar.getId(), ConstraintSet.END);
                break;
            case RIGHT_LOADING:
                this.constraintSet.clear(bar.getId(), ConstraintSet.START);
                break;
        }
        constraintSet.applyTo(this);
    }

    /**
     * 数据加载完毕
     */
    public void loadComplete(ProgressBar bar) {
        this.constraintSet.setVisibility(bar.getId(), INVISIBLE);
        constraintSet.applyTo(this);
    }

    public void setICacheLoadListener(ICacheLoadListener iCacheLoadListener) {
        this.iCacheLoadListener = iCacheLoadListener;
    }
}
