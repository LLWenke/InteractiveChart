package com.wk.chart.compat;

import com.wk.chart.entry.QuantizationEntry;
import com.wk.chart.entry.RateEntry;
import com.wk.chart.entry.ValueEntry;

import java.math.BigDecimal;

public class ValueUtils {
    private static final Long[] values;
    private static final String[] units;
    private static final String[] zeros;

    static {
        values = new Long[]{1000L, 1000000L, 1000000000L, 1000000000000L};
        units = new String[]{"K", "M", "B", "T"};
        zeros = new String[11];
        zeros[0] = "0.";
        zeros[1] = "0.0";
        zeros[2] = "0.00";
        zeros[3] = "0.000";
        zeros[4] = "0.0000";
        zeros[5] = "0.00000";
        zeros[6] = "0.000000";
        zeros[7] = "0.0000000";
        zeros[8] = "0.00000000";
        zeros[9] = "0.000000000";
        zeros[10] = "0.0000000000";
    }

    /**
     * 构建ValueEntry
     *
     * @param result 传入的value值
     * @param scale  精度
     * @return 返回构建后的ValueEntry
     */
    public static ValueEntry buildValue(BigDecimal result, int scale) {
        ValueEntry value = new ValueEntry(scale);
        result = result.setScale(scale, BigDecimal.ROUND_DOWN);
        value.text = result.toPlainString();
        value.value = result.floatValue();
        value.result = (long) (scale > 0 ? value.value * Math.pow(10, scale) : value.value);
        return value;
    }

    /**
     * 复原ValueEntry
     *
     * @param result 传入的result值
     * @param scale  精度
     * @return 返回构建后的ValueEntry
     */
    public static ValueEntry recoveryValue(long result, int scale) {
        ValueEntry value = new ValueEntry(scale);
        StringBuilder text = new StringBuilder(String.valueOf(result));
        if (scale > 0) {
            int length;
            int offset;
            if (result < 0) {
                length = text.length() - 1;
                offset = 1;
            } else {
                length = text.length();
                offset = 0;
            }
            if (scale < length) {
                text.insert(length - scale + offset, ".");
            } else {
                int zeroCount = scale - length;
                text.insert(offset, zeroCount < zeros.length ? zeros[zeroCount] : zeros[zeros.length - 1]);
            }
            value.value = (float) (result / Math.pow(10, scale));
        } else {
            value.value = result;
        }
        value.text = text.toString();
        value.result = result;
        return value;
    }

    /**
     * 复原ValueEntry中的Text属性值
     *
     * @param result 传入的result值
     * @param scale  精度
     * @return 返回构建后ValueEntry中的Text属性值
     */

    public static String recoveryText(long result, int scale) {
        StringBuilder text = new StringBuilder(String.valueOf(result));
        if (scale > 0) {
            int length;
            int offset;
            if (result < 0) {
                length = text.length() - 1;
                offset = 1;
            } else {
                length = text.length();
                offset = 0;
            }
            if (scale < length) {
                text.insert(length - scale + offset, ".");
            } else {
                int zeroCount = scale - length;
                text.insert(offset, zeroCount < zeros.length ? zeros[zeroCount] : zeros[zeros.length - 1]);
            }
        }
        return text.toString();
    }

    /**
     * 格式化value
     *
     * @param value   传入的value值
     * @param scale   精度
     * @param rate    比率(传入NULL不作比率转换)
     * @param hasUnit 带汇率标识，如：¥
     * @return 返回字符串
     */
    public static String format(float value, int scale, RateEntry rate, boolean hasUnit) {
        value = (Float.isNaN(value) || Float.isInfinite(value)) ? 0 : value;
        if (null == rate || rate.getRate().compareTo(BigDecimal.ONE) == 0) {
            return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_DOWN).toPlainString();
        }
        value = rate(value, rate);
        value = (Float.isNaN(value) || Float.isInfinite(value)) ? 0 : value;
        String text = new BigDecimal(value)
                .setScale(rate.getScale(), BigDecimal.ROUND_HALF_UP)
                .toPlainString();
        if (hasUnit) {
            text = rate.getUnit().concat(text);
        }
        return text;
    }

    /**
     * 格式化value(量化)
     *
     * @param value        传入的value值
     * @param scale        精度
     * @param rate         比率(传入NULL不作比率转换)
     * @param quantization 量化实例
     * @param hasUnit      带汇率标识，如：¥
     * @return 返回字符串
     */
    public static String format(float value, int scale, RateEntry rate, QuantizationEntry quantization, boolean hasUnit) {
        value = (Float.isNaN(value) || Float.isInfinite(value)) ? 0 : value;
        if (null == rate || rate.getRate().compareTo(BigDecimal.ONE) == 0) {
            if (value > quantization.getMinFormatNum()) {
                return quantization(value, quantization.getScale());
            }
            return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_DOWN).toPlainString();
        }
        value = rate(value, rate);
        value = (Float.isNaN(value) || Float.isInfinite(value)) ? 0 : value;
        String text;
        if (value > quantization.getMinFormatNum()) {
            text = quantization(value, quantization.getScale());
        } else {
            text = new BigDecimal(value).setScale(rate.getScale(), BigDecimal.ROUND_HALF_UP).toPlainString();
        }
        if (hasUnit) {
            text = rate.getUnit().concat(text);
        }
        return text;
    }

    /**
     * 计算比率值
     *
     * @param value 传入的value值
     * @param rate  比率(传入NULL不作比率转换)
     * @return 返回字符串
     */
    public static float rate(float value, RateEntry rate) {
        value = (Float.isNaN(value) || Float.isInfinite(value)) ? 0 : value;
        if (null == rate || rate.getRate().compareTo(BigDecimal.ONE) == 0) {
            return value;
        }
        return value * rate.getRate().floatValue();
    }

    /**
     * 量化大数值
     *
     * @param value 数值
     * @return 量化后的字符串（带量化单位）
     */
    public static String quantization(double value, int scale) {
        value = (Double.isNaN(value) || Double.isInfinite(value)) ? 0 : value;
        String unit = "";
        int i = values.length - 1;
        while (i >= 0) {
            if (value > values[i]) {
                value /= values[i];
                unit = units[i];
                break;
            }
            i--;
        }
        return new BigDecimal(value)
                .setScale(scale, BigDecimal.ROUND_DOWN)
                .stripTrailingZeros()
                .toPlainString().concat(unit);
    }
}
