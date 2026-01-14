package com.wk.chart.interfaces;

import com.wk.chart.entry.ValueEntry;

import java.util.List;

public interface IAnimator {
    /**
     * 获取动画的属性列表
     *
     * @return 用于动画的属性列表
     */
    List<ValueEntry> getAnimatorEntry();
}
