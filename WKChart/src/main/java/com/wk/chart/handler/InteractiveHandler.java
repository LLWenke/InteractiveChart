

package com.wk.chart.handler;

import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.module.base.AbsChartModule;

/**
 * <p>InteractiveHandler</p>
 */

public abstract class InteractiveHandler {

    public void onLeftRefresh(AbsEntry firstData) {
    }

    public void onRightRefresh(AbsEntry lastData) {
    }

    public boolean onSingleTap(AbsChartModule focusChartModule, float x, float y) {
        return false;
    }

    public boolean onSingleTap(AbsDrawing drawing, float x, float y) {
        return false;
    }

    public boolean onDoubleTap(AbsChartModule focusChartModule, float x, float y) {
        return false;
    }

    public void onHighlight(AbsEntry entry, int entryIndex, float x, float y) {
    }

    public void onCancelHighlight() {
    }
}
