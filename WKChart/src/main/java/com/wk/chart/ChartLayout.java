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
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.AxisDrawing;
import com.wk.chart.drawing.BorderDrawing;
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
import com.wk.chart.drawing.SARDrawing;
import com.wk.chart.drawing.VolumeDrawing;
import com.wk.chart.drawing.WaterMarkingDrawing;
import com.wk.chart.drawing.candle.CandleDrawing;
import com.wk.chart.drawing.candle.CandleSelectorDrawing;
import com.wk.chart.drawing.child.AxisTextMarker;
import com.wk.chart.drawing.child.GridTextMarker;
import com.wk.chart.drawing.depth.DepthDrawing;
import com.wk.chart.drawing.depth.DepthGridDrawing;
import com.wk.chart.drawing.depth.DepthHighlightDrawing;
import com.wk.chart.drawing.depth.DepthPositionDrawing;
import com.wk.chart.drawing.depth.DepthSelectorDrawing;
import com.wk.chart.drawing.timeLine.BreathingLampDrawing;
import com.wk.chart.drawing.timeLine.TimeLineDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ChartCache;
import com.wk.chart.enumeration.ClickDrawingID;
import com.wk.chart.enumeration.DataDisplayType;
import com.wk.chart.enumeration.ExtremumVisible;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.LoadingType;
import com.wk.chart.enumeration.ModuleGroup;
import com.wk.chart.enumeration.ModuleLayoutType;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.enumeration.RenderModel;
import com.wk.chart.interfaces.ICacheLoadListener;
import com.wk.chart.module.AbsModule;
import com.wk.chart.module.CandleModule;
import com.wk.chart.module.DepthModule;
import com.wk.chart.module.FloatModule;
import com.wk.chart.module.IndexModule;
import com.wk.chart.module.TimeLineModule;
import com.wk.chart.module.VolumeModule;
import com.wk.chart.render.AbsRender;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 图表布局
 */
