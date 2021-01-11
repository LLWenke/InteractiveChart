package com.wk.view.tab;

import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.TimeType;

public class TabTimeBean {
    private String tabName;
    private TimeType tabValue;
    private int moduleType;
    private boolean checked;

    public TabTimeBean(String tabName, TimeType tabValue, @ModuleType int moduleType, boolean checked) {
        this.tabName = tabName;
        this.tabValue = tabValue;
        this.moduleType = moduleType;
        this.checked = checked;
    }

    public int getModuleType() {
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
