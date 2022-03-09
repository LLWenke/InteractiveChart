package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.module.VolumeModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>VolumeDrawing K线成交量的绘制</p>
 */

public class VolumeDrawing extends AbsDrawing<CandleRender, VolumeModule> {
    private static final String TAG = "VolumeDrawing";
    private CandleAttribute attribute;//配置文件
    // 上涨画笔
    private final Paint increasingPaint = new Paint();
    // 下跌画笔
    private final Paint decreasingPaint = new Paint();
    // 上涨路径
    private final Path increasingPath = new Path();
    // 下跌路径
    private final Path decreasingPath = new Path();

    private final float[] rectBuffer = new float[4];

    private float borderOffset;//边框偏移量

    @Override
    public void onInit(CandleRender render, VolumeModule chartModule) {
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

        borderOffset = attribute.pointBorderWidth / 2;
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        // 设置画笔颜色
        CandleEntry entry = render.getAdapter().getItem(current);
        boolean isStroke;
        Path path;
        // 设置涨跌路径
        if (entry.getClose().value < entry.getOpen().value) {
            path = decreasingPath;//下跌路径
            isStroke = attribute.decreasingStyle == Paint.Style.STROKE;
        } else {
            path = increasingPath;//上涨或者不涨不跌路径
            isStroke = attribute.increasingStyle == Paint.Style.STROKE;
        }
        // 计算 成交量的矩形坐标
        rectBuffer[0] = current + render.pointsSpace;
        rectBuffer[1] = entry.getVolume().value;
        rectBuffer[2] = current + 1 - render.pointsSpace;
        rectBuffer[3] = extremum[1];
        render.mapPoints(absChartModule.getMatrix(), rectBuffer);
        //边框偏移量修正
        if (isStroke) {
            rectBuffer[0] += borderOffset;
            rectBuffer[2] -= borderOffset;
            rectBuffer[1] += borderOffset;
            rectBuffer[3] -= borderOffset;
        }
        //无成交量的一字板
        if (rectBuffer[3] - rectBuffer[1] < attribute.pointBorderWidth) {
            rectBuffer[1] = rectBuffer[3];
            rectBuffer[1] -= attribute.pointBorderWidth;
        }
        path.addRect(rectBuffer[0], rectBuffer[1], rectBuffer[2], rectBuffer[3], Path.Direction.CW);
    }

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
