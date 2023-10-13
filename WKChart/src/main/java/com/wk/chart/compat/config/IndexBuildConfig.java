package com.wk.chart.compat.config;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.wk.chart.entry.IndexConfigEntry;
import com.wk.chart.enumeration.IndexType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>IndexBuildConfig</p>
 * 指标配置信息类
 */
public class IndexBuildConfig extends AbsBuildConfig {
    private final LinkedHashMap<Integer, IndexConfigEntry> defaultIndexFlagConfig;
    private final HashMap<Integer, IndexConfigEntry> indexFlags;//指标标志位信息
    private final int defaultIndexColor;//指标线条默认颜色
    private final int[] defaultIndexColors;//指标线条默认颜色数组

    public IndexBuildConfig() {
        this(new LinkedHashMap<>());
    }

    public IndexBuildConfig(LinkedHashMap<Integer, IndexConfigEntry> defaultIndexFlagConfig) {
        this.defaultIndexFlagConfig = null == defaultIndexFlagConfig ? new LinkedHashMap<>() : defaultIndexFlagConfig;
        this.indexFlags = new HashMap<>();
        this.defaultIndexColor = 0xff6a879d;
        this.defaultIndexColors = new int[]{0xffff9f00, 0xffe840b5, 0xff8b68c4, 0xff00abff, 0xff489659, 0xfffe0d5e};
    }

    /**
     * 根据指标类型返回对应的指标标志实例
     *
     * @param indexType 指标类型
     * @return 对应的指标标志实例（可能为null）
     */
    public @Nullable
    IndexConfigEntry getIndexTags(@IndexType int indexType) {
        return indexFlags.get(indexType);
    }

    /**
     * 获取默认指标配置信息
     */
    public LinkedHashMap<Integer, IndexConfigEntry> getDefaultIndexConfig() {
        if (!defaultIndexFlagConfig.isEmpty()) {
            return defaultIndexFlagConfig;
        }
        //配置蜡烛图MA
        this.defaultIndexFlagConfig.put(IndexType.CANDLE_MA, buildIndexTagEntry(
                null,
                new String[]{"MA#:", "MA#:", "MA#:", "MA#:", "MA#:", "MA#:"},
                new String[]{"MA", "MA", "MA", "MA", "MA", "MA"},
                new int[]{7, 30, 90, 0, 0, 0},
                new boolean[]{true, true, true, false, false, false},
                defaultIndexColors));
        //配置交易量MA
        this.defaultIndexFlagConfig.put(IndexType.VOLUME_MA, buildIndexTagEntry(
                "Vol(#,#):",
                new String[]{"MA#:", "MA#:"},
                new String[]{"MA", "MA"},
                new int[]{7, 15},
                new boolean[]{true, true},
                defaultIndexColors));
        //配置BOLL
        this.defaultIndexFlagConfig.put(IndexType.BOLL, buildIndexTagEntry(
                "BOLL(#)",
                new String[]{"UP:", "MB:", "DN:"},
                new String[]{"N", "P"},
                new int[]{20, 2},
                new boolean[]{true, true, true},
                defaultIndexColors));
        //配置BOLL
        this.defaultIndexFlagConfig.put(IndexType.SAR, buildIndexTagEntry(
                "SAR(#,#,#)",
                new String[]{"SAR:", "SAR:", "SAR:"},
                new String[]{"N:", "S:", "M:"},
                new int[]{4, 2, 20},
                new boolean[]{true, true, true},
                defaultIndexColors));
        //配置MACD
        this.defaultIndexFlagConfig.put(IndexType.MACD, buildIndexTagEntry(
                "MACD(#,#,#)",
                new String[]{"DIF:", "DEA:", "MACD:"},
                new String[]{"S", "L", "M"},
                new int[]{12, 26, 9},
                new boolean[]{true, true, true},
                defaultIndexColors));
        //配置KDJ
        this.defaultIndexFlagConfig.put(IndexType.KDJ, buildIndexTagEntry(
                "KDJ(#,#,#)",
                new String[]{"K:", "D:", "J:"},
                new String[]{"N", "M1-", "M2-"},
                new int[]{14, 1, 3},
                new boolean[]{true, true, true},
                defaultIndexColors));
        //配置RSI
        this.defaultIndexFlagConfig.put(IndexType.RSI, buildIndexTagEntry(
                "RSI(#,#,#)",
                new String[]{"RSI#:", "RSI#:", "RSI#:"},
                new String[]{"RSI1-", "RSI2-", "RSI3-"},
                new int[]{14, 0, 0},
                new boolean[]{true, false, false},
                defaultIndexColors));
        //配置WR
        this.defaultIndexFlagConfig.put(IndexType.WR, buildIndexTagEntry(
                null,
                new String[]{"WR(#):", "WR(#):", "WR(#):"},
                new String[]{"WR1-", "WR2-", "WR3-"},
                new int[]{14, 0, 0},
                new boolean[]{true, false, false},
                defaultIndexColors));

        return defaultIndexFlagConfig;
    }

    /**
     * 添加指标配置
     *
     * @param names  指标名称（数组）
     * @param flags  指标标识（数组）
     * @param colors 指标颜色（数组）
     */
    private IndexConfigEntry buildIndexTagEntry(String tag, String[] names, String[] terms, int[] flags, boolean[] enables, @ColorInt int[] colors) {
        if (null == names || null == flags || null == colors) {
            return null;
        }
        int count = names.length;
        IndexConfigEntry.FlagEntry[] entries = new IndexConfigEntry.FlagEntry[count];
        for (int i = 0; i < count; i++) {
            String term = "";
            if (i < terms.length) {
                term = terms[i];
            }
            int flag = 0;
            if (i < flags.length) {
                flag = flags[i];
            }
            boolean enable = false;
            if (i < enables.length) {
                enable = enables[i];
            }
            int color = defaultIndexColor;
            if (i < colors.length) {
                color = colors[i];
            }
            entries[i] = new IndexConfigEntry.FlagEntry(names[i], term, flag, color, enable);
        }
        return new IndexConfigEntry(tag, entries);
    }

    /**
     * 构建指标配置（剔除未启用的指标配置）
     */
    public void buildIndexFlags() {
        if (!indexFlags.isEmpty()) {
            return;
        }
        if (defaultIndexFlagConfig.isEmpty()) {
            getDefaultIndexConfig();
        }
        for (Map.Entry<Integer, IndexConfigEntry> item : defaultIndexFlagConfig.entrySet()) {
            int count = 0;
            IndexConfigEntry.FlagEntry[] entries = new IndexConfigEntry.FlagEntry[item.getValue().getFlagEntries().length];
            for (IndexConfigEntry.FlagEntry entry : item.getValue().getFlagEntries()) {
                if (entry.isEnable()) {
                    entries[count] = entry;
                    count++;
                }
            }
            IndexConfigEntry.FlagEntry[] copy = new IndexConfigEntry.FlagEntry[count];
            System.arraycopy(entries, 0, copy, 0, copy.length);
            this.indexFlags.put(item.getKey(), new IndexConfigEntry(item.getValue().getTag(), copy));
        }
    }

    @Override
    public boolean isInit() {
        return !indexFlags.isEmpty();
    }

}
