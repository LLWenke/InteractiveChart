package com.wk.view.tab

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wk.chart.R
import com.wk.chart.enumeration.ModuleType
import com.wk.chart.enumeration.TimeType
import kotlinx.android.synthetic.main.more_tab_layout.view.*

class MorePopupWindow(context: Context, anchor: View, private val mData: ArrayList<TabTimeBean>, chartTabListener: ChartTabListener)
    : SuperPopWindow(context, anchor) {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: MoreTabAdapter? = null

    @SuppressLint("InflateParams")
    override fun initContentView(): View {
        return LayoutInflater.from(context).inflate(R.layout.more_tab_layout, null).also {
            it.recyclerView.layoutManager = GridLayoutManager(context, 5)
            mRecyclerView = it.recyclerView
        }
    }

    init {
        mAdapter = MoreTabAdapter(chartTabListener).also {
            mRecyclerView?.adapter = it
            it.setData(mData)
        }
    }

    fun selectedDefaultTimeType(type: TimeType, @ModuleType moduleType: Int): TabTimeBean? {
        return mAdapter?.selectedItem(type, moduleType)
    }

    fun getSelectedItem(): TabTimeBean? {
        return mAdapter?.getSelectedItem()
    }

    fun recoveryItem() {
        mAdapter?.itemRecovery()
    }

    override fun show(align: Int): Boolean {
        if (align == TOP) {
            contentView.top_shadow.visibility = View.INVISIBLE
            contentView.bottom_shadow.visibility = View.VISIBLE
        } else {
            contentView.top_shadow.visibility = View.VISIBLE
            contentView.bottom_shadow.visibility = View.INVISIBLE
        }
        return super.show(align)
    }
}