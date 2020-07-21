package com.wk.chart.entry;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wk.chart.compat.Utils;


/**
 * <p>FlagEntry</p>
 * 指标标志位实例
 */
public class IndicatorTagEntry {
    private String tag;
    private FlagEntry[] flagEntries;

    public IndicatorTagEntry(@Nullable String tag, FlagEntry[] flagEntries) {
        this.tag = null == tag ? "" : tag;
        this.flagEntries = null == flagEntries ? new FlagEntry[0] : flagEntries;
    }

    public String getTag() {
        return Utils.replacePlaceholder(tag, flagEntries);
    }

    public @NonNull
    FlagEntry[] getFlagEntries() {
        return flagEntries;
    }

    public static class FlagEntry {
        private String name;
        private Integer flag;
        private int color;

        public FlagEntry(String name, Integer flag, int color) {
            this.name = name;
            this.flag = flag;
            this.color = color;
        }

        public String getName() {
            return Utils.replacePlaceholder(name, this);
        }

        public Integer getFlag() {
            return flag;
        }

        public int getColor() {
            return color;
        }

    }

    @Override
    public IndicatorTagEntry clone() {
        FlagEntry[] cloneFlagEntries = new FlagEntry[flagEntries.length];
        for (int i = 0; i < flagEntries.length; i++) {
            FlagEntry entry = flagEntries[i];
            FlagEntry clone = new FlagEntry(entry.getName(), entry.getFlag(), entry.getColor());
            cloneFlagEntries[i] = clone;
        }
        return new IndicatorTagEntry(getTag(), cloneFlagEntries);
    }
}
