package com.wk.chart;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import com.wk.chart.compat.ChartConstraintSet;
import com.wk.chart.drawing.AxisDrawing;
import com.wk.chart.drawing.BOLLDrawing;
import com.wk.chart.drawing.GridDrawing;
import com.wk.chart.drawing.GridLabelDrawing;
import com.wk.chart.drawing.HighlightDrawing;
import com.wk.chart.drawing.IndicatorsLabelDrawing;
import com.wk.chart.drawing.KDJDrawing;
import com.wk.chart.drawing.MACDDrawing;
import com.wk.chart.drawing.MADrawing;
import com.wk.chart.drawing.RSIDrawing;
import com.wk.chart.drawing.VolumeDrawing;
import com.wk.chart.drawing.candle.CandleDrawing;
import com.wk.chart.drawing.candle.CandleSelectorDrawing;
import com.wk.chart.drawing.depth.DepthDrawing;
import com.wk.chart.drawing.depth.DepthHighlightDrawing;
import com.wk.chart.drawing.depth.DepthSelectorDrawing;
import com.wk.chart.drawing.timeLine.TimeLineDrawing;
import com.wk.chart.entry.ChartEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.marker.XAxisTextMarker;
import com.wk.chart.marker.YAxisTextMarker;
import com.wk.chart.render.AbsRender;
import com.wk.chart.stock.BOLLChartModule;
import com.wk.chart.stock.CandleChartModule;
import com.wk.chart.stock.DepthChartModule;
import com.wk.chart.stock.RSIChartModule;
import com.wk.chart.stock.StockKDJIndex;
import com.wk.chart.stock.StockMACDIndex;
import com.wk.chart.stock.TimeLineChartModule;
import com.wk.chart.stock.VolumeChartModule;
import com.wk.chart.stock.base.AbsChartModule;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartLayout extends ConstraintLayout {
  private static final String TAG = "ChartLayout";

  private ChartEntry chartEntry;

  private LinkedHashMap<ModuleType, AbsChartModule> ChartModules;

  private List<ModuleType> enablemoduleTypes;

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
    this.enablemoduleTypes = new ArrayList<>();
  }

  /**
   * 构建图表组件
   */
  private void initChartModules() {
    this.ChartModules.clear();
    CandleChartModule candleModule = new CandleChartModule(ModuleType.CANDLE);
    candleModule.addDrawing(new CandleDrawing());//蜡烛图组件
    candleModule.addDrawing(new MADrawing());//平均线组件
    candleModule.addDrawing(new AxisDrawing());//x轴组件
    candleModule.addDrawing(new IndicatorsLabelDrawing());//指标文字标签组件
    candleModule.addDrawing(new GridLabelDrawing());//Y轴标签组件
    //candleIndex.addDrawing(new EmptyDataDrawing());//空页面组件
    candleModule.setEnable(false);
    this.ChartModules.put(ModuleType.CANDLE, candleModule);

    TimeLineChartModule timeLineModule = new TimeLineChartModule(ModuleType.TIME);
    timeLineModule.addDrawing(new TimeLineDrawing());//分时图组件
    timeLineModule.addDrawing(new AxisDrawing());//x轴组件
    timeLineModule.addDrawing(new GridLabelDrawing());//Y轴标签组件
    timeLineModule.setEnable(false);
    this.ChartModules.put(ModuleType.TIME, timeLineModule);

    VolumeChartModule volumeModule = new VolumeChartModule(ModuleType.VOLUME);
    volumeModule.addDrawing(new VolumeDrawing());
    volumeModule.addDrawing(new MADrawing());
    volumeModule.addDrawing(new IndicatorsLabelDrawing());
    volumeModule.addDrawing(new GridLabelDrawing());
    this.ChartModules.put(ModuleType.VOLUME, volumeModule);

    StockMACDIndex macdModule = new StockMACDIndex(ModuleType.MACD);
    macdModule.addDrawing(new MACDDrawing());
    macdModule.addDrawing(new GridLabelDrawing());
    macdModule.setEnable(false);
    this.ChartModules.put(ModuleType.MACD, macdModule);

    RSIChartModule rsiModule = new RSIChartModule(ModuleType.RSI);
    rsiModule.addDrawing(new RSIDrawing());
    rsiModule.addDrawing(new GridLabelDrawing());
    rsiModule.setEnable(false);
    this.ChartModules.put(ModuleType.RSI, rsiModule);

    StockKDJIndex kdjModule = new StockKDJIndex(ModuleType.KDJ);
    kdjModule.addDrawing(new KDJDrawing());
    kdjModule.addDrawing(new GridLabelDrawing());
    kdjModule.setEnable(false);
    this.ChartModules.put(ModuleType.KDJ, kdjModule);

    BOLLChartModule bollModule = new BOLLChartModule(ModuleType.BOLL);
    bollModule.addDrawing(new BOLLDrawing());
    bollModule.addDrawing(new GridLabelDrawing());
    bollModule.setEnable(false);
    this.ChartModules.put(ModuleType.BOLL, bollModule);
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
          candleHighlight.addMarkerView(new YAxisTextMarker());
          candleHighlight.addMarkerView(new XAxisTextMarker());
          reader.addFloatDrawing(candleHighlight);
          reader.addFloatDrawing(new GridDrawing());
          reader.addFloatDrawing(new CandleSelectorDrawing());
          for (Map.Entry<ModuleType, AbsChartModule> item : ChartModules.entrySet()) {
            reader.addChartModule(item.getValue());
          }
          break;
        case DEPTH://深度图
          DepthChartModule depthChartModule = new DepthChartModule(ModuleType.DEPTH);
          depthChartModule.addDrawing(new AxisDrawing());//x轴组件
          depthChartModule.addDrawing(new DepthDrawing());//x轴组件
          DepthHighlightDrawing depthHighlight = new DepthHighlightDrawing();
          reader.addChartModule(depthChartModule);
          reader.addFloatDrawing(depthHighlight);
          reader.addFloatDrawing(new GridDrawing());
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
    this.enablemoduleTypes.clear();
    AbsChartModule chartModule = ChartModules.get(moduleType);
    if (chartModule.isEnable()) {
      return false;
    }
    chartModule.setEnable(true);
    for (Map.Entry<ModuleType, AbsChartModule> item : ChartModules.entrySet()) {
      if (chartModule.getChartLevel() == item.getValue().getChartLevel()
          && item.getKey() != moduleType && item.getKey() != ModuleType.VOLUME) {
        item.getValue().setEnable(false);
        //记录当前启用的图表组件
      } else if (item.getValue().isEnable() && item.getKey() != ModuleType.VOLUME) {
        this.enablemoduleTypes.add(item.getKey());
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
    return enablemoduleTypes;
  }

  public void setConstraintSet(ChartConstraintSet set) {
    set.applyTo(this);
  }
}
