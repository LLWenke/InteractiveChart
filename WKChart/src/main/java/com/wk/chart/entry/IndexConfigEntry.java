package com.wk.chart.entry;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wk.chart.compat.Utils;


/**
 * <p>IndexConfigEntry</p>
 * 指标标志位实例
 */
public class IndexConfigEntry {
    private final String tag;
    private final String tagText;
    private final FlagEntry[] flagEntries;

    public IndexConfigEntry(@Nullable String tag, FlagEntry[] flagEntries) {
        this.tag = null == tag ? "" : tag;
        this.flagEntries = null == flagEntries ? new FlagEntry[0] : flagEntries;
        this.tagText = Utils.replacePlaceholder(this.tag, this.flagEntries);
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
        private int flag;
        private final int color;
        private boolean enable;

        public FlagEntry(String name, String term, int flag, int color, boolean enable) {
            this.name = name;
            this.term = term;
            this.flag = flag;
            this.color = color;
            this.enable = enable;
            this.nameText = Utils.replacePlaceholder(name, this);
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

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
            this.nameText = Utils.replacePlaceholder(name, this);
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
    }
}
