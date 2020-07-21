package com.wk.chart.compat.config;

public class NormalBuildConfig extends AbsBuildConfig {
    @Override
    public AbsBuildConfig clone() {
        return new NormalBuildConfig();
    }
}
