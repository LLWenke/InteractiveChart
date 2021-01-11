package com.wk.view.tab

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.wk.chart.R
import com.wk.chart.enumeration.IndexType
import com.wk.chart.enumeration.ModuleGroupType
import kotlinx.android.synthetic.main.view_index_layout.view.*


class ChartIndexTabLayout : ConstraintLayout, View.OnClickListener {
    private var mMainCheckedView: View? = null
    private var mAuxiliaryCheckedView: View? = null
    private var mChartTabListener: ChartTabListener? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_index_layout, this, true)
        rb_ma.setOnClickListener(this)
        rb_boll.setOnClickListener(this)
        rb_macd.setOnClickListener(this)
        rb_kdj.setOnClickListener(this)
        rb_rsi.setOnClickListener(this)
        rb_wr.setOnClickListener(this)
        iv_setting.setOnClickListener(this)
    }

    fun setChartTabListener(chartTabListener: ChartTabListener) {
        mChartTabListener = chartTabListener
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rb_ma -> {
                toggleMainCheckedView(v, IndexType.CANDLE_MA)
            }
            R.id.rb_boll -> {
                toggleMainCheckedView(v, IndexType.BOLL)
            }
            R.id.rb_macd -> {
                toggleAuxiliaryCheckedView(v, IndexType.MACD)
            }
            R.id.rb_kdj -> {
                toggleAuxiliaryCheckedView(v, IndexType.KDJ)
            }
            R.id.rb_rsi -> {
                toggleAuxiliaryCheckedView(v, IndexType.RSI)
            }
            R.id.rb_wr -> {
                toggleAuxiliaryCheckedView(v, IndexType.WR)
            }
            R.id.iv_setting -> {
                mChartTabListener?.onSetting()
            }
        }
    }

    private fun toggleMainCheckedView(view: View, @IndexType indexType: Int) {
        if (view == mMainCheckedView) {
            recoveryMainCheckedView()
        } else {
            recoveryMainCheckedView()
            checkedMainView(view)
        }
        val type = if (null == mMainCheckedView) IndexType.NONE else indexType
        mChartTabListener?.onIndexTypeChange(type, ModuleGroupType.MAIN)
    }

    private fun toggleAuxiliaryCheckedView(view: View, @IndexType indexType: Int) {
        if (view == mAuxiliaryCheckedView) {
            recoveryAuxiliaryCheckedView()
        } else {
            recoveryAuxiliaryCheckedView()
            checkedAuxiliaryView(view)
        }
        val type = if (null == mAuxiliaryCheckedView) IndexType.NONE else indexType
        mChartTabListener?.onIndexTypeChange(type, ModuleGroupType.INDEX)
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
                checkedMainView(rb_ma)
            }
            IndexType.BOLL -> {
                recoveryMainCheckedView()
                checkedMainView(rb_boll)
            }
            IndexType.MACD -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(rb_macd)
            }
            IndexType.KDJ -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(rb_kdj)
            }
            IndexType.RSI -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(rb_rsi)
            }
            IndexType.WR -> {
                recoveryAuxiliaryCheckedView()
                checkedAuxiliaryView(rb_wr)
            }
            IndexType.NONE -> {
                if (moduleGroupType == ModuleGroupType.MAIN) {
                    recoveryMainCheckedView()
                } else if (moduleGroupType == ModuleGroupType.INDEX) {
                    recoveryAuxiliaryCheckedView()
                }
            }
        }
    }
}