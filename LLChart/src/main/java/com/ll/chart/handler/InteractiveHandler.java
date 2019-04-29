

package com.ll.chart.handler;

import android.view.MotionEvent;
import com.ll.chart.entry.AbsEntry;

/**
 * <p>InteractiveHandler</p>
 */

public abstract class InteractiveHandler {

  public abstract void onLeftRefresh(AbsEntry firstData);

  public abstract void onRightRefresh(AbsEntry lastData);

  public void onSingleTap(MotionEvent e, float x, float y) {
  }

  public void onDoubleTap(MotionEvent e, float x, float y) {
  }

  public void onHighlight(AbsEntry entry, int entryIndex, float x, float y) {
  }

  public void onCancelHighlight() {
  }
}
