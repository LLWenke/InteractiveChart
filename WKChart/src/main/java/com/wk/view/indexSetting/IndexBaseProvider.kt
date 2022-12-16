package com.wk.view.indexSetting

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wk.chart.R

class IndexBaseProvider : BaseNodeProvider() {
    private val builder = StringBuilder()
    private var indexAdapter: IndexAdapter? = null
    override fun getAdapter(): IndexAdapter? {
        if (null != indexAdapter) {
            return indexAdapter
        }
        indexAdapter = super.getAdapter() as IndexAdapter?
        return indexAdapter
    }

    override val itemViewType: Int
        get() = IndexAdapter.TYPE_BASE

    override val layoutId: Int
        get() = R.layout.item_index_base

    private fun getNext(position: Int): IndexBaseNode? {
        val node = getAdapter()?.getItemOrNull(position + 1) ?: return null
        return if (node is IndexBaseNode) {
            node
        } else null
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val entity = item as IndexBaseNode
        val next = getNext(helper.bindingAdapterPosition)
        helper.setGone(R.id.v_index_title_interval, !entity.isShowInterval)
        helper.setGone(R.id.v_index_title_dividing_line, entity.isExpanded || next == null || next.isShowInterval)
        if (TextUtils.isEmpty(entity.title)) {
            helper.setGone(R.id.tv_index_group_name, true)
        } else {
            helper.setText(R.id.tv_index_group_name, entity.title)
            helper.setGone(R.id.tv_index_group_name, false)
        }
        helper.setText(R.id.tv_index_name, entity.name)
        helper.setText(R.id.tv_index, getIndexValue(entity.childNode))
    }

    private fun getIndexValue(nodes: List<BaseNode>?): String? {
        if (null == nodes || nodes.isEmpty()) {
            return null
        }
        builder.delete(0, builder.length)
        val interval = "   "
        for (node in nodes) {
            if (node is IndexChildNode) {
                if (!node.isEnable()) {
                    continue
                }
                builder.append(node.name).append(node.flag).append(interval)
            }
        }
        val start = builder.length - interval.length
        if (start >= 0) {
            builder.delete(start, builder.length)
        }
        return builder.toString()
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        getAdapter()?.let {
            val count = it.expandOrCollapse(position)
            if (data is IndexBaseNode) {
                if (data.isExpanded) {
                    helper.getView<ImageView>(R.id.iv_index_item_cion).isSelected = true
                    data.footerNode?.let { footer ->
                        val footerPosition = position + count + 1
                        it.data.add(footerPosition, footer)
                        it.notifyItemInserted(footerPosition)
                    }
                } else {
                    helper.getView<ImageView>(R.id.iv_index_item_cion).isSelected = false
                    val footerPosition = position + 1
                    it.data.removeAt(footerPosition)
                    it.notifyItemRemoved(footerPosition)
                }
            }

        }
    }
}