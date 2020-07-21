
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.IndicatorTagEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndicatorType;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.render.CandleRender;


/**
 * <p>IndicatorLabelDrawing</p>
 */

public class IndicatorLabelDrawing extends AbsDrawing<CandleRender, AbsChartModule> {
    private static final String TAG = "IndicatorLabelDrawing";
    private CandleAttribute attribute;//配置文件
    private TextPaint[] labelPaints; // 标签画笔
    private IndicatorTagEntry tagEntry;//指标标识
    private Rect rect = new Rect(); //用于测量文字的实际占用区域
    private TextPaint tagPaint;//tips画笔
    private float x;
    private float y;

    @Override
    public void onInit(CandleRender render, AbsChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();
        //tips画笔初始化
        tagPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tagPaint.setTextSize(attribute.indicatorsTextSize);
        tagPaint.setColor(attribute.indexTagColor);

        switch (attribute.indicatorsLabelLocation) {
            case RIGHT_TOP:
            case RIGHT_BOTTOM:
            case RIGHT_TOP_INSIDE:
            case RIGHT_BOTTOM_INSIDE:
                for (Paint paint : labelPaints) {
                    paint.setTextAlign(Paint.Align.RIGHT);
                }
                break;
        }
        Utils.measureTextArea(tagPaint, rect);
        float indicatorsLabelHeight = attribute.indicatorsTextMarginY * 2f + rect.height();

        switch (attribute.indicatorsLabelLocation) {
            case LEFT_TOP://左上
            case RIGHT_TOP://右上
                setMargin(0, indicatorsLabelHeight, 0, 0);
                break;
            case LEFT_BOTTOM://左下
            case RIGHT_BOTTOM://右下
                setMargin(0, 0, 0, indicatorsLabelHeight);
                break;
        }
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (null == labelPaints || null == tagEntry) {
            return;
        }
        CandleEntry entry;
        if (render.isHighlight()) {
            entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        } else if (attribute.defaultShowLastItem) {
            entry = render.getAdapter().getItem(render.getAdapter().getLastPosition());
        } else {
            return;
        }
        int indicatorType = absChartModule.getIndicatorType();
        ValueEntry[] values = entry.getIndicator(indicatorType);
        if (null == values) {
            return;
        }
        float currentX = x;
        for (int i = 0; i < labelPaints.length; i++) {
            String label;
            if (i == 0) {
                if (indicatorType == IndicatorType.VOLUME_MA) {
                    label = tagEntry.getTag().concat(ValueUtils.formatBig(entry.getVolume().value,
                            render.getAdapter().getScale().getBaseScale()));
                } else {
                    label = tagEntry.getTag();
                }
            } else {
                int position = i - 1;
                ValueEntry value = values[position];
                if (null == value) {
                    continue;
                }
                switch (indicatorType) {
                    case IndicatorType.CANDLE_MA:
                    case IndicatorType.BOLL:
                        label = tagEntry.getFlagEntries()[position].getName().concat(render.exchangeRateConversion(value.text,
                                render.getAdapter().getScale().getQuoteScale()));
                        break;
                    case IndicatorType.VOLUME_MA:
                        label = tagEntry.getFlagEntries()[position].getName().concat(ValueUtils.formatBig(value.value,
                                render.getAdapter().getScale().getBaseScale()));
                        break;
                    default:
                        label = tagEntry.getFlagEntries()[position].getName().concat(value.text);
                        break;
                }
            }
            if (TextUtils.isEmpty(label)) {
                continue;
            }
            canvas.drawText(label, currentX, y, labelPaints[i]);
            switch (attribute.indicatorsLabelLocation) {
                case LEFT_TOP_INSIDE://左上（内部）
                case LEFT_TOP://左上
                case LEFT_BOTTOM_INSIDE://左下（内部）
                case LEFT_BOTTOM://左下
                    currentX += (labelPaints[i].measureText(label) + attribute.indicatorsTextInterval);
                    break;
                case RIGHT_TOP_INSIDE://右上（内部）
                case RIGHT_TOP://右上
                case RIGHT_BOTTOM_INSIDE://右下（内部）
                case RIGHT_BOTTOM://右下
                    int z = labelPaints.length - 1 - i;
                    currentX -= (labelPaints[z].measureText(label) + attribute.indicatorsTextInterval);
                    break;
            }
        }
    }

    @Override
    public void drawOver(Canvas canvas) {

    }

    @Override
    public void onViewChange() {
        tagEntry = render.getAdapter().getBuildConfig().getIndicatorTags(absChartModule.getIndicatorType());
        if (null == tagEntry) {
            return;
        }
        IndicatorTagEntry.FlagEntry[] flagEntries = tagEntry.getFlagEntries();
        //重置指标线画笔/路径等资源
        if (null == labelPaints || labelPaints.length != flagEntries.length) {
            labelPaints = new TextPaint[flagEntries.length + 1];
            labelPaints[0] = tagPaint;
            for (int i = 0; i < flagEntries.length; i++) {
                TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                labelPaint.setTextSize(attribute.indicatorsTextSize);
                labelPaint.setColor(flagEntries[i].getColor());
                labelPaints[i + 1] = labelPaint;
            }
        }
        //计算指标文字位置信息
        switch (attribute.indicatorsLabelLocation) {
            case LEFT_TOP_INSIDE://左上（内部）
                x = viewRect.left + attribute.indicatorsTextMarginX;
                y = viewRect.top
                        + attribute.indicatorsTextMarginY
                        + rect.height()
                        + attribute.borderWidth;
                break;
            case RIGHT_TOP_INSIDE://右上（内部）
                x = viewRect.right - attribute.indicatorsTextMarginX;
                y = viewRect.top
                        + attribute.indicatorsTextMarginY
                        + rect.height()
                        + attribute.borderWidth;
                break;
            case LEFT_TOP://左上
                x = viewRect.left + attribute.indicatorsTextMarginX;
                y = viewRect.top - attribute.indicatorsTextMarginY - attribute.borderWidth;
                break;
            case RIGHT_TOP://右上
                x = viewRect.right - attribute.indicatorsTextMarginX;
                y = viewRect.top - attribute.indicatorsTextMarginY - attribute.borderWidth;
                break;
            case LEFT_BOTTOM_INSIDE://左下（内部）
                x = viewRect.left + attribute.indicatorsTextMarginX;
                y = viewRect.bottom - attribute.indicatorsTextMarginY - attribute.borderWidth;
                break;
            case RIGHT_BOTTOM_INSIDE://右下（内部）
                x = viewRect.right - attribute.indicatorsTextMarginX;
                y = viewRect.bottom - attribute.indicatorsTextMarginY - attribute.borderWidth;
                break;
            case LEFT_BOTTOM://左下
                x = viewRect.left + attribute.indicatorsTextMarginX;
                y = viewRect.bottom
                        + attribute.indicatorsTextMarginY
                        + rect.height()
                        + attribute.borderWidth;
                break;
            case RIGHT_BOTTOM://右下
                x = viewRect.right - attribute.indicatorsTextMarginX;
                y = viewRect.bottom
                        + attribute.indicatorsTextMarginY
                        + rect.height()
                        + attribute.borderWidth;
                break;
        }
    }
}
