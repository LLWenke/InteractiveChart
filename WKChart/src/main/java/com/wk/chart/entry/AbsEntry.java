package com.wk.chart.entry;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class AbsEntry {
    private final Long id; // id
    private final Date time; // 时间
    private final List<ValueEntry> animatorEntry;//用于动画的属性列表

    protected AbsEntry(@NotNull Date time) {
        this.id = time.getTime();
        this.time = time;
        this.animatorEntry = new ArrayList<>();
    }

    /**
     * 获取ID
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * 获取时间
     *
     * @return 时间
     */
    public Date getTime() {
        return time;
    }

    /**
     * 添加用于动画的属性
     */
    public void addAnimatorEntry(ValueEntry... animatorEntry) {
        this.animatorEntry.addAll(Arrays.asList(animatorEntry));
    }

    /**
     * 清空用于动画的属性
     */
    public void clearAnimatorEntry() {
        this.animatorEntry.clear();
    }

    /**
     * 获取动画的属性列表
     *
     * @return 用于动画的属性列表
     */
    public List<ValueEntry> getAnimatorEntry() {
        return animatorEntry;
    }
}
