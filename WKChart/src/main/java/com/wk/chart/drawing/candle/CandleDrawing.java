
package com.wk.chart.drawing.candle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.module.CandleModule;
import com.wk.chart.render.CandleRender;


/**
 * <p>CandleDrawing</p>
 */
public class CandleDrawing extends AbsDrawing<CandleRender, CandleModule> {
    private static final String TAG = "CandleDrawing";
    private CandleAttribute attribute;//配置文件
    // 上涨画笔
    private final Paint increasingPaint = new Paint();
    // 下跌画笔
    private final Paint decreasingPaint = new Paint();
    // 上涨路径
    private final Path increasingPath = new Path();
    // 下跌路径
    private final Path decreasingPath = new Path();
    private float pointBorderOffset;//边框偏移量
    private boolean highlightState = true;//高亮状态
    // 蜡烛图绘制的实际收首尾X轴坐标点（从首尾两根蜡烛图的中心点算起）
    private float beginX, endX = 0;

    @Override
    public void onInit(CandleRender render, CandleModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        increasingPaint.setStyle(attribute.increasingStyle);
        increasingPaint.setStrokeWidth(attribute.pointBorderWidth);
        increasingPaint.setColor(attribute.increasingColor);

        decreasingPaint.setStyle(attribute.decreasingStyle);
        decreasingPaint.setStrokeWidth(attribute.pointBorderWidth);
        decreasingPaint.setColor(attribute.decreasingColor);

        pointBorderOffset = attribute.pointBorderWidth / 2f;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {
        //获取蜡烛图绘制的实际收首尾X轴坐标点（从首尾两根蜡烛图的中心点算起）
        this.beginX = render.getPointX(absChartModule.getMatrix(), begin + 0.5f);
        this.endX = render.getPointX(absChartModule.getMatrix(), end - 1 + 0.5f);
        this.highlightState = true;
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
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
        float[] rectBuffer = absChartModule.getPointRect(render, entry, current);
        //边框偏移量修正
        if (isStroke) {
            rectBuffer[0] += pointBorderOffset;
            rectBuffer[2] -= pointBorderOffset;
            rectBuffer[5] += pointBorderOffset;
            rectBuffer[7] -= pointBorderOffset;
        }
        // 涨停、跌停、或不涨不跌的一字板
        if (rectBuffer[7] - rectBuffer[5] < attribute.pointBorderWidth) {
            rectBuffer[5] -= pointBorderOffset;
            rectBuffer[7] += pointBorderOffset;
        }
        //计算中心线
        float centerLine = rectBuffer[0] + (rectBuffer[2] - rectBuffer[0]) / 2f;
        //添加影线路径
        if (isStroke) {
            //上影线路径
            path.moveTo(centerLine, rectBuffer[1]);
            path.lineTo(centerLine, rectBuffer[5]);
            //下影线路径
            path.moveTo(centerLine, rectBuffer[3]);
            path.lineTo(centerLine, rectBuffer[7]);
        } else {
            float lineLeft = centerLine - pointBorderOffset;
            float lineRight = centerLine + pointBorderOffset;
            //上影线路径
            path.addRect(lineLeft, rectBuffer[5], lineRight, rectBuffer[1], Path.Direction.CW);
            //下影线路径
            path.addRect(lineLeft, rectBuffer[3], lineRight, rectBuffer[7], Path.Direction.CW);
        }
        //添加矩形路径
        path.addRect(rectBuffer[0], rectBuffer[5], rectBuffer[2], rectBuffer[7], Path.Direction.CW);
        // 计算高亮坐标
        if (render.isHighlight() && highlightState) {
            final float[] highlightPoint = render.getHighlightPoint();
            if (rectBuffer[4] <= highlightPoint[0] && highlightPoint[0] <= rectBuffer[6]) {
                highlightPoint[0] = centerLine;
                render.getAdapter().setHighlightIndex(current);
                highlightState = false;
            } else if (highlightPoint[0] <= beginX) {
                highlightPoint[0] = beginX;
                render.getAdapter().setHighlightIndex(begin);
                highlightState = false;
            } else if (highlightPoint[0] >= endX) {
                highlightPoint[0] = endX;
                render.getAdapter().setHighlightIndex(end - 1);
                highlightState = false;
            }
        }
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
