
package com.wk.chart.drawing;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.IndexConfigEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.CandleRender;


/**
 * <p>指标文字标签组件</p>
 */

public class IndexLabelDrawing extends IndexDrawing<CandleRender, AbsModule<?>> {
    private static final String TAG = "IndexLabelDrawing";
    private CandleAttribute attribute;//配置文件
    private TextPaint[] labelPaints; // 标签画笔
    private final Rect rect = new Rect(); //用于测量文字的实际占用区域
    private TextPaint tagPaint;//tips画笔
    private IndexConfigEntry tagEntry;//指标配置
    private float x, y, lineHeight;
    private int lines = 1, lineItemCount = 0;//行数,行item数量

    public IndexLabelDrawing(int indexType) {
        super(indexType);
    }

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
        lineHeight = rect.height();
    }

    @Override
    public void onInitConfig() {
        tagEntry = render.getAdapter().getBuildConfig().getIndexTags(indexType);
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
    }

    @Override
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        CandleEntry entry = render.getAdapter().getItem(render.getAdapter().getLastPosition());
        if (null == entry || null == labelPaints) {
            return margin;
        }
        ValueEntry[] values = getIndexValue(entry);
        if (null == values) {
            return margin;
        }
        viewWidth -= (margin[0] + margin[2]);
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
            String label = tagEntry.getFlagEntries()[i].getNameText().concat(value.valueFormat);
            int position;
            if (align == Paint.Align.RIGHT) {
                position = labelPaints.length - 1 - i;
            } else {
                position = i;
            }
            currentX += (labelPaints[position].measureText(label) + attribute.indexTextInterval);
            if (currentX > viewWidth && lineItemCount == 0) {
                lineItemCount = i;
            }
        }
        lines = (int) Math.ceil(currentX / viewWidth);
        float height = 0;
        if ((attribute.indexLabelPosition & PositionType.OUTSIDE_VERTICAL) != 0) {
            height = attribute.indexTextMarginVertical + (attribute.indexTextMarginVertical + lineHeight) * lines;
        }
        if ((attribute.indexLabelPosition & PositionType.BOTTOM) != 0) {
            margin[3] = height;
        } else {
            margin[1] = height;
        }
        return margin;
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
            String label = tagEntry.getFlagEntries()[i].getNameText().concat(value.valueFormat);
            canvas.drawText(label, currentX, currentY, labelPaints[i]);
            if ((attribute.indexLabelPosition & PositionType.END) != 0) {
                int z = labelPaints.length - 1 - i;
                currentX -= (labelPaints[z].measureText(label) + attribute.indexTextInterval);
            } else {
                currentX += (labelPaints[i].measureText(label) + attribute.indexTextInterval);
            }
            if (lineItemCount > 0 && (i + 1) % lineItemCount == 0) {
                currentX = x;
                currentY += (lineHeight + attribute.indexTextMarginVertical);
            }
        }
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        //计算指标文字位置信息
        float[] drawingNonOverlapMargin = chartModule.getDrawingNonOverlapMargin();//非重叠边距
        float lineOffset = lines > 1 ? (lineHeight + attribute.indexTextMarginVertical) * (lines - 1) : 0;
        if ((attribute.indexLabelPosition & PositionType.END) != 0) {
            x = viewRect.right - attribute.indexTextMarginHorizontal;
        } else {
            x = viewRect.left + attribute.indexTextMarginHorizontal;
        }
        if ((attribute.indexLabelPosition & PositionType.OUTSIDE_VERTICAL) != 0) {
            if ((attribute.indexLabelPosition & PositionType.BOTTOM) != 0) {
                y = viewRect.bottom + drawingNonOverlapMargin[3] + attribute.indexTextMarginVertical;
            } else {
                y = viewRect.top - drawingNonOverlapMargin[1] - attribute.indexTextMarginVertical - lineOffset;
            }
        } else {
            if ((attribute.indexLabelPosition & PositionType.BOTTOM) != 0) {
                y = viewRect.bottom - attribute.indexTextMarginVertical - lineOffset;
            } else {
                y = viewRect.top + attribute.indexTextMarginVertical;
            }
        }
    }

    private String getTag(String tag, CandleEntry entry) {
        if (indexType == IndexType.VOLUME_MA) {
            return tag.concat(entry.getVolume().valueFormat);
        } else {
            return tag;
        }
    }

    private ValueEntry[] getIndexValue(CandleEntry entry) {
        int type = indexType;
        if (type == IndexType.MACD || type == IndexType.SAR) {
            return entry.getIndex(type);
        } else {
            return entry.getLineIndex(type);
        }
    }

    private Paint.Align getTextAlign() {
        if ((attribute.indexLabelPosition & PositionType.END) != 0) {
            return Paint.Align.RIGHT;
        } else {
            return Paint.Align.LEFT;
        }
    }
}
