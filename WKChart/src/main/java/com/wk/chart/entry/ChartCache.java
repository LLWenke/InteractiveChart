package com.wk.chart.entry;

import androidx.annotation.Nullable;

import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleGroup;
import com.wk.chart.enumeration.TimeType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChartCache implements Serializable {
    public @Nullable
    TimeType timeType;
    public float cacheMaxScrollOffset = 0f;
    public float cacheCurrentTransX = 0f;
    public float scale = 1;
    private final Map<Integer, List<TypeEntry>> typeEntryCache;

    public ChartCache() {
        this.typeEntryCache = new HashMap<>();
    }

    public Map<Integer, List<TypeEntry>> getTypeEntryCache() {
        return typeEntryCache;
    }

    public void putTypeEntry(
            @ModuleGroup int moduleGroup,
            List<TypeEntry> list
    ) {
        typeEntryCache.put(moduleGroup, list);
    }

    public static class TypeEntry implements Serializable {
        private @IndexType int moduleIndexType;
        private HashSet<Integer> attachTypeSet;

        public TypeEntry(@IndexType int moduleIndexType, HashSet<Integer> attachTypeSet) {
            this.moduleIndexType = moduleIndexType;
            this.attachTypeSet = attachTypeSet;
        }

        @IndexType
        public int getModuleIndexType() {
            return moduleIndexType;
        }

        public void setModuleIndexType(@IndexType int moduleIndexType) {
            this.moduleIndexType = moduleIndexType;
        }

        public HashSet<Integer> getAttachTypeSet() {
            return attachTypeSet;
        }

        public void setAttachTypeSet(HashSet<Integer> attachTypeSet) {
            this.attachTypeSet = attachTypeSet;
        }
    }
}