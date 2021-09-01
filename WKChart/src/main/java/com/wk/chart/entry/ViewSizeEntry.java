package com.wk.chart.entry;

public class ViewSizeEntry {
    private int width = 0;
    private int height = 0;
    private boolean isRequestLayout = true;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void onRequestLayoutComplete() {
        this.isRequestLayout = false;
    }

    public void onRequestLayout() {
        this.isRequestLayout = true;
        this.width = 0;
        this.height = 0;
    }

    public boolean isNotMeasure() {
        return !isRequestLayout && width > 0 && height > 0;
    }
}
