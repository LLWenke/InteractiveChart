
package com.wk.chart.compat.attribute;

import android.content.Context;

import androidx.annotation.NonNull;

import com.wk.chart.enumeration.DepthGridStyle;


/**
 * <p>深度图属性配置类</p>
 */

public class DepthAttribute extends BaseAttribute {

    public DepthAttribute(@NonNull Context context) {
        super(context);
    }

    /**
     * 与深度图有关的属性
     */
    public float polylineWidth = 6f; // 折线宽度
    public float circleSize = 12f;// 圆点大小


    /**
     * 与 grid 标尺刻度有关属性
     */
    public int depthGridStyle = DepthGridStyle.GAP_STYLE; // 深度图grid样式
}