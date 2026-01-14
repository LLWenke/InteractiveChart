package com.wk.chart.animator;

import android.animation.ValueAnimator;

import androidx.annotation.NonNull;

import com.wk.chart.compat.Utils;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.interfaces.IAnimator;

import java.util.ArrayList;
import java.util.List;

public class ChartAnimator<T extends IAnimator> extends ValueAnimator
        implements ValueAnimator.AnimatorUpdateListener {
    private final List<Double> endValues;
    private final AnimationListener<T> animationListener;
    private List<T> newList;
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

    public void startAnimator(List<T> oldList, List<T> newList, int position) {
        this.position = position;
        this.newList = newList;
        T beginDate = oldList.get(position);
        T endData = newList.get(position);
        if (Utils.listIsEmpty(beginDate.getAnimatorEntry())
                || Utils.listIsEmpty(endData.getAnimatorEntry())
                || getDuration() == 0L) {
            this.animationListener.onAnimation(position, newList);
            return;
        }
//        Log.e("新动画", "-------------------------------");
        if (isRunning()) {
            cancel();
            this.beginDate = this.endData;
        } else {
            this.beginDate = beginDate;
        }
        this.endData = endData;
        this.endValues.clear();
        start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation.getAnimatedFraction() == 0f) return;
        List<ValueEntry> beginAnimator = beginDate.getAnimatorEntry();
        List<ValueEntry> endAnimator = endData.getAnimatorEntry();
        int size = Math.min(beginAnimator.size(), endAnimator.size());
        boolean isInit = endValues.isEmpty();
        for (int i = 0; i < size; i++) {
            ValueEntry begin = beginAnimator.get(i);
            ValueEntry end = endAnimator.get(i);
            if (isInit) endValues.add(end.value);
            double endValue = endValues.get(i);
            end.value = animation.getAnimatedFraction() == 1f ? endValue :
                    begin.value + (endValue - begin.value) * animation.getAnimatedFraction();
//            Log.e("动画执行进度", end.valueFormat + "     执行进度:" + animation.getAnimatedFraction());
        }
//        Log.e("动画数据：", endData.toString());
        this.animationListener.onAnimation(position, newList);
    }

    public interface AnimationListener<T extends IAnimator> {
        void onAnimation(int position, List<T> newList);
    }
}
