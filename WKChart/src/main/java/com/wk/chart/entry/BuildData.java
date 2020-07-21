package com.wk.chart.entry;

import com.wk.chart.compat.config.AbsBuildConfig;

import java.util.List;

public class BuildData<T extends AbsEntry, F extends AbsBuildConfig> {
    private F buildConfig; // 构建配置信息
    private List<T> data;//数据列表列表

    public BuildData(F buildConfig, List<T> data) {
        this.buildConfig = buildConfig;
        this.data = data;
    }

    public F getBuildConfig() {
        return buildConfig;
    }

    public List<T> getData() {
        return data;
    }
}
