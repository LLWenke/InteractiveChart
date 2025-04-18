package com.wk.view.tab

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.wk.chart.R
import com.wk.chart.databinding.ViewIndexLayoutBinding
import com.wk.chart.enumeration.IndexType
import com.wk.chart.enumeration.ModuleGroup
import com.wk.view.ext.binding

class ChartIndexTabLayout : ConstraintLayout, View.OnClickListener {
    private val mBinding by binding<ViewIndexLayoutBinding>(true)
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
            tvEma.setOnClickListener(this@ChartIndexTabLayout)
            tvBoll.setOnClickListener(this@ChartIndexTabLayout)
            tvSar.setOnClickListener(this@ChartIndexTabLayout)
            tvVolume.setOnClickListener(this@ChartIndexTabLayout)
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
                mainIndexViewToggle(IndexType.CANDLE_MA)
            }

            R.id.tv_ema -> {
                mainIndexViewToggle(IndexType.EMA)
            }

            R.id.tv_boll -> {
                mainIndexViewToggle(IndexType.BOLL)
            }

            R.id.tv_sar -> {
                mainIndexViewToggle(IndexType.SAR)
            }

            R.id.tv_volume -> {
                trendIndexViewToggle(IndexType.VOLUME, v)
            }

            R.id.tv_macd -> {
                trendIndexViewToggle(IndexType.MACD, v)
            }

            R.id.tv_kdj -> {
                trendIndexViewToggle(IndexType.KDJ, v)
            }

            R.id.tv_rsi -> {
                trendIndexViewToggle(IndexType.RSI, v)
            }

            R.id.tv_wr -> {
                trendIndexViewToggle(IndexType.WR, v)
            }

            R.id.iv_setting -> {
                mChartTabListener?.onSetting()
            }
        }
    }

    fun selectedDefaultIndexType(
        @ModuleGroup moduleGroupType: Int,
        indexTypeSet: HashSet<Int>?,
    ) {
        if (moduleGroupType == ModuleGroup.MAIN) {
            mainIndexViewSelected(indexTypeSet)
        } else if (moduleGroupType == ModuleGroup.INDEX) {
            trendIndexViewSelected(indexTypeSet)
        }
    }

    /**
     * 主图指标View状态选中
     */
    private fun mainIndexViewSelected(indexTypeSet: HashSet<Int>?) {
        mBinding.tvMa.isSelected = false
        mBinding.tvEma.isSelected = false
        mBinding.tvBoll.isSelected = false
        mBinding.tvSar.isSelected = false
        indexTypeSet?.forEach { indexType ->
            when (indexType) {
                IndexType.CANDLE_MA -> {
                    mBinding.tvMa.isSelected = true
                }

                IndexType.EMA -> {
                    mBinding.tvEma.isSelected = true
                }

                IndexType.BOLL -> {
                    mBinding.tvBoll.isSelected = true
                }

                IndexType.SAR -> {
                    mBinding.tvSar.isSelected = true
                }

            }
        }
    }

    /**
     * 趋势指标View状态选中
     */
    private fun trendIndexViewSelected(indexTypeSet: HashSet<Int>?) {
        mBinding.tvVolume.isSelected = false
        mBinding.tvMacd.isSelected = false
        mBinding.tvKdj.isSelected = false
        mBinding.tvRsi.isSelected = false
        mBinding.tvWr.isSelected = false
        indexTypeSet?.forEach { indexType ->
            when (indexType) {
                IndexType.VOLUME -> {
                    mBinding.tvVolume.isSelected = true
                }

                IndexType.MACD -> {
                    mBinding.tvMacd.isSelected = true
                }

                IndexType.KDJ -> {
                    mBinding.tvKdj.isSelected = true
                }

                IndexType.RSI -> {
                    mBinding.tvRsi.isSelected = true
                }

                IndexType.WR -> {
                    mBinding.tvWr.isSelected = true
                }

            }
        }
    }

    /**
     * 主图指标View状态切换
     */
    private fun mainIndexViewToggle(@IndexType indexType: Int) {
        mChartTabListener?.onAttachIndexTypeChange(
            ModuleGroup.MAIN,
            indexType,
        )?.let {
            mainIndexViewSelected(
                it
            )
        }
    }

    /**
     * 趋势指标View状态切换
     */
    private fun trendIndexViewToggle(@IndexType indexType: Int, v: View) {
        mChartTabListener?.onModuleIndexTypeChange(
            ModuleGroup.INDEX,
            indexType,
            !v.isSelected
        )?.let {
            trendIndexViewSelected(
                it
            )
        }
    }
}