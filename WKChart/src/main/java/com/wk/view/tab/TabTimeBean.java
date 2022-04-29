package com.wk.view.tab;

import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.TimeType;

public class TabTimeBean {
    private String tabName;
    private TimeType tabValue;
    private int moduleType;
    private boolean selected;

    public TabTimeBean(String tabName, TimeType tabValue, @ModuleType int moduleType, boolean selected) {
        this.tabName = tabName;
        this.tabValue = tabValue;
        this.moduleType = moduleType;
        this.selected = selected;
    }

    public @ModuleType
    int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
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
