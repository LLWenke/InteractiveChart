package com.wk.view.indexSetting

import com.chad.library.adapter.base.entity.node.BaseNode
import com.wk.chart.enumeration.IndexType

class IndexFooterNode(@get:IndexType
                      @param:IndexType
                      val indexType: Int,
                      val tips: String?)
    : BaseNode() {

    override val childNode: MutableList<BaseNode>?
        get() = null
}