
package com.wk.chart.compat;

import android.annotation.SuppressLint;
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
    public void measureModuleSize(float width, float height, boolean isProrate) {
        int moduleGroupCount = render.getModuleGroupCount();
        float viewHeight = height - attribute.borderWidth;
        RectF rectF = measureAvailableSize(width, height);
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : render.getModules().entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) {
                    continue;
                }
                if (isProrate) {
                    float availableHeight = module.getModuleGroup() == ModuleGroupType.FLOAT ? viewHeight : rectF.height();
                    float moduleHeight = availableHeight * getProportion(module.getModuleGroup(), moduleGroupCount);
                    module.setRect(0, 0, rectF.width(), moduleHeight);
                } else {
                    float moduleHeight = module.getModuleGroup() == ModuleGroupType.FLOAT ? viewHeight : getHeight(module.getModuleGroup());
                    module.setRect(0, 0, rectF.width(), moduleHeight);
                }
            }
        }
    }

    /**
     * 测量视图可用大小
     */
    private RectF measureAvailableSize(float width, float height) {
        float[] topMargin = null, bottomMargin = null;
        float marginLeft = 0f, marginRight = 0f, borderWidthCount = attribute.borderWidth * 2f;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : render.getModules().entrySet()) {
            float marginTop = 0f, marginBottom = 0f, viewInterval = 0f;
            boolean isValid = false;
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) {
                    continue;
                }
                isValid = true;
                float[] margin = module.getDrawingMargin();
                marginLeft = Math.max(margin[0], marginLeft);
                marginRight = Math.max(margin[2], marginRight);
                if (module.getModuleGroup() == ModuleGroupType.FLOAT) {
                    viewInterval = 0;
                    if (null == topMargin) {
                        marginTop = Math.max(margin[1], marginTop);
                    } else {
                        marginTop = margin[1] > topMargin[1] ? margin[1] - topMargin[1] : marginTop;
                    }
                    if (null == bottomMargin) {
                        marginBottom = Math.max(margin[3], marginBottom);
                    } else {
                        marginBottom = margin[3] > bottomMargin[3] ? margin[3] - bottomMargin[3] : marginBottom;
                    }
                } else {
                    topMargin = null == topMargin ? margin : topMargin;
                    bottomMargin = margin;
                    viewInterval = attribute.viewInterval;
                    marginTop = Math.max(margin[1], marginTop);
                    marginBottom = Math.max(margin[3], marginBottom);
                }
            }
            if (!isValid) {
                continue;
            }
            height -= (marginTop + marginBottom + borderWidthCount + viewInterval);
//            if (render instanceof DepthRender)
//                Log.e("height" + height, "marginTop:" + marginTop + "   marginBottom:" + marginBottom);
        }
        width -= (marginLeft + marginRight + attribute.borderWidth);
        return new RectF(0, 0, width, height + attribute.viewInterval);
    }

    /**
     * 测量视图(实际)占用高度
     */
    public int measureActualOccupyHeight() {
        float[] topMargin = null, bottomMargin = null;
        float viewHeight = 0f, borderWidthCount = attribute.borderWidth * 2f;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : render.getModules().entrySet()) {
            float marginTop = 0f, marginBottom = 0f, moduleHeight = 0f, viewInterval = 0f;
            boolean isValid = false;
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) {
                    continue;
                }
                isValid = true;
                float[] margin = module.getDrawingMargin();
                if (module.getModuleGroup() == ModuleGroupType.FLOAT) {
                    moduleHeight = 0;
                    viewInterval = 0;
                    if (null == topMargin) {
                        marginTop = Math.max(margin[1], marginTop);
                    } else {
                        marginTop = margin[1] > topMargin[1] ? margin[1] - topMargin[1] : marginTop;
                    }
                    if (null == bottomMargin) {
                        marginBottom = Math.max(margin[3], marginBottom);
                    } else {
                        marginBottom = margin[3] > bottomMargin[3] ? margin[3] - bottomMargin[3] : marginBottom;
                    }
                } else {
                    topMargin = null == topMargin ? margin : topMargin;
                    bottomMargin = margin;
                    moduleHeight += module.getRect().height();
                    viewInterval = attribute.viewInterval;
                    marginTop = Math.max(margin[1], marginTop);
                    marginBottom = Math.max(margin[3], marginBottom);
                }
            }
            if (!isValid) {
                continue;
            }
            viewHeight += (moduleHeight + marginTop + marginBottom + borderWidthCount + viewInterval);
//            if (render instanceof CandleRender)
//                Log.e("height(实际)：" + moduleHeight, "marginTop:" + marginTop + "   marginBottom:" + marginBottom + "   viewBorderWidthCount:" + borderWidthCount);
        }
        return (int) Math.ceil((viewHeight - attribute.viewInterval));
    }

    /**
     * 测量视图(预计)占用高度
     */
    public int measureEstimateOccupyHeight() {
        float[] topMargin = null, bottomMargin = null;
        float viewHeight = 0f, borderWidthCount = attribute.borderWidth * 2f;
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : render.getModules().entrySet()) {
            float marginTop = 0f, marginBottom = 0f, moduleHeight = 0f, viewInterval = 0f;
            boolean isValid = false;
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (!module.isAttach()) {
                    continue;
                }
                isValid = true;
                float[] margin = module.getDrawingMargin();
                if (module.getModuleGroup() == ModuleGroupType.FLOAT) {
                    moduleHeight = 0;
                    viewInterval = 0;
                    if (null == topMargin) {
                        marginTop = Math.max(margin[1], marginTop);
                    } else {
                        marginTop = margin[1] > topMargin[1] ? margin[1] - topMargin[1] : marginTop;
                    }
                    if (null == bottomMargin) {
                        marginBottom = Math.max(margin[3], marginBottom);
                    } else {
                        marginBottom = margin[3] > bottomMargin[3] ? margin[3] - bottomMargin[3] : marginBottom;
                    }
                } else {
                    topMargin = null == topMargin ? margin : topMargin;
                    bottomMargin = margin;
                    moduleHeight += getHeight(module.getModuleGroup());
                    viewInterval = attribute.viewInterval;
                    marginTop = Math.max(margin[1], marginTop);
                    marginBottom = Math.max(margin[3], marginBottom);
                }
            }
            if (!isValid) {
                continue;
            }
            viewHeight += (moduleHeight + marginTop + marginBottom + borderWidthCount + viewInterval);
//            Log.e("height(预计)：" + moduleHeight, "marginTop:" + marginTop + "   marginBottom:" + marginBottom + "   viewBorderWidthCount:" + borderWidthCount);
        }
        return (int) Math.ceil(viewHeight - attribute.viewInterval);
    }

    /**
     * 获取模块高度比例
     */
    private float getProportion(@ModuleGroupType int moduleGroupType, int moduleGroupCount) {
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

    /**
     * 获取模块高度
     */
    @SuppressLint("SwitchIntDef")
    private float getHeight(@ModuleGroupType int moduleGroupType) {
        switch (moduleGroupType) {
            case ModuleGroupType.MAIN://主图
                return attribute.mainViewHeight;
            case ModuleGroupType.AUXILIARY: // 副图
                return attribute.auxiliaryViewHeight;
            case ModuleGroupType.INDEX:  // 指标
                return attribute.indexViewHeight;
            default:
                return 0f;
        }
    }
}
