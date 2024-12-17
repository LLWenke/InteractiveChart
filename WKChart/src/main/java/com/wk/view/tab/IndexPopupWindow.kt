package com.wk.view.tab

import android.content.Context
import android.view.View
import com.wk.chart.R
import com.wk.chart.databinding.IndexTabLayoutBinding
import com.wk.chart.enumeration.IndexType
import com.wk.chart.enumeration.ModuleGroup
import com.wk.view.ext.binding


class IndexPopupWindow(
    context: Context,
    anchor: View,
    private val mChartTabListener: ChartTabListener?,
) : SuperPopWindow(context, anchor),
    View.OnClickListener {
    private val mBinding by binding<IndexTabLayoutBinding>(context)

    override fun initContentView(): View {
        mBinding.runCatching {
            tvMa.setOnClickListener(this@IndexPopupWindow)
            tvBoll.setOnClickListener(this@IndexPopupWindow)
            tvSar.setOnClickListener(this@IndexPopupWindow)
            tvVolume.setOnClickListener(this@IndexPopupWindow)
            tvMacd.setOnClickListener(this@IndexPopupWindow)
            tvKdj.setOnClickListener(this@IndexPopupWindow)
            tvRsi.setOnClickListener(this@IndexPopupWindow)
            tvWr.setOnClickListener(this@IndexPopupWindow)
            ivArrow.setOnClickListener(this@IndexPopupWindow)
            tvIndexSetting.setOnClickListener(this@IndexPopupWindow)
            ivMainIndexSwitch.setOnClickListener(this@IndexPopupWindow)
            ivTrendIndexSwitch.setOnClickListener(this@IndexPopupWindow)
        }
        return mBinding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_ma -> {
                mainIndexViewToggle(IndexType.CANDLE_MA)
            }

            R.id.tv_boll -> {
                mainIndexViewToggle(IndexType.BOLL)
            }

            R.id.tv_sar -> {
                mainIndexViewToggle(IndexType.SAR)
            }

            R.id.iv_main_index_switch -> {
                mainIndexViewToggle(IndexType.NONE)
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

            R.id.iv_trend_index_switch -> {
                trendIndexViewToggle(IndexType.NONE, v)
            }

            R.id.iv_arrow,
            R.id.tv_index_setting,
                -> {
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
        mBinding.tvBoll.isSelected = false
        mBinding.tvSar.isSelected = false
        mBinding.ivMainIndexSwitch.isSelected = false
        indexTypeSet?.forEach { indexType ->
            when (indexType) {
                IndexType.CANDLE_MA -> {
                    mBinding.tvMa.isSelected = true
                }

                IndexType.BOLL -> {
                    mBinding.tvBoll.isSelected = true
                }

                IndexType.SAR -> {
                    mBinding.tvSar.isSelected = true
                }

                IndexType.NONE -> {
                    mBinding.ivMainIndexSwitch.isSelected = true
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
        mBinding.ivTrendIndexSwitch.isSelected = false
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

                IndexType.NONE -> {
                    mBinding.ivTrendIndexSwitch.isSelected = true
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