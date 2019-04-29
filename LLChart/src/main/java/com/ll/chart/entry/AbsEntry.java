package com.ll.chart.entry;

import com.ll.chart.compat.ValueUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class AbsEntry {
  private final Date time; // 时间
  private final int scale;//精度
  private final List<ValueEntry> AnimatorEntry;//用于动画的属性列表

  public int getScale() {
    return scale;
  }

  public Date getTime() {
    return time;
  }

  public AbsEntry(Date time, int scale) {
    this.time = time;
    this.scale = scale;
    this.AnimatorEntry = new ArrayList<>();
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
   * @return 返回构建后的value
   */
  public ValueEntry buildValue(double value) {
    return ValueUtils.buildValue(new BigDecimal(String.valueOf(value)), scale);
  }

  /**
   * 构建value
   *
   * @param value 传入的value值
   * @param scale 精度
   * @return 返回构建后的value
   */
  public ValueEntry buildValue(double value, int scale) {
    return ValueUtils.buildValue(new BigDecimal(String.valueOf(value)), scale);
  }

  /**
   * 构建value
   *
   * @param value 传入的value值
   * @return 返回构建后的value
   */
  public ValueEntry buildValue(BigDecimal value) {
    return ValueUtils.buildValue(value, scale);
  }

  /**
   * 构建value
   *
   * @param value 传入的value值
   * @param scale 精度
   * @return 返回构建后的value
   */
  public ValueEntry buildValue(BigDecimal value, int scale) {
    return ValueUtils.buildValue(value, scale);
  }

  /**
   * 复原value
   *
   * @param result 传入的value值
   * @return 返回构建后的value
   */
  public ValueEntry recoveryValue(long result) {
    return ValueUtils.recoveryValue(result, scale);
  }

  /**
   * 复原value
   *
   * @param result 传入的value值
   * @param scale 精度
   * @return 返回构建后的value
   */
  public ValueEntry recoveryValue(long result, int scale) {
    return ValueUtils.recoveryValue(result, scale);
  }
}
