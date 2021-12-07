package com.wk.chart.drawing;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Handler;

import com.wk.chart.adapter.CandleAdapter;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.module.TimeLineModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>BreathingLampDrawing</p>
 * 呼吸灯组件
 */
public class BreathingLampDrawing extends AbsDrawing<CandleRender, TimeLineModule> implements Runnable {
    private CandleAttribute attribute;//配置文件
    private final Paint lampPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] lampShaderColor = new int[4];
    private final float[] lampShaderColorBuffer = new float[4];
    private final float[] points = new float[2];
    private float animationCentre, shaderSize, breathingLampSize;
    private long time = 0;
    private final Handler handler = new Handler();
    private float progress, oldFraction;

    @Override
    public void onInit(CandleRender render, TimeLineModule chartModule) {
        super.onInit(render, chartModule);
        this.attribute = render.getAttribute();
        lampPaint.setStyle(Paint.Style.FILL);
        animationCentre = 0.5f;
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
        render.mapPoints(points);
        if (points[0] > viewRect.width()) {
            handler.removeCallbacksAndMessages(null);
            return;
        }
        float size = attribute.breathingLampRadius;
        float fraction = render.getAdapter().getAnimatorFraction();
        if (fraction > progress) {//满足此种状态，说明动画在顺序执行
            progress = fraction == 1f ? 0 : fraction;
            oldFraction = 0;
        } else if (fraction == progress) {
            progress = fraction == 1f ? 0 : fraction;
            oldFraction = 0;
        } else if (fraction == 0) {
            progress = progress == 1f ? 0 : progress;
            oldFraction = 0;
        } else if (progress < 1f) {//不满足此种状态，说明动画间断
            progress = Math.min(progress + (fraction - oldFraction), 1f);
            oldFraction = fraction;
        }
//        Log.e("onAnimation", "progress：" + progress + "     fraction：" + fraction);
        //区分动画执行区间计算出对应的阴影大小（前半部分/后半部分）
        size += shaderSize * (progress > animationCentre ? ((1f - progress) * 2f) : (progress * 2f));
        RadialGradient gradient = new RadialGradient(
                points[0], points[1],
                breathingLampSize,
                lampShaderColor,
                lampShaderColorBuffer,
                Shader.TileMode.MIRROR
        );
        lampPaint.setShader(gradient);
        canvas.drawCircle(points[0], points[1], size, lampPaint);
        //判断是否需要启动自动闪烁功能
        if (render.isReady() && attribute.breathingLampAutoTwinkleInterval > 0) {
            startAutoTwinkle();
        }

    }

    /**
     * 启动自动闪烁
     */
    private void startAutoTwinkle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - time > attribute.breathingLampAutoTwinkleInterval) {
            time = currentTime;
            handler.postDelayed(this, attribute.breathingLampAutoTwinkleInterval);
        }
    }

    @Override
    public void run() {
//        Log.e("onAnimation", "发送动画刷新通知");
        CandleAdapter adapter = render.getAdapter();
        if (null != adapter && adapter.getLiveState()) {
            render.getAdapter().animationRefresh();
        } else {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
