
package com.wk.chart.drawing;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.IndexConfigEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>MACDDrawing</p>
 */

public class MACDDrawing extends IndexDrawing<CandleRender, AbsModule<?>> {
    private static final String TAG = "MACDDrawing";
    private CandleAttribute attribute;//配置文件

    private final Paint centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //中心线画笔

    private final Paint deaPaint = new Paint();
    private final Paint diffPaint = new Paint();
    // 上涨蜡烛图画笔
    private final Paint increasingPaint = new Paint();
    // 下跌蜡烛图画笔
    private final Paint decreasingPaint = new Paint();
    // 上涨路径
    private final Path increasingPath = new Path();
    // 下跌路径
    private final Path decreasingPath = new Path();
    // dea绘制路径
    private final Path deaPath = new Path();
    // diff绘制路径
    private final Path diffPath = new Path();
    // 折线路径位置信息
    private final float[] pathPts = new float[4];

    private final float[] gridBuffer = new float[2];

    private final float[] rectBuffer = new float[4];

    private float borderOffset;//边框偏移量

    public MACDDrawing() {
        super(IndexType.MACD);
    }

    @Override
    public void onInit(CandleRender render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        centerLinePaint.setColor(attribute.centerLineColor);
        centerLinePaint.setStrokeWidth(attribute.lineWidth);
        centerLinePaint.setStyle(Paint.Style.FILL);

        diffPaint.setStyle(Paint.Style.STROKE);
        diffPaint.setStrokeWidth(attribute.lineWidth);

        deaPaint.setStyle(Paint.Style.STROKE);
        deaPaint.setStrokeWidth(attribute.lineWidth);

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
    public void onInitConfig() {
        IndexConfigEntry indexTag = render.getAdapter().getBuildConfig().getIndexTags(indexType);
        if (null == indexTag || indexTag.getFlagEntries().length < 3) {
            return;
        }
        diffPaint.setColor(indexTag.getFlagEntries()[0].getColor());
        deaPaint.setColor(indexTag.getFlagEntries()[1].getColor());
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        ValueEntry[] values = render.getAdapter().getItem(current).getIndex(indexType);
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
        if (macd.value >= 0f) {
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
        if (rectBuffer[3] - rectBuffer[1] < attribute.pointBorderWidth) {
            if (macd.value >= 0f) {
                rectBuffer[1] = rectBuffer[3];
                rectBuffer[1] -= attribute.pointBorderWidth;
            } else {
                rectBuffer[3] = rectBuffer[1];
                rectBuffer[3] += attribute.pointBorderWidth;
            }
        }
        path.moveTo(rectBuffer[0], rectBuffer[1]);
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
        decreasingPath.close();
        increasingPath.close();
        canvas.drawPath(decreasingPath, decreasingPaint);
        canvas.drawPath(increasingPath, increasingPaint);
        decreasingPath.rewind();
        increasingPath.rewind();
        //绘制MACD指标线
        canvas.drawPath(deaPath, deaPaint);
        canvas.drawPath(diffPath, diffPaint);
        deaPath.rewind();
        diffPath.rewind();
        canvas.restore();
    }
}