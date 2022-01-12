package com.wk.view.tab

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import com.wk.chart.R
import com.wk.chart.enumeration.IndexType
import com.wk.chart.enumeration.ModuleGroupType
import com.wk.view.indexSetting.IndexManager
import kotlinx.android.synthetic.main.index_tab_layout.view.*


class IndexPopupWindow(context: Context, anchor: View, private val chartTabListener: ChartTabListener) : SuperPopWindow(context, anchor),
        View.OnClickListener {
    private var mMainCheckedView: View? = null
    private var mAuxiliaryCheckedView: View? = null

    @SuppressLint("InflateParams")
    override fun initContentView(): View {
        return LayoutInflater.from(context).inflate(R.layout.index_tab_layout, null)
    }

    init {
        contentView.rb_ma.setOnClickListener(this)
        contentView.rb_boll.setOnClickListener(this)
        contentView.iv_main_index_switch.setOnClickListener(this)
        contentView.rb_macd.setOnClickListener(this)
        contentView.rb_kdj.setOnClickListener(this)
        contentView.rb_rsi.setOnClickListener(this)
        contentView.rb_wr.setOnClickListener(this)
        contentView.iv_auxiliary_index_switch.setOnClickListener(this)
        contentView.tv_index_setting.setOnClickListener(this)
        contentView.iv_arrow.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rb_ma -> {
                recoveryMainCheckedView()
                checkedMainView(v)
                chartTabListener.onIndexTypeChange(IndexType.CANDLE_MA, ModuleGroupType.MAIN)
            }
            R.id.rb_boll -> {
                recoveryMainCheckedView()
                checkedMainView(v)
                chartTabListener.onIndexTypeChange(IndexType.BOLL, ModuleGroupType.MAIN)
            }
            R.id.iv_main_index_switch -> {
                if (v.isSelected) {
                    val indexType = IndexManager.getCacheMainIndex(context)
                    checkedDefaultIndexType(indexType, ModuleGroupType.MAIN)
                    chartTabListener.onIndexTypeChange(indexType, ModuleGroupType.MAIN)
                } else {
                    IndexManager.cacheMainIndex(context, getCheckedIndexType(mMainCheckedView))
                    recoveryMainCheckedView()
                    checkedMainView(v)
                    chartTabListener.onIndexTypeChange(IndexType.NONE, ModuleGroupType.MAIN)
                }
            }
            R.id.rb_macd -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(v)
                chartTabListener.onIndexTypeChange(IndexType.MACD, ModuleGroupType.INDEX)
            }
            R.id.rb_kdj -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(v)
                chartTabListener.onIndexTypeChange(IndexType.KDJ, ModuleGroupType.INDEX)
            }
            R.id.rb_rsi -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(v)
                chartTabListener.onIndexTypeChange(IndexType.RSI, ModuleGroupType.INDEX)
            }
            R.id.rb_wr -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(v)
                chartTabListener.onIndexTypeChange(IndexType.WR, ModuleGroupType.INDEX)
            }
            R.id.iv_auxiliary_index_switch -> {
                if (v.isSelected) {
                    val indexType = IndexManager.getCacheAuxiliaryIndex(context)
                    checkedDefaultIndexType(indexType, ModuleGroupType.INDEX)
                    chartTabListener.onIndexTypeChange(indexType, ModuleGroupType.INDEX)
                } else {
                    IndexManager.cacheAuxiliaryIndex(context, getCheckedIndexType(mAuxiliaryCheckedView))
                    recoveryAuxiliaryCheckedView()
                    checkedAuxiliaryView(v)
                    chartTabListener.onIndexTypeChange(IndexType.NONE, ModuleGroupType.INDEX)
                }
            }
            R.id.iv_arrow,
            R.id.tv_index_setting -> {
                chartTabListener.onSetting()
            }
        }
    }

    private fun recoveryMainCheckedView() {
        mMainCheckedView?.let {
            if (it is CompoundButton) {
                it.isChecked = false
            } else {
                it.isSelected = false
            }
            mMainCheckedView = null
        }
    }

    private fun recoveryAuxiliaryCheckedView() {
        mAuxiliaryCheckedView?.let {
            if (it is CompoundButton) {
                it.isChecked = false
            } else {
                it.isSelected = false
            }
            mAuxiliaryCheckedView = null
        }
    }

    private fun checkedMainView(view: View) {
        if (view is CompoundButton) {
            view.isChecked = true
        } else {
            view.isSelected = true
        }
        mMainCheckedView = view
    }

    private fun checkedAuxiliaryView(view: View) {
        if (view is CompoundButton) {
            view.isChecked = true
        } else {
            view.isSelected = true
        }
        mAuxiliaryCheckedView = view
    }

    @SuppressLint("SwitchIntDef")
    fun checkedDefaultIndexType(@IndexType indexType: Int, @ModuleGroupType moduleGroupType: Int) {
        when (indexType) {
            IndexType.CANDLE_MA -> {
                recoveryMainCheckedView()
                checkedMainView(contentView.rb_ma)
            }
            IndexType.BOLL -> {
                recoveryMainCheckedView()
                checkedMainView(contentView.rb_boll)
            }
            IndexType.MACD -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(contentView.rb_macd)
            }
            IndexType.KDJ -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(contentView.rb_kdj)
            }
            IndexType.RSI -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(contentView.rb_rsi)
            }
            IndexType.WR -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(contentView.rb_wr)
            }
            IndexType.NONE -> {
                if (moduleGroupType == ModuleGroupType.MAIN) {
                    recoveryMainCheckedView()
                    checkedMainView(contentView.iv_main_index_switch)
                } else if (moduleGroupType == ModuleGroupType.INDEX) {
                    recoveryAuxiliaryCheckedView()
                    checkedAuxiliaryView(contentView.iv_auxiliary_index_switch)
                }
            }
        }
    }

    /**
     * 获取选中的指标类型
     */
    private fun getCheckedIndexType(checkedView: View?): Int {
        return when (checkedView?.id) {
            R.id.rb_ma -> IndexType.CANDLE_MA
            R.id.rb_boll -> IndexType.BOLL
            R.id.rb_macd -> IndexType.MACD
            R.id.rb_kdj -> IndexType.KDJ
            R.id.rb_rsi -> IndexType.RSI
            R.id.rb_wr -> IndexType.WR
            R.id.iv_main_index_switch,
            R.id.iv_auxiliary_index_switch,
            -> IndexType.NONE
            else -> IndexType.NONE
        }
    }
}