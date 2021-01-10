package com.wk.view.indexSetting

import android.view.View
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wk.chart.R

class IndexFooterProvider : BaseNodeProvider() {
    private var indexAdapter: IndexAdapter? = null
    override fun getAdapter(): IndexAdapter? {
        if (null != indexAdapter) {
            return indexAdapter
        }
        indexAdapter = super.getAdapter() as IndexAdapter?
        return indexAdapter
    }

    override val itemViewType: Int
        get() = IndexAdapter.TYPE_FOOTER

    override val layoutId: Int
        get() = R.layout.item_index_footer

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val entity = item as IndexFooterNode
        helper.setText(R.id.tv_index_tips_value, entity.tips)
        helper.getView<View>(R.id.tv_reset).setOnClickListener { getAdapter()?.resetDefaultChildNode(entity.indexType) }
    }
}