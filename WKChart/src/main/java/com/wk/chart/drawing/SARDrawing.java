
package com.wk.chart.drawing;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>SAR指标组件</p>
 */

public class SARDrawing extends IndexDrawing<CandleRender, AbsModule<?>> {
    private CandleAttribute attribute;//配置文件
    // 上涨画笔
    private final Paint increasingPaint = new Paint();
    // 下跌画笔
    private final Paint decreasingPaint = new Paint();
    // 上涨路径
    private final Path increasingPath = new Path();
    // 下跌路径
    private final Path decreasingPath = new Path();
    //数据点矩形坐标
    private final float[] rectBuffer = new float[4];
    //数据点矩形边框线偏移量
    private float pointBorderOffset;
    //数据点大小偏移量
    private float pointSizeOffset;

    public SARDrawing() {
        super(IndexType.SAR);
    }

    @Override
    public void onInit(CandleRender render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        increasingPaint.setStyle(attribute.increasingStyle);
        increasingPaint.setStrokeWidth(attribute.pointBorderWidth);
        increasingPaint.setColor(Utils.getColorWithAlpha(attribute.increasingColor
                , attribute.darkColorAlpha));

        decreasingPaint.setStyle(attribute.decreasingStyle);
        decreasingPaint.setStrokeWidth(attribute.pointBorderWidth);
        decreasingPaint.setColor(Utils.getColorWithAlpha(attribute.decreasingColor
                , attribute.darkColorAlpha));

        pointBorderOffset = attribute.pointBorderWidth / 2f;
        pointSizeOffset = attribute.pointSize / 2f;
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        ValueEntry[] values = render.getAdapter().getItem(current).getIndex(indexType);
        if (null == values || values.length == 0) {
            return;
        }
        Path path;
        boolean isStroke;
        ValueEntry value = values[0];
        CandleEntry entry = render.getAdapter().getItem(current);
        // 设置涨跌路径
        if (entry.getClose().result < value.result) {
            path = decreasingPath;//下跌路径
            isStroke = attribute.decreasingStyle == Paint.Style.STROKE;
        } else {
            path = increasingPath;//上涨或者不涨不跌路径
            isStroke = attribute.increasingStyle == Paint.Style.STROKE;
        }
        //计算SAR指标点的矩形坐标
        rectBuffer[0] = current + 0.5f;
        rectBuffer[1] = value.value;
        rectBuffer[2] = current + 0.5f;
        rectBuffer[3] = value.value;
        render.mapPoints(chartModule.getMatrix(), rectBuffer);
        rectBuffer[0] -= pointSizeOffset;
        rectBuffer[1] -= pointSizeOffset;
        rectBuffer[2] += pointSizeOffset;
        rectBuffer[3] += pointSizeOffset;
        //边框偏移量修正
        if (isStroke) {
            rectBuffer[0] += pointBorderOffset;
            rectBuffer[2] -= pointBorderOffset;
            rectBuffer[1] += pointBorderOffset;
            rectBuffer[3] -= pointBorderOffset;
        }
        path.addRect(rectBuffer[0], rectBuffer[1], rectBuffer[2], rectBuffer[3], Path.Direction.CW);
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        canvas.save();
        canvas.clipRect(viewRect);
        decreasingPath.close();
        increasingPath.close();
        canvas.drawPath(decreasingPath, decreasingPaint);
        canvas.drawPath(increasingPath, increasingPaint);
        decreasingPath.rewind();
        increasingPath.rewind();
        canvas.restore();
    }
}