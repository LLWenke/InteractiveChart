package com.wk.chart.entry;

import androidx.annotation.NonNull;

import com.wk.chart.enumeration.MarkerPointType;
import com.wk.chart.enumeration.TimeType;
import com.wk.chart.formatter.DateFormatter;
import com.wk.chart.formatter.ValueFormatter;
import com.wk.utils.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>蜡烛图数据类</p>
 */
public class CandleEntry extends IndexEntry {
    private final ValueEntry open;// 开盘价
    private final ValueEntry high;// 最高价
    private final ValueEntry low;// 最低价
    private final ValueEntry close;// 收盘价
    private final ValueEntry volume;// 交易量
    private final ValueEntry changeAmount;// 涨跌额
    private final ValueEntry changeRate;// 涨跌幅
    private String timeText; // 时间显示文本（正常）
    private String shortTimeText; // 时间显示文本（简短）
    private int markerPointType = MarkerPointType.NORMAL; // 标记点类型
    private final List<ValueEntry> animatorEntry;//用于动画的属性列表

    /**
     * 自定义 K 线图用的数据
     *
     * @param open   开盘价
     * @param high   最高价
     * @param low    最低价
     * @param close  收盘价
     * @param volume 量
     * @param time   时间
     */
    public CandleEntry(
            String open,
            String high,
            String low,
            String close,
            String volume,
            @NonNull Date time
    ) {
        super(time);
        this.open = new ValueEntry(NumberUtils.parseDouble(open));
        this.high = new ValueEntry(NumberUtils.parseDouble(high));
        this.low = new ValueEntry(NumberUtils.parseDouble(low));
        this.close = new ValueEntry(NumberUtils.parseDouble(close));
        this.volume = new ValueEntry(NumberUtils.parseDouble(volume));
        this.changeAmount = new ValueEntry(0d);
        this.changeRate = new ValueEntry(0d);
        this.timeText = "";
        this.shortTimeText = "";
        this.animatorEntry = new ArrayList<>();
        this.animatorEntry.add(this.open);
        this.animatorEntry.add(this.high);
        this.animatorEntry.add(this.low);
        this.animatorEntry.add(this.close);
        this.animatorEntry.add(this.volume);
    }

    /**
     * 构建值精度(耗时操作，建议放在子线程)
     *
     * @param scale     精度
     * @param formatter 数值格式化工具
     */
    public void buildValueScale(@NonNull ScaleEntry scale, @NonNull ValueFormatter formatter) {
        int quoteScale = scale.getQuoteScale();
        BigDecimal openValue = NumberUtils.parseBigDecimal(open.value, quoteScale);
        BigDecimal closeValue = NumberUtils.parseBigDecimal(close.value, quoteScale);
        BigDecimal highValue = NumberUtils.parseBigDecimal(high.value, quoteScale);
        BigDecimal lowValue = NumberUtils.parseBigDecimal(low.value, quoteScale);
        BigDecimal volumeValue = NumberUtils.parseBigDecimal(volume.value, 2);
        BigDecimal changeAmountValue = closeValue.subtract(openValue);
        BigDecimal changeRateValue = changeAmountValue.multiply(NumberUtils.parseBigDecimal("100")).divide(
                openValue.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : openValue, 2, RoundingMode.DOWN);
        String direction = openValue.compareTo(closeValue) > 0 ? "" : "+";
        this.open.value = openValue.doubleValue();
        this.open.valueFormat = formatter.formatFixed(openValue, quoteScale,false);
        this.high.value = highValue.doubleValue();
        this.high.valueFormat = formatter.formatFixed(highValue, quoteScale,false);
        this.low.value = lowValue.doubleValue();
        this.low.valueFormat = formatter.formatFixed(lowValue, quoteScale,false);
        this.close.value = closeValue.doubleValue();
        this.close.valueFormat = formatter.formatFixed(closeValue, quoteScale,false);
        this.volume.value = volumeValue.doubleValue();
        this.volume.valueFormat = formatter.formatUnit(volumeValue, 2,false);
        this.changeAmount.value = changeAmountValue.doubleValue();
        this.changeAmount.valueFormat = direction + formatter.formatFixed(changeAmountValue, quoteScale,false);
        this.changeRate.value = changeRateValue.doubleValue();
        this.changeRate.valueFormat = direction + formatter.formatFixed(changeRateValue, 2,false) + "%";
    }

    /**
     * 构建时间显示文字
     *
     * @param timeType  时间类型
     * @param formatter 日期格式化工具
     */
    public void buildTimeText(@NonNull TimeType timeType, @NonNull DateFormatter formatter) {
        this.shortTimeText = formatter.formatDateToString(getTime(), timeType.pattern());
        this.timeText = formatter.formatDateToString(getTime(), TimeType.MONTH.pattern());
    }

    public @MarkerPointType
    int getMarkerPointType() {
        return markerPointType;
    }

    public void setMarkerPointType(@MarkerPointType int markerPointType) {
        this.markerPointType = markerPointType;
    }

    public String getShortTimeText() {
        return shortTimeText;
    }

    public String getTimeText() {
        return timeText;
    }

    public ValueEntry getOpen() {
        return open;
    }

    public ValueEntry getHigh() {
        return high;
    }

    public ValueEntry getLow() {
        return low;
    }

    public ValueEntry getClose() {
        return close;
    }

    public ValueEntry getVolume() {
        return volume;
    }

    public ValueEntry getChangeAmount() {
        return changeAmount;
    }

    public ValueEntry getChangeRate() {
        return changeRate;
    }

    @Override
    public List<ValueEntry> getAnimatorEntry() {
        return animatorEntry;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nCandleEntry{" +
                "open=" + getOpen().value +
                ", high=" + getHigh().value +
                ", low=" + getLow().value +
                ", close=" + getClose().value +
                ", volume=" + getVolume().value +
                ", changeAmount=" + getChangeAmount().value +
                ", changeRate=" + getChangeRate().value +
                ", time=" + getTime() +
                '}';
    }
}