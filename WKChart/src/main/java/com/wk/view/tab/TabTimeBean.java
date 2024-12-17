package com.wk.view.tab;

import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.TimeType;

public class TabTimeBean {
    private String tabName;
    private TimeType tabValue;
    @IndexType
    private int indexType;
    private boolean selected;

    public TabTimeBean(
            String tabName,
            TimeType tabValue,
            @IndexType int indexType,
            boolean selected
    ) {
        this.tabName = tabName;
        this.tabValue = tabValue;
        this.indexType = indexType;
        this.selected = selected;
    }

    public @IndexType int getIndexType() {
        return indexType;
    }

    public void setIndexType(@IndexType int indexType) {
        this.indexType = indexType;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public TimeType getTabValue() {
        return tabValue;
    }

    public void setTabValue(TimeType tabValue) {
        this.tabValue = tabValue;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
