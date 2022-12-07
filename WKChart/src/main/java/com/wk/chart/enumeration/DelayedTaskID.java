package com.wk.chart.enumeration;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>DelayedTaskID</p>
 * 延时任务ID
 */
@IntDef({
        DelayedTaskID.ID_CANCEL_HIGHLIGHT
})
@Retention(RetentionPolicy.SOURCE)
public @interface DelayedTaskID {
    int ID_CANCEL_HIGHLIGHT = 1;//延时取消高亮标识
}
