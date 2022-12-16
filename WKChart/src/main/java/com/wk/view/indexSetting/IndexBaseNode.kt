package com.wk.view.indexSetting

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.entity.node.NodeFooterImp
import com.wk.chart.enumeration.IndexType

class IndexBaseNode(
        @get:IndexType
        @param:IndexType
        val indexType: Int,
        private val nodes: MutableList<BaseNode>,
        val name: String?,
        val title: String?,
        val isShowInterval: Boolean,
        private val indexFooterNode: IndexFooterNode)
    : BaseExpandNode(), NodeFooterImp {
    init {
        isExpanded = false
    }

    override val childNode: MutableList<BaseNode>
        get() = nodes

    override val footerNode: BaseNode?
        get() = if (isExpanded) indexFooterNode else null

}