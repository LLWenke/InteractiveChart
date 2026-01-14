package com.wk.view.indexSetting

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.wk.chart.enumeration.IndexType
import com.wk.chart.formatter.ValueFormatter
import com.wk.view.indexSetting.IndexManager.getIndexConfigs

class IndexAdapter : BaseNodeAdapter() {
    val mFormatter: ValueFormatter = ValueFormatter()

    init {
        addFullSpanNodeProvider(IndexBaseProvider())
        addNodeProvider(IndexChildProvider())
        addFooterNodeProvider(IndexFooterProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        return when (data[position]) {
            is IndexBaseNode -> {
                TYPE_BASE
            }

            is IndexChildNode -> {
                TYPE_CHILD
            }

            is IndexFooterNode -> {
                TYPE_FOOTER
            }

            else -> -1
        }
    }

    fun notifyBaseNode(@IndexType indexType: Int) {
        val position = getBaseNodePosition(indexType)
        if (position == -1) {
            return
        }
        notifyItemChanged(position)
    }

    private fun getBaseNodePosition(@IndexType indexType: Int): Int {
        for (i in data.indices) {
            val node = getItem(i)
            if (node is IndexBaseNode) {
                if (node.indexType == indexType) {
                    return i
                }
            }
        }
        return -1
    }

    fun resetDefaultChildNode(@IndexType indexType: Int) {
        val defaultIndex = getIndexConfigs(indexType)
        for (p in data.indices) {
            val node = getItem(p) as? IndexBaseNode ?: continue
            if (node.indexType != indexType) {
                continue
            }
            if (null == defaultIndex || null == node.childNode) {
                break
            }
            val childCount = node.childNode?.size ?: 0
            var i = 0
            while (i < childCount && i < defaultIndex.flagEntries.size) {
                val child = node.childNode?.get(i) as? IndexChildNode
                val entry = defaultIndex.flagEntries[i]
                child?.flag = entry.flag
                i++
            }
            if (childCount == 0) {
                break
            }
            notifyItemRangeChanged(p + 1, p + childCount)
            break
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (null == recyclerView.itemAnimator) {
            return
        }
        recyclerView.itemAnimator?.changeDuration = 0
        (recyclerView.itemAnimator as SimpleItemAnimator?)?.supportsChangeAnimations = false
    }

    companion object {
        const val TYPE_BASE = 111
        const val TYPE_CHILD = 112
        const val TYPE_FOOTER = 113
    }
}