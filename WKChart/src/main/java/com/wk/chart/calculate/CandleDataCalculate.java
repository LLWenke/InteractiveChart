package com.wk.chart.calculate;

import androidx.annotation.NonNull;

import com.wk.chart.compat.config.IndexBuildConfig;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.IndexConfigEntry;
import com.wk.chart.entry.ScaleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CandleDataCalculate {
    private final CalculationCache calculationCache = new CalculationCache();//计算结果缓存类

    /**
     * 计算 MA
     */
    public void calculateMA(
            @NonNull List<CandleEntry> data,
            @NonNull IndexBuildConfig indicatorConfig,
            @NonNull ValueFormatter formatter,
            @NotNull ScaleEntry scale,
            int startPosition
    ) {
        IndexConfigEntry candleIndicatorTag = indicatorConfig.getIndexTags(IndexType.CANDLE_MA);
        IndexConfigEntry volumeIndicatorTag = indicatorConfig.getIndexTags(IndexType.VOLUME_MA);
        int candleIndicatorCount = null == candleIndicatorTag ? 0 : candleIndicatorTag.getFlagEntries().length;
        int volumeIndicatorCount = null == volumeIndicatorTag ? 0 : volumeIndicatorTag.getFlagEntries().length;
        double[] candleMA, volumeMA;
        if (startPosition == 0) {
            candleMA = new double[candleIndicatorCount];
            volumeMA = new double[volumeIndicatorCount];
        } else {
            candleMA = null == calculationCache.candleMA ? new double[candleIndicatorCount] : calculationCache.candleMA.clone();
            volumeMA = null == calculationCache.volumeMA ? new double[volumeIndicatorCount] : calculationCache.volumeMA.clone();
        }
        for (int i = startPosition, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            //计算蜡烛图MA值
            ValueEntry[] candleValues = new ValueEntry[candleIndicatorCount];
            for (int j = 0; j < candleIndicatorCount; j++) {
                candleMA[j] += entry.getClose().value;
                int flag = (int) candleIndicatorTag.getFlagEntries()[j].getFlag();
                int startIndex = flag - 1;
                if (i > startIndex) {
                    candleMA[j] -= data.get(i - flag).getClose().value;
                    candleValues[j] = new ValueEntry(candleMA[j] / flag).formatFixed(formatter, scale.getQuoteScale());
                } else if (i == startIndex) {
                    candleValues[j] = new ValueEntry(candleMA[j] / flag).formatFixed(formatter, scale.getQuoteScale());
                }
            }
            //存储此次计算结果
            entry.putLineIndex(IndexType.CANDLE_MA, candleValues);
            //计算交易量图MA值
            ValueEntry[] volumeValues = new ValueEntry[volumeIndicatorCount];
            for (int j = 0; j < volumeIndicatorCount; j++) {
                volumeMA[j] += entry.getVolume().value;
                int flag = (int) volumeIndicatorTag.getFlagEntries()[j].getFlag();
                int startIndex = flag - 1;
                if (i > startIndex) {
                    volumeMA[j] -= data.get(i - flag).getVolume().value;
                    volumeValues[j] = new ValueEntry(volumeMA[j] / flag).formatUnit(formatter, 2);
                } else if (i == startIndex) {
                    volumeValues[j] = new ValueEntry(volumeMA[j] / flag).formatUnit(formatter, 2);
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
     * 计算 EMA
     * 计算公式为：EMA(今日) = (今日收盘价 - 昨日EMA) × (2/(N+1)) + 昨日EMA‌，其中N为计算周期，平滑系数通过2/(N+1)确定
     */
    public void calculateEMA(
            @NonNull List<CandleEntry> data,
            @NonNull IndexBuildConfig indicatorConfig,
            @NonNull ValueFormatter formatter,
            @NotNull ScaleEntry scale,
            int startPosition
    ) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.EMA);
        int indicatorCount = null == indicatorTag ? 0 : indicatorTag.getFlagEntries().length;
        double[] ema;
        if (startPosition == 0) {
            ema = new double[indicatorCount];
        } else {
            ema = null == calculationCache.ema ? new double[indicatorCount] : calculationCache.ema.clone();
        }
        //计算蜡烛图EMA值
        for (int i = startPosition, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            ValueEntry[] values = new ValueEntry[indicatorCount];
            for (int j = 0; j < indicatorCount; j++) {
                int flag = (int) indicatorTag.getFlagEntries()[j].getFlag();
                int startIndex = flag - 1;
                if (i > startIndex) {
                    ema[j] = (entry.getClose().value - ema[j]) * (2d / (flag + 1)) + ema[j];
                    values[j] = new ValueEntry(ema[j]).formatFixed(formatter, scale.getQuoteScale());
                } else if (i == startIndex) {
                    ema[j] = entry.getClose().value;
                    values[j] = new ValueEntry(ema[j]).formatFixed(formatter, scale.getQuoteScale());
                }
            }
            //存储此次计算结果
            entry.putLineIndex(IndexType.EMA, values);
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.ema = ema.clone();
            }
        }
    }

    /**
     * BOLL(n)计算公式：
     * MA=n日内的收盘价之和÷n。
     * MD=n日的平方根（C－MA）的两次方之和除以n
     * MB=n日的MA
     * UP=MB+p×MD
     * DN=MB－p×MD
     * p为参数，可根据股票的特性来做相应的调整，一般默认为2
     *
     * @param data 数据集合
     */
    public void calculateBOLL(
            @NonNull List<CandleEntry> data,
            @NonNull IndexBuildConfig indicatorConfig,
            @NonNull ValueFormatter formatter,
            @NotNull ScaleEntry scale,
            int startPosition
    ) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.BOLL);
        if (null == indicatorTag || indicatorTag.getFlagEntries().length < 2) return;
        int n = (int) indicatorTag.getFlagEntries()[0].getFlag();
        int p = (int) indicatorTag.getFlagEntries()[1].getFlag();
        double bollMA = startPosition == 0 ? 0 : calculationCache.bollMA;
        int position = n - 1;
        for (int i = startPosition, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            bollMA += entry.getClose().value;
            if (i >= position) {
                if (i > position) {
                    bollMA -= data.get(i - n).getClose().value;
                }
                //n日MA
                double maValue = bollMA / n;
                double md = 0;
                for (int j = i - position; j <= i; j++) {
                    //n日
                    double value = data.get(j).getClose().value - maValue;
                    md += value * value;
                }
                md = md / n;
                md = (double) Math.sqrt(md);
                double up = maValue + p * md;//上轨线
                double dn = maValue - p * md;//下轨线
                //精度恢复运算
                ValueEntry[] bollValues = new ValueEntry[3];
                bollValues[0] = new ValueEntry(up).formatFixed(formatter, scale.getQuoteScale());
                bollValues[1] = new ValueEntry(maValue).formatFixed(formatter, scale.getQuoteScale());
                bollValues[2] = new ValueEntry(dn).formatFixed(formatter, scale.getQuoteScale());
                //存储此次计算结果
                entry.putLineIndex(IndexType.BOLL, bollValues);
            }
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.bollMA = bollMA;
            }
        }
    }

    /**
     * 计算 SAR
     * SAR（i）= SAR（i-1）+（EP（i-1）- SAR（i-1））× AF（i）
     * SAR(N,S,M),N为计算周期,S为步长,M为极值
     * SAR(0.02,0.02,0.2)表示计算4日抛物转向，步长为2%，极限值为20%
     *
     * @param data 数据集合
     */
    public void calculateSAR2(
            @NonNull List<CandleEntry> data,
            @NonNull IndexBuildConfig indicatorConfig,
            @NonNull ValueFormatter formatter,
            @NotNull ScaleEntry scale,
            int startPosition
    ) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.SAR);
        if (null == indicatorTag || indicatorTag.getFlagEntries().length < 3) return;
        double start = indicatorTag.getFlagEntries()[0].getFlag();
        double increment = indicatorTag.getFlagEntries()[1].getFlag();
        double max = indicatorTag.getFlagEntries()[2].getFlag();
        double af;
        double sar, ep;
        boolean upTrend;
        if (startPosition == 0) {
            ep = 0;
            af = 0;
            sar = 0;
            upTrend = true;
        } else {
            ep = calculationCache.ep;
            af = calculationCache.af;
            sar = calculationCache.sar;
            upTrend = calculationCache.upTrend;
        }
        for (int i = startPosition, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            double high = entry.getHigh().value;
            double low = entry.getLow().value;
            if (i == 0) {
                sar = low;
                ep = high;
            } else {
                CandleEntry prev = data.get(i - 1);
                double prevHigh = prev.getHigh().value;
                double prevLow = prev.getLow().value;
                if (i == 1) {
                    // 确定初始趋势方向
                    double close = entry.getClose().value;
                    if (close > prevHigh) {
                        upTrend = true;
                        sar = prevLow;
                        ep = high;
                    } else if (close < prevLow) {
                        upTrend = false;
                        sar = prevHigh;
                        ep = low;
                    } else {
                        upTrend = true;
                        sar = prevLow;
                        ep = Math.max(prevHigh, high);
                    }
                    af = start;
                } else {
                    // 标准 SAR 计算
                    double prevSAR = sar;
                    CandleEntry prev2 = data.get(i - 2);
                    if (upTrend) {
                        sar = prevSAR + af * (ep - prevSAR);
                        sar = Math.min(sar, Math.min(prevLow, prev2.getLow().value));
                        if (low < sar) {
                            upTrend = false;
                            sar = Math.max(ep, prevHigh);
                            ep = low;
                            af = start;
                        } else if (high > ep) {
                            ep = high;
                            af = Math.min(af + increment, max);
                        }
                    } else {
                        sar = prevSAR - af * (prevSAR - ep);
                        sar = Math.max(sar, Math.max(prevHigh, prev2.getHigh().value));
                        if (high > sar) {
                            upTrend = true;
                            sar = Math.min(ep, prevLow);
                            ep = high;
                            af = start;
                        } else if (low < ep) {
                            ep = low;
                            af = Math.min(af + increment, max);
                        }
                    }
                }
            }
            //精度恢复运算
            ValueEntry[] sarValues = new ValueEntry[1];
            sarValues[0] = new ValueEntry(sar).formatFixed(formatter, scale.getQuoteScale());
            //存储此次计算结果
            entry.putIndex(IndexType.SAR, sarValues);
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                calculationCache.ep = ep;
                calculationCache.af = af;
                calculationCache.sar = sar;
                calculationCache.upTrend = upTrend;
            }
        }
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
     */
    public void calculateMACD(
            @NonNull List<CandleEntry> data,
            @NonNull IndexBuildConfig indicatorConfig,
            @NonNull ValueFormatter formatter,
            @NotNull ScaleEntry scale,
            int startPosition
    ) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.MACD);
        if (null == indicatorTag || indicatorTag.getFlagEntries().length < 3) return;
        int s = (int) indicatorTag.getFlagEntries()[0].getFlag();
        int l = (int) indicatorTag.getFlagEntries()[1].getFlag();
        int m = (int) indicatorTag.getFlagEntries()[2].getFlag();
        double ema_s, ema_l, dea;
        if (startPosition == 0) {
            ema_s = 0;
            ema_l = 0;
            dea = 0;
        } else {
            ema_s = calculationCache.emaS;
            ema_l = calculationCache.emaL;
            dea = calculationCache.dea;
        }
        int startIndex = l - 1;
        for (int i = startPosition, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            if (i == 0) {
                ema_s = entry.getClose().value;
                ema_l = entry.getClose().value;
            } else {
                ema_s = ((s - 1) * ema_s + 2 * entry.getClose().value) / (s + 1);
                ema_l = ((l - 1) * ema_l + 2 * entry.getClose().value) / (l + 1);
            }
            //计算dif
            double dif = ema_s - ema_l;
            //计算dea
            if (i == 0) {
                dea = 0;
            } else {
                dea = ((m - 1) * dea + 2 * dif) / (m + 1);
            }
            //计算macd
//            double macd = 2 * (dif - dea);
            double macd = dif - dea;
            if (i >= startIndex) {
                ValueEntry[] macdValues = new ValueEntry[3];
                macdValues[0] = new ValueEntry(dif).formatFixed(formatter, scale.getQuoteScale());
                if (i >= startIndex + m - 1) {
                    macdValues[1] = new ValueEntry(dea).formatFixed(formatter, scale.getQuoteScale());
                    macdValues[2] = new ValueEntry(macd).formatFixed(formatter, scale.getQuoteScale());
                }
                //存储此次计算结果
                entry.putIndex(IndexType.MACD, macdValues);
            }
            //将倒数第二次的计算结果缓存
            if (i == z - 2) {
                this.calculationCache.emaS = ema_s;
                this.calculationCache.emaL = ema_l;
                this.calculationCache.dea = dea;
            }
        }
    }

    /**
     * 计算 RSI
     */
    public void calculateRSI(
            @NonNull List<CandleEntry> data,
            @NonNull IndexBuildConfig indicatorConfig,
            @NonNull ValueFormatter formatter,
            @NotNull ScaleEntry scale,
            int startPosition
    ) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.RSI);
        int indicatorCount = null == indicatorTag ? 0 : indicatorTag.getFlagEntries().length;
        double[] rsiABSEma, rsiMaxEma;
        if (startPosition == 0) {
            rsiABSEma = new double[indicatorCount];
            rsiMaxEma = new double[indicatorCount];
        } else {
            rsiABSEma = null == calculationCache.rsiABSEma ? new double[indicatorCount] : calculationCache.rsiABSEma.clone();
            rsiMaxEma = null == calculationCache.rsiMaxEma ? new double[indicatorCount] : calculationCache.rsiMaxEma.clone();
        }
        for (int i = startPosition, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            ValueEntry[] values = new ValueEntry[indicatorCount];
            double Rmax = 0, RAbs = 0;
            if (i > 0) {
                Rmax = Math.max(0, entry.getClose().value - data.get(i - 1).getClose().value);
                RAbs = Math.abs(entry.getClose().value - data.get(i - 1).getClose().value);
            }
            for (int j = 0; j < indicatorCount; j++) {
                int flag = (int) indicatorTag.getFlagEntries()[j].getFlag();
                rsiABSEma[j] = (RAbs + (flag - 1) * rsiABSEma[j]) / flag;
                rsiMaxEma[j] = (Rmax + (flag - 1) * rsiMaxEma[j]) / flag;
                if (i >= flag - 1 && rsiABSEma[j] != 0) {
                    double value = 100d * rsiMaxEma[j] / rsiABSEma[j];
                    values[j] = new ValueEntry(value);
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
    public void calculateKDJ(
            @NonNull List<CandleEntry> data,
            @NonNull IndexBuildConfig indicatorConfig,
            @NonNull ValueFormatter formatter,
            @NotNull ScaleEntry scale,
            int startPosition
    ) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.KDJ);
        if (null == indicatorTag || indicatorTag.getFlagEntries().length < 3) return;
        double k, d, j;
        int n = Math.max(0, (int) indicatorTag.getFlagEntries()[0].getFlag() - 1);
        int m1 = (int) indicatorTag.getFlagEntries()[1].getFlag();
        int m2 = (int) indicatorTag.getFlagEntries()[2].getFlag();
        if (startPosition == 0) {
            k = 0;
            d = 0;
        } else {
            k = calculationCache.k;
            d = calculationCache.d;
        }
        for (int i = startPosition, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            int startIndex = i - n;
            if (startIndex < 0) {
                startIndex = 0;
            }
            double max = Long.MIN_VALUE;
            double min = Long.MAX_VALUE;
            for (int index = startIndex; index <= i; index++) {
                max = Math.max(max, data.get(index).getHigh().value);
                min = Math.min(min, data.get(index).getLow().value);
            }
            double rsv = 0;
            if (max != min) {
                rsv = 100d * (entry.getClose().value - min) / (max - min);
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
                values[0] = new ValueEntry(k).formatFixed(formatter, scale.getQuoteScale());
                values[1] = new ValueEntry(d).formatFixed(formatter, scale.getQuoteScale());
                values[2] = new ValueEntry(j).formatFixed(formatter, scale.getQuoteScale());
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
    public void calculateWR(
            @NonNull List<CandleEntry> data,
            @NonNull IndexBuildConfig indicatorConfig,
            @NonNull ValueFormatter formatter,
            @NotNull ScaleEntry scale,
            int startPosition
    ) {
        IndexConfigEntry indicatorTag = indicatorConfig.getIndexTags(IndexType.WR);
        int indicatorCount = null == indicatorTag ? 0 : indicatorTag.getFlagEntries().length;
        for (int i = startPosition, z = data.size(); i < z; i++) {
            CandleEntry entry = data.get(i);
            ValueEntry[] values = new ValueEntry[indicatorCount];
            for (int j = 0; j < indicatorCount; j++) {
                int flag = (int) indicatorTag.getFlagEntries()[j].getFlag();
                int startIndex = flag - 1;
                if (i < startIndex) {
                    continue;
                }
                double max = Long.MIN_VALUE;
                double min = Long.MAX_VALUE;
                for (int index = i - startIndex; index <= i; index++) {
                    max = Math.max(max, data.get(index).getHigh().value);
                    min = Math.min(min, data.get(index).getLow().value);
                }
                double value = 0;
                if (max != min) {
                    value = 100d * (max - entry.getClose().value) / (max - min);
                }
                values[j] = new ValueEntry(value).formatFixed(formatter, scale.getQuoteScale());
            }
            entry.putLineIndex(IndexType.WR, values);
        }
    }


    /**
     * 计算结果缓存类
     */
    private static class CalculationCache {
        //MA
        double[] candleMA;
        double[] volumeMA;
        //EMA
        double[] ema;
        //macd
        double emaS = 0;
        double emaL = 0;
        double dea = 0;
        //BOLL
        double bollMA = 0;
        //RSI
        double[] rsiABSEma;
        double[] rsiMaxEma;
        //KDJ
        double k = 0;
        double d = 0;
        //SAR
        double ep = 0;
        double af = 0.0;
        double sar = 0;
        boolean upTrend = true;
    }
}
