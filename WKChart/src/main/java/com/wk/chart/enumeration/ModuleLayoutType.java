package com.wk.chart.enumeration;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 模块布局类型
 */
@IntDef({
        ModuleLayoutType.OVERLAP,
        ModuleLayoutType.SEPARATE,
})
@Retention(RetentionPolicy.SOURCE)
public @interface ModuleLayoutType {
    int OVERLAP = 0;// 重叠

    int SEPARATE = 1;// 分离
}



