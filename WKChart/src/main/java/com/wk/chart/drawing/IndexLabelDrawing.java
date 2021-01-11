
package com.wk.chart.drawing;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.IndexConfigEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.CandleRender;


/**
 * <p>IndexLabelDrawing</p>
 */

public class IndexLabelDrawing extends AbsDrawing<CandleRender, AbsModule<?>> {
    private static final String TAG = "IndexLabelDrawing";
    private CandleAttribute attribute;//配置文件
    private TextPaint[] labelPaints; // 标签画笔
    private final Rect rect = new Rect(); //用于测量文字的实际占用区域
    private TextPaint tagPaint;//tips画笔
    private IndexConfigEntry tagEntry;//指标配置
    private float x, y, left, right, offset;
    private int lines = 1, lineItemCount = 0;//行数,行item数量

    @Override
    public void onInit(CandleRender render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();
        //tips画笔初始化
        tagPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tagPaint.setTextSize(attribute.indexTextSize);
        tagPaint.setColor(attribute.indexTagColor);
        tagPaint.setTextAlign(getTextAlign());

        Utils.measureTextArea(tagPaint, rect);
        left = viewRect.left + attribute.indexTextMarginX;
        right = viewRect.right - attribute.indexTextMarginX;
        offset = attribute.indexTextMarginY + rect.height();
    }

    @Override
    public float[] onInitMargin() {
        CandleEntry entry = render.getAdapter().getItem(render.getAdapter().getLastPosition());
        if (null == entry || null == labelPaints) {
            return super.onInitMargin();
        }
        ValueEntry[] values = getIndexValue(entry);
        if (null == values) {
            return super.onInitMargin();
        }
        lineItemCount = 0;
        String tag = getTag(tagEntry.getTagText(), entry);
        float currentX = tagPaint.measureText(tag) + attribute.indexTextInterval * 2f;
        Paint.Align align = getTextAlign();
        int count = Math.min(values.length, labelPaints.length);
        for (int i = 0; i < count; i++) {
            ValueEntry value = values[i];
            if (null == value) {
                continue;
            }
            String label = getLabel(tagEntry.getFlagEntries()[i].getNameText(), value);
            int position;
            if (align == Paint.Align.RIGHT) {
                position = labelPaints.length - 1 - i;
            } else {
                position = i;
            }
            currentX += (labelPaints[position].measureText(label) + attribute.indexTextInterval);
            if (currentX > viewRect.width() && lineItemCount == 0) {
                lineItemCount = i;
            }
        }
        lines = (int) Math.ceil(currentX / viewRect.width());
        Log.e("lines" + lines, "     lineItemCount" + lineItemCount + "   count" + count);
        float indexLabelHeight = attribute.indexTextMarginY * 2f + rect.height();
        if (lines > 1) {
            indexLabelHeight *= 2;
        }
        switch (attribute.indexLabelLocation) {
            case LEFT_TOP://左上
            case RIGHT_TOP://右上
                margin[1] = indexLabelHeight;
                break;
            case LEFT_BOTTOM://左下
            case RIGHT_BOTTOM://右下
                margin[3] = indexLabelHeight;
                break;
        }
        return margin;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (null == labelPaints || null == tagEntry) {
            return;
        }
        CandleEntry entry;
        if (render.isHighlight()) {
            entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        } else if (attribute.indexDefaultShowLastItemInfo) {
            entry = render.getAdapter().getItem(render.getAdapter().getLastPosition());
        } else {
            return;
        }
        ValueEntry[] values = getIndexValue(entry);
        float currentX = x;
        float currentY = y;
        String tag = getTag(tagEntry.getTagText(), entry);
        if (!TextUtils.isEmpty(tag)) {
            canvas.drawText(getTag(tagEntry.getTagText(), entry), currentX, currentY, tagPaint);
            currentX += (tagPaint.measureText(tag) + attribute.indexTextInterval);
        }
        if (null == values) {
            return;
        }
        int count = Math.min(values.length, labelPaints.length);
        for (int i = 0; i < count; i++) {
            ValueEntry value = values[i];
            if (null == value) {
                continue;
            }
            String label = getLabel(tagEntry.getFlagEntries()[i].getNameText(), value);
            canvas.drawText(label, currentX, currentY, labelPaints[i]);
            switch (attribute.indexLabelLocation) {
                case LEFT_TOP_INSIDE://左上（内部）
                case LEFT_TOP://左上
                case LEFT_BOTTOM_INSIDE://左下（内部）
                case LEFT_BOTTOM://左下
                    currentX += (labelPaints[i].measureText(label) + attribute.indexTextInterval);
                    break;
                case RIGHT_TOP_INSIDE://右上（内部）
                case RIGHT_TOP://右上
                case RIGHT_BOTTOM_INSIDE://右下（内部）
                case RIGHT_BOTTOM://右下
                    int z = labelPaints.length - 1 - i;
                    currentX -= (labelPaints[z].measureText(label) + attribute.indexTextInterval);
                    break;
            }
            if (lineItemCount > 0 && (i + 1) % lineItemCount == 0) {
                currentX = x;
                currentY += offset;
            }
        }
    }

