package com.wk.chart.drawing.timeLine;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import com.wk.chart.adapter.CandleAdapter;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.IndexDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>呼吸灯组件</p>
 */
public class BreathingLampDrawing extends IndexDrawing<CandleRender, AbsModule<?>> {
    private CandleAttribute attribute;//配置文件
    private final Paint lampPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] lampShaderColor = new int[4];
    private final float[] lampShaderColorBuffer = new float[4];
    private final float[] points = new float[2];
    private float shaderSize, breathingLampSize;
    private long time = 0;

    public BreathingLampDrawing() {
        super(IndexType.TIME_LINE);
    }

    @Override
    public void onInit(CandleRender render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        this.attribute = render.getAttribute();
        lampPaint.setStyle(Paint.Style.FILL);
        breathingLampSize = attribute.breathingLampRadius * 3f;
        shaderSize = breathingLampSize - attribute.breathingLampRadius;
        lampShaderColor[0] = attribute.breathingLampColor;
        lampShaderColor[1] = attribute.breathingLampColor;
        lampShaderColor[2] = Utils.getColorWithAlpha(attribute.breathingLampColor, attribute.shaderBeginColorAlpha);
        lampShaderColor[3] = Utils.getColorWithAlpha(attribute.breathingLampColor, 0.05f);
        lampShaderColorBuffer[0] = 0f;
        lampShaderColorBuffer[1] = attribute.breathingLampRadius / breathingLampSize;
        lampShaderColorBuffer[2] = lampShaderColorBuffer[1];
        lampShaderColorBuffer[3] = 1f;
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        CandleEntry entry = render.getAdapter().getItem(render.getAdapter().getLastPosition());
        points[0] = render.getAdapter().getLastPosition() + 0.5f;
        points[1] = entry.getClose().value;
        render.mapPoints(chartModule.getMatrix(), points);
        if (points[0] > viewRect.width()) return;
        float size = attribute.breathingLampRadius;
        float interval = attribute.breathingLampAutoTwinkleInterval;
        long currentTime = System.currentTimeMillis();
        float fraction = (currentTime - time) / interval;
        if (fraction >= 1f) {
            time = currentTime;
            fraction = 0;
        }
        //区分动画执行区间计算出对应的阴影大小（前半部分/后半部分）
        size += shaderSize * (fraction > 0.5f ? ((1f - fraction) * 2f) : (fraction * 2f));
        lampPaint.setShader(new RadialGradient(
                points[0], points[1],
                breathingLampSize,
                lampShaderColor,
                lampShaderColorBuffer,
                Shader.TileMode.MIRROR
        ));
        canvas.drawCircle(points[0], points[1], size, lampPaint);
        if (interval > 0) startAutoTwinkle();
    }

    /**
     * 启动自动闪烁
     */
    private void startAutoTwinkle() {
        CandleAdapter adapter = render.getAdapter();
        if (null != adapter && adapter.getLiveState()) {
            adapter.animationRefresh();
        }
    }
}
