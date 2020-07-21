
package com.wk.chart.compat;

import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.module.base.AuxiliaryChartModule;
import com.wk.chart.render.AbsRender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>MeasureUtils</p>
 */

public class MeasureUtils {
    private static final String TAG = "MeasureUtils";
    private final AbsRender render;
    private BaseAttribute attribute;
    private final List<AbsChartModule> chartModules = new ArrayList<>();//存储高度比例信息的集合
    private boolean isInitProportion = true;//是否初始化高度比例
    private float[] oldViewTBCoordinates;//用来储存每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...](old)
    private float[] newViewTBCoordinates;//用来储存每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...](new)
    private int enableModuleCount = 0;//启用的Module的数量
    private int separateModuleCount = 1;//启用的Module中属于分离状态的Module数量

    public MeasureUtils(@NonNull AbsRender render) {
        this.render = render;
        this.attribute = render.getAttribute();
    }

    /**
     * 计算子View高度，并返回真实高度
     */
    public float childViewHeightMeasure(float height) {
        float viewBorderWidthCount = attribute.borderWidth * 2;
        float viewIntervalCount = render.getFloatChartModule().getMargin()[1]
                + render.getFloatChartModule().getMargin()[3];
        float proportion = 1f;
        this.enableModuleCount = 0;
        this.separateModuleCount = 1;
        if (isInitProportion) {
            Collections.sort(chartModules, (arg0, arg1) -> arg1.getViewHeight().compareTo(arg0.getViewHeight()));
        }
        for (AbsChartModule item : chartModules) {
            if (!item.isEnable()) {
                continue;
            }
            separateModuleCount += (item instanceof AuxiliaryChartModule && ((AuxiliaryChartModule) item).isSeparateState()) ? 1 : 0;
            enableModuleCount++;
            viewIntervalCount += (attribute.viewInterval + item.getMargin()[1] + item.getMargin()[3] + viewBorderWidthCount);
        }
        //减去最后一个view的viewInterval+ borderWidth（也就是最后一个view没有间隔）
        viewIntervalCount -= attribute.viewInterval;
        height -= viewIntervalCount;
        float parentHeight = viewIntervalCount;
        float value;
        for (int i = 0, z = chartModules.size(), count = enableModuleCount; i < z; i++) {
            AbsChartModule item = chartModules.get(i);
            if (isInitProportion) {
                if (item.getViewHeight() == 0 && proportion < 1) {
                    value = proportion / (item.isEnable() ? --count : count);
                } else {
                    value = item.getViewHeight() / height;
                    proportion -= item.isEnable() ? value : 0;
                }
                item.setProportion(value == 0 ? getDefaultProportion(item.getModuleType()) : value);
            }
            item.setViewHeight(height * item.getProportion());
            parentHeight += item.isEnable() ? item.getViewHeight() : 0;
//            Log.e(TAG, "--Proportion：" + item.getProportion());
//            Log.e(TAG, "--viewHeight：" + item.getViewHeight());
        }
        isInitProportion = false;
        return parentHeight;
    }

    /**
     * 初始化每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...]
     * 此方法只更新top和bottom(如：y0 y1)
     */
    public void initViewCoordinates() {
        int coordinatesCount = enableModuleCount * 4;
        if (null == oldViewTBCoordinates || coordinatesCount != oldViewTBCoordinates.length) {
            oldViewTBCoordinates = new float[coordinatesCount];
            newViewTBCoordinates = new float[coordinatesCount];
        }
        for (int i = 0, z = chartModules.size(), point = -1; i < z && i < oldViewTBCoordinates.length; i++) {
            AbsChartModule item = chartModules.get(i);
            if (item.isEnable()) {
                point += 2;
                oldViewTBCoordinates[point] = item.getRect().top;
                point += 2;
                oldViewTBCoordinates[point] = item.getRect().bottom;
            }
        }
    }

    /**
     * 构建每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...]
     * 此方法只更新left和right(如：x0 x1)
     */
    public float[] buildViewLRCoordinates(float x0, float x1) {
        return buildViewLRCoordinates(x0, x1, 0, 0, null);
    }

    /**
     * 构建每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...]
     * 此方法只更新left和right(如：x0 x1)
     * 如果focusArea不为null的时候，会在focusArea所在的坐标上更新y0 y1 (即top 和 bottom)
     */
    public float[] buildViewLRCoordinates(float x0, float x1, float y0, float y1, RectF focusArea) {
        for (int i = 0; i < newViewTBCoordinates.length; i += 2) {
            newViewTBCoordinates[i] = x0;
            i += 2;
            newViewTBCoordinates[i] = x1;
            int topPosition = i - 1;
            int bottomPosition = i + 1;
            if (null != focusArea && focusArea.top <= oldViewTBCoordinates[topPosition]
                    && focusArea.bottom >= oldViewTBCoordinates[bottomPosition]) {
                newViewTBCoordinates[topPosition] = y0;
                newViewTBCoordinates[bottomPosition] = y1;
            } else {
                newViewTBCoordinates[topPosition] = oldViewTBCoordinates[topPosition];
                newViewTBCoordinates[bottomPosition] = oldViewTBCoordinates[bottomPosition];
            }
        }
        return newViewTBCoordinates;
    }

    /**
     * 根据图表类型返回默认的高度比例
     */
    private float getDefaultProportion(ModuleType moduleType) {
        switch (moduleType) {
            case CANDLE:// K线 模块高度
            case TIME:// 分时图 模块高度
                return 1f - 0.2f * (float) separateModuleCount;
            case VOLUME: // 交易量 模块高度
            case MACD:  // MACD 模块高度
            case KDJ:  // KDJ 模块高度
            case RSI:  // RSI 模块高度
            case BOLL:  // BOLL 模块高度
            case WR:// WR 模块高度
                return 0.2f;
            case DEPTH:// 深度图 模块高度
            default:
                return 1f;
        }
    }

    /**
     * 更新视图集合
     */
    public void chartModuleNotifyDataSetChanged(List<AbsChartModule<? extends AbsEntry>> modules) {
        chartModules.clear();
        chartModules.addAll(modules);
        isInitProportion = true;
    }
}
