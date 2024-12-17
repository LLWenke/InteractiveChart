package com.wk.view.tab

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.wk.chart.databinding.MoreTabLayoutBinding
import com.wk.chart.enumeration.IndexType
import com.wk.chart.enumeration.TimeType
import com.wk.view.ext.binding

class MorePopupWindow(
    context: Context,
    anchor: View,
    private val mData: ArrayList<TabTimeBean>,
    private val chartTabListener: ChartTabListener
) : SuperPopWindow(context, anchor) {
    private val mBinding by binding<MoreTabLayoutBinding>(context)
    private var mAdapter: MoreTabAdapter? = null

    override fun initContentView(): View {
        mBinding.recyclerView.layoutManager = GridLayoutManager(context, 5)
        mAdapter = MoreTabAdapter(chartTabListener).also {
            mBinding.recyclerView.adapter = it
            it.setData(mData)
        }
        return mBinding.root
    }

    fun selectedDefaultTimeType(type: TimeType, @IndexType indexType: Int): TabTimeBean? {
        return mAdapter?.selectedItem(type, indexType)
    }

    fun getSelectedItem(): TabTimeBean? {
        return mAdapter?.getSelectedItem()
    }

    fun recoveryItem() {
        mAdapter?.itemRecovery()
    }

    override fun show(align: Int): Boolean {
        if (align == TOP) {
            mBinding.topShadow.visibility = View.INVISIBLE
            mBinding.bottomShadow.visibility = View.VISIBLE
        } else {
            mBinding.topShadow.visibility = View.VISIBLE
            mBinding.bottomShadow.visibility = View.INVISIBLE
        }
        return super.show(align)
    }
}