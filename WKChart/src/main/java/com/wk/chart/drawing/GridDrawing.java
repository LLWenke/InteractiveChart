
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;

import com.wk.chart.compat.DisplayTypeUtils;
import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.render.CandleRender;

/**
 * >Grid轴绘制组件
 * <p>GridDrawing</p>
 */

public class GridDrawing extends AbsDrawing<CandleRender, AbsChartModule> {
    private static final String TAG = "GridDrawing";
    private BaseAttribute attribute;//配置文件
    private Paint gridDividingLinePaint = new Paint();  //分割线画笔
    private TextPaint gridLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Grid 轴标签的画笔
    private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Grid 轴网格线画笔
    private float[] point;
    private String[] label;
    private Path dividingLinePath = new Path();// 分割线绘制路径
    private Rect rect = new Rect(); //用于测量文字的实际占用区域

    private float gridLabelY;//gridLabel的Y轴坐标

    private int position;//label下标

    @Override
    public void onInit(CandleRender render, AbsChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        gridDividingLinePaint.setStyle(Paint.Style.STROKE);
        gridDividingLinePaint.setStrokeWidth(attribute.gridDividingLineWidth);
        gridDividingLinePaint.setColor(attribute.borderColor);

        gridLabelPaint.setTypeface(FontStyle.typeFace);
        gridLabelPaint.setTextSize(attribute.labelSize);
        gridLabelPaint.setColor(attribute.labelColor);
        gridLabelPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(attribute.lineWidth);
        gridPaint.setColor(attribute.lineColor);

        int size = attribute.gridCount + 3;
        point = new float[size * 2];
        label = new String[size];

        Utils.measureTextArea(gridLabelPaint, rect);
        setMargin(0, 0, 0,
                attribute.gridLabelMarginTop
                        + attribute.gridLabelMarginBottom
                        + rect.height()
                        + attribute.gridDividingLineWidth);
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {
        position = 0;
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        //每隔特定个 entry，记录一个 X 轴label的位置信息和值
        if (current == 0 || current == render.getAdapter().getLastPosition() || current % render.getInterval() != 0) {
            return;
        }
        point[position * 2] = current + 0.5f;
        label[position] = DisplayTypeUtils.format(render.getAdapter().getItem(current).getTime(),
                render.getAdapter().getDisplayType());
        position++;
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        render.mapPoints(render.getMainChartModule().getMatrix(), point);
        for (int i = 0; i < label.length; i++) {
            if (TextUtils.isEmpty(label[i])) {
                continue;
            }
            int xIndex = i * 2;
            canvas.drawText(label[i], point[xIndex], gridLabelY, gridLabelPaint);
            // 跳过超出显示区域的线
            if (!attribute.gridLineState || point[xIndex] < viewRect.left || point[xIndex] > viewRect.right) {
                continue;
            }
            canvas.drawLines(render.getMeasureUtils().buildViewLRCoordinates
                    (point[xIndex], point[xIndex]), gridPaint);
        }
    }

    @Override
    public void drawOver(Canvas canvas) {
        // 绘制外层边框线
        if (attribute.gridDividingLineWidth > 0) {
            canvas.drawPath(dividingLinePath, gridDividingLinePaint);
        }
    }

    @Override
    public void onViewChange() {
        float labelAreaHeight = rect.height() + attribute.gridLabelMarginTop + attribute.gridLabelMarginBottom;
        float bottom = viewRect.bottom + attribute.borderWidth;
        borderPts = render.getBorderPoints(viewRect.left,
                bottom,
                viewRect.right,
                bottom + labelAreaHeight);
        gridLabelY = bottom + rect.height() + attribute.gridLabelMarginTop;
        computationGridDividingLine();
    }

    private void computationGridDividingLine() {
        if (attribute.gridDividingLineWidth > 0) {
            dividingLinePath.rewind();
            dividingLinePath.moveTo(borderPts[0], borderPts[1]);
            dividingLinePath.lineTo(borderPts[2], borderPts[1]);
            dividingLinePath.moveTo(borderPts[0], borderPts[3]);
            dividingLinePath.lineTo(borderPts[2], borderPts[3]);
        }
    }

}
