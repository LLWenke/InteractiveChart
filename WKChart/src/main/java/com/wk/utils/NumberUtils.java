package com.wk.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

    /**
     * 安全解析字符串=>Double
     *
     * @param value 字符串值
     */
    public static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            return 0d;
        }
    }

    /**
     * 安全解析字符串=>BigDecimal
     *
     * @param value 字符串值
     */
    public static BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception ignored) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 安全解析双精度=>BigDecimal (指定精度)
     * 这里使用HALF_UP是为了减少double的精度差
     *
     * @param value 字符串值
     * @param scale 精度
     */
    public static BigDecimal parseBigDecimal(double value, int scale) {
        try {
            return (new BigDecimal(String.valueOf(value))).setScale(scale, RoundingMode.DOWN);
        } catch (Exception ignored) {
            return BigDecimal.ZERO;
        }
    }
}
