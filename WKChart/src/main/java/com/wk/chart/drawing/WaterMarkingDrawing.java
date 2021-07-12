
package com.wk.chart.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.enumeration.DrawingAlign;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * 水印组件
 * <p>WaterMarkingDrawing</p>
 */

public class WaterMarkingDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<?>> {
    private BaseAttribute attribute;//配置文件

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);//水印画笔
    private final Matrix matrix = new Matrix();//水印矩阵
    private Bitmap bitmap;//水印图

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();
        bitmap = Utils.drawableToBitmap(attribute.waterMarkingDrawable);
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (null == bitmap) {
            return;
        }
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    @Override
    public void drawOver(Canvas canvas) {

    }

    @Override
    public void onLayoutComplete() {
        if (null == bitmap) {
            return;
        }
        float width = attribute.waterMarkingWidth == 0 ? bitmap.getWidth() : attribute.waterMarkingWidth;
        float height = attribute.waterMarkingHeight == 0 ? bitmap.getHeight() : attribute.waterMarkingHeight;
        matrix.reset();
        matrix.setScale(width / bitmap.getWidth(), height / bitmap.getHeight());
        float x = attribute.waterMarkingMarginX + attribute.borderWidth;
        float y = attribute.waterMarkingMarginY + attribute.borderWidth;
        if ((attribute.waterMarkingAlign & DrawingAlign.RIGHT) != 0) {
            x = viewRect.right - width - x;
        }
        if ((attribute.waterMarkingAlign & DrawingAlign.BOTTOM) != 0) {
            y = viewRect.bottom - height - y;
        }
        matrix.setTranslate(x, y);
    }
}
