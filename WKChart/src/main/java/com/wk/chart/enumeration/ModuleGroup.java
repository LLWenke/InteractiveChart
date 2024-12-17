package com.wk.chart.enumeration;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 模块分组
 */
@IntDef({
        ModuleGroup.MAIN,
        ModuleGroup.INDEX,
        ModuleGroup.FLOAT
})
@Retention(RetentionPolicy.SOURCE)
public @interface ModuleGroup {
    int MAIN = 0;// 主图

    int INDEX = 1;// 指标

    int FLOAT = 2;// 浮动
}