    @Override
    public void drawOver(Canvas canvas) {

    }

    @Override
    public void onViewChange() {
        tagEntry = render.getAdapter().getBuildConfig().getIndexTags(absChartModule.getAttachIndexType());
        if (null == tagEntry) {
            return;
        }
        IndexConfigEntry.FlagEntry[] flagEntries = tagEntry.getFlagEntries();
        Paint.Align align = getTextAlign();
        //重置指标线画笔/路径等资源
        if (null == labelPaints || labelPaints.length != flagEntries.length) {
            labelPaints = new TextPaint[flagEntries.length];
            for (int i = 0; i < flagEntries.length; i++) {
                TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                labelPaint.setTextSize(attribute.indexTextSize);
                labelPaint.setColor(flagEntries[i].getColor());
                labelPaint.setTextAlign(align);
                labelPaints[i] = labelPaint;
            }
        }
        float lineHeight = 0;
        if (lines > 1) {
            lineHeight = offset;
        }
        //计算指标文字位置信息
        switch (attribute.indexLabelLocation) {
            case LEFT_TOP_INSIDE://左上（内部）
                x = left;
                y = viewRect.top + offset + attribute.borderWidth;
                break;
            case RIGHT_TOP_INSIDE://右上（内部）
                x = right;
                y = viewRect.top + offset + attribute.borderWidth;
                break;
            case LEFT_TOP://左上
                x = left;
                y = viewRect.top - attribute.indexTextMarginY - attribute.borderWidth - lineHeight;
                break;
            case RIGHT_TOP://右上
                x = right;
                y = viewRect.top - attribute.indexTextMarginY - attribute.borderWidth - lineHeight;
                break;
            case LEFT_BOTTOM_INSIDE://左下（内部）
                x = left;
                y = viewRect.bottom - attribute.indexTextMarginY - attribute.borderWidth - lineHeight;
                break;
            case RIGHT_BOTTOM_INSIDE://右下（内部）
                x = right;
                y = viewRect.bottom - attribute.indexTextMarginY - attribute.borderWidth - lineHeight;
                break;
            case LEFT_BOTTOM://左下
                x = left;
                y = viewRect.bottom + offset + attribute.borderWidth;
                break;
            case RIGHT_BOTTOM://右下
                x = right;
                y = viewRect.bottom + offset + attribute.borderWidth;
                break;
        }
    }


    private String getTag(String tag, CandleEntry entry) {
        if (absChartModule.getAttachIndexType() == IndexType.VOLUME_MA) {
            return tag.concat(ValueUtils.formatBig(entry.getVolume().value,
                    render.getAdapter().getScale().getBaseScale()));
        } else {
            return tag;
        }
    }

    @SuppressLint("SwitchIntDef")
    private String getLabel(String name, ValueEntry value) {
        String label;
        switch (absChartModule.getAttachIndexType()) {
            case IndexType.CANDLE_MA:
            case IndexType.BOLL:
                label = name.concat(render.exchangeRateConversion(value.text,
                        render.getAdapter().getScale().getQuoteScale()));
                break;
            case IndexType.VOLUME_MA:
                label = name.concat(ValueUtils.formatBig(value.value,
                        render.getAdapter().getScale().getBaseScale()));
                break;
            default:
                label = name.concat(value.text);
                break;
        }
        return label;
    }

    private ValueEntry[] getIndexValue(CandleEntry entry) {
        int type = absChartModule.getAttachIndexType();
        if (type == IndexType.MACD) {
            return entry.getIndex(type);
        } else {
            return entry.getLineIndex(type);
        }
    }

    private Paint.Align getTextAlign() {
        switch (attribute.indexLabelLocation) {
            case RIGHT_TOP:
            case RIGHT_BOTTOM:
            case RIGHT_TOP_INSIDE:
            case RIGHT_BOTTOM_INSIDE:
                return Paint.Align.RIGHT;
            default:
                return Paint.Align.LEFT;
        }
    }
}
