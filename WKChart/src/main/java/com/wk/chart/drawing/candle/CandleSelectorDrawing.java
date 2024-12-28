
package com.wk.chart.drawing.candle;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import com.wk.chart.R;
import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.SelectorItemEntry;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>CandleSelectorDrawing</p>
 */

public class CandleSelectorDrawing extends AbsDrawing<CandleRender, AbsModule<AbsEntry>> {
    private static final String TAG = "CandleSelectorDrawing";

    private CandleAttribute attribute;//配置文件

    private final Paint selectorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//选择器边框画笔
    private final Paint selectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//选择器背景画笔
    private final TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//label画笔
    private final TextPaint valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//value画笔(默认)
    private final TextPaint increasingValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//上涨value画笔
    private final TextPaint decreasingValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//下跌value画笔

    private Paint.FontMetrics metrics;
    private final float[] viewRectBuffer = new float[4]; // 计算选择器矩形坐标用的
    private float[] drawingNonOverlapMargin;//非重叠边距

    private float selectedWidth;//信息选择框的宽度
    private float selectedHeight;//信息选择框的高度
    private SelectorItemEntry[] selectorInfo;//选择器信息集合
    private float selectorBorderOffset;//选择器边框偏移量
    private final int itemCount = 8;//选择器中的条目数

    @Override
    public void onInit(CandleRender render, AbsModule<AbsEntry> chartModule) {
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

        increasingValuePaint.setTextSize(attribute.selectorValueSize);
        increasingValuePaint.setColor(attribute.increasingColor);
        increasingValuePaint.setTypeface(FontStyle.typeFace);

        decreasingValuePaint.setTextSize(attribute.selectorValueSize);
        decreasingValuePaint.setColor(attribute.decreasingColor);
        decreasingValuePaint.setTypeface(FontStyle.typeFace);

        metrics = attribute.selectorLabelSize > attribute.selectorValueSize ?
                labelPaint.getFontMetrics() : valuePaint.getFontMetrics();

        selectorBorderOffset = attribute.selectorBorderWidth / 2;
        selectorInfo = new SelectorItemEntry[itemCount];
        for (int i = 0; i < itemCount; i++) {
            selectorInfo[i] = new SelectorItemEntry()
                    .setLabelPaint(labelPaint)
                    .setValuePaint(valuePaint)
                    .setLabel(getLabel(i));
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
        SelectorItemEntry firstItem = selectorInfo[0];
        float width = firstItem.getLabelPaint().measureText(firstItem.getLabel())
                + firstItem.getValuePaint().measureText(firstItem.getValue())
                + attribute.selectorPadding * 2
                + attribute.selectorIntervalHorizontal;
        this.selectedWidth = Math.max(selectedWidth, width);
        this.selectedHeight =
                selectedHeight > 0 ? selectedHeight : attribute.selectorIntervalVertical *
                        (selectorInfo.length + 1) + textHeight * selectorInfo.length;

        //负责选择器左右漂浮
        float x = render.getHighlightPoint()[0];
        if (x > viewRect.width() / 2) {
            left = viewRect.left + attribute.selectorMarginHorizontal + drawingNonOverlapMargin[0];
        } else {
            left = viewRect.right - selectedWidth - attribute.selectorMarginHorizontal
                    - drawingNonOverlapMargin[2];
        }

        //计算选择器坐标位置
        viewRectBuffer[0] = left;
        viewRectBuffer[1] = top;
        viewRectBuffer[2] = left + selectedWidth;
        viewRectBuffer[3] = top + selectedHeight;

        //绘制选择器外边框
        canvas.drawRoundRect(viewRectBuffer[0], viewRectBuffer[1], viewRectBuffer[2],
                viewRectBuffer[3], attribute.selectorRadius, attribute.selectorRadius,
                selectorBorderPaint
        );

        //绘制选择器填充背景
        canvas.drawRoundRect(
                viewRectBuffer[0] + selectorBorderOffset,
                viewRectBuffer[1] + selectorBorderOffset,
                viewRectBuffer[2] - selectorBorderOffset,
                viewRectBuffer[3] - selectorBorderOffset,
                attribute.selectorRadius,
                attribute.selectorRadius,
                selectorBackgroundPaint
        );

        //绘制选择器内容信息
        float y =
                top + attribute.selectorIntervalVertical + (textHeight - metrics.bottom - metrics.top) / 2;
        for (SelectorItemEntry item : selectorInfo) {
            //绘制label
            canvas.drawText(item.getLabel(), viewRectBuffer[0] + attribute.selectorPadding, y,
                    item.getLabelPaint()
            );
            //绘制value
            canvas.drawText(item.getValue(),
                    viewRectBuffer[2]
                            - item.getValuePaint().measureText(item.getValue())
                            - attribute.selectorPadding, y, item.getValuePaint()
            );
            //计算Y轴位置
            y += textHeight + attribute.selectorIntervalVertical;
        }
    }

    /**
     * 装载选择器的内容信息
     */
    private void loadSelectorInfo() {
        CandleEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        //时间
        selectorInfo[0].setValue(entry.getTimeText());
        //开
        selectorInfo[1].setValue(render.getAdapter().rateConversion(entry.getOpen(), false, false));
        //高
        selectorInfo[2].setValue(render.getAdapter().rateConversion(entry.getHigh(), false, false));
        //低
        selectorInfo[3].setValue(render.getAdapter().rateConversion(entry.getLow(), false, false));
        //收
        selectorInfo[4].setValue(render.getAdapter()
                .rateConversion(entry.getClose(), false, false));
        String symbol;
        TextPaint paint;
        if (entry.getClose().value < entry.getOpen().value) {
            paint = decreasingValuePaint;//下跌
            symbol = "";
        } else {
            paint = increasingValuePaint;//上涨或者不涨不跌
            symbol = "+";
        }
        //涨跌幅
        selectorInfo[5].setValue(symbol.concat(entry.getChangeProportion().text).concat("%"))
                .setValuePaint(paint);
        //涨跌额
        selectorInfo[6].setValue(symbol.concat(render.getAdapter()
                        .rateConversion(entry.getChangeAmount(), false, true)))
                .setValuePaint(paint);
        //成交量
        selectorInfo[7].setValue(render.getAdapter()
                .quantizationConversion(entry.getVolume(), true));
    }

    /**
     * 获取选择器的Label
     */
    private String getLabel(int position) {
        switch (position) {
            case 0:
                return attribute.context.getString(R.string.wk_time_value);
            case 1:
                return attribute.context.getString(R.string.wk_open);
            case 2:
                return attribute.context.getString(R.string.wk_high);
            case 3:
                return attribute.context.getString(R.string.wk_low);
            case 4:
                return attribute.context.getString(R.string.wk_close);
            case 5:
                return attribute.context.getString(R.string.wk_change_proportion);
            case 6:
                return attribute.context.getString(R.string.wk_change_amount);
            case 7:
                return attribute.context.getString(R.string.wk_volume);
        }
        return "";
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        drawingNonOverlapMargin = chartModule.getDrawingNonOverlapMargin();
    }
}
