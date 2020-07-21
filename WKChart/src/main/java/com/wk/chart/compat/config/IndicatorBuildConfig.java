package com.wk.chart.compat.config;

import android.util.ArrayMap;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.wk.chart.entry.IndicatorTagEntry;
import com.wk.chart.enumeration.IndicatorType;

import java.util.Map;

/**
 * <p>IndicatorBuildConfig</p>
 * 指标配置信息类
 */
public class IndicatorBuildConfig extends AbsBuildConfig {

    private final ArrayMap<Integer, IndicatorTagEntry> indicatorFlags;//指标标志位信息
    private final int[] defaultIndicatorColor;//指标线条默认颜色

    public IndicatorBuildConfig() {
        this.indicatorFlags = new ArrayMap<>();
        this.defaultIndicatorColor = new int[]{0xff9660c4, 0xff84aad5, 0xff55b263, 0xff7F9976, 0xff34a9ff};
    }

    /**
     * 根据指标类型返回对应的指标标志实例
     *
     * @param indicatorType 指标类型
     * @return 对应的指标标志实例（可能为null）
     */
    public @Nullable
    IndicatorTagEntry getIndicatorTags(@IndicatorType int indicatorType) {
        return indicatorFlags.get(indicatorType);
    }

    /**
     * 构建默认配置信息
     */
    public IndicatorBuildConfig buildDefaultConfig() {
        //配置蜡烛图MA
        putIndicatorConfig(IndicatorType.CANDLE_MA,
                null,
                new String[]{"MA#:", "MA#:", "MA#:"},
                new int[]{7, 15, 30},
                defaultIndicatorColor);
        //配置交易量MA
        putIndicatorConfig(IndicatorType.VOLUME_MA,
                "Vol(#,#):",
                new String[]{"MA#:", "MA#:"},
                new int[]{7, 15},
                defaultIndicatorColor);
        //配置MACD
        putIndicatorConfig(IndicatorType.MACD,
                "MACD(#,#,#)",
                new String[]{"DIF:", "DEA:", "MACD:"},
                new int[]{12, 26, 9},
                defaultIndicatorColor);
        //配置KDJ
        putIndicatorConfig(IndicatorType.KDJ,
                "KDJ(#,#,#)",
                new String[]{"K:", "D:", "J:"},
                new int[]{9, 3, 3},
                defaultIndicatorColor);
        //配置RSI
        putIndicatorConfig(IndicatorType.RSI,
                "RSI(#,#,#)",
                new String[]{"RSI#:", "RSI#:", "RSI#:"},
                new int[]{6, 12, 24},
                defaultIndicatorColor);
        //配置BOLL
        putIndicatorConfig(IndicatorType.BOLL,
                "BOLL(#)",
                new String[]{"UP:", "MB:", "DN:"},
                new int[]{20, 20, 20},
                defaultIndicatorColor);
         //配置WR
        putIndicatorConfig(IndicatorType.WR,
                "WR(#)",
                new String[]{""},
                new int[]{14},
                defaultIndicatorColor);

        return this;
    }

    /**
     * 添加指标配置
     *
     * @param indicatorType 指标类型
     * @param names         指标名称（数组）
     * @param flags         指标标识（数组）
     * @param colors        指标颜色（数组）
     */
    private IndicatorBuildConfig putIndicatorConfig(@IndicatorType int indicatorType, String tag, String[] names, int[] flags, @ColorInt int[] colors) {
        if (null == names || null == flags || null == colors) {
            return this;
        }
        int count = getMin(names.length, flags.length, colors.length);
        IndicatorTagEntry.FlagEntry[] entries = new IndicatorTagEntry.FlagEntry[count];
        for (int i = 0; i < count; i++) {
            entries[i] = new IndicatorTagEntry.FlagEntry(names[i], flags[i], colors[i]);
        }
        this.indicatorFlags.put(indicatorType, new IndicatorTagEntry(tag, entries));
        return this;
    }

    /**
     * 获取最小值
     */
    private int getMin(int... num) {
        int min = Integer.MAX_VALUE;
        for (Integer item : num) {
            min = Math.min(min, item);
        }
        return min;
    }

    /**
     * 深拷贝一个配置类
     *
     * @return 配置类（复制品）
     */
    @Override
    public IndicatorBuildConfig clone() {
        IndicatorBuildConfig clone = new IndicatorBuildConfig();
        for (Map.Entry<Integer, IndicatorTagEntry> entry : this.indicatorFlags.entrySet()) {
            clone.indicatorFlags.put(entry.getKey(), entry.getValue().clone());
        }
        return clone;
    }
}
