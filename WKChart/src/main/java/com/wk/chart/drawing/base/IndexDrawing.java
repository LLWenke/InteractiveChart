package com.wk.chart.drawing.base;


import com.wk.chart.enumeration.IndexType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>IndexDrawing</p>
 * 指标绘制组件
 */
public abstract class IndexDrawing<T extends AbsRender<?, ?>, A extends AbsModule<?>> extends AbsDrawing<T, A> {
    protected @IndexType
    int indexType;//指标类型

    protected IndexDrawing(@IndexType int indexType) {
        this.indexType = indexType;
    }

    public @IndexType
    int getIndexType() {
        return indexType;
    }
}
