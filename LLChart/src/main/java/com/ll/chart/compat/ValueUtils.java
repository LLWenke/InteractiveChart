package com.ll.chart.compat;

import com.ll.chart.entry.ValueEntry;
import java.math.BigDecimal;

public class ValueUtils {
  private static final int[] values;
  private static final String[] units;
  private static final String[] zeros;

  static {
    values = new int[] { 1000, 1000000 };
    units = new String[] { "K", "M" };
    zeros = new String[11];
    zeros[0] = "0.";
    zeros[1] = "0.0";
    zeros[2] = "0.00";
    zeros[3] = "0.000";
    zeros[4] = "0.0000";
    zeros[5] = "0.00000";
    zeros[6] = "0.000000";
    zeros[7] = "0.0000000";
    zeros[8] = "0.00000000";
    zeros[9] = "0.000000000";
    zeros[10] = "0.0000000000";
  }

  /**
   * 构建value
   *
   * @param result 传入的value值
   * @param scale 精度
   * @return 返回构建后的value
   */
  public static ValueEntry buildValue(BigDecimal result, int scale) {
    ValueEntry value = new ValueEntry();
    StringBuilder text = new StringBuilder(result.setScale(scale, BigDecimal.ROUND_DOWN)
        .toPlainString());
    value.text = text.toString();
    value.value = Float.parseFloat(value.text);
    value.result = Long.parseLong(scale > 0 ? text.deleteCharAt(value.text.length() - 1 - scale)
        .toString() : value.text);
    return value;
  }

  /**
   * 复原value
   *
   * @param result 传入的value值
   * @param scale 精度
   * @return 返回构建后的value
   */
  public static ValueEntry recoveryValue(long result, int scale) {
    ValueEntry value = new ValueEntry();
    StringBuilder text = new StringBuilder(String.valueOf(result));
    if (scale > 0) {
      int length;
      int offset;
      if (result < 0) {
        length = text.length() - 1;
        offset = 1;
      } else {
        length = text.length();
        offset = 0;
      }
      if (scale < length) {
        text.insert(length - scale + offset, ".");
      } else {
        int zeroCount = scale - length;
        text.insert(offset, zeroCount < zeros.length ? zeros[zeroCount] : zeros[zeros.length - 1]);
      }
    }
    value.text = text.toString();
    value.value = Float.parseFloat(value.text);
    value.result = result;
    return value;
  }

  /**
   * 格式化value
   *
   * @param value 传入的value值
   * @return 返回字符串
   */
  public static String format(float value, int scale) {
    value = Float.isNaN(value) ? 0 : value;
    return new BigDecimal(String.valueOf(value)).setScale(scale, BigDecimal.ROUND_DOWN)
        .toPlainString();
  }

  //必须是排好序的
  public static String formatBig(float value) {
    String unit = "";
    int i = values.length - 1;
    while (i >= 0) {
      if (value > values[i]) {
        value /= values[i];
        unit = units[i];
        break;
      }
      i--;
    }
    return format(value, 2).concat(unit);
  }
}
