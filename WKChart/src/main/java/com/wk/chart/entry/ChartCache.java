package com.wk.chart.entry;

import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.TimeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChartCache implements Serializable {
    public TimeType timeType;
    public int beginPosition = 0;
    public float scale = 1;
    public List<TypeEntry> types;

    public ChartCache() {
        this.types = new ArrayList<>();
    }

    public static class TypeEntry implements Serializable {
        private @ModuleType
        int moduleType;
        private @ModuleGroupType
        int moduleGroupType;
        private @IndexType
        int indexType;

        public TypeEntry(@ModuleType int moduleType, @ModuleGroupType int moduleGroupType, @IndexType int indexType) {
            this.moduleType = moduleType;
            this.moduleGroupType = moduleGroupType;
            this.indexType = indexType;
        }

        public @ModuleType
        int getModuleType() {
            return moduleType;
        }

        public void setModuleType(@ModuleType int moduleType) {
            this.moduleType = moduleType;
        }

        public @ModuleGroupType
        int getModuleGroupType() {
            return moduleGroupType;
        }

        public void setModuleGroupType(@ModuleGroupType int moduleGroupType) {
            this.moduleGroupType = moduleGroupType;
        }

        public @IndexType
        int getIndexType() {
            return indexType;
        }

        public void setIndexType(@IndexType int indexType) {
            this.indexType = indexType;
        }
    }
}
