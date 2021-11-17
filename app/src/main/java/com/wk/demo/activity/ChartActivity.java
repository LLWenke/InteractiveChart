package com.wk.demo.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import com.wk.chart.ChartLayout;
import com.wk.chart.ChartView;
import com.wk.chart.adapter.CandleAdapter;
import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.ChartConstraintSet;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.config.IndexBuildConfig;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ChartCache;
import com.wk.chart.enumeration.DataType;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.ObserverArg;
import com.wk.chart.enumeration.TimeType;
import com.wk.chart.handler.InteractiveHandler;
import com.wk.chart.interfaces.ICacheLoadListener;
import com.wk.demo.R;
import com.wk.demo.model.ServiceMessage;
import com.wk.demo.service.PushService;
import com.wk.view.indexSetting.IndexManager;
import com.wk.view.indexSetting.IndexSettingActivity;
import com.wk.view.tab.ChartIndexTabLayout;
import com.wk.view.tab.ChartTabLayout;
import com.wk.view.tab.ChartTabListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.wk.demo.util.DataUtils.candleEntries;
import static com.wk.demo.util.DataUtils.depthEntries;

/**
 * <p>MainActivity</p>
 */

public class ChartActivity extends AppCompatActivity implements View.OnClickListener,
        ChartTabListener, ICacheLoadListener, IndexManager.IndexConfigChangeListener {
    public static final String DATA_SHOW_KEY = "DATA_SHOW_KEY";//数据展示类型KEY
    private static final int LEFT_LOADING = 0;//左滑动加载
    private static final int RIGHT_LOADING = 1;//右滑动加载
    private static final int REFRESH_LOADING = 2;//刷新

    private ChartIndexTabLayout chartIndexTabLayout;
    private ChartTabLayout chartTabLayout;
    private ChartLayout chartLayout;
    private ChartView candleChart;
    private ChartView depthChart;
    private ProgressBar candleProgressBar;
    private ProgressBar depthProgressBar;
    private ChartConstraintSet constraintSet;

    private ChartCache mChartCache;

    private int orientation;

    private int loadStartPos = 0;
    private int loadEndPos = 0;
    private int loadCount = 200;
    private CandleAdapter candleAdapter;
    private DepthAdapter depthAdapter;

    private int dataShowType;//数据展示类型

    //数据监视器
    private final Observer dataSetObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            switch ((ObserverArg) arg) {
                case INIT:
                case ATTR_UPDATE:
                case REFRESH:
                    if (dataShowType == DataType.REAL_TIME.ordinal()) {
                        PushService.stopPush();
                        startPush();
                    }
                case ADD:
                case NORMAL:
                    if (null == candleAdapter || candleAdapter.getCount() == 0) {
                        return;
                    }
                    loadComplete(candleProgressBar);
                    loadComplete(depthProgressBar);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataShowType = getIntent().getIntExtra(DATA_SHOW_KEY, DataType.PAGING.ordinal());
        initUI();
        initChart();
        loadBegin(REFRESH_LOADING, candleProgressBar, candleChart);
        loadBegin(REFRESH_LOADING, depthProgressBar, depthChart);
        //延时执行，因为adapter中的异步任务可能与adapter的setBuildConfig中的异步任务冲突而出现无数据的情况，所以暂时使用延时避免，后期再做优化，（如果接入的是网络数据，则没有问题，因为网络请求自带延迟）
        new Handler().postDelayed(() -> {
            if (isFinishing()) {
                return;
            }
            recoveryChartState();
        }, 1000);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
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
        startIntent.putExtra("scale", candleAdapter.getScale());
        startIntent.putExtra("open", lastEntry.getOpen().value);
        startIntent.putExtra("high", lastEntry.getHigh().value);
        startIntent.putExtra("low", lastEntry.getLow().value);
        startIntent.putExtra("close", lastEntry.getClose().value);
        startIntent.putExtra("volume", lastEntry.getVolume().value);
        startIntent.putExtra("time", lastEntry.getTime().getTime());
//        this.startService(startIntent);
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
        this.chartTabLayout = findViewById(R.id.chart_tab_view);
        this.chartIndexTabLayout = findViewById(R.id.chart_index_tab_view);
        chartLayout = findViewById(R.id.chart_layout);
        candleChart = findViewById(R.id.candle_chart);
        depthChart = findViewById(R.id.depth_chart);
        candleProgressBar = findViewById(R.id.candle_loading_bar);
        depthProgressBar = findViewById(R.id.depth_loading_bar);
        this.constraintSet = new ChartConstraintSet(chartLayout);
        this.chartLayout.setICacheLoadListener(this);
        if (null != chartTabLayout) {
            this.chartTabLayout.setChartTabListener(this);
        }
        if (null != chartIndexTabLayout) {
            this.chartIndexTabLayout.setChartTabListener(this);
        }
        IndexManager.INSTANCE.addIndexBuildConfigChangeListener(this);
    }

    private void initChart() {
        if (null == depthAdapter) {
            this.depthAdapter = new DepthAdapter(4, 4, "BTC", "USDT");
        }
        this.depthChart.setAdapter(depthAdapter);
        if (null == candleAdapter) {
            this.candleAdapter = new CandleAdapter(new IndexBuildConfig(IndexManager.INSTANCE.getIndexConfigs(this)));
            this.candleAdapter.setScale(4, 4);
        }
        if (dataShowType == DataType.REAL_TIME.ordinal()) {
            this.chartLayout.setDataDisplayType(DataType.REAL_TIME);
        }
        this.candleAdapter.registerDataSetObserver(dataSetObserver);
        this.candleChart.setAdapter(candleAdapter);

        this.candleChart.setInteractiveHandler(new InteractiveHandler() {
            @Override
            public void onLeftRefresh(AbsEntry firstData) {
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

            @Override
            public void onRightRefresh(AbsEntry lastData) {
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
        }
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        } else {
            finish();
        }
    }

    /**
     * 恢复图表状态
     */
    private void recoveryChartState() {
        if (null == mChartCache) {
            TimeType timeType = TimeType.day;
            chartTabLayout.checkedDefaultTimeType(timeType, ModuleType.CANDLE);
            chartTabLayout.checkedDefaultIndexType(chartLayout.getNowIndexType(ModuleGroupType.MAIN), ModuleGroupType.MAIN);
            chartTabLayout.checkedDefaultIndexType(chartLayout.getNowIndexType(ModuleGroupType.INDEX), ModuleGroupType.INDEX);
            if (null != chartIndexTabLayout) {
                chartIndexTabLayout.checkedDefaultIndexType(chartLayout.getNowIndexType(ModuleGroupType.INDEX), ModuleGroupType.INDEX);
            }
            switchTimeType(timeType, ModuleType.CANDLE);
        } else {
            chartLayout.loadChartCache(mChartCache);
        }
    }

    /**
     * 保存图表状态
     */
    private void saveChartState() {
        mChartCache = chartLayout.chartCache();
    }

    /**
     * 切换图表组件数据显示类型
     *
     * @param timeType 类型
     */
    private void switchTimeType(TimeType timeType, @ModuleType int moduleType) {
        loadBegin(REFRESH_LOADING, candleProgressBar, candleChart);
        candleAdapter.resetData(timeType,
                dataShowType == DataType.REAL_TIME.ordinal() ?
                        getNewestData(500) : getInit());
        if (depthAdapter.getCount() == 0) {
            depthAdapter.resetData(depthEntries);
        }
        if (chartLayout.switchModuleType(moduleType, ModuleGroupType.MAIN)) {
            candleChart.onViewInit();
        }
    }

    private List<CandleEntry> getInit() {
        this.loadStartPos = candleEntries.size() / 2;
        this.loadEndPos = loadStartPos + loadCount;
        this.loadEndPos = Math.min(loadEndPos, candleEntries.size());
        List<CandleEntry> set = new ArrayList<>();
        for (int i = loadStartPos; i < loadEndPos; i++) {
            set.add(candleEntries.get(i));
        }
        return set;
    }

    private List<CandleEntry> getHeader() {
        int end = loadStartPos;
        this.loadStartPos = loadStartPos - loadCount;
        this.loadStartPos = Math.max(loadStartPos, 0);
        List<CandleEntry> entries = new ArrayList<>();
        for (int i = loadStartPos; i < end; i++) {
            entries.add(candleEntries.get(i));
        }
        return entries;
    }

    private List<CandleEntry> getFooter() {
        int start = loadEndPos;
        this.loadEndPos = loadEndPos + loadCount;
        this.loadEndPos = Math.min(loadEndPos, candleEntries.size());
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
    public void loadBegin(int loadingType, ProgressBar bar, ChartView chart) {
        this.constraintSet.setVisibility(bar.getId(), VISIBLE);
        this.constraintSet.connect(bar.getId(), ConstraintSet.START, chart.getId(),
                ConstraintSet.START, Utils.dp2px(this, 30));
        this.constraintSet.connect(bar.getId(), ConstraintSet.END, chart.getId(),
                ConstraintSet.END, Utils.dp2px(this, 30));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IndexManager.INSTANCE.removeIndexBuildConfigChangeListener(this);
        PushService.stopPush();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onLoadCacheTypes(TimeType timeType, boolean isLoadData, List<com.wk.chart.entry.ChartCache.TypeEntry> types) {
        int mainModuleType = ModuleType.CANDLE;
        for (ChartCache.TypeEntry entry : types) {
            if (entry.getModuleGroupType() == ModuleGroupType.MAIN) {
                mainModuleType = entry.getModuleType();
            }
            chartTabLayout.checkedDefaultIndexType(entry.getIndexType(), entry.getModuleGroupType());
            if (null != chartIndexTabLayout) {
                chartIndexTabLayout.checkedDefaultIndexType(entry.getIndexType(), entry.getModuleGroupType());
            }
        }
        chartTabLayout.checkedDefaultTimeType(timeType, mainModuleType);
        if (isLoadData) {
            onTimeTypeChange(timeType, mainModuleType);
        }
    }

    @Override
    public void onIndexConfigChange() {
        candleAdapter.setBuildConfig(new IndexBuildConfig(IndexManager.INSTANCE.getIndexConfigs(this)));
    }

    @Override
    public void onTimeTypeChange(@NotNull TimeType type, int moduleType) {
        switchTimeType(type, moduleType);
    }

    @Override
    public void onIndexTypeChange(int indexType, int moduleGroupType) {
        chartLayout.switchIndexType(indexType, moduleGroupType);
        candleChart.onViewInit();
    }

    @Override
    public void onOrientationChange() {
        if (isLand()) {
            setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        }
    }

    @Override
    public void onSetting() {
        IndexManager.INSTANCE.setIndexBuildConfig(candleAdapter.getBuildConfig().getDefaultIndexConfig());
        startActivityForResult(new Intent(this, IndexSettingActivity.class), 999);
    }

    private Boolean isLand() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

}
