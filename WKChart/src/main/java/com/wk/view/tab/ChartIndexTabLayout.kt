package com.wk.view.tab

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.wk.chart.R
import com.wk.chart.databinding.ViewIndexLayoutBinding
import com.wk.chart.enumeration.IndexType
import com.wk.chart.enumeration.ModuleGroupType
import com.wk.view.ext.binding

class ChartIndexTabLayout : ConstraintLayout, View.OnClickListener {
    private val mBinding by binding<ViewIndexLayoutBinding>(true)
    private var mMainIndexSelectedView: View? = null
    private var mTrendIndexSelectedView: View? = null
    private var mChartTabListener: ChartTabListener? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        mBinding.runCatching {
            setPadding(0, 20, 0, 16)
            tvMa.setOnClickListener(this@ChartIndexTabLayout)
            tvBoll.setOnClickListener(this@ChartIndexTabLayout)
            tvSar.setOnClickListener(this@ChartIndexTabLayout)
            tvMacd.setOnClickListener(this@ChartIndexTabLayout)
            tvKdj.setOnClickListener(this@ChartIndexTabLayout)
            tvRsi.setOnClickListener(this@ChartIndexTabLayout)
            tvWr.setOnClickListener(this@ChartIndexTabLayout)
            ivSetting.setOnClickListener(this@ChartIndexTabLayout)
        }
    }

    fun setChartTabListener(chartTabListener: ChartTabListener) {
        mChartTabListener = chartTabListener
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_ma -> {
                mainIndexViewToggle(v, IndexType.CANDLE_MA)
            }

            R.id.tv_boll -> {
                mainIndexViewToggle(v, IndexType.BOLL)
            }

            R.id.tv_sar -> {
                mainIndexViewToggle(v, IndexType.SAR)
            }

            R.id.tv_macd -> {
                trendIndexViewToggle(v, IndexType.MACD)
            }

            R.id.tv_kdj -> {
                trendIndexViewToggle(v, IndexType.KDJ)
            }

            R.id.tv_rsi -> {
                trendIndexViewToggle(v, IndexType.RSI)
            }

            R.id.tv_wr -> {
                trendIndexViewToggle(v, IndexType.WR)
            }

            R.id.iv_setting -> {
                mChartTabListener?.onSetting()
            }
        }
    }

    fun selectedDefaultIndexType(@IndexType indexType: Int, @ModuleGroupType moduleGroupType: Int) {
        if (moduleGroupType == ModuleGroupType.MAIN) {
            mainIndexViewSelected(getSelectedView(indexType))
        } else if (moduleGroupType == ModuleGroupType.INDEX) {
            trendIndexViewSelected(getSelectedView(indexType))
        }
    }

    /**
     * 获取选中指标对应的View
     */
    private fun getSelectedView(@IndexType indexType: Int): View? {
        return when (indexType) {
            IndexType.CANDLE_MA -> {
                mBinding.tvMa
            }

            IndexType.BOLL -> {
                mBinding.tvBoll
            }

            IndexType.SAR -> {
                mBinding.tvSar
            }

            IndexType.MACD -> {
                mBinding.tvMacd
            }

            IndexType.KDJ -> {
                mBinding.tvKdj
            }

            IndexType.RSI -> {
                mBinding.tvRsi
            }

            IndexType.WR -> {
                mBinding.tvWr
            }

            else -> {
                null
            }
        }
    }

    /**
     * 主图指标View状态选中
     */
    private fun mainIndexViewSelected(view: View?) {
        mMainIndexSelectedView?.isSelected = false
        view?.isSelected = true
        mMainIndexSelectedView = view
    }

    /**
     * 主图指标View状态切换
     */
    private fun mainIndexViewToggle(view: View, @IndexType indexType: Int) {
        mMainIndexSelectedView?.isSelected = mMainIndexSelectedView == view
        view.isSelected = !view.isSelected
        if (view.isSelected) {
            mMainIndexSelectedView = view
            mChartTabListener?.onIndexTypeChange(indexType, ModuleGroupType.MAIN)
        } else {
            mMainIndexSelectedView = null
            mChartTabListener?.onIndexTypeChange(IndexType.NONE, ModuleGroupType.MAIN)
        }
    }

    /**
     * 趋势指标View状态切换
     */
    private fun trendIndexViewToggle(view: View, @IndexType indexType: Int) {
        mTrendIndexSelectedView?.isSelected = mTrendIndexSelectedView == view
        view.isSelected = !view.isSelected
        if (view.isSelected) {
            mTrendIndexSelectedView = view
            mChartTabListener?.onIndexTypeChange(indexType, ModuleGroupType.INDEX)
        } else {
            mTrendIndexSelectedView = null
            mChartTabListener?.onIndexTypeChange(IndexType.NONE, ModuleGroupType.INDEX)
        }
    }

    /**
     * 趋势指标View状态选中
     */
    private fun trendIndexViewSelected(view: View?) {
        mTrendIndexSelectedView?.isSelected = false
        view?.isSelected = true
        mTrendIndexSelectedView = view
    }
}