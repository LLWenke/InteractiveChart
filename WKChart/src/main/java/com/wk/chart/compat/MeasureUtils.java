
package com.wk.chart.compat;

import android.graphics.RectF;

import androidx.annotation.Nullable;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.module.base.AbsModule;

import java.util.List;
import java.util.Map;


/**
 * <p>MeasureUtils</p>
 */

public class MeasureUtils {
    private static final String TAG = "MeasureUtils";

    /**
     * 测量模块大小
     *
     * @param attribute  视图属性配置
     * @param viewRect   视图矩形
     * @param modulesMap 图表指标模型列表
     * @param isProrate  是否为比例高度
     */
    public void measureModuleSize(BaseAttribute attribute, RectF viewRect,
                                  Map<Integer, List<AbsModule<AbsEntry>>> modulesMap,
                                  boolean isProrate) {
        int moduleGroupCount = getAttachModuleGroupCount(modulesMap);
        float floatWidth, floatHeight, modulesWidth, modulesHeight;
        float totalModulesVerticalMargin = 0f, maxModulesHorizontalMargin = 0f;
        List<AbsModule<AbsEntry>> modules = modulesMap.get(ModuleGroupType.FLOAT);
        float[] maxMargin = getModuleMaxMargin(modules);
        floatWidth = viewRect.width() - maxMargin[0] - maxMargin[2];
        floatHeight = viewRect.height() - maxMargin[1] - maxMargin[3];
        //计算Module的最大水平边距和累计垂直边距(垂直边距包含：模块间隔)
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : modulesMap.entrySet()) {
            if (item.getKey() == ModuleGroupType.FLOAT) continue;
            maxMargin = getModuleMaxMargin(item.getValue());
            maxModulesHorizontalMargin = Math.max(maxModulesHorizontalMargin, maxMargin[0] + maxMargin[2]);
            totalModulesVerticalMargin += (maxMargin[1] + maxMargin[3] + attribute.viewInterval);
        }
        //修正Module可用区域宽高
        modulesWidth = floatWidth - maxModulesHorizontalMargin;
        modulesHeight = floatHeight - totalModulesVerticalMargin + attribute.viewInterval;
        //计算Module自身的宽高
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : modulesMap.entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) continue;
                switch (item.getKey()) {
                    case ModuleGroupType.MAIN://主图
                        if (isProrate) {
                            module.onSizeChanged(modulesWidth, modulesHeight * (1f - 0.15f * (float) (moduleGroupCount - 1)));
                        } else {
                            module.onSizeChanged(modulesWidth, attribute.mainViewHeight);
                        }
                        break;
                    case ModuleGroupType.AUXILIARY: //副图
                        if (isProrate) {
                            module.onSizeChanged(modulesWidth, modulesHeight * 0.15f);
                        } else {
                            module.onSizeChanged(modulesWidth, attribute.auxiliaryViewHeight);
                        }
                        break;
                    case ModuleGroupType.INDEX:  //指标
                        if (isProrate) {
                            module.onSizeChanged(modulesWidth, modulesHeight * 0.15f);
                        } else {
                            module.onSizeChanged(modulesWidth, attribute.indexViewHeight);
                        }
                        break;
                    case ModuleGroupType.FLOAT:  //浮动
                        module.onSizeChanged(floatWidth, floatHeight);
                        break;
                }
            }
        }
    }

    /**
     * 测量视图高度(module固定高度)
     *
     * @param attribute  视图属性配置
     * @param modulesMap 图表指标模型列表
     * @return module总高度
     */
    public int measureViewHeight(BaseAttribute attribute, Map<Integer, List<AbsModule<AbsEntry>>> modulesMap) {
        float viewHeight = 0f, moduleHeight, viewInterval;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : modulesMap.entrySet()) {
            if (!moduleAttach(item.getValue())) continue;
            //计算Module的高度
            switch (item.getKey()) {
                case ModuleGroupType.MAIN://主图
                    moduleHeight = attribute.mainViewHeight;
                    viewInterval = attribute.viewInterval;
                    break;
                case ModuleGroupType.AUXILIARY: // 副图
                    moduleHeight = attribute.auxiliaryViewHeight;
                    viewInterval = attribute.viewInterval;
                    break;
                case ModuleGroupType.INDEX:  // 指标
                    moduleHeight = attribute.indexViewHeight;
                    viewInterval = attribute.viewInterval;
                    break;
                default:
                    moduleHeight = 0;
                    viewInterval = 0;
                    break;
            }
            //计算Module的垂直边距
            float[] maxMargin = getModuleMaxMargin(item.getValue());
            viewHeight += (moduleHeight + maxMargin[1] + maxMargin[3] + viewInterval);
        }
//        Log.e(TAG, "height：" + (int) Math.ceil(viewHeight - attribute.viewInterval));
        return (int) Math.ceil(viewHeight - attribute.viewInterval);
    }

    /**
     * 获取启用的分组数量
     *
     * @param modulesMap 图表指标模型列表
     * @return 启用的分组数量
     */
    private int getAttachModuleGroupCount(Map<Integer, List<AbsModule<AbsEntry>>> modulesMap) {
        int count = 0;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : modulesMap.entrySet()) {
            if (item.getKey() == ModuleGroupType.FLOAT) continue;
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isAttach()) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    /**
     * Module是否贴附到视图
     *
     * @param modules Module集合
     * @return 是否贴附到视图
     */
    private boolean moduleAttach(@Nullable List<AbsModule<AbsEntry>> modules) {
        if (null == modules) {
            return false;
        }
        for (AbsModule<AbsEntry> module : modules) {
            if (module.isAttach()) return true;
        }
        return false;
    }

    /**
     * 获取Module的边距
     *
     * @param modules Module集合
     * @return 边距 [left, top, right, bottom]
     */
    public float[] getModuleMaxMargin(@Nullable List<AbsModule<AbsEntry>> modules) {
        float[] maxMargin = new float[4];
        if (null == modules) return maxMargin;
        for (AbsModule<AbsEntry> module : modules) {
            if (!module.isAttach()) continue;
            float[] margins = module.getDrawingMargin();
            if (margins[0] > maxMargin[0]) {
                maxMargin[0] = margins[0];
            }
            if (margins[1] > maxMargin[1]) {
                maxMargin[1] = margins[1];
            }
            if (margins[2] > maxMargin[2]) {
                maxMargin[2] = margins[2];
            }
            if (margins[3] > maxMargin[3]) {
                maxMargin[3] = margins[3];
            }
        }
        return maxMargin;
    }
}
