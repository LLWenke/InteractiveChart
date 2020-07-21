
package com.wk.chart.compat.attribute;

import android.content.Context;

import androidx.annotation.NonNull;

import com.wk.chart.compat.attribute.BaseAttribute;

/**
 * <p>蜡烛图属性配置类</p>
 */

public class CandleAttribute extends BaseAttribute {

    public CandleAttribute(@NonNull Context context) {
        super(context);
    }

    /**
     * 与蜡烛图有关的属性
     */


    /**
     * 与分时图有关的属性
     */
    public float timeLineWidth = 3f; // 分时线宽度
    public int timeLineColor = 0xFF52649C; // 分时线颜色
}