package com.wk.chart.entry;

import com.wk.chart.interfaces.IAnimator;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public abstract class AbsEntry implements IAnimator {
    private final Long id; // id
    private final Date time; // 时间

    protected AbsEntry(@NotNull Date time) {
        this.id = time.getTime();
        this.time = time;
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
}
