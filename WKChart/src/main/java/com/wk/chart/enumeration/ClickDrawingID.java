package com.wk.chart.enumeration;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>ClickDrawingID</p>
 * 响应点击事件的绘制组件ID
 */
@IntDef({
        ClickDrawingID.ID_NONE,
        ClickDrawingID.ID_EXTREMUM_TAG,
        ClickDrawingID.ID_CURSOR
})
@Retention(RetentionPolicy.SOURCE)
public @interface ClickDrawingID {
    int ID_NONE = 0;//无组件处理
    int ID_EXTREMUM_TAG = 1;//极值标签组件
    int ID_CURSOR = 2;//游标指示器组件
}
