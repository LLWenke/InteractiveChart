package com.wk.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.wk.chart.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class WeightView extends View implements ValueAnimator.AnimatorUpdateListener {
    private RectF leftRect, rightRect;
    private Path leftPath, rightPath;
    private Paint leftPaint, rightPaint;
    private TextPaint leftLabelPaint, rightLabelPaint, leftValuePaint, rightValuePaint;
    private ValueAnimator animator;
    private DecimalFormat format;

    private boolean initState;
    private double leftWeightValue, rightWeightValue, leftAmount, rightAmount;
    private int leftColor, rightColor, labelTextColor, valueTextColor;
    private String leftLabel, rightLabel;
    private float viewWidth, labelSize, valueSize, weightLineHeight, labelMargin, animatedFraction,
            valueMargin, leftLabelWidth, rightLabelWidth, leftWeightEnd, baseY, radius;
    private float[] leftRadius, rightRadius;

    public WeightView(Context context) {
        this(context, null);
    }

    public WeightView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.weightViewAttr, defStyleAttr, defStyleAttr);
        try {
            leftColor = a.getColor(R.styleable.weightViewAttr_leftColor,
                    0xff00abff);
            rightColor = a.getColor(R.styleable.weightViewAttr_rightColor,
                    0xffff8100);
            labelTextColor = a.getColor(R.styleable.weightViewAttr_labelTextColor,
                    0xffffffff);
            valueTextColor = a.getColor(R.styleable.weightViewAttr_valueTextColor,
                    0xffffffff);
            leftLabel = a.getString(R.styleable.weightViewAttr_leftLabel);
            rightLabel = a.getString(R.styleable.weightViewAttr_rightLabel);
            radius = a.getDimension(R.styleable.weightViewAttr_radius,
                    0);
            labelSize = a.getDimension(R.styleable.weightViewAttr_labelTextSize,
                    dip2px(10));
            valueSize = a.getDimension(R.styleable.weightViewAttr_valueTextSize,
                    dip2px(10));
            weightLineHeight = a.getDimension(R.styleable.weightViewAttr_weightLineHeight,
                    dip2px(10));
            labelMargin = a.getDimension(R.styleable.weightViewAttr_labelMargin,
                    dip2px(15));
            valueMargin = a.getDimension(R.styleable.weightViewAttr_valueMargin,
                    dip2px(5));
            init();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }


    }

    private void init() {
        this.format = new DecimalFormat("0.00%");
        this.format.setRoundingMode(RoundingMode.HALF_UP);
        this.leftRect = new RectF();
        this.rightRect = new RectF();
        this.leftPath = new Path();
        this.rightPath = new Path();
        this.leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.rightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.leftLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        this.rightLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        this.leftValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        this.rightValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        this.leftRadius = new float[8];
        this.rightRadius = new float[8];
        //设置属性
        this.leftPaint.setColor(leftColor);
        this.leftPaint.setStyle(Paint.Style.FILL);

        this.rightPaint.setColor(rightColor);
        this.leftPaint.setStyle(Paint.Style.FILL);

        this.leftLabelPaint.setColor(labelTextColor);
        this.leftLabelPaint.setTextSize(labelSize);
        this.leftLabelPaint.setStyle(Paint.Style.FILL);

        this.rightLabelPaint.setColor(labelTextColor);
        this.rightLabelPaint.setTextSize(labelSize);
        this.rightLabelPaint.setStyle(Paint.Style.FILL);
        this.rightLabelPaint.setTextAlign(Paint.Align.RIGHT);

        this.leftValuePaint.setColor(valueTextColor);
        this.leftValuePaint.setTextSize(valueSize);
        this.leftValuePaint.setStyle(Paint.Style.FILL);

        this.rightValuePaint.setColor(valueTextColor);
        this.rightValuePaint.setTextSize(valueSize);
        this.rightValuePaint.setStyle(Paint.Style.FILL);
        this.rightValuePaint.setTextAlign(Paint.Align.RIGHT);

        this.animator = new ValueAnimator().setDuration(500);
        this.animator.addUpdateListener(this);

        this.initState = false;

        this.leftLabelWidth = leftLabelPaint.measureText(leftLabel);
        this.rightLabelWidth = rightLabelPaint.measureText(rightLabel);
        Paint measureTextPaint = labelSize > valueSize ? leftLabelPaint : leftValuePaint;
        this.baseY = (weightLineHeight / 2f) - ((measureTextPaint.descent() + measureTextPaint.ascent()) / 2f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) (getPaddingTop()
                + getPaddingBottom()
                + weightLineHeight);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w == 0 ? 1 : w;
        float top = getPaddingTop();
        this.leftRect.set(getPaddingLeft(), top, viewWidth / 2f, top + weightLineHeight);
        this.rightRect.set(viewWidth / 2f, top, viewWidth - getPaddingRight(), top + weightLineHeight);
        this.leftRadius[0] = leftRadius[1] = leftRadius[6] = leftRadius[7] = radius;
        this.rightRadius[2] = rightRadius[3] = rightRadius[4] = rightRadius[5] = radius;
        if (isInEditMode()) {
            this.initState = false;
            updateWeightValue(577.786, 399.6577);
        }
    }

    public void updateWeightValue(Double leftAmountValue, Double rightAmountValue) {
        this.leftAmount = checkNumber(leftAmountValue, leftAmount);
        this.rightAmount = checkNumber(rightAmountValue, rightAmount);
        double count = leftAmount + rightAmount;
        count = Math.max(count, 1);
        this.leftWeightValue = leftAmount / count;
        this.rightWeightValue = rightAmount / count;
        this.leftWeightEnd = (float) (leftWeightValue * viewWidth);
        if (initState) {
            this.animator.setFloatValues(0, 1);
            this.animator.start();
            this.initState = false;
        } else {
            this.animatedFraction = 1;
            this.leftRect.set(leftRect.left, leftRect.top, leftWeightEnd, leftRect.bottom);
            this.rightRect.set(leftWeightEnd, rightRect.top, rightRect.right, rightRect.bottom);
            postInvalidateOnAnimation();
        }
    }

    /**
     * 验证数值是否合法
     */
    private double checkNumber(Double number, double defaultValue) {
        if (number == null || Double.isNaN(number) || Double.isInfinite(number)) {
            return defaultValue;
        }
        return number;
    }

    public void initAnimatedState() {
        this.initState = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float leftLabelX = leftRect.left + labelMargin;
        float rightLabelX = rightRect.right - labelMargin;
        //绘制比重线
        leftPath.addRoundRect(leftRect, leftRadius, Path.Direction.CW);
        rightPath.addRoundRect(rightRect, rightRadius, Path.Direction.CW);
        canvas.drawPath(leftPath, leftPaint);
        canvas.drawPath(rightPath, rightPaint);
        leftPath.rewind();
        rightPath.rewind();
        //绘制Label文字
        canvas.drawText(leftLabel, leftLabelX, baseY, leftLabelPaint);
        canvas.drawText(rightLabel, rightLabelX, baseY, rightLabelPaint);
        //绘制比重值
        canvas.drawText(format.format(leftWeightValue * animatedFraction),
                leftLabelX + leftLabelWidth + valueMargin, baseY, leftValuePaint);
        canvas.drawText(format.format(rightWeightValue * animatedFraction),
                rightLabelX - rightLabelWidth - valueMargin, baseY, rightValuePaint);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置颜色
     *
     * @param leftColor  左边对比条的颜色
     * @param rightColor 右边对比条的颜色
     */
    public void setViewColor(int leftColor, int rightColor) {
        this.leftColor = leftColor;
        this.rightColor = rightColor;
        this.leftPaint.setColor(leftColor);
        this.rightPaint.setColor(rightColor);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.animatedFraction = valueAnimator.getAnimatedFraction();
        this.leftRect.set(leftRect.left, leftRect.top,
                leftWeightEnd * animatedFraction, leftRect.bottom);
        this.rightRect.set(leftRect.right,
                rightRect.top, rightRect.right, rightRect.bottom);
        postInvalidateOnAnimation();
    }
}
