package com.wk.view.tab

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.wk.chart.R
import com.wk.chart.enumeration.IndexType
import com.wk.chart.enumeration.ModuleGroupType
import com.wk.view.indexSetting.IndexManager
import kotlinx.android.synthetic.main.index_tab_layout.view.*


class IndexPopupWindow(context: Context, anchor: View, private val mChartTabListener: ChartTabListener) : SuperPopWindow(context, anchor),
        View.OnClickListener {
    private var mMainIndexSelectedView: View? = null
    private var mTrendIndexSelectedView: View? = null

    @SuppressLint("InflateParams")
    override fun initContentView(): View {
        return LayoutInflater.from(context).inflate(R.layout.index_tab_layout, null)
    }

    init {
        contentView.tv_ma.setOnClickListener(this)
        contentView.tv_boll.setOnClickListener(this)
        contentView.tv_macd.setOnClickListener(this)
        contentView.tv_kdj.setOnClickListener(this)
        contentView.tv_rsi.setOnClickListener(this)
        contentView.tv_wr.setOnClickListener(this)
        contentView.iv_arrow.setOnClickListener(this)
        contentView.tv_index_setting.setOnClickListener(this)
        contentView.iv_main_index_switch.setOnClickListener(this)
        contentView.iv_trend_index_switch.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_ma -> {
                mainIndexViewToggle(v, IndexType.CANDLE_MA)
            }
            R.id.tv_boll -> {
                mainIndexViewToggle(v, IndexType.BOLL)
            }
            R.id.iv_main_index_switch -> {
                if (v.isSelected) {
                    val indexType = IndexManager.getCacheMainIndex(context)
                    getSelectedView(indexType, ModuleGroupType.MAIN)?.let {
                        mainIndexViewToggle(it, indexType)
                    }
                } else {
                    IndexManager.cacheMainIndex(context, getSelectedIndexType(mMainIndexSelectedView))
                    mainIndexViewToggle(v, IndexType.NONE)
                }
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
            R.id.iv_trend_index_switch -> {
                if (v.isSelected) {
                    val indexType = IndexManager.getCacheTrendIndex(context)
                    getSelectedView(indexType, ModuleGroupType.INDEX)?.let {
                        trendIndexViewToggle(it, indexType)
                    }
                } else {
                    IndexManager.cacheTrendIndex(context, getSelectedIndexType(mTrendIndexSelectedView))
                    trendIndexViewToggle(v, IndexType.NONE)
                }
            }
            R.id.iv_arrow,
            R.id.tv_index_setting -> {
                mChartTabListener.onSetting()
            }
        }
    }

    fun selectedDefaultIndexType(@IndexType indexType: Int, @ModuleGroupType moduleGroupType: Int) {
        if (moduleGroupType == ModuleGroupType.MAIN) {
            mainIndexViewSelected(getSelectedView(indexType, moduleGroupType))
        } else if (moduleGroupType == ModuleGroupType.INDEX) {
            trendIndexViewSelected(getSelectedView(indexType, moduleGroupType))
        }
    }

    /**
     * 获取选中的指标类型
     */
    private fun getSelectedIndexType(checkedView: View?): Int {
        return when (checkedView?.id) {
            R.id.tv_ma -> IndexType.CANDLE_MA
            R.id.tv_boll -> IndexType.BOLL
            R.id.tv_macd -> IndexType.MACD
            R.id.tv_kdj -> IndexType.KDJ
            R.id.tv_rsi -> IndexType.RSI
            R.id.tv_wr -> IndexType.WR
            R.id.iv_main_index_switch,
            R.id.iv_trend_index_switch,
            -> IndexType.NONE
            else -> IndexType.NONE
        }
    }

    /**
     * 获取选中指标对应的View
     */
    private fun getSelectedView(@IndexType indexType: Int, @ModuleGroupType moduleGroupType: Int): View? {
        return when (indexType) {
            IndexType.CANDLE_MA -> {
                contentView.tv_ma
            }
            IndexType.BOLL -> {
                contentView.tv_boll
            }
            IndexType.MACD -> {
                contentView.tv_macd
            }
            IndexType.KDJ -> {
                contentView.tv_kdj
            }
            IndexType.RSI -> {
                contentView.tv_rsi
            }
            IndexType.WR -> {
                contentView.tv_wr
            }
            IndexType.NONE -> {
                when (moduleGroupType) {
                    ModuleGroupType.MAIN -> {
                        contentView.iv_main_index_switch
                    }
                    ModuleGroupType.INDEX -> {
                        contentView.iv_trend_index_switch
                    }
                    else -> {
                        null
                    }
                }
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
            mChartTabListener.onIndexTypeChange(indexType, ModuleGroupType.MAIN)
        } else {
            mMainIndexSelectedView = null
            mChartTabListener.onIndexTypeChange(IndexType.NONE, ModuleGroupType.MAIN)
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
            mChartTabListener.onIndexTypeChange(indexType, ModuleGroupType.INDEX)
        } else {
            mTrendIndexSelectedView = null
            mChartTabListener.onIndexTypeChange(IndexType.NONE, ModuleGroupType.INDEX)
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