

package com.ll.chart.handler;

import android.view.MotionEvent;
import com.ll.chart.entry.AbsEntry;

/**
 * <p>InteractiveHandler</p>
 */

public abstract class InteractiveHandler {

  public abstract void onLeftRefresh(AbsEntry firstData);

  public abstract void onRightRefresh(AbsEntry lastData);

  public boolean onSingleTap(MotionEvent e, float x, float y) {
    return false;
  }

  public boolean onDoubleTap(MotionEvent e, float x, float y) {
    return false;
  }

  public void onHighlight(AbsEntry entry, int entryIndex, float x, float y) {
  }

  public void onCancelHighlight() {
  }
}
