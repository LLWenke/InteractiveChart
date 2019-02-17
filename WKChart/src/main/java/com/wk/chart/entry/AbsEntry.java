package com.wk.chart.entry;

import com.wk.chart.compat.ValueUtils;
import java.math.BigDecimal;
import java.util.Date;

public abstract class AbsEntry {
  private Date time; // 时间
  private int scale;//精度

  public int getScale() {
    return scale;
  }

  public Date getTime() {
    return time;
  }

  public AbsEntry(Date time, int scale) {
    this.time = time;
    this.scale = scale;
  }

  /**
   * 构建value
   *
   * @param value 传入的value值
   * @return 返回构建后的value
   */
  ValueEntry buildValue(double value) {
    return ValueUtils.buildValue(new BigDecimal(String.valueOf(value)), scale);
  }

  /**
   * 构建value
   *
   * @param value 传入的value值
   * @param scale 精度
   * @return 返回构建后的value
   */
  ValueEntry buildValue(double value, int scale) {
    return ValueUtils.buildValue(new BigDecimal(String.valueOf(value)), scale);
  }

  /**
   * 构建value
   *
   * @param value 传入的value值
   * @return 返回构建后的value
   */
  ValueEntry buildValue(BigDecimal value) {
    return ValueUtils.buildValue(value, scale);
  }

  /**
   * 构建value
   *
   * @param value 传入的value值
   * @param scale 精度
   * @return 返回构建后的value
   */
  ValueEntry buildValue(BigDecimal value, int scale) {
    return ValueUtils.buildValue(value, scale);
  }

  /**
   * 复原value
   *
   * @param result 传入的value值
   * @return 返回构建后的value
   */
  ValueEntry recoveryValue(long result) {
    return ValueUtils.recoveryValue(result, scale);
  }

  /**
   * 复原value
   *
   * @param result 传入的value值
   * @param scale 精度
   * @return 返回构建后的value
   */
  ValueEntry recoveryValue(long result, int scale) {
    return ValueUtils.recoveryValue(result, scale);
  }
}
