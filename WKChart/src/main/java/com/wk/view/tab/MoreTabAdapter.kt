package com.wk.view.tab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.wk.chart.R
import com.wk.chart.enumeration.ModuleType
import com.wk.chart.enumeration.TimeType
import kotlinx.android.synthetic.main.item_more_tab.view.*

/**
 * 更多子tab中的adapter
 */
class MoreTabAdapter(private val chartTabListener: ChartTabListener?) : RecyclerView.Adapter<MoreTabAdapter.MoreTabHolder>(), View.OnClickListener {

    private val mData: ArrayList<TabTimeBean> = ArrayList()
    private var mRecoveryPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreTabHolder {
        return MoreTabHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_more_tab, parent, false))
    }

    override fun onBindViewHolder(holder: MoreTabHolder, position: Int) {
        getItem(position)?.let {
            holder.mRadioButton?.let { rb ->
                rb.isChecked = it.isChecked
                rb.tag = position
                rb.text = it.tabName
                rb.setOnClickListener(this)
            }
        }
    }

    fun itemRecovery() {
        getItem(mRecoveryPosition)?.let { recovery ->
            recovery.isChecked = false
            notifyItemChanged(mRecoveryPosition)
            mRecoveryPosition = -1
        }
    }

    private fun itemChecked(checkedPosition: Int): TabTimeBean? {
        getItem(checkedPosition)?.let { checked ->
            checked.isChecked = true
            notifyItemChanged(checkedPosition)
            mRecoveryPosition = checkedPosition
            return checked
        }
        return null
    }

    fun setData(data: ArrayList<TabTimeBean>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): TabTimeBean? {
        val size = mData.size
        if (position < 0 || position >= size) {
            return null
        }
        return mData[position]
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun getCheckedItem(): TabTimeBean? {
        return getItem(mRecoveryPosition)
    }

    fun checkedItem(type: TimeType, @ModuleType moduleType: Int): TabTimeBean? {
        for (i in mData.indices) {
            val item = mData[i]
            if (item.moduleType == moduleType && item.tabValue == type) {
                itemRecovery()
                itemChecked(i)?.let {
                    return it
                }
                return null
            }
        }
        return null
    }

    override fun onClick(v: View?) {
        v?.tag?.let {
            itemRecovery()
            itemChecked(it as Int)?.let { bean ->
                chartTabListener?.onTimeTypeChange(bean.tabValue, bean.moduleType)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
        if (null == recyclerView.itemAnimator) {
            return
        }
        recyclerView.itemAnimator?.changeDuration = 0
        (recyclerView.itemAnimator as SimpleItemAnimator?)?.supportsChangeAnimations = false
    }

    inner class MoreTabHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mRadioButton: RadioButton? = null

        init {
            mRadioButton = itemView.radio_button
        }
    }
}