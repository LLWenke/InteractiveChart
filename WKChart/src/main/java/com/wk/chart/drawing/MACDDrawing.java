
package com.wk.chart.drawing;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.module.MACDChartModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>MACDDrawing</p>
 */

public class MACDDrawing extends AbsDrawing<CandleRender, MACDChartModule> {
    private static final String TAG = "MACDDrawing";
    private CandleAttribute attribute;//配置文件

    private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //边框画笔
    private Paint centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //中心线画笔

    private Paint deaPaint = new Paint();
    private Paint diffPaint = new Paint();
    // 上涨蜡烛图画笔
    private Paint increasingPaint = new Paint();
    // 下跌蜡烛图画笔
    private Paint decreasingPaint = new Paint();
    // 上涨路径
    private Path increasingPath = new Path();
    // 下跌路径
    private Path decreasingPath = new Path();
    // dea绘制路径
    private Path deaPath = new Path();
    // diff绘制路径
    private Path diffPath = new Path();
    // 折线路径位置信息
    private final float[] pathPts = new float[4];

    private float[] gridBuffer = new float[2];

    private float[] rectBuffer = new float[4];

    private float borderOffset;//边框偏移量

    @Override
    public void onInit(CandleRender render, MACDChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        centerLinePaint.setColor(attribute.centerLineColor);
        centerLinePaint.setStrokeWidth(attribute.lineWidth);
        centerLinePaint.setStyle(Paint.Style.FILL);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(attribute.borderWidth);
        borderPaint.setColor(attribute.borderColor);

        diffPaint.setStyle(Paint.Style.STROKE);
        diffPaint.setStrokeWidth(attribute.lineWidth);
        diffPaint.setColor(attribute.line1Color);

        deaPaint.setStyle(Paint.Style.STROKE);
        deaPaint.setStrokeWidth(attribute.lineWidth);
        deaPaint.setColor(attribute.line2Color);

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
        ValueEntry[] values = render.getAdapter().getItem(current).getIndicator(absChartModule.getIndicatorType());
        if (null == values || values.length < 3) {
            return;
        }
        Path path;
        boolean isStroke, isComputationDiffPath = null != values[0], isComputationDeaPath = null != values[1];
        pathPts[0] = pathPts[2] = current + 0.5f;
        if (isComputationDiffPath) {
            pathPts[3] = values[0].value;
        }
        if (isComputationDeaPath) {
            pathPts[1] = values[1].value;
        }
        render.mapPoints(pathPts);
        if (isComputationDiffPath) {
            if (diffPath.isEmpty()) diffPath.moveTo(pathPts[2], pathPts[3]);
            else diffPath.lineTo(pathPts[2], pathPts[3]);
        }
        if (isComputationDeaPath) {
            if (deaPath.isEmpty()) deaPath.moveTo(pathPts[0], pathPts[1]);
            else deaPath.lineTo(pathPts[0], pathPts[1]);
        }
        //计算MACD矩形路径
        ValueEntry macd = values[2];
        if (null == macd) {
            return;
        }
        if (macd.value >= 0) {
            path = increasingPath;//上涨或者不涨不跌路径
            isStroke = attribute.increasingStyle == Paint.Style.STROKE;
            rectBuffer[1] = macd.value;
            rectBuffer[3] = 0;
        } else {
            path = decreasingPath;//下跌路径
            isStroke = attribute.decreasingStyle == Paint.Style.STROKE;
            rectBuffer[1] = 0;
            rectBuffer[3] = macd.value;
        }
        rectBuffer[0] = current + render.pointsSpace;
        rectBuffer[2] = current + 1 - render.pointsSpace;
        render.mapPoints(rectBuffer);
        //边框偏移量修正
        if (isStroke) {
            rectBuffer[0] += borderOffset;
            rectBuffer[2] -= borderOffset;
            rectBuffer[1] += borderOffset;
            rectBuffer[3] -= borderOffset;
        }
        // 涨停、跌停、或不涨不跌的一字板
        if (rectBuffer[3] - rectBuffer[1] < 2) {
            if (macd.value >= 0) {
                rectBuffer[1] -= 2;
            } else {
                rectBuffer[3] += 2;
            }
        }
        path.addRect(rectBuffer[0], rectBuffer[1], rectBuffer[2], rectBuffer[3], Path.Direction.CW);
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        canvas.save();
        canvas.clipRect(viewRect);
        //绘制中线
        gridBuffer[0] = 0;
        gridBuffer[1] = 0;
        render.mapPoints(gridBuffer);
        canvas.drawLine(viewRect.left, gridBuffer[1], viewRect.right, gridBuffer[1], centerLinePaint);
        //绘制MACD矩形
        canvas.drawPath(decreasingPath, decreasingPaint);
        canvas.drawPath(increasingPath, increasingPaint);
        decreasingPath.reset();
        increasingPath.reset();
        //绘制MACD指标线
        canvas.drawPath(deaPath, deaPaint);
        canvas.drawPath(diffPath, diffPaint);
        deaPath.reset();
        diffPath.reset();
        canvas.restore();
    }

    @Override
    public void drawOver(Canvas canvas) {
        if (attribute.borderWidth > 0) {
            canvas.drawRect(borderPts[0], borderPts[1], borderPts[2], borderPts[3], borderPaint);
        }
    }

    @Override
    public void onViewChange() {
        super.onViewChange();
    }
}
