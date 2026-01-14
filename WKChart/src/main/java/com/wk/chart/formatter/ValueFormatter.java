package com.wk.chart.formatter;


import static com.wk.utils.NumberUtils.parseBigDecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 值格式化工具类
 * - 支持大数单位 K/M/B
 * - 支持千分号
 */
public class ValueFormatter {
    private final ThreadLocal<Map<Integer, DecimalFormat>> FORMATTER_CACHE =
            ThreadLocal.withInitial(HashMap::new);
    private final BigDecimal THOUSAND = new BigDecimal("1000");
    private final BigDecimal MILLION = new BigDecimal("1000000");
    private final BigDecimal BILLION = new BigDecimal("1000000000");
    private final BigDecimal TRILLION = new BigDecimal("1000000000000");
    private final BigDecimal[] VALUES = {THOUSAND, MILLION, BILLION, TRILLION};
    private final String[] UNITS = {"K", "M", "B", "T"};

    /**
     * 获取格式化器
     *
     * @param scale 精度
     * @return 格式化器
     */
    private DecimalFormat getFormatter(int scale) {
        Map<Integer, DecimalFormat> map = FORMATTER_CACHE.get();
        return Objects.requireNonNull(map).computeIfAbsent(scale, s -> {
            DecimalFormat df = new DecimalFormat();
            df.setGroupingUsed(true);
            df.setGroupingSize(3);
            df.setMaximumFractionDigits(s);
            df.setRoundingMode(RoundingMode.DOWN);
            return df;
        });
    }

    /**
     * 带单位格式化（大数字）(去掉末尾的0)
     *
     * @param value 数值
     * @param scale 精度
     */
    public String formatUnitStripTrailingZeros(double value, int scale) {
        return formatUnit(parseBigDecimal(String.valueOf(value)), scale, true);
    }

    /**
     * 带单位格式化（大数字）
     *
     * @param value 数值
     * @param scale 精度
     */
    public String formatUnit(double value, int scale) {
        return formatUnit(parseBigDecimal(String.valueOf(value)), scale, false);
    }

    /**
     * 带单位格式化（大数字）
     *
     * @param value 数值
     * @param scale 精度
     */
    public String formatUnit(String value, int scale) {
        return formatUnit(parseBigDecimal(value), scale, false);
    }

    /**
     * 带单位格式化（大数字）
     *
     * @param value              数值
     * @param scale              精度
     * @param stripTrailingZeros 是否去掉末尾的0
     */
    public String formatUnit(BigDecimal value, int scale, boolean stripTrailingZeros) {
        String unit = "";
        for (int i = VALUES.length - 1; i >= 0; i--) {
            if (value.compareTo(VALUES[i]) >= 0) {
                value = value.divide(VALUES[i], scale + 4, RoundingMode.DOWN);
                unit = UNITS[i];
                break;
            }
        }
        return formatFixed(value, scale, stripTrailingZeros) + unit;
    }

    /**
     * 格式化
     *
     * @param value 数值
     * @param scale 精度
     */
    public String formatFixed(double value, int scale) {
        return formatFixed(parseBigDecimal(String.valueOf(value)), scale, false);
    }

    /**
     * 格式化(去掉末尾的0)
     *
     * @param value 数值
     * @param scale 精度
     */
    public String formatFixedStripTrailingZeros(double value, int scale) {
        return formatFixed(parseBigDecimal(String.valueOf(value)), scale, true);
    }

    /**
     * 格式化
     *
     * @param value 数值
     * @param scale 精度
     */
    public String formatFixed(String value, int scale) {
        return formatFixed(parseBigDecimal(value), scale, false);
    }

    /**
     * 固定小数位 + 千分号
     *
     * @param value              数值
     * @param scale              精度
     * @param stripTrailingZeros 是否去掉末尾的0
     * @return 格式化后的字符串
     */
    public String formatFixed(BigDecimal value, int scale, boolean stripTrailingZeros) {
        DecimalFormat df = getFormatter(scale);
        if (null == df) return value.toPlainString();
        df.setMinimumFractionDigits(stripTrailingZeros ? 0 : scale);
        return df.format(value);
    }
}