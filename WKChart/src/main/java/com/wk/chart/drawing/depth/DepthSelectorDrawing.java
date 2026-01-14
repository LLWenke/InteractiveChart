
package com.wk.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import com.wk.chart.R;
import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.entry.SelectorItemEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.DepthRender;

/**
 * <p>深度图选择器组件</p>
 */

public class DepthSelectorDrawing extends IndexDrawing<DepthRender, AbsModule<AbsEntry>> {
    private static final String TAG = "DepthSelectorDrawing";
    private DepthAttribute attribute;//配置文件

    private final Paint selectorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//选择器边框画笔
    private final Paint selectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//选择器背景画笔
    private final TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//label画笔
    private final TextPaint valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//value画笔
    private final TextPaint unitPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//unit画笔

    private Paint.FontMetrics metrics;
    private final float[] viewRectBuffer = new float[4]; // 计算选择器矩形坐标用的
    private float[] drawingNonOverlapMargin;//非重叠边距

    private float selectedWidth;//信息选择框的宽度
    private float selectedHeight;//信息选择框的高度
    private SelectorItemEntry[] selectorInfo;//选择器信息集合
    private float selectorBorderOffset;//选择器边框偏移量
    private final int itemCount = 3;//选择器中的条目数

    public DepthSelectorDrawing() {
        super(IndexType.DEPTH);
    }

    @Override
    public void onInit(DepthRender render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        selectorBorderPaint.setStyle(Paint.Style.STROKE);
        selectorBorderPaint.setStrokeWidth(attribute.selectorBorderWidth);
        selectorBorderPaint.setColor(attribute.selectorBorderColor);

        selectorBackgroundPaint.setStyle(Paint.Style.FILL);
        selectorBackgroundPaint.setColor(attribute.selectorBackgroundColor);

        labelPaint.setTextSize(attribute.selectorLabelSize);
        labelPaint.setColor(attribute.selectorLabelColor);
        labelPaint.setTypeface(FontStyle.typeFace);

        valuePaint.setTextSize(attribute.selectorValueSize);
        valuePaint.setColor(attribute.selectorValueColor);
        valuePaint.setTypeface(FontStyle.typeFace);

        unitPaint.setTextSize(attribute.selectorValueSize);
        unitPaint.setColor(attribute.selectorLabelColor);
        unitPaint.setTypeface(FontStyle.boldTypeFace);

        metrics = attribute.selectorLabelSize > attribute.selectorValueSize ?
                labelPaint.getFontMetrics() : valuePaint.getFontMetrics();

        selectorBorderOffset = attribute.selectorBorderWidth / 2;
        selectorInfo = new SelectorItemEntry[itemCount];
        for (int i = 0; i < itemCount; i++) {
            selectorInfo[i] = new SelectorItemEntry();
        }
    }

    @Override
    public void drawOver(Canvas canvas) {
        if (!render.isHighlight()) {
            return;
        }
        //初始信息
        float textHeight = metrics.descent - metrics.ascent;
        float left;
        float top = viewRect.top + attribute.selectorMarginVertical + drawingNonOverlapMargin[1];

        //添加选择器内容
        loadSelectorInfo();

        //动态计算选择器宽度和高度
        float width = 0;
        for (SelectorItemEntry item : selectorInfo) {
            float textWidth = item.getLabelPaint().measureText(item.getLabel())
                    + item.getValuePaint().measureText(item.getValue())
                    + item.getUnitPaint().measureText(item.getUnit());
            width = Math.max(width, textWidth);
        }
        width += (attribute.selectorPadding * 2 + attribute.selectorIntervalHorizontal);
        this.selectedWidth = Math.max(selectedWidth, width);
        this.selectedHeight = selectedHeight > 0 ? selectedHeight : attribute.selectorIntervalVertical *
                (selectorInfo.length + 1) + textHeight * selectorInfo.length;

        //负责选择器显示位置
        left = viewRect.width() / 2 - selectedWidth / 2 - drawingNonOverlapMargin[0];

        //计算选择器坐标位置
        viewRectBuffer[0] = left;
        viewRectBuffer[1] = top;
        viewRectBuffer[2] = left + selectedWidth;
        viewRectBuffer[3] = top + selectedHeight;

        //绘制选择器外边框
        canvas.drawRoundRect(viewRectBuffer[0], viewRectBuffer[1], viewRectBuffer[2],
                viewRectBuffer[3], attribute.selectorRadius, attribute.selectorRadius,
                selectorBorderPaint);

        //绘制选择器填充背景
        canvas.drawRoundRect(viewRectBuffer[0] + selectorBorderOffset, viewRectBuffer[1] + selectorBorderOffset,
                viewRectBuffer[2] - selectorBorderOffset, viewRectBuffer[3] - selectorBorderOffset,
                attribute.selectorRadius, attribute.selectorRadius, selectorBackgroundPaint);

        //绘制选择器内容信息
        float y = top + attribute.selectorIntervalVertical + (textHeight - metrics.bottom - metrics.top) / 2;
        for (SelectorItemEntry item : selectorInfo) {
            //绘制label
            float x = viewRectBuffer[0] + attribute.selectorPadding;
            canvas.drawText(item.getLabel(), x, y, item.getLabelPaint());
            //绘制unit
            x = viewRectBuffer[2] - item.getUnitPaint().measureText(item.getUnit())
                    - attribute.selectorPadding;
            canvas.drawText(item.getUnit(), x, y, item.getUnitPaint());
            //绘制value
            x -= item.getValuePaint().measureText(item.getValue());
            canvas.drawText(item.getValue(), x, y, item.getValuePaint());
            //计算Y轴位置
            y += textHeight + attribute.selectorIntervalVertical;
        }
    }

    /**
     * 装载选择器的内容信息
     */
    private void loadSelectorInfo() {
        DepthEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        //价格
        selectorInfo[0]
                .setLabel(attribute.context.getString(R.string.wk_price))
                .setLabelPaint(labelPaint)
                .setValue(entry.getPrice().valueFormat)
                .setValuePaint(valuePaint)
                .setUnit(" ".concat(render.getAdapter().getScale().getQuoteUnit()))
                .setUnitPaint(unitPaint);

        //总量
        selectorInfo[1]
                .setLabel(attribute.context.getString(R.string.wk_total_amount))
                .setLabelPaint(labelPaint)
                .setValue(entry.getTotalAmount().valueFormat)
                .setValuePaint(valuePaint)
                .setUnit(" ".concat(render.getAdapter().getScale().getBaseUnit()))
                .setUnitPaint(unitPaint);
        //总成本
        selectorInfo[2]
                .setLabel(attribute.context.getString(R.string.wk_total_cost))
                .setLabelPaint(labelPaint)
                .setValue(entry.getTotalPrice().valueFormat)
                .setValuePaint(valuePaint)
                .setUnit(" ".concat(render.getAdapter().getScale().getQuoteUnit()))
                .setUnitPaint(unitPaint);
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        drawingNonOverlapMargin = chartModule.getDrawingNonOverlapMargin();
    }
}
