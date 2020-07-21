package com.wk.chart.adapter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.wk.chart.compat.DateUtil;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.config.IndicatorBuildConfig;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.IndicatorTagEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.DisplayType;
import com.wk.chart.enumeration.IndicatorType;
import com.wk.chart.enumeration.PushType;
import com.wk.chart.module.base.AbsChartModule;

import java.util.Date;
import java.util.List;

public class CandleAdapter extends AbsAdapter<CandleEntry, IndicatorBuildConfig> {

    private CalculationCache calculationCache = new CalculationCache();//计算结果缓存类
    private DisplayType displayType = DisplayType.oneHour;//显示模式

    public CandleAdapter() {
        super(new IndicatorBuildConfig().buildDefaultConfig());
    }

    public CandleAdapter(CandleAdapter adapter) {
        super(adapter);
        this.displayType = adapter.displayType;
        this.calculationCache = adapter.calculationCache;
    }

    /**
     * 获取显示模式
     */
    public DisplayType getDisplayType() {
        return displayType;
    }

    /**
     * 在给定的范围内，计算最大值和最小值
     */
    @Override
    public void computeMinAndMax(int start, int end, List<AbsChartModule<? extends AbsEntry>> chartModules) {
        low = Float.MAX_VALUE;
        high = -Float.MAX_VALUE;

        for (AbsChartModule item : chartModules) {
            if (item.isEnable()) {
                item.resetMinMax();
            }
        }
        for (int i = start; i < end; i++) {
            CandleEntry entry = getItem(i);
            if (entry.getLow().value < low) {
                low = entry.getLow().value;
                minYIndex = i;
            }
            if (entry.getHigh().value > high) {
                high = entry.getHigh().value;
                maxYIndex = i;
            }
            for (AbsChartModule item : chartModules) {
                if (item.isEnable()) {
                    item.computeMinMax(entry);
                }
            }
        }
    }

    @Override
    public void setBuildConfig(IndicatorBuildConfig buildConfig) {
        stopAnimator();
        this.calculationCache.init();
        super.setBuildConfig(buildConfig);
    }

    /**
     * 刷新数据
     */
    public synchronized void resetData(DisplayType type, List<CandleEntry> data) {
        if (type == null) {
            return;
        }
        stopAnimator();
        this.displayType = type;
        this.calculationCache.init();
        super.resetData(data);
    }

    /**
     * 向头部添加一组数据
     */
    @Override
    public synchronized void addHeaderData(List<CandleEntry> data) {
        if (!Utils.listIsEmpty(data)) {
            stopAnimator();
            this.calculationCache.init();
        }
        super.addHeaderData(data);
    }

    /**
     * 向尾部添加一组数据
     */
    @Override
    public synchronized void addFooterData(List<CandleEntry> data) {
        if (!Utils.listIsEmpty(data)) {
            stopAnimator();
            this.calculationCache.init();
        }
        super.addFooterData(data);
    }

    /**
     * 数据推送
     */
    public PushType dataPush(CandleEntry data) {
        if (null == data) {
            return PushType.INVALID;
        }
        if (getCount() == 0) {
            addFooterData(data);
            return PushType.ADD;
        }
        Date endDate = data.getTime();
        if (isWorking() || null == endDate) {
            return PushType.INVALID;
        }
        this.calculationCache.index = getCount() - 1;
        PushType pushType = getPushType(endDate);
        switch (pushType) {
            case UPDATE://修改
                changeItem(getCount() - 1, data);
                break;
            case ADD://添加
                addFooterData(data);
                break;
        }
        Log.e("dataPush-->" + pushType, data.toString());
        return pushType;
    }

    @Override
    public void changeItem(int position, CandleEntry data) {
        CandleEntry entry = getItem(position);
        if (null == entry) {
            return;
        }
        data.setMarkerPointType(entry.getMarkerPointType());
        super.changeItem(position, data);
    }

    /**
     * 判断数据是更新还是追加
     */
    private PushType getPushType(Date endDate) {
        long diff = getDateDiff(getItem(getLastPosition()).getTime(), endDate);
        //Log.e("lastTime:", DisplayTypeUtils.selectorFormat(getItem(getLastPosition()).getTime(),
        //    getInstance()));
        //Log.e("endDate:", DisplayTypeUtils.selectorFormat(endDate,
        //    getInstance()));
        if (diff < 0) {
            return PushType.INVALID;
        } else if (diff < displayType.value()) {
            return PushType.UPDATE;//修改
        } else if (diff == displayType.value()) {
            return PushType.ADD;//添加
        } else {
            return PushType.INTERMITTENT;//间断
        }
    }

