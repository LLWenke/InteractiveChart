
package com.wk.chart.drawing.candle;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;

import androidx.annotation.RequiresApi;

import com.wk.chart.R;
import com.wk.chart.compat.DisplayTypeUtils;
import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.SelectorItemEntry;
import com.wk.chart.module.FloatModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>CandleSelectorDrawing</p>
 */

public class CandleSelectorDrawing extends AbsDrawing<CandleRender, FloatModule> {
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

    private float selectedWidth;//信息选择框的宽度
    private float selectedHeight;//信息选择框的高度
    private SelectorItemEntry[] selectorInfo;//选择器信息集合
    private float selectorBorderOffset;//选择器边框偏移量
    private int itemCount = 8;//选择器中的条目数

    @Override
    public void onInit(CandleRender render, FloatModule chartModule) {
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
            selectorInfo[i] = new SelectorItemEntry();
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

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void drawOver(Canvas canvas) {
        if (!render.isHighlight()) {
            return;
        }
        //初始信息
        float textHeight = metrics.descent - metrics.ascent;
        float left;
        float top = viewRect.top + attribute.selectorMarginY + attribute.borderWidth;

        //添加选择器内容
        loadSelectorInfo();

        //动态计算选择器宽度和高度
        SelectorItemEntry firstItem = selectorInfo[0];
        float width = firstItem.getLabelPaint().measureText(firstItem.getLabel())
                + firstItem.getValuePaint().measureText(firstItem.getValue())
                + attribute.selectorPadding * 2
                + attribute.selectorIntervalX;
        this.selectedWidth = selectedWidth < width ? width : selectedWidth;
        this.selectedHeight = selectedHeight > 0 ? selectedHeight : attribute.selectorIntervalY *
                (selectorInfo.length + 1) + textHeight * selectorInfo.length;

        //负责选择器左右漂浮
        float x = render.getHighlightPoint()[0];
        if (x > viewRect.width() / 2) {
            left = viewRect.left + attribute.selectorMarginX + attribute.borderWidth;
        } else {
            left = viewRect.right - selectedWidth - attribute.selectorMarginX - attribute.borderWidth;
        }

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
        float y = top + attribute.selectorIntervalY + (textHeight - metrics.bottom - metrics.top) / 2;
        for (SelectorItemEntry item : selectorInfo) {
            //绘制label
            canvas.drawText(item.getLabel(), viewRectBuffer[0] + attribute.selectorPadding, y,
                    item.getLabelPaint());
            //绘制value
            canvas.drawText(item.getValue(),
                    viewRectBuffer[2]
                            - item.getValuePaint().measureText(item.getValue())
                            - attribute.selectorPadding, y, item.getValuePaint());
            //计算Y轴位置
            y += textHeight + attribute.selectorIntervalY;
        }
    }

    /**
     * 装载选择器的内容信息
     */
    private void loadSelectorInfo() {
        CandleEntry point = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        //时间
        selectorInfo[0]
                .setLabel(attribute.context.getString(R.string.wk_time_value))
                .setLabelPaint(labelPaint)
                .setValue(DisplayTypeUtils.selectorFormat(point.getTime(),
                        render.getAdapter().getTimeType()))
                .setValuePaint(valuePaint);
        //开
        selectorInfo[1]
                .setLabel(attribute.context.getString(R.string.wk_open))
                .setLabelPaint(labelPaint)
                .setValue(render.getAdapter().rateConversion(point.getOpen().value,
                        render.getAdapter().getScale().getQuoteScale(), true))
                .setValuePaint(valuePaint);
        //高
        selectorInfo[2]
                .setLabel(attribute.context.getString(R.string.wk_high))
                .setLabelPaint(labelPaint)
                .setValue(render.getAdapter().rateConversion(point.getHigh().value,
                        render.getAdapter().getScale().getQuoteScale(), true))
                .setValuePaint(valuePaint);
        //低
        selectorInfo[3]
                .setLabel(attribute.context.getString(R.string.wk_low))
                .setLabelPaint(labelPaint)
                .setValue(render.getAdapter().rateConversion(point.getLow().value,
                        render.getAdapter().getScale().getQuoteScale(), true))
                .setValuePaint(valuePaint);
        //收
        selectorInfo[4]
                .setLabel(attribute.context.getString(R.string.wk_close))
                .setLabelPaint(labelPaint)
                .setValue(render.getAdapter().rateConversion(point.getClose().value,
                        render.getAdapter().getScale().getQuoteScale(), true))
                .setValuePaint(valuePaint);
        //涨跌幅
        String symbol;
        TextPaint paint;
        if (point.getClose().value < point.getOpen().value) {
            paint = decreasingValuePaint;//下跌
            symbol = "";
        } else {
            paint = increasingValuePaint;//上涨或者不涨不跌
            symbol = "+";
        }
        selectorInfo[5]
                .setLabel(attribute.context.getString(R.string.wk_change_proportion))
                .setLabelPaint(labelPaint)
                .setValue(symbol.concat(point.getChangeProportion().text).concat("%"))
                .setValuePaint(paint);
        //涨跌额
        selectorInfo[6]
                .setLabel(attribute.context.getString(R.string.wk_change_amount))
                .setLabelPaint(labelPaint)
                .setValue(symbol.concat(render.getAdapter().rateConversion(point.getChangeAmount().value,
                        render.getAdapter().getScale().getQuoteScale(), true)))
                .setValuePaint(paint);
        //成交量
        selectorInfo[7]
                .setLabel(attribute.context.getString(R.string.wk_volume))
                .setLabelPaint(labelPaint)
                .setValue(ValueUtils.quantization(point.getVolume().value,
                        render.getAdapter().getScale().getBaseScale()))
                .setValuePaint(valuePaint);
    }

    @Override
    public boolean onDrawingClick(float x, float y) {
        return render.isHighlight() && Utils.contains(viewRectBuffer, x, y);
    }
}
