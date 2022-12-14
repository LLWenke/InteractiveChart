

package com.wk.chart.handler;

import com.wk.chart.entry.AbsEntry;

/**
 * <p>InteractiveHandler</p>
 */

public abstract class InteractiveHandler {

    public void onLeftLoad(AbsEntry firstData) {
    }

    public void onRightLoad(AbsEntry lastData) {
    }

    public boolean onSingleClick(int clickId, float x, float y) {
        return false;
    }

    public void onHighlight(AbsEntry entry, int entryIndex, float x, float y) {
    }

    public void onCancelHighlight() {
    }
}
