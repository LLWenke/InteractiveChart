package com.wk.chart.adapter;

import com.wk.chart.compat.DateUtil;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.config.IndexBuildConfig;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.IndexConfigEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.PushType;
import com.wk.chart.enumeration.TimeType;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CandleAdapter extends AbsAdapter<CandleEntry, IndexBuildConfig> {

    private CalculationCache calculationCache = new CalculationCache();//计算结果缓存类
    private TimeType timeType = null;//显示模式

    public CandleAdapter() {
        this(new IndexBuildConfig());
    }

    public CandleAdapter(IndexBuildConfig indexBuildConfig) {
        super(indexBuildConfig);
    }

    public CandleAdapter(CandleAdapter adapter) {
        super(adapter);
        this.timeType = adapter.timeType;
        this.calculationCache = adapter.calculationCache;
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
    public void computeMinAndMax(int start, int end) {
        low = Float.MAX_VALUE;
        high = -Float.MAX_VALUE;
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
        }
    }

    @Override
    public void setBuildConfig(IndexBuildConfig buildConfig) {
        stopAnimator();
        this.calculationCache.init();
        super.setBuildConfig(buildConfig);
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
        if (null == type) {
            return;
        }
        stopAnimator();
        this.calculationCache.init();
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
        if (null == timeType) {
            return;
        }
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
        if (null == timeType) {
            return;
        }
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
        if (null == data || null == timeType) {
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
        return pushType;
    }

    @Override
    public void changeItem(int position, CandleEntry data) {
        if (null == timeType) {
            return;
        }
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
        if (null == timeType || diff < 0) {
            return PushType.INVALID;
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
        } else if (timeType == TimeType.month) {//月
            return DateUtil.getMonthDiff(startDate, endDate);
        } else {
            return DateUtil.getDateDiff(startDate, endDate, timeType.msec());
        }
    }

    /**
     * 构建数据
     */
    @Override
    void buildData(@NonNull IndexBuildConfig buildConfig, @NonNull List<CandleEntry> data) {
        buildConfig.buildIndexFlags();
        //计算 MA MACD BOLL RSI KDJ MR 指标
        computeMA(data, buildConfig);
        computeMACD(data, buildConfig);
        computeBOLL(data, buildConfig);
        computeRSI(data, buildConfig);
        computeKDJ(data, buildConfig);
        computeWR(data, buildConfig);
        buildTimeText(data);
    }

    /**
     * 构建时间显示文字
     */
    private void buildTimeText(@NonNull List<CandleEntry> data) {
        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            if (null == timeType) break;
            CandleEntry entry = data.get(i);
            entry.setShortTimeText(DateUtil.formatDateToString(entry.getTime(), timeType.pattern()));
            entry.setTimeText(DateUtil.formatDateToString(entry.getTime(), timeType == TimeType.day
                    ? DateUtil.DATE_FORMAT_YMD : DateUtil.DATE_FORMAT_YMDHM));
        }
    }

    /**
     * 计算 MA
     */
    private void computeMA(@NonNull List<CandleEntry> data, @NonNull IndexBuildConfig indicatorConfig) {
        IndexConfigEntry candleIndicatorTag = indicatorConfig.getIndexTags(IndexType.CANDLE_MA);
        IndexConfigEntry volumeIndicatorTag = indicatorConfig.getIndexTags(IndexType.VOLUME_MA);
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
                int startIndex = flag - 1;
                if (i > startIndex) {
                    candleMA[j] -= data.get(i - flag).getClose().result;
                    candleValues[j] = entry.buildQuoteScaleValue(candleMA[j] / flag);
                } else if (i == startIndex) {
                    candleValues[j] = entry.buildQuoteScaleValue(candleMA[j] / flag);
                }
            }
            //存储此次计算结果
            entry.putLineIndex(IndexType.CANDLE_MA, candleValues);
            //计算交易量图MA值
            ValueEntry[] volumeValues = new ValueEntry[volumeIndicatorCount];
            for (int j = 0; j < volumeIndicatorCount; j++) {
                volumeMA[j] += entry.getVolume().result;
                int flag = volumeIndicatorTag.getFlagEntries()[j].getFlag();
                int startIndex = flag - 1;
                if (i > startIndex) {
                    volumeMA[j] -= data.get(i - flag).getVolume().result;
                    volumeValues[j] = entry.buildBaseScaleValue(volumeMA[j] / flag);
                } else if (i == startIndex) {
                    volumeValues[j] = entry.buildBaseScaleValue(volumeMA[j] / flag);
                }
            }
            //存储此次计算结果
            entry.putLineIndex(IndexType.VOLUME_MA, volumeValues);
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.candleMA = candleMA.clone();
                this.calculationCache.volumeMA = volumeMA.clone();
            }
        }
    }

    /**
     * 计算 MACD
     * {12, 26, 9},
     */
    private void computeMACD(@NonNull List<CandleEntry> data, @NonNull IndexBuildConfig indicatorConfig) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.MACD);
        if (null == indicatorTag || indicatorTag.getFlagEntries().length < 3) {
            return;
        }
        calculateMACD(data, indicatorTag.getFlagEntries()[0].getFlag(), indicatorTag.getFlagEntries()[1].getFlag()
                , indicatorTag.getFlagEntries()[2].getFlag());
    }

    /**
     * MACD(s,l,m)，一般取MACD(12,26,9)。
     * `EMAx=((x-1)/(x+1.0)*前一日EMA)+2.0/(x+1)*今日收盘价`;其中第一日的EMA是当日的收盘价。
     * `EMA12=(11/13.0)*[前一日EMA12]+2.0/13*[今日quotes.c]`
     * `EMA26=(25/27.0)*[前一日EMA26]+2.0/27*[今日quotes.c]`
     * DIF:`DIF=EMA12-EMA26`
     * DEA:`DEA=8/10.0*(前一日的DEA)+2/10.0*今日DIF`
     * MACD:`2*(DIF-DEA)`
     *
     * @param data 数据集合
     * @param s    一般为12
     * @param l    一般为26
     * @param m    一般为9
     */
    private void calculateMACD(@NonNull List<CandleEntry> data, int s, int l, int m) {
        long ema_s = calculationCache.ema_s;
        long ema_l = calculationCache.ema_l;
        long dea = calculationCache.dea;
        int startIndex = l - 1;
        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            if (i == 0) {
                ema_s = entry.getClose().result;
                ema_l = entry.getClose().result;
            } else {
                ema_s = ((s - 1) * ema_s + 2 * entry.getClose().result) / (s + 1);
                ema_l = ((l - 1) * ema_l + 2 * entry.getClose().result) / (l + 1);
            }
            //计算dif
            long dif = ema_s - ema_l;
            //计算dea
            if (i == 0) {
                dea = 0;
            } else {
                dea = (dea * (m - 1) + dif * 2) / (m + 1);
            }
            //计算macd
//            long macd = 2 * (dif - dea);
            long macd = dif - dea;
            if (i >= startIndex) {
                ValueEntry[] macdValues = new ValueEntry[3];
                macdValues[0] = entry.buildQuoteScaleValue(dif);
                if (i >= startIndex + m - 1) {
                    macdValues[1] = entry.buildQuoteScaleValue(dea);
                    macdValues[2] = entry.buildQuoteScaleValue(macd);
                }
                //存储此次计算结果
                entry.putIndex(IndexType.MACD, macdValues);
            }
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.ema_s = ema_s;
                this.calculationCache.ema_l = ema_l;
                this.calculationCache.dea = dea;
            }
        }
    }


    /**
     * BOLL(n)计算公式：
     * MA=n日内的收盘价之和÷n。
     * MD=n日的平方根（C－MA）的两次方之和除以n
     * MB=（n－1）日的MA
     * UP=MB+p×MD
     * DN=MB－p×MD
     * p为参数，可根据股票的特性来做相应的调整，一般默认为2
     *
     * @param data 数据集合
     * @param n    周期，一般为26
     * @param p    参数，可根据股票的特性来做相应的调整，一般默认为2
     */
    public void calculateBOLL(@NonNull List<CandleEntry> data, int n, int p) {
        long bollMA = calculationCache.bollMA;
        long bollPreviousMA = calculationCache.bollPreviousMA;
        int position = n - 1;
        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            bollMA += entry.getClose().result;
            bollPreviousMA += entry.getClose().result;
            if (i >= position) {
                if (i > position) {
                    bollMA -= data.get(i - n).getClose().result;
                }
                if (i > position - 1) {
                    bollPreviousMA -= data.get(i - position).getClose().result;
                }
                //n日MA
                long maValue = bollMA / n;
                //n-1日MA
                long previousMaValue = bollPreviousMA / position;
                long md = 0;
                for (int j = i - position; j <= i; j++) {
                    //n日
                    long value = data.get(j).getClose().result - maValue;
                    md += value * value;
                }
                md = md / n;
                md = (long) Math.sqrt(md);
                //(n－1）日的MA
                long up = previousMaValue + p * md;//上轨线
                long dn = previousMaValue - p * md;//下轨线
                //精度恢复运算
                ValueEntry[] bollValues = new ValueEntry[3];
                bollValues[0] = entry.buildQuoteScaleValue(up);
                bollValues[1] = entry.buildQuoteScaleValue(previousMaValue);
                bollValues[2] = entry.buildQuoteScaleValue(dn);
                //存储此次计算结果
                entry.putLineIndex(IndexType.BOLL, bollValues);
            }
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.bollMA = bollMA;
                this.calculationCache.bollPreviousMA = bollPreviousMA;
            }
        }
    }

    /**
     * 计算 BOLL
     */
    private void computeBOLL(@NonNull List<CandleEntry> data, @NonNull IndexBuildConfig indicatorConfig) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.BOLL);
        if (null == indicatorTag || indicatorTag.getFlagEntries().length < 2) {
            return;
        }
        calculateBOLL(data, indicatorTag.getFlagEntries()[0].getFlag(), indicatorTag.getFlagEntries()[1].getFlag());
    }

    /**
     * 计算 RSI
     */
    private void computeRSI(@NonNull List<CandleEntry> data, @NonNull IndexBuildConfig indicatorConfig) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.RSI);
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
                if (i >= flag - 1 && rsiABSEma[j] != 0) {
                    long value = (long) ValueUtils.pow10(getScale().getQuoteScale() + 2) * rsiMaxEma[j] / rsiABSEma[j];
                    values[j] = entry.buildQuoteScaleValue(value);
                }

            }
            entry.putLineIndex(IndexType.RSI, values);
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.rsiABSEma = rsiABSEma.clone();
                this.calculationCache.rsiMaxEma = rsiMaxEma.clone();
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
    private void computeKDJ(@NonNull List<CandleEntry> data, @NonNull IndexBuildConfig indicatorConfig) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.KDJ);
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
            CandleEntry entry = data.get(i);
            int startIndex = i - n;
            if (startIndex < 0) {
                startIndex = 0;
            }
            long max = Long.MIN_VALUE;
            long min = Long.MAX_VALUE;
            for (int index = startIndex; index <= i; index++) {
                max = Math.max(max, data.get(index).getHigh().result);
                min = Math.min(min, data.get(index).getLow().result);
            }
            long rsv = 0;
            if (max != min) {
                rsv = (long) (Math.pow(10, getScale().getQuoteScale() + 2) * (entry.getClose().result - min) / (max - min));
            }
            if (i == 0) {
                k = 50;
                d = 50;
            } else {
                k = (rsv + (m1 - 1) * k) / m1;
                d = (k + (m2 - 1) * d) / m2;
            }
            if (i >= n) {
                j = 3 * k - 2 * d;
                ValueEntry[] values = new ValueEntry[3];
                values[0] = entry.buildQuoteScaleValue(k);
                values[1] = entry.buildQuoteScaleValue(d);
                values[2] = entry.buildQuoteScaleValue(j);
                entry.putLineIndex(IndexType.KDJ, values);
            }
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
    private void computeWR(@NonNull List<CandleEntry> data, @NonNull IndexBuildConfig indicatorConfig) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.WR);
        int indicatorCount = null == indicatorTag ? 0 : indicatorTag.getFlagEntries().length;
        for (int i = calculationCache.index, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            ValueEntry[] values = new ValueEntry[indicatorCount];
            for (int j = 0; j < indicatorCount; j++) {
                int flag = indicatorTag.getFlagEntries()[j].getFlag();
                int startIndex = flag - 1;
                if (i < startIndex) {
                    continue;
                }
                long max = Long.MIN_VALUE;
                long min = Long.MAX_VALUE;
                for (int index = i - startIndex; index <= i; index++) {
                    max = Math.max(max, data.get(index).getHigh().result);
                    min = Math.min(min, data.get(index).getLow().result);
                }
                long value = 0;
                if (max != min) {
                    value = (long) ValueUtils.pow10(getScale().getQuoteScale() + 2) * (max - entry.getClose().result) / (max - min);
                }
                values[j] = entry.buildQuoteScaleValue(value);
            }
            entry.putLineIndex(IndexType.WR, values);
        }
    }

    /**
     * 计算结果缓存类
     */
    static class CalculationCache {
        //平均线
        long[] candleMA;
        long[] volumeMA;
        //macd
        long ema_s = 0;
        long ema_l = 0;
        long dea = 0;
        //BOLL
        long bollMA = 0;
        long bollPreviousMA = 0;
        //RSI
        long[] rsiABSEma;
        long[] rsiMaxEma;
        //KDJ
        long k = 0;
        long d = 0;
        //数据起始下标
        int index = 0;

        public void init() {
            //平均线
            candleMA = null;
            volumeMA = null;
            //macd
            ema_s = 0;
            ema_l = 0;
            dea = 0;
            //BOLL
            bollMA = 0;
            bollPreviousMA = 0;
            //RSI
            rsiABSEma = null;
            rsiMaxEma = null;
            //KDJ
            k = 0;
            d = 0;
            //数据起始下标
            index = 0;
        }
    }
}
