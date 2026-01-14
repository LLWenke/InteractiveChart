package com.wk.chart.entry;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wk.chart.compat.Utils;
import com.wk.chart.formatter.ValueFormatter;
import com.wk.utils.NumberUtils;


/**
 * <p>IndexConfigEntry</p>
 * 指标标志位实例
 */
public class IndexConfigEntry {
    private final int scale;
    private final String tag;
    private final String tagText;
    private final FlagEntry[] flagEntries;

    public IndexConfigEntry(@Nullable String tag, FlagEntry[] flagEntries, int scale, ValueFormatter formatter) {
        this.scale = scale;
        this.tag = null == tag ? "" : tag;
        this.flagEntries = null == flagEntries ? new FlagEntry[0] : flagEntries;
        this.tagText = Utils.replacePlaceholder(this.tag, formatter, scale, this.flagEntries);
    }

    public int getScale() {
        return scale;
    }

    public String getTag() {
        return tag;
    }

    public String getTagText() {
        return tagText;
    }

    public @NonNull
    FlagEntry[] getFlagEntries() {
        return flagEntries;
    }

    public static class FlagEntry {
        private final String name;
        private String nameText;
        private final String term;
        private double flag;
        private final int color;
        private boolean enable;
        private final int scale;

        public FlagEntry(String name, String term, double flag, int scale, int color, boolean enable, ValueFormatter formatter) {
            this.name = name;
            this.term = term;
            this.color = color;
            this.enable = enable;
            this.scale = scale;
            this.flag = NumberUtils.parseBigDecimal(flag, scale).doubleValue();
            this.nameText = Utils.replacePlaceholder(name, formatter, scale, this);
        }

        public String getName() {
            return name;
        }

        public String getNameText() {
            return nameText;
        }

        public String getTerm() {
            return term;
        }

        public double getFlag() {
            return flag;
        }

        public void setFlag(double flag, ValueFormatter formatter) {
            this.flag = NumberUtils.parseBigDecimal(flag, scale).doubleValue();
            this.nameText = Utils.replacePlaceholder(name, formatter, scale, this);
        }

        public int getColor() {
            return color;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public int getScale() {
            return scale;
        }
    }
}
