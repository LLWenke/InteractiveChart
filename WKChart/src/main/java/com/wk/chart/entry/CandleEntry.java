package com.wk.chart.entry;

import android.graphics.Rect;
import android.util.ArrayMap;

import com.wk.chart.compat.ValueUtils;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.MarkerPointType;

import java.math.BigDecimal;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>CandleEntry</p>
 */
public class CandleEntry extends AbsEntry {
    // 初始需全部赋值的属性
    private final ValueEntry open; // 开盘价
    private final ValueEntry high; // 最高价
    private final ValueEntry low; // 最低价
    private final ValueEntry close; // 收盘价
    private final ValueEntry volume; // 量
    private final ValueEntry changeAmount; // 涨跌额
    private final ValueEntry changeProportion; // 涨跌幅
    private final ArrayMap<Integer, ValueEntry[]> index; // 指标
    private final ArrayMap<Integer, ValueEntry[]> lineIndex; // 折线指标
    private final Rect markerPointRect;//标记点位置区域矩形
    private @MarkerPointType
    int markerPointType = MarkerPointType.NORMAL; // 标记点类型
    private String timeText = ""; // 时间显示文本（正常）
    private String shortTimeText = ""; // 时间显示文本（简短）

    /**
     * 自定义 K 线图用的数据
     *
     * @param scale  精度实例
     * @param open   开盘价
     * @param high   最高价
     * @param low    最低价
     * @param close  收盘价
     * @param volume 量
     * @param time   时间
     */
    public CandleEntry(@NonNull ScaleEntry scale, double open, double high, double low, double close,
                       double volume, Date time) {
        super(scale, time);
        this.markerPointRect = new Rect();
        this.open = buildValue(open, scale.getQuoteScale());
        this.high = buildValue(high, scale.getQuoteScale());
        this.low = buildValue(low, scale.getQuoteScale());
        this.close = buildValue(close, scale.getQuoteScale());
        this.volume = buildValue(volume, scale.getBaseScale());
        this.changeAmount = buildValue(getClose().result - getOpen().result, scale.getQuoteScale());
        this.changeProportion = buildValue(ValueUtils.scaleDivide(getChangeAmount().result, getOpen().result, 4), 2);
        this.index = new ArrayMap<>();
        this.lineIndex = new ArrayMap<>();
        addAnimatorEntry(this.close, this.high, this.low, this.volume);
    }

    /**
     * 自定义 K 线图用的数据
     *
     * @param scale  精度实例
     * @param open   开盘价
     * @param high   最高价
     * @param low    最低价
     * @param close  收盘价
     * @param volume 量
     * @param time   时间
     */
    public CandleEntry(@NonNull ScaleEntry scale, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close,
                       BigDecimal volume, Date time) {
        super(scale, time);
        this.markerPointRect = new Rect();
        this.open = buildValue(open, scale.getQuoteScale());
        this.high = buildValue(high, scale.getQuoteScale());
        this.low = buildValue(low, scale.getQuoteScale());
        this.close = buildValue(close, scale.getQuoteScale());
        this.volume = buildValue(volume, scale.getBaseScale());
        this.changeAmount = buildValue(getClose().result - getOpen().result, scale.getQuoteScale());
        this.changeProportion = buildValue(ValueUtils.scaleDivide(getChangeAmount().result, getOpen().result, 4), 2);
        this.index = new ArrayMap<>();
        this.lineIndex = new ArrayMap<>();
        addAnimatorEntry(this.close, this.high, this.low, this.volume);
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

    public ValueEntry getChangeProportion() {
        return changeProportion;
    }

    /**
     * 构建QuoteScale精度的value
     *
     * @param value 值
     * @return 复原后的ValueEntry
     */
    public ValueEntry buildQuoteScaleValue(long value) {
        return buildValue(value, getScale().getQuoteScale());
    }

    /**
     * 构建BaseScale精度的value
     *
     * @param value 值
     * @return 复原后的ValueEntry
     */
    public ValueEntry buildBaseScaleValue(long value) {
        return buildValue(value, getScale().getBaseScale());
    }

    public void putLineIndex(@IndexType int indexType, ValueEntry... values) {
        this.lineIndex.put(indexType, values);
    }

    public @Nullable
    ValueEntry[] getLineIndex(@IndexType int indexType) {
        return lineIndex.get(indexType);
    }

    public void putIndex(@IndexType int indexType, ValueEntry... values) {
        this.index.put(indexType, values);
    }

    public @Nullable
    ValueEntry[] getIndex(@IndexType int indexType) {
        return index.get(indexType);
    }

    public @MarkerPointType
    int getMarkerPointType() {
        return markerPointType;
    }

    public void setMarkerPointType(@MarkerPointType int markerPointType) {
        this.markerPointType = markerPointType;
    }

    /**
     * 重置标记点位置区域坐标
     */
    public void updateMarkerRect(float left, float top, float right, float bottom) {
        this.markerPointRect.set((int) left, (int) top, (int) right, (int) bottom);
    }

    public Rect getMarkerPointRect() {
        return markerPointRect;
    }

    public String getShortTimeText() {
        return shortTimeText;
    }

    public void setShortTimeText(String shortTimeText) {
        this.shortTimeText = null == shortTimeText ? "" : shortTimeText;
    }

    public String getTimeText() {
        return timeText;
    }

    public void setTimeText(String timeText) {
        this.timeText = null == timeText ? "" : timeText;
    }

    @Override
    public String toString() {
        return "\nCandleEntry{" +
                "open=" + getOpen().text +
                ", high=" + getHigh().text +
                ", low=" + getLow().text +
                ", close=" + getClose().text +
                ", volume=" + getVolume().text +
                ", time=" + getTime() +
                '}';
    }
}