public class ChartLayout extends ConstraintLayout {
    private static final String TAG = "ChartLayout";
    private final ConstraintSet constraintSet;
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
    }

    /**
     * 构建图表组件
     */
    protected void initChartModules(AbsRender<?, ?> render) {
        render.resetChartModules();
        CandleModule candleModule = buildCandleModule();
        candleModule.setEnable(true);
        render.addModule(resetModuleDataDisplayType(candleModule, render));

        TimeLineModule timeLineModule = buildTimeLineModule();
        timeLineModule.setEnable(false);
        render.addModule(resetModuleDataDisplayType(timeLineModule, render));

        VolumeModule volumeModule = buildVolumeModule();
        volumeModule.setEnable(false);
        render.addModule(volumeModule);

        IndexModule macdIndexModule = buildMACDIndexModule();
        macdIndexModule.setEnable(false);
        render.addModule(macdIndexModule);

        IndexModule kdjIndexModule = buildKDJIndexModule();
        kdjIndexModule.setEnable(false);
        render.addModule(kdjIndexModule);

        IndexModule rsiIndexModule = buildRSIIndexModule();
        rsiIndexModule.setEnable(false);
        render.addModule(rsiIndexModule);

        IndexModule wrIndexModule = buildWRIndexModule();
        wrIndexModule.setEnable(false);
        render.addModule(wrIndexModule);

        FloatModule floatModule = buildFloatModule();
        floatModule.setEnable(true);
        render.addModule(floatModule);
    }

    /**
     * 构建深度图组件
     */
    protected void initDepthChartModules(AbsRender<?, ?> render) {
        render.resetChartModules();
        DepthModule depthModule = buildDepthModule();
        depthModule.setEnable(true);
        render.addModule(depthModule);

        FloatModule floatModule = buildFloatModule2();
        floatModule.setEnable(true);
        render.addModule(floatModule);
    }

    /**
     * 构建蜡烛图模型
     *
     * @return 蜡烛图模型
     */
    private CandleModule buildCandleModule() {
        CandleModule candleModule = new CandleModule();
        candleModule.addDrawing(new WaterMarkingDrawing());//水印组件
        candleModule.addDrawing(new AxisDrawing(5, false));//axis轴组件
        candleModule.addDrawing(new GridLineDrawing());//grid轴组件
        candleModule.addDrawing(new CandleDrawing());//蜡烛图组件
        candleModule.addDrawing(new IndexLineDrawing(IndexType.CANDLE_MA));//MA组件
        candleModule.addDrawing(new IndexLabelDrawing(IndexType.CANDLE_MA));//MA指标文字标签组件
        candleModule.addDrawing(new IndexLineDrawing(IndexType.EMA));//EMA组件
        candleModule.addDrawing(new IndexLabelDrawing(IndexType.EMA));//EMA指标文字标签组件
        candleModule.addDrawing(new IndexLineDrawing(IndexType.BOLL));//BOLL指标组件
        candleModule.addDrawing(new IndexLabelDrawing(IndexType.BOLL));//BOLL指标文字标签组件
        candleModule.addDrawing(new SARDrawing());//SAR指标组件
        candleModule.addDrawing(new IndexLabelDrawing(IndexType.SAR));//SAR指标文字标签组件
        candleModule.addDrawing(new MarkerPointDrawing());//标记点绘制组件
        candleModule.addDrawing(new ExtremumTagDrawing(ClickDrawingID.ID_EXTREMUM_TAG));//极值标签组件
        candleModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        candleModule.addAttachIndex(IndexType.CANDLE_MA);
        return candleModule;
    }

    /**
     * 构建分时图模型
     *
     * @return 分时图模型
     */
    private TimeLineModule buildTimeLineModule() {
        TimeLineModule timeLineModule = new TimeLineModule();
        timeLineModule.addDrawing(new WaterMarkingDrawing());//水印组件
        timeLineModule.addDrawing(new AxisDrawing(5, false));//axis轴组件
        timeLineModule.addDrawing(new GridLineDrawing());//grid轴组件
        timeLineModule.addDrawing(new TimeLineDrawing());//分时图组件
        timeLineModule.addDrawing(new BreathingLampDrawing());//呼吸灯组件
        timeLineModule.addDrawing(new MarkerPointDrawing());//标记点绘制组件
        timeLineModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        return timeLineModule;
    }

    /**
     * 构建交易量图模型
     *
     * @return 交易量图模型
     */
    private VolumeModule buildVolumeModule() {
        VolumeModule volumeModule = new VolumeModule();
        volumeModule.addDrawing(new GridLineDrawing());//grid轴组件
        volumeModule.addDrawing(new VolumeDrawing());//交易量组件
        volumeModule.addDrawing(new IndexLineDrawing(IndexType.VOLUME_MA));//MA组件
        volumeModule.addDrawing(new IndexLabelDrawing(IndexType.VOLUME_MA));//MA指标文字标签组件
        volumeModule.addDrawing(new ExtremumLabelDrawing(
                ExtremumVisible.MAX_VISIBLE,
                true,
                false
        ));//Axis轴标签组件
        volumeModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        volumeModule.addAttachIndex(IndexType.VOLUME_MA);
        return volumeModule;
    }

    /**
     * 构建MACD指标图模型
     *
     * @return MACD指标图模型
     */
    protected IndexModule buildMACDIndexModule() {
        IndexModule macdModule = new IndexModule(IndexType.MACD);
        macdModule.addDrawing(new GridLineDrawing());//grid轴组件
        macdModule.addDrawing(new MACDDrawing());//MACD 指标组件
        macdModule.addDrawing(new IndexLabelDrawing(IndexType.MACD));//MACD 指标文字标签组件
        macdModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        return macdModule;
    }

    /**
     * 构建KDJ指标图模型
     *
     * @return KDJ指标图模型
     */
    protected IndexModule buildKDJIndexModule() {
        IndexModule kdjModule = new IndexModule(IndexType.KDJ);
        kdjModule.addDrawing(new IndexLineDrawing(IndexType.KDJ));//KDJ 指标线组件
        kdjModule.addDrawing(new IndexLabelDrawing(IndexType.KDJ));//KDJ 指标文字标签组件
        kdjModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        return kdjModule;
    }

    /**
     * 构建RSI指标图模型
     *
     * @return RSI指标图模型
     */
    protected IndexModule buildRSIIndexModule() {
        IndexModule rsiModule = new IndexModule(IndexType.RSI);
        rsiModule.addDrawing(new IndexLineDrawing(IndexType.RSI));//RSI 指标线组件
        rsiModule.addDrawing(new IndexLabelDrawing(IndexType.RSI));//RSI 指标文字标签组件
        rsiModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        return rsiModule;
    }

    /**
     * 构建WR指标图模型
     *
     * @return WR指标图模型
     */
    protected IndexModule buildWRIndexModule() {
        IndexModule wrModule = new IndexModule(IndexType.WR);
        wrModule.addDrawing(new IndexLineDrawing(IndexType.WR));//WR 指标线组件
        wrModule.addDrawing(new IndexLabelDrawing(IndexType.WR));//WR 指标文字标签组件
        wrModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        return wrModule;
    }

    /**
     * 构建Depth图模型
     *
     * @return Depth图模型
     */
    protected DepthModule buildDepthModule() {
        DepthModule depthModule = new DepthModule();
        depthModule.addDrawing(new AxisDrawing(5, true));//axis轴组件
        depthModule.addDrawing(new DepthGridDrawing());//深度图grid轴组件
        depthModule.addDrawing(new DepthDrawing());//深度图组件
        depthModule.addDrawing(new DepthPositionDrawing());//深度图盘口组件
        depthModule.addDrawing(new DepthHighlightDrawing(
                new AxisTextMarker(),
                new GridTextMarker()
        ));//深度图高亮组件
        depthModule.addDrawing(new DepthSelectorDrawing());//深度图选择器组件
        depthModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//边框组件
        return depthModule;
    }


    /**
     * 构建浮动模型
     *
     * @return 浮动模型
     */
    protected FloatModule buildFloatModule() {
        FloatModule floatModule = new FloatModule();
        floatModule.addDrawing(new GridLabelDrawing());
        floatModule.addDrawing(new HighlightDrawing(new AxisTextMarker(), new GridTextMarker()));
        floatModule.addDrawing(new CandleSelectorDrawing());
        return floatModule;
    }

    /**
     * 构建浮动模型2
     *
     * @return 浮动模型
     */
    protected FloatModule buildFloatModule2() {
        FloatModule floatModule = new FloatModule();
        floatModule.addDrawing(new BorderDrawing(PositionType.START | PositionType.BOTTOM | PositionType.END));//边框组件
        return floatModule;
    }

    /**
     * @param module 模型
     * @param render 渲染器
     * @return 模型
     */
    private AbsModule<?> resetModuleDataDisplayType(AbsModule<?> module, AbsRender<?, ?> render) {
        //设置指标模块数据显示类型
        if (render.getAttribute().dataDisplayType == DataDisplayType.REAL_TIME) {
            //禁用右滑
            render.getAttribute().enableRightLoadMore = false;
            //添加右固定偏移量
            if (render.getAttribute().rightScrollOffset == 0) {
                render.getAttribute().rightScrollOffset = 250;
            }
            //添加游标指示器组件
            module.addDrawing(new CursorDrawing(ClickDrawingID.ID_CURSOR));
        } else {
            //启用右滑
            render.getAttribute().enableRightLoadMore = true;
            //消除右固定偏移量
            render.getAttribute().rightScrollOffset = 0;
            //移除游标指示器组件
            module.removeDrawing(CursorDrawing.class);
        }
        return module;
    }

    /**
     * 初始化 图表
     */
    public void initChart() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            View view = getChildAt(i);
            if (!(view instanceof ChartView)) {
                continue;
            }
            ChartView chartView = (ChartView) view;
            AbsRender<?, ?> render = chartView.getRender();
            switch (chartView.getRenderModel()) {
                case CANDLE://蜡烛图
                    initChartModules(render);
                    break;
                case DEPTH://深度图
                    initDepthChartModules(render);
                    break;
            }
        }
    }

    /**
     * 切换图表组件启用状态
     *
     * @param renderModel     渲染模型
     * @param moduleIndexType 组件指标类型
     * @param moduleGroupType 模块分组
     */
    public boolean moduleEnable(
            RenderModel renderModel,
            @ModuleGroup int moduleGroupType,
            @IndexType int moduleIndexType
    ) {
        return moduleEnable(renderModel, moduleGroupType, moduleIndexType, true);
    }

    /**
     * 切换图表组件启用状态
     *
     * @param renderModel     渲染模型
     * @param moduleIndexType 组件指标类型
     * @param moduleGroupType 模块分组
     * @param moduleEnable    模块启用
     */
    public boolean moduleEnable(
            RenderModel renderModel,
            @ModuleGroup int moduleGroupType,
            @IndexType int moduleIndexType,
            boolean moduleEnable
    ) {
        ChartView chartView = getChartView(renderModel);
        if (null == chartView) return false;
        AbsRender<?, ?> render = chartView.getRender();
        BaseAttribute attribute = render.getAttribute();
        List<AbsModule<AbsEntry>> modules = render.getModules().get(moduleGroupType);
        if (null == modules || null == attribute || modules.isEmpty()) {
            return false;
        }
        boolean state = false;
        int layoutType = moduleGroupType == ModuleGroup.MAIN ? attribute.mainModuleLayoutType : (
                moduleGroupType == ModuleGroup.INDEX ? attribute.indexModuleLayoutType :
                        ModuleLayoutType.OVERLAP
        );
        for (AbsModule<AbsEntry> item : modules) {
            if (moduleIndexType == item.getModuleIndexType()) {
                if (item.isEnable() != moduleEnable) {
                    item.setEnable(moduleEnable);
                    state = true;
                }
            } else if (item.isEnable() && moduleIndexType == IndexType.NONE) {
                item.setEnable(false);
                state = true;
            } else if (item.isEnable() && layoutType == ModuleLayoutType.OVERLAP) {
                item.setEnable(false);
                state = true;
            }
        }
        return state;
    }

    /**
     * 重置图表组件附加指标集
     *
     * @param renderModel     渲染模型
     * @param moduleIndexType 组件指标类型
     * @param moduleGroupType 模块分组
     */
    public boolean moduleAttachIndexReset(
            RenderModel renderModel,
            @ModuleGroup int moduleGroupType,
            @IndexType int moduleIndexType,
            HashSet<Integer> attachIndexSet
    ) {
        ChartView chartView = getChartView(renderModel);
        if (null == chartView) return false;
        AbsRender<?, ?> render = chartView.getRender();
        List<AbsModule<AbsEntry>> modules = render.getModules().get(moduleGroupType);
        if (null == modules || modules.isEmpty()) {
            return false;
        }
        for (AbsModule<AbsEntry> item : modules) {
            if (moduleIndexType == item.getModuleIndexType()) {
                item.setAttachIndexSet(attachIndexSet);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取已启用的模块Attach指标set
     *
     * @param renderModel     渲染模型
     * @param moduleGroupType 模块分组
     */
    public @Nullable HashSet<Integer> getEnableModuleAttachIndexTypeSet(
            RenderModel renderModel, @ModuleGroup int moduleGroupType
    ) {
        ChartView chartView = getChartView(renderModel);
        if (null == chartView) return null;
        AbsRender<?, ?> render = chartView.getRender();
        List<AbsModule<AbsEntry>> modules = render.getModules().get(moduleGroupType);
        HashSet<Integer> indexSet = new HashSet<>();
        if (null == modules || modules.isEmpty()) {
            indexSet.add(IndexType.NONE);
            return indexSet;
        }
        for (AbsModule<AbsEntry> item : modules) {
            if (item.isEnable()) {
                indexSet.addAll(item.getAttachIndexSet());
            }
        }
        if (indexSet.isEmpty()) {
            indexSet.add(IndexType.NONE);
        }
        return indexSet;
    }

    /**
     * 获取已启用的模块指标set
     *
     * @param renderModel     渲染模型
     * @param moduleGroupType 模块分组
     */
    public @Nullable HashSet<Integer> getEnableModuleIndexTypeSet(
            RenderModel renderModel, @ModuleGroup int moduleGroupType
    ) {
        ChartView chartView = getChartView(renderModel);
        if (null == chartView) return null;
        AbsRender<?, ?> render = chartView.getRender();
        List<AbsModule<AbsEntry>> modules = render.getModules().get(moduleGroupType);
        HashSet<Integer> indexSet = new HashSet<>();
        if (null == modules || modules.isEmpty()) {
            indexSet.add(IndexType.NONE);
            return indexSet;
        }
        for (AbsModule<AbsEntry> item : modules) {
            if (item.isEnable()) {
                indexSet.add(item.getModuleIndexType());
            }
        }
        if (indexSet.isEmpty()) {
            indexSet.add(IndexType.NONE);
        }
        return indexSet;
    }

    /**
     * 缓存图表信息
     *
     * @param renderModel 渲染模型
     * @return 缓存信息
     */
    public @Nullable ChartCache chartCache(RenderModel renderModel) {
        ChartView chartView = getChartView(renderModel);
        if (null == chartView) return null;
        AbsRender<?, ?> render = chartView.getRender();
        ChartCache chartCache = new ChartCache();
        chartCache.scale = render.getAttribute().currentScale;
        chartCache.cacheMaxScrollOffset = render.getMaxScrollOffset();
        chartCache.cacheCurrentTransX = render.getCurrentTransX();
        AbsAdapter<?, ?> adapter = render.getAdapter();
        if (adapter instanceof CandleAdapter) {
            chartCache.timeType = ((CandleAdapter) adapter).getTimeType();
        }
        LinkedHashMap<Integer, List<AbsModule<AbsEntry>>> modules = render.getModules();
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : modules.entrySet()) {
            List<ChartCache.TypeEntry> entries = new ArrayList<>();
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isEnable()) continue;
                entries.add(new ChartCache.TypeEntry(
                        module.getModuleIndexType(),
                        module.getAttachIndexSet()
                ));
            }
            chartCache.putTypeEntry(item.getKey(), entries);
        }
        return chartCache;
    }

    /**
     * 加载图表缓存信息
     *
     * @param renderModel 渲染模型
     * @param chartCache  图表缓存
     */
    public void loadChartCache(RenderModel renderModel, @NotNull ChartCache chartCache) {
        ChartView chartView = getChartView(renderModel);
        if (null == chartView) return;
        boolean isNeedLoadData = false;
        AbsRender<?, ?> render = chartView.getRender();
        render.getAttribute().currentScale = chartCache.scale;
        render.setCacheMaxScrollOffset(chartCache.cacheMaxScrollOffset);
        render.setCacheCurrentTransX(chartCache.cacheCurrentTransX);
        AbsAdapter<?, ?> adapter = render.getAdapter();
        if (adapter instanceof CandleAdapter) {
            CandleAdapter candleAdapter = (CandleAdapter) adapter;
            isNeedLoadData = candleAdapter.isNeedLoadData(chartCache.timeType);
        }
        Map<Integer, List<ChartCache.TypeEntry>> map = chartCache.getTypeEntryCache();
        for (Map.Entry<Integer, List<ChartCache.TypeEntry>> types : map.entrySet()) {
            for (ChartCache.TypeEntry entry : types.getValue()) {
                moduleEnable(renderModel, types.getKey(), entry.getModuleIndexType());
                moduleAttachIndexReset(
                        renderModel,
                        types.getKey(),
                        entry.getModuleIndexType(),
                        entry.getAttachTypeSet()
                );
            }
        }
        chartView.post(chartView::onViewInit);
        if (null != iCacheLoadListener) {
            this.iCacheLoadListener.onLoadCacheTypes(
                    chartCache.timeType,
                    isNeedLoadData,
                    chartCache.getTypeEntryCache()
            );
        }
    }

    /**
     * 根据渲染模型获取对应的图表View
     *
     * @param renderModel 渲染模型
     * @return 对应的图表View
     */
    public @Nullable ChartView getChartView(RenderModel renderModel) {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            View view = getChildAt(i);
            if (view instanceof ChartView) {
                ChartView chartView = (ChartView) view;
                if (renderModel == chartView.getRenderModel()) {
                    return chartView;
                }
            }
        }
        return null;
    }

    /**
     * 数据开始加载
     *
     * @param loadingType 加载框出现类型
     */
    public void loadBegin(LoadingType loadingType, ProgressBar bar, ChartView chart) {
        this.constraintSet.setVisibility(bar.getId(), VISIBLE);
        this.constraintSet.connect(
                bar.getId(),
                ConstraintSet.START,
                chart.getId(),
                ConstraintSet.START,
                Utils.dp2px(getContext(), 30)
        );
        this.constraintSet.connect(
                bar.getId(),
                ConstraintSet.END,
                chart.getId(),
                ConstraintSet.END,
                Utils.dp2px(getContext(), 30)
        );
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
