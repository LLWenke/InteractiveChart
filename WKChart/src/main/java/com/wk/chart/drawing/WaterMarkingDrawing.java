
package com.wk.chart.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

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
    private BaseAttribute attribute;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix matrix = new Matrix();
    private float watermarkWidth, watermarkHeight;
    private Bitmap bitmap = null;
    private Drawable lastDrawable = null;

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();
        updateBitmap();
    }

    private void updateBitmap() {
        if (attribute.waterMarkingDrawable != null && attribute.waterMarkingDrawable != lastDrawable) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
            bitmap = Utils.drawableToBitmap(attribute.waterMarkingDrawable);
            lastDrawable = attribute.waterMarkingDrawable;
        }
        watermarkWidth = attribute.waterMarkingWidth > 0 ?
                attribute.waterMarkingWidth : (bitmap != null ? bitmap.getWidth() : 0);
        watermarkHeight = attribute.waterMarkingHeight > 0 ?
                attribute.waterMarkingHeight : (bitmap != null ? bitmap.getHeight() : 0);
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (bitmap == null || bitmap.isRecycled()) return;
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        if (bitmap == null || bitmap.isRecycled()) return;
        matrix.reset();
        float x, y;
        float scaleX = watermarkWidth / bitmap.getWidth();
        float scaleY = watermarkHeight / bitmap.getHeight();
        if (scaleX != 1.0f || scaleY != 1.0f) {
            matrix.setScale(scaleX, scaleY);
        }
        if ((attribute.waterMarkingPosition & PositionType.CENTER_HORIZONTAL) != 0) {
            x = viewRect.left + (viewRect.width() - watermarkWidth) / 2;
        } else if ((attribute.waterMarkingPosition & PositionType.END) != 0) {
            x = viewRect.right - watermarkWidth - attribute.waterMarkingMarginHorizontal;
        } else {
            x = viewRect.left + attribute.waterMarkingMarginHorizontal;
        }
        if ((attribute.waterMarkingPosition & PositionType.CENTER_VERTICAL) != 0) {
            y = viewRect.top + (viewRect.height() - watermarkHeight) / 2;
        } else if ((attribute.waterMarkingPosition & PositionType.BOTTOM) != 0) {
            y = viewRect.bottom - watermarkHeight - attribute.waterMarkingMarginVertical;
        } else {
            y = viewRect.top + attribute.waterMarkingMarginVertical;
        }
        matrix.postTranslate(x, y);
    }
}
