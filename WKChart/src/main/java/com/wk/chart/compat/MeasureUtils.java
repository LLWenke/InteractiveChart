
package com.wk.chart.compat;

import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

import java.util.List;
import java.util.Map;


/**
 * <p>MeasureUtils</p>
 */

public class MeasureUtils {
    private static final String TAG = "MeasureUtils";
    private final AbsRender<?, ?> render;
    private final BaseAttribute attribute;

    public MeasureUtils(@NonNull AbsRender<?, ?> render) {
        this.render = render;
        this.attribute = render.getAttribute();
    }

    /**
     * 测量模块大小
     */
    public void measureModuleSize(float width, float height) {
        int moduleGroupCount = render.getModuleGroupCount();
        RectF rectF = measureAvailableSize(width, height);
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : render.getModules().entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isAttach()) {
                    float availableHeight = module.getModuleGroup() == ModuleGroupType.FLOAT ? (height - attribute.borderWidth * 2f) : rectF.height();
                    module.setWidth(rectF.width());
                    module.setHeight(availableHeight * getDefaultProportion(module.getModuleGroup(), moduleGroupCount));
                }
            }
        }
    }

    /**
     * 测量视图可用大小
     */
    private RectF measureAvailableSize(float width, float height) {
        float viewBorderWidthCount = attribute.borderWidth * 2f;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : render.getModules().entrySet()) {
            float marginLeft = 0f, marginTop = 0f, marginRight = 0f, marginBottom = 0f;
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isAttach()) {
                    float[] margin = module.getMargin();
                    if (margin[0] > marginLeft) {
                        marginLeft = margin[0];
                    }
                    if (margin[1] > marginTop) {
                        marginTop = margin[1];
                    }
                    if (margin[2] > marginRight) {
                        marginRight = margin[2];
                    }
                    if (margin[3] > marginBottom) {
                        marginBottom = margin[3];
                    }
                }
            }
            width -= (marginLeft + marginRight + viewBorderWidthCount);
            height -= (marginTop + marginBottom + viewBorderWidthCount + attribute.viewInterval);
//            Log.e("height" + height, "marginTop:" + marginTop + "   marginBottom:" + marginBottom);
        }
        return new RectF(attribute.borderWidth, attribute.borderWidth, width, height + attribute.viewInterval);
    }

    /**
     * 智能分配图表类型组的高度比例
     */
    private float getDefaultProportion(@ModuleGroupType int moduleGroupType, int moduleGroupCount) {
        switch (moduleGroupType) {
            case ModuleGroupType.MAIN://主图
                return 1f - 0.15f * (float) (moduleGroupCount - 1);
            case ModuleGroupType.AUXILIARY: // 副图
            case ModuleGroupType.INDEX:  // 指标
                return 0.15f;
            case ModuleGroupType.FLOAT:  // 浮动模块
                return 1f;
            case ModuleGroupType.NONE:  // 空
            default:
                return 0f;
        }
    }
}
