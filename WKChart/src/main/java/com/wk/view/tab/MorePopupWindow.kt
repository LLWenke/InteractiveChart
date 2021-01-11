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
    override fun initView(): View {
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

    fun checkedDefaultTimeType(type: TimeType, @ModuleType moduleType: Int): TabTimeBean? {
        return mAdapter?.checkedItem(type, moduleType)
    }

    fun getCheckedItem(): TabTimeBean? {
        return mAdapter?.getCheckedItem()
    }

    fun recoveryItem() {
        mAdapter?.itemRecovery()
    }
}