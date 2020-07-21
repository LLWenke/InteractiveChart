package com.wk.chart.drawing;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.module.VolumeChartModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>VolumeDrawing K线成交量的绘制</p>
 */

public class VolumeDrawing extends AbsDrawing<CandleRender, VolumeChartModule> {
    private static final String TAG = "VolumeDrawing";
    private CandleAttribute attribute;//配置文件
    // 蜡烛图边框线画笔
    private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 上涨画笔
    private Paint increasingPaint = new Paint();
    // 下跌画笔
    private Paint decreasingPaint = new Paint();
    // 上涨路径
    private Path increasingPath = new Path();
    // 下跌路径
    private Path decreasingPath = new Path();

    private float[] rectBuffer = new float[4];

    private float borderOffset;//边框偏移量

    @Override
    public void onInit(CandleRender render, VolumeChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(attribute.borderWidth);
        borderPaint.setColor(attribute.borderColor);

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
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

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
        render.mapPoints(rectBuffer);
        //边框偏移量修正
        if (isStroke) {
            rectBuffer[0] += borderOffset;
            rectBuffer[2] -= borderOffset;
            rectBuffer[1] += borderOffset;
            rectBuffer[3] -= borderOffset;
        }
        //无成交量的一字板
        if (rectBuffer[3] - rectBuffer[1] < 2) {
            rectBuffer[1] -= 2;
        }
        path.addRect(rectBuffer[0], rectBuffer[1], rectBuffer[2], rectBuffer[3], Path.Direction.CW);
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        canvas.save();
        canvas.clipRect(viewRect);
        canvas.drawPath(decreasingPath, decreasingPaint);
        canvas.drawPath(increasingPath, increasingPaint);
        decreasingPath.reset();
        increasingPath.reset();
        canvas.restore();
    }

    @Override
    public void drawOver(Canvas canvas) {
        //绘制外层边框线
        if (attribute.borderWidth > 0) {
            canvas.drawRect(borderPts[0], borderPts[1], borderPts[2], borderPts[3], borderPaint);
        }
    }

    @Override
    public void onViewChange() {
        super.onViewChange();
    }
}
