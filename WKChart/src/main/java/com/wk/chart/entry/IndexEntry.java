package com.wk.chart.entry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wk.chart.enumeration.IndexType;

import java.util.Date;
import java.util.HashMap;

public abstract class IndexEntry extends AbsEntry {
    private final HashMap<Integer, ValueEntry[]> index; // 指标
    private final HashMap<Integer, ValueEntry[]> lineIndex; // 折线指标

    protected IndexEntry(@NonNull Date time) {
        super(time);
        this.index = new HashMap<>();
        this.lineIndex = new HashMap<>();
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
}
