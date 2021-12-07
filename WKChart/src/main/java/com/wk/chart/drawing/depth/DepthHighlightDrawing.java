
package com.wk.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.enumeration.HighLightStyle;
import com.wk.chart.marker.AbsMarker;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;
import com.wk.chart.render.DepthRender;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>DepthHighlightDrawing</p>
 */

public class DepthHighlightDrawing extends AbsDrawing<DepthRender, AbsModule<AbsEntry>> {
    private static final String TAG = "DepthHighlightDrawing";
    private DepthAttribute attribute;//配置文件

    private final Paint bidHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 买单高亮线条画笔
    private final Paint askHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 卖单高亮线条画笔
    private final Paint bidCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//买单圆点画笔
    private final Paint askCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//卖单圆点画笔
    private final Path highlightPath = new Path();

    private final float[] highlightPoint = new float[2];//高亮线条x，y
    private final float[] markerViewInfo = new float[4];//markerView的left,top,right,bottom信息
    private final String[] markerText = new String[2];//marker中显示的值

    private final List<AbsMarker<AbsRender<?, ?>>> markerViewList = new ArrayList<>();

    @Override
    public void onInit(DepthRender render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        bidHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bidHighlightPaint.setColor(Utils.getColorWithAlpha(attribute.increasingColor
                , attribute.shaderBeginColorAlpha));
        bidHighlightPaint.setStrokeWidth(attribute.polylineWidth);

        askHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        askHighlightPaint.setColor(Utils.getColorWithAlpha(attribute.decreasingColor
                , attribute.shaderBeginColorAlpha));
        askHighlightPaint.setStrokeWidth(attribute.polylineWidth);

        bidCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bidCirclePaint.setColor(attribute.increasingColor);
        bidCirclePaint.setStrokeWidth(attribute.circleSize);

        askCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        askCirclePaint.setColor(attribute.decreasingColor);
        askCirclePaint.setStrokeWidth(attribute.circleSize);

        if (attribute.highLightStyle == HighLightStyle.DOTTED) {
            DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10f, 5f}, 0);
            bidHighlightPaint.setPathEffect(dashPathEffect);
            askHighlightPaint.setPathEffect(dashPathEffect);
        }

        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            markerView.onInit(render);
        }
    }

    @Override
    public float[] onInitMargin() {
        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            float[] markerMargin = markerView.onInitMargin();
            margin[0] = Math.max(margin[0], markerMargin[0]);
            margin[1] = Math.max(margin[1], markerMargin[1]);
            margin[2] = Math.max(margin[2], markerMargin[2]);
            margin[3] = Math.max(margin[3], markerMargin[3]);
        }
        return margin;
    }

    @Override
    public void drawOver(Canvas canvas) {
        if (!render.isHighlight()) {
            return;
        }
        // 绘制高亮
        float top, bottom;
        Paint circlePant;//圆点画笔
        Paint highlightPaint;//高亮线画笔
        DepthEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        //获取当前焦点区域内的chartModule
        AbsModule<? extends AbsEntry> chartModule = render.getFocusModuleCache();
        if (null == chartModule) {
            return;
        }
        //区分区域
        if (entry.getType() == DepthAdapter.BID) {
            circlePant = bidCirclePaint;
            highlightPaint = bidHighlightPaint;
        } else {
            circlePant = askCirclePaint;
            highlightPaint = askHighlightPaint;
        }

        highlightPoint[0] = render.getHighlightPoint()[0];
        highlightPoint[1] = render.getHighlightPoint()[1];
        markerText[0] = entry.getTotalAmount().text;
        markerText[1] = render.getAdapter().rateConversion(entry.getPrice(), false, false);

        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            markerView.onMarkerViewMeasure(
                    chartModule.getRect(),
                    chartModule.getMatrix(),
                    highlightPoint[0],
                    highlightPoint[1],
                    markerText,
                    markerViewInfo,
                    true);
        }
        if (attribute.highLightStyle != HighLightStyle.NONE) {
            if (markerViewInfo[1] <= chartModule.getRect().top) {
                top = markerViewInfo[3];
                bottom = chartModule.getRect().bottom;
            } else {
                top = chartModule.getRect().top;
                bottom = markerViewInfo[1];
            }
            highlightPath.moveTo(highlightPoint[0], top);
            highlightPath.lineTo(highlightPoint[0], bottom);
            canvas.drawPath(highlightPath, highlightPaint);
            highlightPath.rewind();
        }
        canvas.drawCircle(highlightPoint[0], highlightPoint[1], attribute.circleSize / 2, circlePant);

        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            markerView.onMarkerViewDraw(canvas, markerText);
        }
    }

    public void addMarkerView(AbsMarker<AbsRender<?, ?>> markerView) {
        markerViewList.add(markerView);
    }
}
