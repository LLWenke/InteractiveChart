package com.wk.chart.drawing.depth;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.R;
import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.DepthModule;
import com.wk.chart.render.DepthRender;

/**
 * <p>深度图盘口组件</p>
 */

public class DepthPositionDrawing extends IndexDrawing<DepthRender, DepthModule> {
    private static final String TAG = "DepthPositionDrawing";
    private DepthAttribute attribute;//配置文件
    // 盘口标签的画笔
    private final TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    // 盘口买单画笔
    private final TextPaint bidPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    // 盘口卖单画笔
    private final TextPaint askPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    // 盘口刻度的画笔
    private final Paint scalePaint = new Paint();
    // 盘口刻度绘制路径
    private final Path scalePath = new Path();
    //用于测量盘口矩形bid标志文字的实际占用区域
    private final Rect bidLabelRect = new Rect();
    //用于测量盘口矩形ask标志文字的实际占用区域
    private final Rect askLabelRect = new Rect();
    // 盘口刻度线长度
    private int scaleLineLength;
    // 盘口刻度线偏移量
    private float scaleLineOffset;
    // 盘口买高、卖低
    private ValueEntry bidHigh, askLow;
    // 盘口买、卖标签
    private String bidLabel, askLabel;
    // 盘口刻度上、中位置
    private float top, center;
    // 盘口矩形标志大小、一半大小
    private float rectSize, rectSizeHalf;
    // 盘口标签文字高度、一半高度
    private float labelHeight, labelHeightHalf;
    // 盘口标签文字边距
    private float labelMargin;

    public DepthPositionDrawing() {
        super(IndexType.DEPTH);
    }

    @Override
    public void onInit(DepthRender render, DepthModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        labelPaint.setTypeface(FontStyle.typeFace);
        labelPaint.setTextSize(attribute.labelSize);
        labelPaint.setColor(attribute.labelColor);

        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setStrokeWidth(attribute.lineWidth);
        scalePaint.setColor(attribute.labelColor);

        bidPaint.setColor(attribute.increasingColor);
        bidPaint.setStyle(Paint.Style.FILL);
        bidPaint.setTextSize(attribute.labelSize);

        askPaint.setColor(attribute.decreasingColor);
        askPaint.setStyle(Paint.Style.FILL);
        askPaint.setTextSize(attribute.labelSize);

        bidLabel = attribute.context.getString(R.string.wk_bid);
        askLabel = attribute.context.getString(R.string.wk_ask);
        labelMargin = 10f;
        scaleLineLength = 10;
        labelHeight = attribute.labelSize;
        labelHeightHalf = labelHeight / 2f;
        rectSize = labelHeight;
        rectSizeHalf = rectSize / 2f;
        top = labelHeight + 30f;
        scaleLineOffset = attribute.lineWidth / 2f;

        Utils.measureTextArea(bidPaint, bidLabelRect, bidLabel);
        Utils.measureTextArea(askPaint, askLabelRect, askLabel);
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        DepthEntry entry = render.getAdapter().getItem(current);
        ValueEntry price = entry.getPrice();
        int type = entry.getType();
        if (type == DepthAdapter.BID && (null == bidHigh || price.result >= bidHigh.result)) {
            bidHigh = price;
        } else if (type == DepthAdapter.ASK && (null == askLow || price.result <= askLow.result)) {
            askLow = price;
        }
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (null == askLow || null == bidHigh) return;
        int scale = null == bidHigh.scale ? 0 : bidHigh.scale;
        String diffLabel = render.getAdapter()
                .rateConversion(askLow.result - bidHigh.result, scale, false, true);
        float bidPriceWidth = labelPaint.measureText(bidHigh.text, 0, bidHigh.text.length());
        float diffLabelWidth = labelPaint.measureText(diffLabel, 0, diffLabel.length());
        float lineWidth = Math.max(diffLabelWidth + labelMargin * 2f, 50f);
        float left = center - lineWidth / 2f;
        float right = left + lineWidth;
        float offset = ((right - left) - diffLabelWidth) / 2f;
        float rectTop = top + scaleLineLength + attribute.labelSize + 30f;
        float bidRectOffset = (rectSize - bidLabelRect.height()) / 2f;
        float askRectOffset = (rectSize - askLabelRect.height()) / 2f;
        scalePath.moveTo(left, top + scaleLineLength);
        scalePath.lineTo(left, top);
        scalePath.lineTo(right, top);
        scalePath.lineTo(right, top + scaleLineLength);
        canvas.save();
        canvas.clipRect(viewRect);
        canvas.drawPath(scalePath, scalePaint);
        canvas.drawText(diffLabel, left + offset, top + scaleLineOffset + labelHeight, labelPaint);
        canvas.drawText(
                bidHigh.text,
                left - bidPriceWidth - labelMargin,
                top - scaleLineOffset + labelHeightHalf,
                labelPaint
        );
        canvas.drawText(
                askLow.text,
                right + labelMargin,
                top - scaleLineOffset + labelHeightHalf,
                labelPaint
        );
        canvas.drawText(
                bidLabel,
                left - rectSizeHalf - bidLabelRect.width() - labelMargin,
                rectTop + bidLabelRect.height() - bidRectOffset,
                bidPaint
        );
        canvas.drawText(
                askLabel,
                right + rectSizeHalf + labelMargin,
                rectTop + askLabelRect.height() - askRectOffset,
                askPaint
        );
        canvas.drawRect(
                left - rectSizeHalf,
                rectTop,
                left + rectSizeHalf,
                rectTop + rectSize,
                bidPaint
        );
        canvas.drawRect(
                right - rectSizeHalf,
                rectTop,
                right + rectSizeHalf,
                rectTop + rectSize,
                askPaint
        );
        scalePath.rewind();
        canvas.restore();
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        this.center = viewRect.left + (viewRect.right - viewRect.left) / 2f;
    }
}