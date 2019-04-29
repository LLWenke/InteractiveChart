package com.ll.demo.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.ll.chart.Chart;
import com.ll.chart.ChartLayout;
import com.ll.chart.adapter.CandleAdapter;
import com.ll.chart.adapter.DepthAdapter;
import com.ll.chart.compat.ChartConstraintSet;
import com.ll.chart.compat.Utils;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.enumeration.DataDisplayType;
import com.ll.chart.enumeration.DisplayType;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.enumeration.ObserverArg;
import com.ll.chart.handler.InteractiveHandler;
import com.ll.demo.R;
import com.ll.demo.model.ChartCache;
import com.ll.demo.model.ServiceMessage;
import com.ll.demo.service.PushService;
import com.ll.demo.view.FontCheckBox;
import com.ll.demo.view.FontRadioButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.ll.demo.util.DataUtils.candleEntries;
import static com.ll.demo.util.DataUtils.depthEntries;

/**
 * <p>MainActivity</p>
 */

public class ChartActivity extends AppCompatActivity
    implements View.OnClickListener {
  public static final String DATA_SHOW_KEY = "DATA_SHOW_KEY";//数据展示类型KEY
  private static final int LEFT_LOADING = 0;//左滑动加载
  private static final int RIGHT_LOADING = 1;//右滑动加载
  private static final int REFRESH_LOADING = 2;//刷新

  private ChartLayout chartLayout;
  private Chart candleChart;
  private Chart depthChart;
  private ProgressBar candleProgressBar;
  private ProgressBar depthProgressBar;
  private ChartConstraintSet constraintSet;

  private FontRadioButton rbTimeLine;
  private FontRadioButton rbOneMinute;
  private FontRadioButton rbFifteenMinute;
  private FontRadioButton rbOneHour;
  private FontRadioButton rbSixHour;
  private FontRadioButton rbOneDay;

  private LinearLayout foldingMenu;
  private FontCheckBox foldingMenuBtn;
  private FontRadioButton macdBtn;
  private FontRadioButton rsiBtn;
  private FontRadioButton kdjBtn;
  private FontRadioButton bollBtn;

  private int orientation;
  private ChartCache chartCache;

  private int loadStartPos = 0;
  private int loadEndPos = 0;
  private int loadCount = 200;
  private CandleAdapter candleAdapter;
  private DepthAdapter depthAdapter;

  private int dataShowType;//数据展示类型

  //数据监视器
  private Observer dataSetObserver = new Observer() {
    @Override public void update(Observable o, Object arg) {
      switch ((ObserverArg) arg) {
        case init:
          loadComplete(candleProgressBar);
          loadComplete(depthProgressBar);
          if (dataShowType == DataDisplayType.REAL_TIME.ordinal()) {
            PushService.stopPush();
            startPush();
          }
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.chartCache = new ChartCache();
    this.dataShowType = getIntent().getIntExtra(DATA_SHOW_KEY, DataDisplayType.PAGING.ordinal());
    initUI();
    initChart();
    loadBegin(REFRESH_LOADING, candleProgressBar, candleChart);
    loadBegin(REFRESH_LOADING, depthProgressBar, depthChart);
    loadData();
    recoveryChartState();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.orientation != orientation) {
      saveChartState();
      initUI();
      initChart();
      recoveryChartState();
      this.orientation = getResources().getConfiguration().orientation;
    }
  }

  private void startPush() {
    EventBus.getDefault().register(this);
    Intent startIntent = new Intent(this, PushService.class);
    CandleEntry lastEntry = candleAdapter.getItem(candleAdapter.getLastPosition());
    startIntent.putExtra("scale", lastEntry.getScale());
    startIntent.putExtra("open", lastEntry.getOpen().value);
    startIntent.putExtra("high", lastEntry.getHigh().value);
    startIntent.putExtra("low", lastEntry.getLow().value);
    startIntent.putExtra("close", lastEntry.getClose().value);
    startIntent.putExtra("volume", lastEntry.getVolume().value);
    startIntent.putExtra("time", lastEntry.getTime().getTime());
    this.startService(startIntent);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onPush(ServiceMessage msg) {
    if (null == msg) {
      return;
    }
    switch (msg.getWhat()) {
      case PushService.CANDLE:
        CandleEntry candleEntry = (CandleEntry) msg.getEntry();
        if (null != candleEntry) {
          candleAdapter.dataPush(candleEntry);
        }
        break;

      case PushService.DEPTH:

        break;
    }
  }

  /**
   * 初始化UI
   */
  private void initUI() {
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      setContentView(R.layout.activity_chart_land);
    } else if (getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT) {
      setContentView(R.layout.activity_chart_port);
    }
    this.rbTimeLine = findViewById(R.id.rb_time_line);
    this.rbOneMinute = findViewById(R.id.rb_one_minute);
    this.rbFifteenMinute = findViewById(R.id.rb_fifteen_minute);
    this.rbOneHour = findViewById(R.id.rb_one_hour);
    this.rbSixHour = findViewById(R.id.rb_six_hour);
    this.rbOneDay = findViewById(R.id.rb_one_day);
    this.foldingMenuBtn = findViewById(R.id.folding_menu_btn);
    this.macdBtn = findViewById(R.id.macd_btn);
    this.rsiBtn = findViewById(R.id.rsi_btn);
    this.kdjBtn = findViewById(R.id.kdj_btn);
    this.bollBtn = findViewById(R.id.boll_btn);
    this.foldingMenu = findViewById(R.id.folding_menu);
    ImageView btnDirection = findViewById(R.id.btn_direction);

    chartLayout = findViewById(R.id.chart_layout);
    candleChart = findViewById(R.id.candle_chart);
    depthChart = findViewById(R.id.depth_chart);
    candleProgressBar = findViewById(R.id.candle_loading_bar);
    depthProgressBar = findViewById(R.id.depth_loading_bar);

    this.constraintSet = new ChartConstraintSet(chartLayout);

    this.rbTimeLine.setOnClickListener(this);
    this.rbOneMinute.setOnClickListener(this);
    this.rbFifteenMinute.setOnClickListener(this);
    this.rbOneHour.setOnClickListener(this);
    this.rbSixHour.setOnClickListener(this);
    this.rbOneDay.setOnClickListener(this);
    btnDirection.setOnClickListener(this);
    this.foldingMenuBtn.setOnClickListener(this);
    this.macdBtn.setOnClickListener(this);
    this.rsiBtn.setOnClickListener(this);
    this.kdjBtn.setOnClickListener(this);
    this.bollBtn.setOnClickListener(this);
  }

  private void initChart() {
    if (null == depthAdapter) {
      this.depthAdapter = new DepthAdapter("BTC",
          "USDT", 2);
      this.depthAdapter.setScale(4);
    }
    this.depthChart.setAdapter(depthAdapter);
    if (null == candleAdapter) {
      this.candleAdapter = new CandleAdapter();
      this.candleAdapter.setScale(4);
    }
    if (dataShowType == DataDisplayType.REAL_TIME.ordinal()) {
      this.chartLayout.setDataDisplayType(DataDisplayType.REAL_TIME);
    }
    this.candleAdapter.registerDataSetObserver(dataSetObserver);
    this.candleChart.setAdapter(candleAdapter);

    this.candleChart.setInteractiveHandler(new InteractiveHandler() {
      @Override public void onLeftRefresh(AbsEntry firstData) {
        loadBegin(LEFT_LOADING, candleProgressBar, candleChart);
        // 模拟耗时
        candleChart.postDelayed(() -> {
          List<CandleEntry> entries = getHeader();
          candleAdapter.addHeaderData(entries);
          if (entries.size() == 0) {
            Toast.makeText(ChartActivity.this, "已经到达最左边了", LENGTH_SHORT).show();
          }
          loadComplete(candleProgressBar);
        }, 1000);
      }

      @Override public void onRightRefresh(AbsEntry lastData) {
        loadBegin(RIGHT_LOADING, candleProgressBar, candleChart);
        // 模拟耗时
        candleChart.postDelayed(() -> {
          List<CandleEntry> entries = getFooter();
          candleAdapter.addFooterData(entries);
          if (entries.size() == 0) {
            Toast.makeText(ChartActivity.this, "已经到达最右边了", LENGTH_SHORT).show();
          }
          loadComplete(candleProgressBar);
        }, 1000);
      }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.folding_menu_btn:
        this.foldingMenu.setVisibility(foldingMenuBtn.isChecked() ? VISIBLE : GONE);
        break;
      case R.id.macd_btn:
        switchModuleType(ModuleType.MACD);
        break;
      case R.id.rsi_btn:
        switchModuleType(ModuleType.RSI);
        break;
      case R.id.kdj_btn:
        switchModuleType(ModuleType.KDJ);
        break;
      case R.id.boll_btn:
        switchModuleType(ModuleType.BOLL);
        break;
      case R.id.rb_time_line:
        switchModuleType(ModuleType.TIME);
        break;
      case R.id.rb_one_minute:
        Toast.makeText(this, "点击了一分钟按钮", LENGTH_SHORT).show();
        switchModuleType(ModuleType.CANDLE);
        break;
      case R.id.rb_fifteen_minute:
        Toast.makeText(this, "点击了十五分钟按钮", LENGTH_SHORT).show();
        switchModuleType(ModuleType.CANDLE);
        break;
      case R.id.rb_one_hour:
        Toast.makeText(this, "点击了一小时按钮", LENGTH_SHORT).show();
        switchModuleType(ModuleType.CANDLE);
        break;
      case R.id.rb_six_hour:
        Toast.makeText(this, "点击了六小时按钮", LENGTH_SHORT).show();
        switchModuleType(ModuleType.CANDLE);
        break;
      case R.id.rb_one_day:
        Toast.makeText(this, "点击了一天按钮", LENGTH_SHORT).show();
        switchModuleType(ModuleType.CANDLE);
        break;
      case R.id.btn_direction:
        setRequestedOrientation(getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        break;
    }
  }

  /**
   * 切换图表组件显示状态
   *
   * @param moduleType 类型
   */
  private void switchModuleType(ModuleType moduleType) {
    if (chartLayout.switchModuleType(moduleType)) {
      candleChart.onViewChanged();
    }
  }

  @Override public void onBackPressed() {
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
    } else {
      finish();
    }
  }

  /**
   * 保存图表状态
   */
  private void saveChartState() {
    this.chartCache.enableModuleTypes = chartLayout.getEnableModuleTypes();
    this.chartCache.scale = candleChart.getRender().getAttribute().currentScale;
    this.chartCache.beginPosition = candleChart.getRender().getBegin();
    CandleAdapter adapter = (CandleAdapter) candleChart.getRender().getAdapter();
    if (null != adapter && !rbTimeLine.isChecked()) {
      this.chartCache.displayType = adapter.getDisplayType();
    } else {
      this.chartCache.displayType = null;
    }
  }

  /**
   * 恢复图表状态
   */
  private void recoveryChartState() {
    if (!Utils.listIsEmpty(chartCache.enableModuleTypes)) {
      for (ModuleType item : chartCache.enableModuleTypes) {
        switch (item) {
          case TIME:
            rbTimeLine.setChecked(true);
            chartLayout.switchModuleType(ModuleType.TIME);
            break;
          case MACD:
          default:
            macdBtn.setChecked(true);
            chartLayout.switchModuleType(ModuleType.MACD);
            break;
          case RSI:
            rsiBtn.setChecked(true);
            chartLayout.switchModuleType(ModuleType.RSI);
            break;
          case KDJ:
            kdjBtn.setChecked(true);
            chartLayout.switchModuleType(ModuleType.KDJ);
            break;
          case BOLL:
            bollBtn.setChecked(true);
            chartLayout.switchModuleType(ModuleType.BOLL);
            break;
        }
      }
      candleChart.onViewChanged();
    } else {
      rbOneHour.setChecked(true);
      macdBtn.setChecked(true);
      chartLayout.switchModuleType(ModuleType.CANDLE);
      chartLayout.switchModuleType(ModuleType.MACD);
      candleChart.onViewChanged();
    }
    if (null != chartCache.displayType) {
      FontRadioButton button;
      switch (chartCache.displayType) {
        case oneMinute://一分钟
          button = rbOneMinute;
          break;
        case fifteenMinute://十五分钟
          button = rbFifteenMinute;
          break;
        case oneHour://一小时
        default:
          button = rbOneHour;
          break;
        case sixHour://六小时
          button = rbSixHour;
          break;
        case oneDay://一天
          button = rbOneDay;
          break;
      }
      button.setChecked(true);
      chartLayout.switchModuleType(ModuleType.CANDLE);
    }
    candleChart.getRender().getAttribute().currentScale = chartCache.scale;
    this.candleChart.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            candleChart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            candleChart.getRender().toTransX(chartCache.beginPosition);
          }
        });
  }

  private void loadData() {
    candleAdapter.resetData(DisplayType.oneDay,
        dataShowType == DataDisplayType.REAL_TIME.ordinal() ?
            getNewestData(500) : getInit());
    depthAdapter.resetData(depthEntries);
  }

  private List<CandleEntry> getInit() {
    this.loadStartPos = candleEntries.size() / 2;
    this.loadEndPos = loadStartPos + loadCount;
    this.loadEndPos = loadEndPos > candleEntries.size() ? candleEntries.size() : loadEndPos;
    List<CandleEntry> set = new ArrayList<>();
    for (int i = loadStartPos; i < loadEndPos; i++) {
      set.add(candleEntries.get(i));
    }
    return set;
  }

  private List<CandleEntry> getHeader() {
    int end = loadStartPos;
    this.loadStartPos = loadStartPos - loadCount;
    this.loadStartPos = loadStartPos < 0 ? 0 : loadStartPos;
    List<CandleEntry> entries = new ArrayList<>();
    for (int i = loadStartPos; i < end; i++) {
      entries.add(candleEntries.get(i));
    }
    return entries;
  }

  private List<CandleEntry> getFooter() {
    int start = loadEndPos;
    this.loadEndPos = loadEndPos + loadCount;
    this.loadEndPos = loadEndPos > candleEntries.size() ? candleEntries.size() : loadEndPos;
    List<CandleEntry> entries = new ArrayList<>();
    for (int i = start; i < loadEndPos; i++) {
      entries.add(candleEntries.get(i));
    }
    return entries;
  }

  /**
   * 获取最新数据
   *
   * @return 数据
   */
  private List<CandleEntry> getNewestData(int loadCount) {
    List<CandleEntry> entries = new ArrayList<>();
    this.loadStartPos = candleEntries.size() > loadCount ? candleEntries.size() - loadCount : 0;
    this.loadEndPos = candleEntries.size();
    for (int i = loadStartPos; i < loadEndPos; i++) {
      entries.add(candleEntries.get(i));
    }
    return entries;
  }

  /**
   * 数据开始加载
   *
   * @param loadingType 加载框出现类型
   */
  public void loadBegin(int loadingType, ProgressBar bar, Chart chart) {
    this.constraintSet.setVisibility(bar.getId(), VISIBLE);
    this.constraintSet.connect(bar.getId(), ConstraintSet.START, chart.getId(),
        ConstraintSet.START, Utils.dpTopx(this, 30));
    this.constraintSet.connect(bar.getId(), ConstraintSet.END, chart.getId(),
        ConstraintSet.END, Utils.dpTopx(this, 30));
    switch (loadingType) {
      case LEFT_LOADING:
        this.constraintSet.clear(bar.getId(), ConstraintSet.END);
        break;
      case RIGHT_LOADING:
        this.constraintSet.clear(bar.getId(), ConstraintSet.START);
        break;
    }
    this.chartLayout.setConstraintSet(constraintSet);
  }

  /**
   * 数据加载完毕
   */
  public void loadComplete(ProgressBar bar) {
    this.constraintSet.setVisibility(bar.getId(), View.INVISIBLE);
    this.chartLayout.setConstraintSet(constraintSet);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    PushService.stopPush();
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }
}
