package com.wk.chart.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wk.chart.calculate.CandleDataCalculate;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.config.IndexBuildConfig;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.PushType;
import com.wk.chart.enumeration.TimeType;
import com.wk.utils.DateUtils;

import java.util.Date;
import java.util.List;

public class CandleAdapter extends AbsAdapter<CandleEntry, IndexBuildConfig> {
    private final CandleDataCalculate calculate;//数据计算类
    private TimeType timeType = null;//显示模式

    public CandleAdapter() {
        this(new IndexBuildConfig());
    }

    public CandleAdapter(IndexBuildConfig indexBuildConfig) {
        super(indexBuildConfig);
        this.calculate = new CandleDataCalculate();
    }

    public CandleAdapter(CandleAdapter adapter) {
        super(adapter);
        this.timeType = adapter.timeType;
        this.calculate = adapter.calculate;
    }

    /**
     * 获取时间类型
     */
    public @Nullable
    TimeType getTimeType() {
        return timeType;
    }

    /**
     * 是否需要加载数据
     *
     * @return true 需要  false 不需要
     */
    public boolean isNeedLoadData(@Nullable TimeType timeType) {
        return null == this.timeType || this.timeType != timeType || getCount() <= 0;
    }

    /**
     * 在给定的范围内，计算最大值和最小值
     */
    @Override
    public void calculateMinAndMax(int start, int end) {
        minYIndex = maxYIndex = start;
        CandleEntry minEntry = getItem(minYIndex);
        CandleEntry maxEntry = getItem(maxYIndex);
        for (int i = start + 1; i < end; i++) {
            CandleEntry entry = getItem(i);
            if (entry.getLow().value < minEntry.getLow().value) {
                minYIndex = i;
                minEntry = entry;
            }
            if (entry.getHigh().value > maxEntry.getHigh().value) {
                maxYIndex = i;
                maxEntry = entry;
            }
        }
    }

    /**
     * 清空数据
     */
    @Override
    public synchronized void clearData() {
        timeType = null;
        super.clearData();
    }

    /**
     * 刷新数据
     */
    public synchronized void setData(TimeType type, List<CandleEntry> data) {
        if (null == type) return;
        stopAnimator();
        stopAsyTask();
        if (type == timeType) {
            super.updateData(data);
        } else {
            this.timeType = type;
            super.resetData(data);
        }
    }

    /**
     * 向头部添加一组数据
     */
    @Override
    public synchronized void addHeaderData(List<CandleEntry> data) {
        if (null == timeType) return;
        if (!Utils.listIsEmpty(data)) {
            stopAnimator();
        }
        super.addHeaderData(data);
    }

    /**
     * 向尾部添加一组数据
     */
    @Override
    public synchronized void addFooterData(List<CandleEntry> data) {
        if (null == timeType) return;
        if (!Utils.listIsEmpty(data)) {
            stopAnimator();
        }
        super.addFooterData(data);
    }

    /**
     * 数据推送
     */
    public PushType dataPush(CandleEntry data) {
        if (null == data || null == timeType || getCount() == 0) {
            return PushType.INVALID;
        }
        Date endDate = data.getTime();
        if (null == endDate) {
            return PushType.INVALID;
        }
        PushType pushType = getPushType(endDate);
        switch (pushType) {
            case UPDATE://修改
                changeItem(getLastPosition(), data);
                break;
            case ADD://添加
                addFooterData(data);
                break;
        }
        return pushType;
    }

    @Override
    public void changeItem(int position, CandleEntry data) {
        if (null == timeType) return;
        CandleEntry entry = getItem(position);
        if (null == entry) return;
        data.setMarkerPointType(entry.getMarkerPointType());
        super.changeItem(position, data);
    }

    /**
     * 判断数据是更新还是追加
     */
    private PushType getPushType(Date endDate) {
        long diff = getDateDiff(getItem(getLastPosition()).getTime(), endDate);
        if (null == timeType || diff < 0) {
            return PushType.INVALID;//无效
        } else if (diff < timeType.value()) {
            return PushType.UPDATE;//修改
        } else if (diff == timeType.value()) {
            return PushType.ADD;//添加
        } else {
            return PushType.INTERMITTENT;//间断
        }
    }

    /**
     * 获取时间间隔
     */
    private long getDateDiff(Date startDate, Date endDate) {
        if (null == timeType) {
            return -1;
        } else if (timeType == TimeType.MONTH) {//月
            return DateUtils.getMonthDiff(startDate, endDate);
        } else {
            return DateUtils.getDateDiff(startDate, endDate, timeType.msec());
        }
    }

    /**
     * 构建数据
     */
    @Override
    void buildData(@NonNull IndexBuildConfig buildConfig, @NonNull List<CandleEntry> data, int startPosition) {
        //构建指标配置参数
        buildConfig.buildIndexFlags(getValueFormatter());
        //构建数据基础属性值
        buildValueScale(data, startPosition);
        buildTimeText(data, startPosition);
        //计算 MA EMA MACD BOLL SAR RSI KDJ WR 指标
        calculate.calculateMA(data, buildConfig, getValueFormatter(), getScale(), startPosition);
        calculate.calculateEMA(data, buildConfig, getValueFormatter(), getScale(), startPosition);
        calculate.calculateMACD(data, buildConfig, getValueFormatter(), getScale(), startPosition);
        calculate.calculateBOLL(data, buildConfig, getValueFormatter(), getScale(), startPosition);
        calculate.calculateSAR2(data, buildConfig, getValueFormatter(), getScale(), startPosition);
        calculate.calculateRSI(data, buildConfig, getValueFormatter(), getScale(), startPosition);
        calculate.calculateKDJ(data, buildConfig, getValueFormatter(), getScale(), startPosition);
        calculate.calculateWR(data, buildConfig, getValueFormatter(), getScale(), startPosition);
    }

    /**
     * 构建值精度
     */
    private void buildValueScale(@NonNull List<CandleEntry> data, int startPosition) {
        for (int i = startPosition, z = data.size(); i < z; i++) {
            data.get(i).buildValueScale(getScale(), getValueFormatter());
        }
    }

    /**
     * 构建时间显示文字
     */
    private void buildTimeText(@NonNull List<CandleEntry> data, int startPosition) {
        for (int i = startPosition, z = data.size(); i < z; i++) {
            if (null == timeType) break;
            data.get(i).buildTimeText(timeType, getDateFormatter());
        }
    }
}
