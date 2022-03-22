
package com.wk.chart.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.ExtremumVisible;
import com.wk.chart.interfaces.IDrawingClickListener;
import com.wk.chart.module.CandleModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>ExtremumTagDrawing</p>
 * 极值标签组件
 */

public class ExtremumTagDrawing extends AbsDrawing<CandleRender, CandleModule> implements IDrawingClickListener {
    private static final String TAG = "ExtremumTagDrawing";
    private CandleAttribute attribute;//配置文件
    // 当前可见区域内的极值画笔
    private final TextPaint extremumPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint drawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//极值标签drawable画笔
    private final Rect extremumRect = new Rect(); // 用于计算极值文字位置
    private final Rect maxDrawableRect = new Rect(); // MAX标签drawable位置区域
    private final Rect minDrawableRect = new Rect(); // MIN标签drawable位置区域
    private final float[] extremumBuffer = new float[4]; // 用于计算极值坐标
    private float drawableWidth, drawableHeight, expandWidth;//drawable的宽高,扩展宽度
    private Bitmap bitmap;//极值标签drawable转换成的Bitmap

    public ExtremumTagDrawing(int id) {
        super(id);
    }

    @Override
    public void onInit(CandleRender render, CandleModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        extremumPaint.setStyle(Paint.Style.FILL);
        extremumPaint.setTypeface(FontStyle.typeFace);
        extremumPaint.setTextSize(attribute.candleExtremumLabelSize);
        extremumPaint.setColor(attribute.candleExtremumLableColor);

        bitmap = Utils.drawableToBitmap(attribute.extremumTagDrawable);
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        canvas.save();
        canvas.clipRect(viewRect);
        CandleEntry max = render.getAdapter().getItem(render.getAdapter().getMaxYIndex());
        CandleEntry min = render.getAdapter().getItem(render.getAdapter().getMinYIndex());
        extremumBuffer[0] = render.getAdapter().getMaxYIndex() + 0.5f;
        extremumBuffer[2] = render.getAdapter().getMinYIndex() + 0.5f;
        extremumBuffer[1] = max.getHigh().value;
        extremumBuffer[3] = min.getLow().value;
        render.mapPoints(absChartModule.getMatrix(), extremumBuffer);
        // 绘制当前显示区域的最大文字（max）
        String maxValue = render.getAdapter().rateConversion(max.getHigh(), false, false);
        String text = "← ".concat(maxValue);
        extremumPaint.getTextBounds(text, 0, text.length(), extremumRect);
        //文字align调整
        float left = extremumBuffer[0];
        float top = extremumBuffer[1] + extremumRect.height() / 2f;
        float drawableLeft = left + extremumRect.width() + attribute.extremumTagDrawableMarginHorizontal;
        float currentExpandWidth = 0;
        if ((attribute.extremumTagDrawableVisible & ExtremumVisible.MAX_VISIBLE) != 0) {
            currentExpandWidth = expandWidth;
        }
        if (extremumBuffer[0] + extremumRect.width() + currentExpandWidth > viewRect.right) {
            text = maxValue.concat(" →");
            left = extremumBuffer[0] - extremumRect.width();
            drawableLeft = left - drawableWidth - attribute.extremumTagDrawableMarginHorizontal;
        }
        //绘制文字
        canvas.drawText(text, left, top, extremumPaint);
        //绘制极值标签drawable
        if (null != bitmap && currentExpandWidth > 0) {
            float drawableTop = top - drawableHeight + (drawableHeight - extremumRect.height()) / 2f;
            maxDrawableRect.set((int) drawableLeft, (int) drawableTop, (int) (drawableLeft + drawableWidth), (int) (drawableTop + drawableHeight));
            canvas.drawBitmap(bitmap, null, maxDrawableRect, drawablePaint);
            //增大maxDrawableRect的点击区域
            maxDrawableRect.set((int) (maxDrawableRect.left - attribute.candleExtremumLabelSize),
                    (int) (maxDrawableRect.top - attribute.candleExtremumLabelSize),
                    (int) (maxDrawableRect.right + attribute.candleExtremumLabelSize),
                    (int) (maxDrawableRect.bottom + attribute.candleExtremumLabelSize));
        }

        // 绘制当前显示区域的最小文字（min）
        String minValue = render.getAdapter().rateConversion(min.getLow(), false, false);
        text = "← ".concat(minValue);
        extremumPaint.getTextBounds(text, 0, text.length(), extremumRect);
        //文字align调整
        left = extremumBuffer[2];
        top = extremumBuffer[3] + extremumRect.height() / 2f;
        drawableLeft = left + extremumRect.width() + attribute.extremumTagDrawableMarginHorizontal;
        currentExpandWidth = 0;
        if ((attribute.extremumTagDrawableVisible & ExtremumVisible.MIN_VISIBLE) != 0) {
            currentExpandWidth = expandWidth;
        }
        if (extremumBuffer[2] + extremumRect.width() + currentExpandWidth > viewRect.right) {
            text = minValue.concat(" →");
            left = extremumBuffer[2] - extremumRect.width();
            drawableLeft = left - drawableWidth - attribute.extremumTagDrawableMarginHorizontal;
        }
        //绘制文字
        canvas.drawText(text, left, top, extremumPaint);
        //绘制极值标签drawable
        if (null != bitmap && currentExpandWidth > 0) {
            float drawableTop = top - drawableHeight + (drawableHeight - extremumRect.height()) / 2f;
            minDrawableRect.set((int) drawableLeft, (int) drawableTop, (int) (drawableLeft + drawableWidth), (int) (drawableTop + drawableHeight));
            canvas.drawBitmap(bitmap, null, minDrawableRect, drawablePaint);
            //增大minDrawableRect的点击区域
            minDrawableRect.set((int) (minDrawableRect.left - attribute.candleExtremumLabelSize),
                    (int) (minDrawableRect.top - attribute.candleExtremumLabelSize),
                    (int) (minDrawableRect.right + attribute.candleExtremumLabelSize),
                    (int) (minDrawableRect.bottom + attribute.candleExtremumLabelSize));
        }
        canvas.restore();
    }

    @Override
    public void onLayoutComplete() {
        if (null == bitmap) {
            return;
        }
        drawableWidth = attribute.extremumTagDrawableWidth == 0 ? bitmap.getWidth() : attribute.extremumTagDrawableWidth;
        drawableHeight = attribute.extremumTagDrawableHeight == 0 ? bitmap.getHeight() : attribute.extremumTagDrawableHeight;
        expandWidth = drawableWidth + attribute.extremumTagDrawableMarginHorizontal * 2f;
    }

    @Override
    public boolean onDrawingClick(float x, float y) {
        return maxDrawableRect.contains((int) x, (int) y) || minDrawableRect.contains((int) x, (int) y);
    }
}
