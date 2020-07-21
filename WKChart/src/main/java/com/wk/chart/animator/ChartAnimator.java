package com.wk.chart.animator;

import android.animation.ValueAnimator;

import androidx.annotation.NonNull;


import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ValueEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartAnimator<T extends AbsEntry> extends ValueAnimator
        implements ValueAnimator.AnimatorUpdateListener {
    private final List<Float> endValues;
    private final AnimationListener<T> animationListener;
    private boolean buildState;//数据构建状态（true:代表新数据，需要构建数据，false：为旧数据，无需构建数据）
    private int position;
    private T beginDate;
    private T endData;

    public ChartAnimator(@NonNull AnimationListener<T> animationListener, long duration) {
        this.animationListener = animationListener;
        this.endValues = new ArrayList<>();
        setDuration(duration);
        setFloatValues(0, 1);
        addUpdateListener(this);
    }

    public void startAnimator(T beginDate, T endData, int position, boolean buildState) {
        this.position = position;
        this.buildState = buildState;
        if (Utils.listIsEmpty(beginDate.getAnimatorEntry())
                || Utils.listIsEmpty(endData.getAnimatorEntry())) {
            this.animationListener.onAnimation(position, endData, buildState);
        }
//        Log.e("新动画", "-------------------------------");
        if (isRunning()) {
            cancel();
            this.beginDate = this.endData;
            this.endData = endData;
        } else {
            this.beginDate = beginDate;
            this.endData = endData;
        }
        this.endValues.clear();
        start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (!buildState) {
            this.animationListener.onAnimation(position, endData, false);
            return;
        }
        if (animation.getAnimatedFraction() == 0.0f) {
            return;
        }
//        Log.e("动画执行进度", "begin:" + beginDate.toString() + "\nend:" + endData.toString());
        boolean isInit = endValues.size() == 0;
        for (int i = 0, z = beginDate.getAnimatorEntry().size(); i < z; i++) {
            ValueEntry begin = beginDate.getAnimatorEntry().get(i);
            ValueEntry end = endData.getAnimatorEntry().get(i);
            if (isInit) {
                this.endValues.add(end.value);
            }
            float endValue = endValues.get(i);
            if (begin.value == endValue) {
//                Log.e("动画执行进度", "begin.value == endValue--->continue");
                continue;
            }
            float updateValue = animation.getAnimatedFraction() == 1.0f ? endValue :
                    (begin.value + ((endValue - begin.value) * animation.getAnimatedFraction()));
            end.result = (long) (updateValue * Math.pow(10, end.getScale()));
            end.text = ValueUtils.recoveryText(end.result, end.getScale());
            end.value = updateValue;
//            Log.e("动画执行进度", end.text + "     执行进度:" + animation.getAnimatedFraction() + "    beginScale:" + begin.getScale() + "    endScale:" + end.getScale());
//            Log.e("动画执行进度", "result:" + end.result + "   value:" + end.value + "   text:" + end.text);
        }
//        Log.e("动画数据：", endData.toString());
        this.animationListener.onAnimation(position, endData, buildState);
    }

    public interface AnimationListener<T extends AbsEntry> {
        void onAnimation(int position, T updateData, boolean buildState);
    }
}
