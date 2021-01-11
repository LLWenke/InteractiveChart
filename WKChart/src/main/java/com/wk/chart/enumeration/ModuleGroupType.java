package com.wk.chart.enumeration;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 模块分组类型
 */
@IntDef({
        ModuleGroupType.NONE,
        ModuleGroupType.MAIN,
        ModuleGroupType.AUXILIARY,
        ModuleGroupType.INDEX,
        ModuleGroupType.FLOAT
})
@Retention(RetentionPolicy.SOURCE)
public @interface ModuleGroupType {
    int NONE = -1;// 无

    int MAIN = 0;// 主图

    int AUXILIARY = 1;// 副图

    int INDEX = 2;// 指标

    int FLOAT = 3;// 浮动模块
}



