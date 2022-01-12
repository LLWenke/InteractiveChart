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
import com.wk.chart.enumeration.ModuleType
import com.wk.chart.enumeration.TimeType
import kotlinx.android.synthetic.main.view_tab_layout.view.*

class ChartTabLayout : ConstraintLayout, View.OnClickListener, ChartTabListener {
    companion object {
        const val SPOT = 111
        const val CONTRACT = 112
        const val VERTICAL = 121
        const val HORIZONTAL = 122
    }

    private var mRecoveryPosition = -1
    private var mChartTabListener: ChartTabListener? = null
    private var mMorePopupWindow: MorePopupWindow? = null
    private var mIndexPopupWindow: IndexPopupWindow? = null
    private val mBaseTabViews = ArrayList<CompoundButton>()
    private val mBaseData = ArrayList<TabTimeBean>()
    private val mMoreData = ArrayList<TabTimeBean>()
    private var mTabType: Int = SPOT
    private var mTabAlign: Int = SuperPopWindow.TOP
    private var mOrientation: Int = VERTICAL

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(attrs)
        initData()
        initTab()
        initPopWindow()
    }

    @SuppressLint("InflateParams")
    private fun initView(attrs: AttributeSet?) {
        attrs?.let {
            val a = context.theme.obtainStyledAttributes(
                    it, R.styleable.chartTabAttr, 0, 0)
            try {
                mTabType = a.getInteger(R.styleable.chartTabAttr_tabType, mTabType)
                mTabAlign = a.getInteger(R.styleable.chartTabAttr_tabAlign, mTabAlign)
                mOrientation = a.getInteger(R.styleable.chartTabAttr_orientation, mOrientation)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                a.recycle()
            }
        }
        LayoutInflater.from(context).inflate(R.layout.view_tab_layout, this, true)
        rb_1.setOnClickListener(this)
        rb_2.setOnClickListener(this)
        rb_3.setOnClickListener(this)
        rb_4.setOnClickListener(this)
        rb_more.setOnClickListener(this)
        rb_index.setOnClickListener(this)
        iv_orientation.setOnClickListener(this)
        if (mOrientation == VERTICAL) {
            iv_orientation.isSelected = false
            iv_orientation.visibility = View.VISIBLE
            rb_index.visibility = View.VISIBLE
        } else {
            iv_orientation.isSelected = true
            rb_index.visibility = View.GONE
            iv_orientation.visibility = View.GONE
        }
        mBaseTabViews.clear()
        mBaseTabViews.add(rb_1)
        mBaseTabViews.add(rb_2)
        mBaseTabViews.add(rb_3)
        mBaseTabViews.add(rb_4)
    }

    private fun initPopWindow() {
        mMorePopupWindow = MorePopupWindow(context, this, mMoreData, this).also {
            it.setOnDismissListener {
                rb_more?.let { more ->
                    more.isSelected = false
//                    more.tag = false
                }
            }
        }
        mIndexPopupWindow = IndexPopupWindow(context, this, this).also {
            it.setOnDismissListener {
                rb_index?.let { index ->
                    index.isSelected = false
//                    index.tag = false
                }
            }
        }
    }

    private fun initTab() {
        var i = 0
        while (i < mBaseTabViews.size && i < mBaseData.size) {
            val tab = mBaseTabViews[i]
            val bean = mBaseData[i]
            tab.tag = i
            tab.text = bean.tabName
            tab.isChecked = bean.isChecked
            i++
        }
        rb_more.tag = true
        rb_index.tag = true
    }

    private fun initData() {
        if (mTabType == SPOT) {
            //初始化币币基本Tab数据
            mBaseData.clear()
            mBaseData.add(TabTimeBean(context.getString(R.string.wk_time_line), TimeType.oneMinute, ModuleType.TIME, false))
            mBaseData.add(TabTimeBean(context.getString(R.string.wk_15m), TimeType.fifteenMinute, ModuleType.CANDLE, false))
            mBaseData.add(TabTimeBean(context.getString(R.string.wk_4h), TimeType.fourHour, ModuleType.CANDLE, false))
            mBaseData.add(TabTimeBean(context.getString(R.string.wk_day), TimeType.day, ModuleType.CANDLE, false))
            //初始化币币更多Tab数据
            mMoreData.clear()
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_1m), TimeType.oneMinute, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_5m), TimeType.fiveMinute, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_30m), TimeType.thirtyMinute, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_1h), TimeType.oneHour, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_2h), TimeType.twoHour, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_8h), TimeType.eightHour, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_week), TimeType.week, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_month), TimeType.month, ModuleType.CANDLE, false))
        } else if (mTabType == CONTRACT) {
            //初始化合约基本Tab数据
            mBaseData.clear()
            mBaseData.add(TabTimeBean(context.getString(R.string.wk_time_line), TimeType.oneMinute, ModuleType.TIME, false))
            mBaseData.add(TabTimeBean(context.getString(R.string.wk_15m), TimeType.fifteenMinute, ModuleType.CANDLE, false))
            mBaseData.add(TabTimeBean(context.getString(R.string.wk_4h), TimeType.fourHour, ModuleType.CANDLE, false))
            mBaseData.add(TabTimeBean(context.getString(R.string.wk_day), TimeType.day, ModuleType.CANDLE, false))
            //初始化合约更多Tab数据
            mMoreData.clear()
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_1m), TimeType.oneMinute, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_5m), TimeType.fiveMinute, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_30m), TimeType.thirtyMinute, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_1h), TimeType.oneHour, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_2h), TimeType.twoHour, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_6h), TimeType.sixHour, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_12h), TimeType.twelveHour, ModuleType.CANDLE, false))
            mMoreData.add(TabTimeBean(context.getString(R.string.wk_week), TimeType.week, ModuleType.CANDLE, false))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rb_1,
            R.id.rb_2,
            R.id.rb_3,
            R.id.rb_4 -> {
                v.tag?.toString()?.toIntOrNull()?.let {
                    tabRecovery()
                    tabMoreRecovery()
                    tabChecked(it)?.let { bean ->
                        onTimeTypeChange(bean.tabValue, bean.moduleType)
                    }
                }
            }
            R.id.rb_more -> {
                if (v.tag == false) {
                    v.tag = true
                    return
                }
                if (v.isSelected) {
                    v.isSelected = false
                    mMorePopupWindow?.dismiss()
                } else {
                    v.isSelected = showMorePopupWindow()
                }
            }
            R.id.rb_index -> {
                if (v.tag == false) {
                    v.tag = true
                    return
                }
                if (v.isSelected) {
                    v.isSelected = false
                    mIndexPopupWindow?.dismiss()
                } else {
                    v.isSelected = showIndexPopupWindow()
                }
            }
            R.id.iv_orientation -> {
                onOrientationChange()
            }
        }
    }

    private fun showMorePopupWindow(): Boolean {
        return mMorePopupWindow?.show(mTabAlign) ?: false
    }

    private fun showIndexPopupWindow(): Boolean {
        return mIndexPopupWindow?.show(mTabAlign) ?: false
    }

    private fun tabRecovery() {
        if (mRecoveryPosition >= 0 && mRecoveryPosition < mBaseData.size && mRecoveryPosition < mBaseTabViews.size) {
            mBaseData[mRecoveryPosition].isChecked = false
            mBaseTabViews[mRecoveryPosition].isChecked = false
            mRecoveryPosition = -1
        }
    }

    private fun tabChecked(checkedPosition: Int): TabTimeBean? {
        if (checkedPosition >= 0 && checkedPosition < mBaseData.size && checkedPosition < mBaseTabViews.size) {
            mBaseData[checkedPosition].isChecked = true
            mBaseTabViews[checkedPosition].isChecked = true
            mRecoveryPosition = checkedPosition
            return mBaseData[checkedPosition]
        }
        return null
    }

    private fun tabMoreRecovery() {
        rb_more?.let { tab ->
            if (tab.isChecked) {
                tab.setText(R.string.wk_more)
                tab.isChecked = false
                tab.isSelected = false
                mMorePopupWindow?.recoveryItem()
                mMorePopupWindow?.dismiss()
            }
        }
    }

    private fun tabMoreChecked(bean: TabTimeBean) {
        rb_more?.let { tab ->
            tabRecovery()
            tab.text = bean.tabName
            tab.isChecked = true
            tab.isSelected = false
            mMorePopupWindow?.dismiss()
        }
    }

    fun setChartTabListener(chartTabListener: ChartTabListener) {
        mChartTabListener = chartTabListener
    }

    override fun onTimeTypeChange(type: TimeType, @ModuleType moduleType: Int) {
        mMorePopupWindow?.getCheckedItem()?.let {
            tabMoreChecked(it)
        }
        mChartTabListener?.onTimeTypeChange(type, moduleType)
    }

    override fun onIndexTypeChange(@IndexType indexType: Int, @ModuleGroupType moduleGroupType: Int) {
        mChartTabListener?.onIndexTypeChange(indexType, moduleGroupType)
    }

    override fun onOrientationChange() {
        mChartTabListener?.onOrientationChange()
    }

    override fun onSetting() {
        mIndexPopupWindow?.dismiss()
        mChartTabListener?.onSetting()
    }

    fun checkedDefaultTimeType(type: TimeType, @ModuleType moduleType: Int): TabTimeBean? {
        getCheckedPosition(type, moduleType)?.let {
            tabRecovery()
            tabMoreRecovery()
            return tabChecked(it)
        }
        mMorePopupWindow?.checkedDefaultTimeType(type, moduleType)?.let {
            tabMoreChecked(it)
            return it
        }
        return null
    }

    fun checkedDefaultIndexType(@IndexType indexType: Int, @ModuleGroupType moduleGroupType: Int) {
        mIndexPopupWindow?.checkedDefaultIndexType(indexType, moduleGroupType)
    }

    private fun getCheckedPosition(type: TimeType, @ModuleType moduleType: Int): Int? {
        for (i in mBaseData.indices) {
            val item = mBaseData[i]
            if (item.moduleType == moduleType && item.tabValue == type) {
                return i
            }
        }
        return null
    }
}

interface ChartTabListener {
    fun onTimeTypeChange(type: TimeType, @ModuleType moduleType: Int)
    fun onIndexTypeChange(@IndexType indexType: Int, @ModuleGroupType moduleGroupType: Int)
    fun onOrientationChange()
    fun onSetting()
}