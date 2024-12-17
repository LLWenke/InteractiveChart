
package com.wk.chart.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>水印组件</p>
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
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (null == bitmap) {
            return;
        }
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        if (null == bitmap) return;
        float width = attribute.waterMarkingWidth == 0 ? bitmap.getWidth() : attribute.waterMarkingWidth;
        float height = attribute.waterMarkingHeight == 0 ? bitmap.getHeight() : attribute.waterMarkingHeight;
        matrix.reset();
        matrix.setScale(width / bitmap.getWidth(), height / bitmap.getHeight());
        float x, y;
        if ((attribute.waterMarkingPosition & PositionType.END) != 0) {
            x = viewRect.right - attribute.waterMarkingMarginHorizontal - width;
        } else {
            x = viewRect.left + attribute.waterMarkingMarginHorizontal;
        }
        if ((attribute.waterMarkingPosition & PositionType.BOTTOM) != 0) {
            y = viewRect.bottom - attribute.waterMarkingMarginVertical - height;
        } else {
            y = viewRect.top + attribute.waterMarkingMarginVertical;
        }
        matrix.setTranslate(x, y);
    }
}
