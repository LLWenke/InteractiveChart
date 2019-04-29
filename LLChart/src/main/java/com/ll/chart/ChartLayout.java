package com.ll.chart;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import com.ll.chart.compat.ChartConstraintSet;
import com.ll.chart.drawing.AxisDrawing;
import com.ll.chart.drawing.AxisExtremumDrawing;
import com.ll.chart.drawing.BOLLDrawing;
import com.ll.chart.drawing.CursorDrawing;
import com.ll.chart.drawing.GridDrawing;
import com.ll.chart.drawing.HighlightDrawing;
import com.ll.chart.drawing.IndicatorsLabelDrawing;
import com.ll.chart.drawing.KDJDrawing;
import com.ll.chart.drawing.MACDDrawing;
import com.ll.chart.drawing.MADrawing;
import com.ll.chart.drawing.RSIDrawing;
import com.ll.chart.drawing.VolumeDrawing;
import com.ll.chart.drawing.candle.CandleDrawing;
import com.ll.chart.drawing.candle.CandleSelectorDrawing;
import com.ll.chart.drawing.depth.DepthDrawing;
import com.ll.chart.drawing.depth.DepthGridDrawing;
import com.ll.chart.drawing.depth.DepthHighlightDrawing;
import com.ll.chart.drawing.depth.DepthSelectorDrawing;
import com.ll.chart.drawing.timeLine.TimeLineDrawing;
import com.ll.chart.entry.ChartEntry;
import com.ll.chart.enumeration.DataDisplayType;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.enumeration.RenderModel;
import com.ll.chart.marker.AxisTextMarker;
import com.ll.chart.marker.GridTextMarker;
import com.ll.chart.module.BOLLChartModule;
import com.ll.chart.module.CandleChartModule;
import com.ll.chart.module.DepthChartModule;
import com.ll.chart.module.RSIChartModule;
import com.ll.chart.module.KDJChartModule;
import com.ll.chart.module.MACDChartModule;
import com.ll.chart.module.TimeLineChartModule;
import com.ll.chart.module.VolumeChartModule;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.module.base.AuxiliaryChartModule;
import com.ll.chart.module.base.MainChartModule;
import com.ll.chart.render.AbsRender;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartLayout extends ConstraintLayout {
  private static final String TAG = "ChartLayout";

  private ChartEntry chartEntry;

  private LinkedHashMap<ModuleType, AbsChartModule> ChartModules;

  private List<ModuleType> enableModuleTypes;

  private DataDisplayType dataDisplayType;

  public ChartLayout(Context context) {
    this(context, null);
  }

  public ChartLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ChartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onFinishInflate() {
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
    candleModule.addDrawing(new CandleDrawing());//蜡烛图组件
    candleModule.addDrawing(new MADrawing());//平均线组件
    candleModule.addDrawing(new AxisDrawing());//x轴组件
    candleModule.addDrawing(new IndicatorsLabelDrawing());//指标文字标签组件
    candleModule.addDrawing(new AxisExtremumDrawing());//Y轴标签组件
    //candleIndex.addDrawing(new EmptyDataDrawing());//空页面组件
    candleModule.setEnable(false);
    this.ChartModules.put(candleModule.getModuleType(), candleModule);

    TimeLineChartModule timeLineModule = new TimeLineChartModule();
    timeLineModule.addDrawing(new TimeLineDrawing());//分时图组件
    timeLineModule.addDrawing(new AxisDrawing());//x轴组件
    timeLineModule.addDrawing(new AxisExtremumDrawing());//Y轴标签组件
    timeLineModule.setEnable(false);
    this.ChartModules.put(timeLineModule.getModuleType(), timeLineModule);

    VolumeChartModule volumeModule = new VolumeChartModule();
    volumeModule.addDrawing(new VolumeDrawing());
    volumeModule.addDrawing(new MADrawing());
    volumeModule.addDrawing(new IndicatorsLabelDrawing());
    volumeModule.addDrawing(new AxisExtremumDrawing());
    this.ChartModules.put(volumeModule.getModuleType(), volumeModule);

    MACDChartModule macdModule = new MACDChartModule();
    macdModule.addDrawing(new MACDDrawing());
    macdModule.addDrawing(new AxisExtremumDrawing());
    macdModule.setEnable(false);
    this.ChartModules.put(macdModule.getModuleType(), macdModule);

    RSIChartModule rsiModule = new RSIChartModule();
    rsiModule.addDrawing(new RSIDrawing());
    rsiModule.addDrawing(new AxisExtremumDrawing());
    rsiModule.setEnable(false);
    this.ChartModules.put(rsiModule.getModuleType(), rsiModule);

    KDJChartModule kdjModule = new KDJChartModule();
    kdjModule.addDrawing(new KDJDrawing());
    kdjModule.addDrawing(new AxisExtremumDrawing());
    kdjModule.setEnable(false);
    this.ChartModules.put(kdjModule.getModuleType(), kdjModule);

    BOLLChartModule bollModule = new BOLLChartModule();
    bollModule.addDrawing(new BOLLDrawing());
    bollModule.addDrawing(new AxisExtremumDrawing());
    bollModule.setEnable(false);
    this.ChartModules.put(bollModule.getModuleType(), bollModule);
  }

  /**
   * 初始化 图表
   */
  private void initChart() {
    for (int i = 0, z = getChildCount(); i < z; i++) {
      View view = getChildAt(i);
      if (!(view instanceof Chart)) {
        continue;
      }
      Chart chart = (Chart) view;
      AbsRender reader = chart.getRender();
      switch (chart.getRenderModel()) {
        case CANDLE://蜡烛图
          HighlightDrawing candleHighlight = new HighlightDrawing();
          candleHighlight.addMarkerView(new GridTextMarker());
          candleHighlight.addMarkerView(new AxisTextMarker());
          reader.addFloatDrawing(candleHighlight);
          reader.addFloatDrawing(new GridDrawing());
          reader.addFloatDrawing(new CandleSelectorDrawing());
          for (Map.Entry<ModuleType, AbsChartModule> item : ChartModules.entrySet()) {
            reader.addChartModule(item.getValue());
          }
          break;
        case DEPTH://深度图
          DepthChartModule depthChartModule = new DepthChartModule();
          depthChartModule.addDrawing(new AxisDrawing());//x轴组件
          depthChartModule.addDrawing(new DepthGridDrawing());//Y轴组件
          depthChartModule.addDrawing(new DepthDrawing());//深度图组件
          DepthHighlightDrawing depthHighlight = new DepthHighlightDrawing();
          depthHighlight.addMarkerView(new GridTextMarker());
          depthHighlight.addMarkerView(new AxisTextMarker());
          reader.addChartModule(depthChartModule);
          reader.addFloatDrawing(depthHighlight);
          reader.addFloatDrawing(new DepthSelectorDrawing());
          chart.setEnableRightRefresh(false);
          chart.setEnableLeftRefresh(false);
          break;
      }
      this.chartEntry.setChart(chart);
    }
  }

  /**
   * 切换图表组件显示状态
   *
   * @param moduleType 类型
   */
  public boolean switchModuleType(ModuleType moduleType) {
    if (!ChartModules.containsKey(moduleType)) {
      return false;
    }
    this.enableModuleTypes.clear();
    AbsChartModule chartModule = ChartModules.get(moduleType);
    if (null == chartModule || chartModule.isEnable()) {
      return false;
    }
    chartModule.setEnable(true);
    Class classType = chartModule instanceof MainChartModule ?
        MainChartModule.class : AuxiliaryChartModule.class;
    for (Map.Entry<ModuleType, AbsChartModule> item : ChartModules.entrySet()) {
      if (item.getValue().isEnable() && classType.isInstance(item.getValue())
          && item.getKey() != moduleType && item.getKey() != ModuleType.VOLUME) {
        item.getValue().setEnable(false);
      } else if (item.getValue().isEnable() && item.getKey() != ModuleType.VOLUME) {
        //记录当前启用的图表组件
        this.enableModuleTypes.add(item.getKey());
      }
    }
    return true;
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
   * 设置图表数据显示模式（1:分页模式，2:实时模式）
   */
  public void setDataDisplayType(DataDisplayType type) {
    if (type == dataDisplayType) {
      return;
    }
    this.dataDisplayType = type;
    for (int i = 0, z = getChildCount(); i < z; i++) {
      View view = getChildAt(i);
      if (view instanceof Chart) {
        Chart chart = (Chart) view;
        if (chart.getRenderModel() == RenderModel.CANDLE) {
          AbsRender reader = chart.getRender();
          if (type == DataDisplayType.REAL_TIME) {
            chart.setEnableRightRefresh(false);//禁用右滑
            reader.getAttribute().rightScrollOffset = 250;  //添加右固定偏移量
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

  public void setConstraintSet(ChartConstraintSet set) {
    set.applyTo(this);
  }
}