    /**
     * 获取时间间隔
     */
    private long getDateDiff(Date startDate, Date endDate) {
        switch (displayType) {
            case month://月
                return DateUtil.getMonthDiff(startDate, endDate);
            default:
                return DateUtil.getDateDiff(startDate, endDate, displayType.msec());
        }
    }

    /**
     * 构建数据
     */
    @Override
    void buildData(@NonNull IndicatorBuildConfig buildConfig, @NonNull List<CandleEntry> data) {
        //计算 MA MACD BOLL RSI KDJ MR 指标
        computeMA(data, buildConfig);
        computeMACD(data, buildConfig);
        computeBOLL(data, buildConfig);
        computeRSI(data, buildConfig);
        computeKDJ(data, buildConfig);
        calculateWR(data, buildConfig);
    }

    /**
     * 计算 MA
     */
    private void computeMA(@NonNull List<CandleEntry> data, @NonNull IndicatorBuildConfig indicatorConfig) {
        IndicatorTagEntry candleIndicatorTag = indicatorConfig.getIndicatorTags(IndicatorType.CANDLE_MA);
        IndicatorTagEntry volumeIndicatorTag = indicatorConfig.getIndicatorTags(IndicatorType.VOLUME_MA);
        int candleIndicatorCount = null == candleIndicatorTag ? 0 : candleIndicatorTag.getFlagEntries().length;
        int volumeIndicatorCount = null == volumeIndicatorTag ? 0 : volumeIndicatorTag.getFlagEntries().length;
        long[] candleMA = null == calculationCache.candleMA ? new long[candleIndicatorCount] : calculationCache.candleMA.clone();
        long[] volumeMA = null == calculationCache.volumeMA ? new long[volumeIndicatorCount] : calculationCache.volumeMA.clone();

        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            //计算蜡烛图MA值
            ValueEntry[] candleValues = new ValueEntry[candleIndicatorCount];
            for (int j = 0; j < candleIndicatorCount; j++) {
                candleMA[j] += entry.getClose().result;
                int flag = candleIndicatorTag.getFlagEntries()[j].getFlag();
                if (i >= flag) {
                    candleMA[j] -= data.get(i - flag).getClose().result;
                    candleValues[j] = entry.recoveryQuoteScaleValue(candleMA[j] / flag);
                }
            }
            //存储此次计算结果
            entry.putIndicator(IndicatorType.CANDLE_MA, candleValues);
            //计算交易量图MA值
            ValueEntry[] volumeValues = new ValueEntry[volumeIndicatorCount];
            for (int j = 0; j < volumeIndicatorCount; j++) {
                volumeMA[j] += entry.getVolume().result;
                int flag = volumeIndicatorTag.getFlagEntries()[j].getFlag();
                if (i >= flag) {
                    volumeMA[j] -= data.get(i - flag).getVolume().result;
                    volumeValues[j] = entry.recoveryBaseScaleValue(volumeMA[j] / flag);
                }
            }
            //存储此次计算结果
            entry.putIndicator(IndicatorType.VOLUME_MA, volumeValues);
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.candleMA = candleMA;
                this.calculationCache.volumeMA = volumeMA;
            }
        }
    }

    /**
     * 计算 MACD
     * {12, 26, 9},
     */
    private void computeMACD(@NonNull List<CandleEntry> data, @NonNull IndicatorBuildConfig indicatorConfig) {
        IndicatorTagEntry indicatorTag = indicatorConfig.getIndicatorTags(IndicatorType.MACD);
        int indicatorCount = null == indicatorTag ? 0 : indicatorTag.getFlagEntries().length;
        long[] macd = null == calculationCache.macd ? new long[indicatorCount] : calculationCache.macd.clone();
        if (null == indicatorTag || indicatorTag.getFlagEntries().length < 3) {
            return;
        }
        int flag, begin;
        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            ValueEntry[] macdValues = new ValueEntry[indicatorTag.getFlagEntries().length];
            if (i == 0) {
                macd[0] = macd[1] = entry.getClose().result;
                continue;
            }
            // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
            // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
            flag = indicatorTag.getFlagEntries()[0].getFlag();
            macd[0] = (macd[0] * (flag - 1) + entry.getClose().result * 2) / (flag + 1);
            begin = flag = indicatorTag.getFlagEntries()[1].getFlag();
            macd[1] = (macd[1] * (flag - 1) + entry.getClose().result * 2) / (flag + 1);
            // DIF = EMA（12） - EMA（26） 。
            // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
            // 用（DIF-DEA）*2 即为 MACD 柱状图。
            flag = indicatorTag.getFlagEntries()[2].getFlag();
            long dif = macd[0] - macd[1];
            macd[2] = (macd[2] * (flag - 1) + dif * 2) / (flag + 1);
            if (i >= begin - 1) {
                macdValues[0] = entry.recoveryQuoteScaleValue(dif);
            }
            if (i >= begin - 1 + indicatorTag.getFlagEntries()[2].getFlag() - 1) {
                macdValues[1] = entry.recoveryQuoteScaleValue(macd[2]);
                macdValues[2] = entry.recoveryQuoteScaleValue((dif - macd[2]) * 2);
            }
            //存储此次计算结果
            entry.putIndicator(IndicatorType.MACD, macdValues);
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.macd = macd;
            }
        }
    }

    /**
     * 计算 BOLL 需要在计算 MA 之后进行
     */
    private void computeBOLL(@NonNull List<CandleEntry> data, @NonNull IndicatorBuildConfig indicatorConfig) {
        IndicatorTagEntry indicatorTag = indicatorConfig.getIndicatorTags(IndicatorType.BOLL);
        if (null == indicatorTag || indicatorTag.getFlagEntries().length == 0) {
            return;
        }
        long bollMA = calculationCache.bollMA;
        int flag = indicatorTag.getFlagEntries()[0].getFlag();
        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            bollMA += entry.getClose().result;
            ValueEntry[] bollValues = new ValueEntry[indicatorTag.getFlagEntries().length];
            if (i < flag) {
                continue;
            }
            //计算bollMA
            bollMA -= data.get(i - flag).getClose().result;
            long maValue = bollMA / flag;
            bollValues[0] = entry.recoveryQuoteScaleValue(maValue);
            long md = 0;
            if (bollValues.length > 1) {
                for (int b = i - flag + 1; b <= i; b++) {
                    long c = data.get(b).getClose().result;
                    long value = c - maValue;
                    md += value * value;
                }
                md = md / (flag - 1);
                md = (long) Math.sqrt(md);
                bollValues[1] = entry.recoveryQuoteScaleValue(maValue + 2 * md);
            }
            if (bollValues.length > 2) {
                bollValues[2] = entry.recoveryQuoteScaleValue(maValue - 2 * md);
            }
            //存储此次计算结果
            entry.putIndicator(IndicatorType.BOLL, bollValues);

            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.bollMA = bollMA;
            }
        }
    }

    /**
     * 计算 RSI
     */
    private void computeRSI(@NonNull List<CandleEntry> data, @NonNull IndicatorBuildConfig indicatorConfig) {
        IndicatorTagEntry indicatorTag = indicatorConfig.getIndicatorTags(IndicatorType.RSI);
        int indicatorCount = null == indicatorTag ? 0 : indicatorTag.getFlagEntries().length;
        long[] rsiABSEma = null == calculationCache.rsiABSEma ? new long[indicatorCount] : calculationCache.rsiABSEma.clone();
        long[] rsiMaxEma = null == calculationCache.rsiMaxEma ? new long[indicatorCount] : calculationCache.rsiMaxEma.clone();

        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            ValueEntry[] values = new ValueEntry[indicatorCount];
            long Rmax = 0, RAbs = 0;
            if (i > 0) {
                Rmax = Math.max(0, entry.getClose().result - data.get(i - 1).getClose().result);
                RAbs = Math.abs(entry.getClose().result - data.get(i - 1).getClose().result);
            }
            for (int j = 0; j < indicatorCount; j++) {
                int flag = indicatorTag.getFlagEntries()[j].getFlag();
                rsiABSEma[j] = (RAbs + (flag - 1) * rsiABSEma[j]) / flag;
                rsiMaxEma[j] = (Rmax + (flag - 1) * rsiMaxEma[j]) / flag;
                if (i >= flag && rsiABSEma[j] != 0) {
                    long value = (long) Math.pow(10, getScale().getQuoteScale() + 2) * rsiMaxEma[j] / rsiABSEma[j];
                    values[j] = entry.recoveryQuoteScaleValue(value);
                }

            }
            entry.putIndicator(IndicatorType.RSI, values);
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.rsiABSEma = rsiABSEma;
                this.calculationCache.rsiMaxEma = rsiMaxEma;
            }
        }
    }

    /**
     * 计算 KDJ
     * RSV=100 ×（C－L9）/（H9－L9）(公式中，C为第9日的收盘价；L9为9日内的最低价；H9为9日内的最高价)
     * K值=2/3×第8日K值+1/3×第9日RSV （简化公式为：（第9日RSV+2×第8日K值）/M1  ）
     * D值=2/3×第8日D值+1/3×第9日K值 （简化公式为：（第9日K值+2×第8日D值）/M2  ）
     * J值=3*第9日K值-2*第9日D值
     */
    private void computeKDJ(@NonNull List<CandleEntry> data, @NonNull IndicatorBuildConfig indicatorConfig) {
        IndicatorTagEntry indicatorTag = indicatorConfig.getIndicatorTags(IndicatorType.KDJ);
        if (null == indicatorTag || indicatorTag.getFlagEntries().length < 3) {
            return;
        }
        long k = calculationCache.k;
        long d = calculationCache.d;
        long j;
        int n = Math.max(0, indicatorTag.getFlagEntries()[0].getFlag() - 1);
        int m1 = indicatorTag.getFlagEntries()[1].getFlag();
        int m2 = indicatorTag.getFlagEntries()[2].getFlag();
        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            if (i < n) {
                continue;
            }
            ValueEntry[] values = new ValueEntry[indicatorTag.getFlagEntries().length];
            CandleEntry entry = data.get(i);
            int startIndex = i - n;

            long max = Long.MIN_VALUE;
            long min = Long.MAX_VALUE;
            for (int index = startIndex; index <= i; index++) {
                max = Math.max(max, data.get(index).getHigh().result);
                min = Math.min(min, data.get(index).getLow().result);
            }
            long rsv = 0;
            if (max != min) {
                rsv = (long) Math.pow(10, getScale().getQuoteScale() + 2) * (entry.getClose().result - min) / (max - min);
            }

            k = (rsv + 2 * k) / m1;
            d = (k + 2 * d) / m2;
            j = 3 * k - 2 * d;
            values[0] = entry.recoveryQuoteScaleValue(k);
            values[1] = entry.recoveryQuoteScaleValue(d);
            values[2] = entry.recoveryQuoteScaleValue(j);
            entry.putIndicator(IndicatorType.KDJ, values);
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.k = k;
                this.calculationCache.d = d;
            }
        }
    }

    /**
     * 计算 WR
     */
    private void calculateWR(@NonNull List<CandleEntry> data, @NonNull IndicatorBuildConfig indicatorConfig) {
        IndicatorTagEntry indicatorTag = indicatorConfig.getIndicatorTags(IndicatorType.WR);
        int indicatorCount = null == indicatorTag ? 0 : indicatorTag.getFlagEntries().length;
        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            ValueEntry[] values = new ValueEntry[indicatorCount];
            for (int j = 0; j < indicatorCount; j++) {
                int flag = indicatorTag.getFlagEntries()[j].getFlag();
                if (i < flag) {
                    continue;
                }
                long max = Long.MIN_VALUE;
                long min = Long.MAX_VALUE;
                for (int index = i - flag; index <= i; index++) {
                    max = Math.max(max, data.get(index).getHigh().result);
                    min = Math.min(min, data.get(index).getLow().result);
                }
                long value = 0;
                if (max != min) {
                    value = (long) Math.pow(10, getScale().getQuoteScale() + 2) * (max - entry.getClose().result) / (max - min);
                }
                values[j] = entry.recoveryQuoteScaleValue(value);
            }
            entry.putIndicator(IndicatorType.WR, values);
        }
    }

    /**
     * 计算结果缓存类
     */
    class CalculationCache {
        //平均线
        long[] candleMA;
        long[] volumeMA;
        //macd
        long[] macd;
        //BOLL
        long bollMA = 0;
        //RSI
        long[] rsiABSEma;
        long[] rsiMaxEma;
        //KDJ(若无前一日K值与D值，则可以分别用50代替)
        long k = 50;
        long d = 50;
        //WR
        long[] wr;
        //数据起始下标
        int index = 0;

        public void init() {
            //平均线
            candleMA = null;
            volumeMA = null;
            //macd
            macd = null;
            //BOLL
            bollMA = 0;
            //RSI
            rsiABSEma = null;
            rsiMaxEma = null;
            //WR
            wr = null;
            //KDJ(若无前一日K值与D值，则可以分别用50代替)
            k = 50;
            d = 50;
            //数据起始下标
            index = 0;
        }
    }
}
