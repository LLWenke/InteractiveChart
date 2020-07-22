package com.wk.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wk.chart.compat.ChartConstraintSet;
import com.wk.chart.drawing.AxisDrawing;
import com.wk.chart.drawing.AxisExtremumDrawing;
import com.wk.chart.drawing.BreathingLampDrawing;
import com.wk.chart.drawing.CursorDrawing;
import com.wk.chart.drawing.EmptyDataDrawing;
import com.wk.chart.drawing.ExtremumTagDrawing;
import com.wk.chart.drawing.GridDrawing;
import com.wk.chart.drawing.HighlightDrawing;
import com.wk.chart.drawing.IndicatorLabelDrawing;
import com.wk.chart.drawing.IndicatorLineDrawing;
import com.wk.chart.drawing.MACDDrawing;
import com.wk.chart.drawing.MarkerPointDrawing;
import com.wk.chart.drawing.VolumeDrawing;
import com.wk.chart.drawing.WaterMarkingDrawing;
import com.wk.chart.drawing.candle.CandleDrawing;
import com.wk.chart.drawing.candle.CandleSelectorDrawing;
import com.wk.chart.drawing.depth.DepthDrawing;
import com.wk.chart.drawing.depth.DepthGridDrawing;
import com.wk.chart.drawing.depth.DepthHighlightDrawing;
import com.wk.chart.drawing.depth.DepthSelectorDrawing;
import com.wk.chart.drawing.timeLine.TimeLineDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ChartEntry;
import com.wk.chart.enumeration.DataType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.RenderModel;
import com.wk.chart.marker.AxisTextMarker;
import com.wk.chart.marker.GridTextMarker;
import com.wk.chart.module.BOLLChartModule;
import com.wk.chart.module.CandleChartModule;
import com.wk.chart.module.DepthChartModule;
import com.wk.chart.module.KDJChartModule;
import com.wk.chart.module.MACDChartModule;
import com.wk.chart.module.RSIChartModule;
import com.wk.chart.module.TimeLineChartModule;
import com.wk.chart.module.VolumeChartModule;
import com.wk.chart.module.WRChartModule;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.module.base.AuxiliaryChartModule;
import com.wk.chart.module.base.MainChartModule;
import com.wk.chart.render.AbsRender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartLayout extends ConstraintLayout {
    private static final String TAG = "ChartLayout";
    private ChartEntry chartEntry;

    private LinkedHashMap<ModuleType, AbsChartModule<? extends AbsEntry>> ChartModules;

    private List<ModuleType> enableModuleTypes;

    private DataType dataDisplayType;

    public ChartLayout(Context context) {
        this(context, null);
    }

    public ChartLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
        initChartModules();
        initChart();
    }

    /**
     * 初始化
     */
    private void init() {
        this.chartEntry = new ChartEntry();
        this.ChartModules = new LinkedHashMap<>();
        this.enableModuleTypes = new ArrayList<>();
    }

    /**
     * 构建图表组件
     */
    private void initChartModules() {
        this.ChartModules.clear();
        CandleChartModule candleModule = new CandleChartModule();
        candleModule.addDrawing(new WaterMarkingDrawing());//水印组件
        candleModule.addDrawing(new CandleDrawing());//蜡烛图组件
        candleModule.addDrawing(new IndicatorLineDrawing());//平均线组件
        candleModule.addDrawing(new AxisDrawing());//x轴组件
//        candleModule.addDrawing(new GridDrawing());//Y轴组件
        candleModule.addDrawing(new IndicatorLabelDrawing());//指标文字标签组件
        candleModule.addDrawing(new MarkerPointDrawing());//标记点绘制组件
        candleModule.addDrawing(new ExtremumTagDrawing());//极值标签组件
        candleModule.addDrawing(new AxisExtremumDrawing());//x轴极值组件
//        candleModule.addDrawing(new EmptyDataDrawing());//空页面组件
        this.ChartModules.put(candleModule.getModuleType(), candleModule);

        TimeLineChartModule timeLineModule = new TimeLineChartModule();
        timeLineModule.addDrawing(new WaterMarkingDrawing());//水印组件
        timeLineModule.addDrawing(new AxisDrawing());//x轴组件
        timeLineModule.addDrawing(new GridDrawing());//Y轴组件
        timeLineModule.addDrawing(new TimeLineDrawing());//分时图组件
        timeLineModule.addDrawing(new MarkerPointDrawing());//标记点绘制组件
        timeLineModule.addDrawing(new AxisExtremumDrawing());//x轴极值组件
        timeLineModule.addDrawing(new IndicatorLabelDrawing());//指标文字标签组件
        timeLineModule.addDrawing(new BreathingLampDrawing());//呼吸灯组件
        this.ChartModules.put(timeLineModule.getModuleType(), timeLineModule);

        VolumeChartModule volumeModule = new VolumeChartModule();
        volumeModule.addDrawing(new VolumeDrawing());
        volumeModule.addDrawing(new IndicatorLineDrawing());
        volumeModule.addDrawing(new IndicatorLabelDrawing());
        volumeModule.addDrawing(new AxisExtremumDrawing());
        volumeModule.setSeparateState(true);
        this.ChartModules.put(volumeModule.getModuleType(), volumeModule);

        MACDChartModule macdModule = new MACDChartModule();
        macdModule.addDrawing(new MACDDrawing());
        macdModule.addDrawing(new AxisExtremumDrawing());
        macdModule.addDrawing(new IndicatorLabelDrawing());//指标文字标签组件
        this.ChartModules.put(macdModule.getModuleType(), macdModule);

        RSIChartModule rsiModule = new RSIChartModule();
        rsiModule.addDrawing(new IndicatorLineDrawing());
        rsiModule.addDrawing(new AxisExtremumDrawing());
        rsiModule.addDrawing(new IndicatorLabelDrawing());//指标文字标签组件
        this.ChartModules.put(rsiModule.getModuleType(), rsiModule);

        KDJChartModule kdjModule = new KDJChartModule();
        kdjModule.addDrawing(new IndicatorLineDrawing());
        kdjModule.addDrawing(new AxisExtremumDrawing());
        kdjModule.addDrawing(new IndicatorLabelDrawing());//指标文字标签组件
        this.ChartModules.put(kdjModule.getModuleType(), kdjModule);

        BOLLChartModule bollModule = new BOLLChartModule();
        bollModule.addDrawing(new IndicatorLineDrawing());
        bollModule.addDrawing(new AxisExtremumDrawing());
        bollModule.addDrawing(new IndicatorLabelDrawing());//指标文字标签组件
        this.ChartModules.put(bollModule.getModuleType(), bollModule);

        WRChartModule wrModule = new WRChartModule();
        wrModule.addDrawing(new IndicatorLineDrawing());
        wrModule.addDrawing(new AxisExtremumDrawing());
        wrModule.addDrawing(new IndicatorLabelDrawing());//指标文字标签组件
        this.ChartModules.put(wrModule.getModuleType(), wrModule);
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
            ChartView chart = (ChartView) view;
            AbsRender reader = chart.getRender();
            switch (chart.getRenderModel()) {
                case CANDLE://蜡烛图
                    HighlightDrawing candleHighlight = new HighlightDrawing();
                    candleHighlight.addMarkerView(new AxisTextMarker());
                    candleHighlight.addMarkerView(new GridTextMarker());
                    reader.addFloatDrawing(candleHighlight);
                    reader.addFloatDrawing(new GridDrawing());
                    reader.addFloatDrawing(new CandleSelectorDrawing());
                    for (Map.Entry<ModuleType, AbsChartModule<? extends AbsEntry>> item : ChartModules.entrySet()) {
                        reader.addChartModule(item.getValue());
                    }
                    break;
                case DEPTH://深度图
                    DepthChartModule depthChartModule = new DepthChartModule();
                    depthChartModule.addDrawing(new AxisDrawing());//x轴组件
                    depthChartModule.addDrawing(new DepthGridDrawing());//Y轴组件
                    depthChartModule.addDrawing(new DepthDrawing());//深度图组件
                    DepthHighlightDrawing depthHighlight = new DepthHighlightDrawing();
                    depthHighlight.addMarkerView(new AxisTextMarker());
                    depthHighlight.addMarkerView(new GridTextMarker());
                    reader.addChartModule(depthChartModule);
                    reader.addFloatDrawing(depthHighlight);
                    reader.addFloatDrawing(new DepthSelectorDrawing());
                    chart.setEnableRightRefresh(false);
                    chart.setEnableLeftRefresh(false);
                    depthChartModule.setEnable(true);
                    break;
            }
            this.chartEntry.setChart(chart);
        }
    }

    /**
     * 切换图表组件显示状态
     *
     * @param moduleTypes 类型
     */
    public int switchModuleType(ModuleType... moduleTypes) {
        int changeCount = 0;
        for (ModuleType moduleType : moduleTypes) {
            if (!ChartModules.containsKey(moduleType)) {
                continue;
            }
            AbsChartModule chartModule = ChartModules.get(moduleType);
            if (null == chartModule || chartModule.isEnable()) {
                continue;
            }
            this.enableModuleTypes.clear();
            chartModule.setEnable(true);
            Class classType = chartModule instanceof MainChartModule ?
                    MainChartModule.class : AuxiliaryChartModule.class;
            for (Map.Entry<ModuleType, AbsChartModule<? extends AbsEntry>> item : ChartModules.entrySet()) {
                if (item.getValue() instanceof AuxiliaryChartModule && ((AuxiliaryChartModule) item.getValue()).isSeparateState()) {
                    continue;
                }
                if (item.getValue().isEnable() && classType.isInstance(item.getValue()) && item.getKey() != moduleType) {
                    item.getValue().setEnable(false);
                    changeCount++;
                } else if (item.getValue().isEnable()) {
                    //记录当前启用的图表组件
                    this.enableModuleTypes.add(item.getKey());
                    changeCount++;
                }
            }
        }
        return changeCount;
    }

    /**
     * 获取当前启用的图表组件
     *
     * @return List<ModuleType> 当前启用的图表组件类型集合
     */
    public List<ModuleType> getEnableModuleTypes() {
        return enableModuleTypes;
    }

    /**
     * 根据RenderModel获取对应图表中的ModuleType
     *
     * @return ModuleType
     */
    public @Nullable
    ModuleType getMainModuleType(RenderModel renderModel) {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            View view = getChildAt(i);
            if (view instanceof ChartView) {
                ChartView chart = (ChartView) view;
                if (chart.getRenderModel() == renderModel) {
                    return chart.getRender().getMainChartModule().getModuleType();
                }
            }
        }
        return null;
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
                    AbsRender reader = chart.getRender();
                    if (type == DataType.REAL_TIME) {
                        chart.setEnableRightRefresh(false);//禁用右滑
                        //添加右固定偏移量
                        if (reader.getAttribute().rightScrollOffset == 0) {
                            reader.getAttribute().rightScrollOffset = 250;
                        }
                        chart.getRender().getChartModule(ModuleType.CANDLE).addDrawing(new CursorDrawing());
                        chart.getRender().getChartModule(ModuleType.TIME).addDrawing(new CursorDrawing());
                    } else {
                        chart.setEnableRightRefresh(true);//启用右滑
                        reader.getAttribute().rightScrollOffset = 0;  //消除右固定偏移量
                        chart.getRender().getChartModule(ModuleType.CANDLE).removeDrawing(CursorDrawing.class);
                        chart.getRender().getChartModule(ModuleType.TIME).removeDrawing(CursorDrawing.class);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 根据参数中的Module 获取列表中下一个同级别的Module
     *
     * @param absChartModule 当前Module
     * @return 下一个同级别的Module
     */
    public @Nullable
    AbsChartModule getNextChartModule(AbsChartModule absChartModule) {
        Class classType = absChartModule instanceof MainChartModule ?
                MainChartModule.class : AuxiliaryChartModule.class;
        Iterator<Map.Entry<ModuleType, AbsChartModule<? extends AbsEntry>>> iterator = ChartModules.entrySet().iterator();
        AbsChartModule first = null;
        boolean isFindNext = false;
        while (iterator.hasNext()) {
            Map.Entry<ModuleType, AbsChartModule<? extends AbsEntry>> item = iterator.next();
            if (null == first && classType.isInstance(item.getValue())) {
                first = item.getValue();
            }
            if (item.getValue().getModuleType() == absChartModule.getModuleType()) {
                isFindNext = true;
                continue;
            }
            if (isFindNext && classType.isInstance(item.getValue())) {
                return item.getValue();
            }
        }
        return first;
    }

    public void setConstraintSet(ChartConstraintSet set) {
        set.applyTo(this);
    }
}
