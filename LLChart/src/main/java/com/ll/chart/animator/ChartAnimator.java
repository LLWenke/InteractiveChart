package com.ll.chart.animator;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import com.ll.chart.compat.Utils;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.entry.ValueEntry;
import java.util.ArrayList;
import java.util.List;

public class ChartAnimator<T extends AbsEntry> extends ValueAnimator
    implements ValueAnimator.AnimatorUpdateListener {
  private final List<Long> endValue;
  private final AnimationListener<T> animationListener;
  private int position;
  private T beginDate;
  private T endData;

  public ChartAnimator(@NonNull AnimationListener<T> animationListener, long duration) {
    this.animationListener = animationListener;
    this.endValue = new ArrayList<>();
    setDuration(duration);
    setFloatValues(0, 100);
    addUpdateListener(this);
  }

  public void startAnimator(T beginDate, T endData, int position) {
    this.position = position;
    if (Utils.listIsEmpty(beginDate.getAnimatorEntry())
        || Utils.listIsEmpty(endData.getAnimatorEntry())) {
      this.animationListener.onAnimation(position, endData);
    }
    //Log.e("新动画", "-------------------------------");
    if (isRunning()) {
      cancel();
      this.beginDate = this.endData;
      this.endData = endData;
    } else {
      this.beginDate = beginDate;
      this.endData = endData;
    }
    this.endValue.clear();
    start();
  }

  @Override public void onAnimationUpdate(ValueAnimator animation) {
    if (animation.getAnimatedFraction() == 0.0f) {
      return;
    }
    boolean isInit = endValue.size() == 0;
    for (int i = 0, z = beginDate.getAnimatorEntry().size(); i < z; i++) {
      if (isInit) {
        this.endValue.add(endData.getAnimatorEntry().get(i).result);
      }
      ValueEntry begin = beginDate.getAnimatorEntry().get(i);
      ValueEntry end = endData.getAnimatorEntry().get(i);
      long updateValue = animation.getAnimatedFraction() == 1.0f ? endValue.get(i) : (long)
          (begin.result + ((endValue.get(i) - begin.result) * animation.getAnimatedFraction()));
      if (begin.result == updateValue) {
        //Log.e("continue", "continue" + animation.getAnimatedFraction());
        continue;
      }
      ValueEntry update = endData.recoveryValue(updateValue);
      end.result = update.result;
      end.value = update.value;
      end.text = update.text;
      //Log.e("动画执行进度", end.text + "     执行进度:" + animation.getAnimatedFraction());
    }
    //Log.e("动画数据：", endData.toString());
    this.animationListener.onAnimation(position, endData);
  }

  public interface AnimationListener<T extends AbsEntry> {
    void onAnimation(int position, T updateData);
  }
}
