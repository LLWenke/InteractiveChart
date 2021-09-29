package com.wk.chart.entry;

import androidx.annotation.NonNull;

import com.wk.chart.compat.ValueUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class AbsEntry {
    private final Date time; // 时间

    private final List<ValueEntry> AnimatorEntry;//用于动画的属性列表
    private final ScaleEntry scale;// 精度

    public Date getTime() {
        return time;
    }

    public AbsEntry(@NonNull ScaleEntry scale, Date time) {
        this.scale = scale;
        this.time = time;
        this.AnimatorEntry = new ArrayList<>();
    }

    /**
     * 获取精度配置信息实例
     *
     * @return 精度实例
     */
    public final ScaleEntry getScale() {
        return scale;
    }

    /**
     * 添加用于动画的属性
     */
    public void addAnimatorEntry(ValueEntry... AnimatorEntry) {
        this.AnimatorEntry.addAll(Arrays.asList(AnimatorEntry));
    }

    /**
     * 获取动画的属性列表
     *
     * @return 用于动画的属性列表
     */
    public List<ValueEntry> getAnimatorEntry() {
        return AnimatorEntry;
    }

    /**
     * 构建value
     *
     * @param value 传入的value值
     * @param scale 精度
     * @return 返回构建后的value
     */
    public ValueEntry buildValue(double value, int scale) {
        return ValueUtils.buildEntry(new BigDecimal(String.valueOf(value)), scale);
    }

    /**
     * 构建value
     *
     * @param value 传入的value值
     * @param scale 精度
     * @return 返回构建后的value
     */
    public ValueEntry buildValue(BigDecimal value, int scale) {
        return ValueUtils.buildEntry(value, scale);
    }

    /**
     * 构建value
     *
     * @param result 传入的value值
     * @param scale  精度
     * @return 返回构建后的value
     */
    public ValueEntry buildValue(long result, int scale) {
        return ValueUtils.buildEntry(result, scale);
    }
}
