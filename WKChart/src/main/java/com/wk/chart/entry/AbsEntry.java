package com.wk.chart.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class AbsEntry {
    private final Date time; // 时间

    private final List<ValueEntry> AnimatorEntry;//用于动画的属性列表

    public Date getTime() {
        return time;
    }

    public AbsEntry(Date time) {
        this.time = time;
        this.AnimatorEntry = new ArrayList<>();
    }

    /**
     * 添加用于动画的属性
     */
    public void addAnimatorEntry(ValueEntry... AnimatorEntry) {
        this.AnimatorEntry.addAll(Arrays.asList(AnimatorEntry));
    }

    /**
     * 清空用于动画的属性
     */
    public void clearAnimatorEntry() {
        this.AnimatorEntry.clear();
    }

    /**
     * 获取动画的属性列表
     *
     * @return 用于动画的属性列表
     */
    public List<ValueEntry> getAnimatorEntry() {
        return AnimatorEntry;
    }
}
