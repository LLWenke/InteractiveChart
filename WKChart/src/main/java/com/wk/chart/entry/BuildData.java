package com.wk.chart.entry;

import androidx.annotation.NonNull;

import com.wk.chart.compat.config.AbsBuildConfig;
import com.wk.chart.enumeration.ObserverArg;

import java.util.List;

public class BuildData<T extends AbsEntry, F extends AbsBuildConfig> {
    private final F buildConfig; // 构建配置信息
    private final List<T> data;//数据列表列表
    private final Integer startPosition;//起始位置
    private final ObserverArg observerArg;//观察者模式

    public BuildData(@NonNull F buildConfig,
                     @NonNull List<T> data,
                     @NonNull ObserverArg observerArg,
                     @NonNull Integer startPosition) {
        this.startPosition = startPosition;
        this.observerArg = observerArg;
        this.buildConfig = buildConfig;
        this.data = data;
    }

    public @NonNull
    Integer getStartPosition() {
        return startPosition;
    }

    public ObserverArg getObserverArg() {
        return observerArg;
    }

    public F getBuildConfig() {
        return buildConfig;
    }

    public List<T> getData() {
        return data;
    }

    public int getDataSize() {
        return data.size();
    }
}
