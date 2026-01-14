package com.wk.view.indexSetting

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wk.chart.R
import com.wk.utils.NumberUtils
import com.wk.view.FontEditTextView

class IndexChildProvider : BaseNodeProvider() {
    private var indexAdapter: IndexAdapter? = null
    override fun getAdapter(): IndexAdapter? {
        if (null != indexAdapter) {
            return indexAdapter
        }
        indexAdapter = super.getAdapter() as IndexAdapter?
        return indexAdapter
    }

    override val itemViewType: Int
        get() = IndexAdapter.TYPE_CHILD

    override val layoutId: Int
        get() = R.layout.item_index_child

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val entity = item as IndexChildNode
        val input = helper.getView<FontEditTextView>(R.id.et_index_value)
        input.setListener(null)
        helper.setText(R.id.tv_index_label, entity.name)
        input.setText(
            if (0.0 == entity.flag) null else NumberUtils.parseBigDecimal(
                entity.flag,
                entity.scale
            ).stripTrailingZeros().toPlainString()
        )
        if (entity.color == 0) {
            helper.getView<View>(R.id.v_index_background).backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.colorTextAuxiliary)
            input.setTextColor(ContextCompat.getColor(context, R.color.colorTextImportant))
        } else {
            helper.getView<View>(R.id.v_index_background).backgroundTintList =
                ColorStateList.valueOf(entity.color)
            input.setTextColor(entity.color)
        }
        setCheckBoxImageRes(helper, entity.imageRes)
        input.setListener { text: CharSequence ->
            if (text.isEmpty() || "0" == text.trim()) {
                entity.flag = 0.0
                entity.setEnable(false)
            } else {
                entity.flag = text.toString().toDouble()
                entity.setEnable(true)
            }
            setCheckBoxImageRes(helper, entity.imageRes)
            getAdapter()?.notifyBaseNode(entity.indexType)
        }
        helper.getView<View>(R.id.iv_index_check_box).setOnClickListener {
            entity.setEnable(!entity.isEnable())
            setCheckBoxImageRes(helper, entity.imageRes)
            getAdapter()?.notifyBaseNode(entity.indexType)
        }
    }

    private fun setCheckBoxImageRes(helper: BaseViewHolder, imageRes: Int?) {
        imageRes?.let {
            helper.setGone(R.id.iv_index_check_box, false)
            helper.setImageResource(R.id.iv_index_check_box, imageRes)
            return
        }
        helper.setGone(R.id.iv_index_check_box, true)
    }
}