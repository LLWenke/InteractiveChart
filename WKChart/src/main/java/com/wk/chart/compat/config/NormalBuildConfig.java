package com.wk.chart.compat.config;

public class NormalBuildConfig extends AbsBuildConfig {

    private boolean isInit = false;

    public void setInit(boolean init) {
        isInit = init;
    }

    @Override
    public boolean isInit() {
        return isInit;
    }
}
