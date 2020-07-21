package com.wk.demo.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.wk.chart.ChartView
import com.wk.chart.adapter.CandleAdapter
import com.wk.chart.adapter.DepthAdapter
import com.wk.chart.compat.Utils
import com.wk.chart.entry.CandleEntry
import com.wk.chart.entry.DepthEntry
import com.wk.chart.enumeration.DisplayType
import com.wk.chart.compat.ChartConstraintSet
import com.wk.demo.util.DataUtils
import kotlinx.android.synthetic.main.activity_demo_port.candle_chart
import kotlinx.android.synthetic.main.activity_demo_port.candle_loading_bar
import kotlinx.android.synthetic.main.activity_demo_port.chart_layout
import kotlinx.android.synthetic.main.activity_demo_port.depth_chart
import kotlinx.android.synthetic.main.activity_demo_port.depth_loading_bar
import java.util.ArrayList

class DemoActivity : AppCompatActivity() {
    private val LEFT_LOADING = 0//左滑动加载
    private val RIGHT_LOADING = 1//右滑动加载
    private val REFRESH_LOADING = 2//刷新

    private var constraintSet: ChartConstraintSet? = null

    private var loadStartPos = 0
    private var loadEndPos = 0
    private val loadCount = 200
    private var candleAdapter: CandleAdapter? = null
    private var depthAdapter: DepthAdapter? = null
    private var candleEntries: MutableList<CandleEntry>? = null
    private var depthEntries: MutableList<DepthEntry>? = null
    private var task: AsyncTask<Void, Void, Void>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.constraintSet = ChartConstraintSet(chart_layout)
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        } else {
            finish()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun loadData() {
        loadBegin(REFRESH_LOADING, candle_loading_bar, candle_chart)
        loadBegin(REFRESH_LOADING, depth_loading_bar, depth_chart)
        task = object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                candleEntries = DataUtils.getCandelData(this@DemoActivity, depthAdapter!!.scale)
                depthEntries = DataUtils.getDepthData(this@DemoActivity, depthAdapter!!.scale)
                return null
            }

            override fun onPostExecute(aVoid: Void) {
                candleAdapter!!.resetData(DisplayType.oneHour, getInit())
                depthAdapter!!.resetData(depthEntries)
                loadComplete(candle_loading_bar)
                loadComplete(depth_loading_bar)
            }
        }.execute()
    }

    private fun getInit(): List<CandleEntry> {
        loadStartPos = candleEntries!!.size / 2
        loadEndPos = loadStartPos + loadCount
        loadEndPos = if (loadEndPos > candleEntries!!.size) candleEntries!!.size else loadEndPos
        val set = ArrayList<CandleEntry>()
        for (i in loadStartPos until loadEndPos) {
            set.add(candleEntries!!.get(i))
        }
        return set
    }

    private fun getHeader(): List<CandleEntry> {
        val end = loadStartPos
        loadStartPos = loadStartPos - loadCount
        loadStartPos = if (loadStartPos < 0) 0 else loadStartPos
        val entries = ArrayList<CandleEntry>()
        for (i in loadStartPos until end) {
            entries.add(candleEntries!!.get(i))
        }
        return entries
    }

    private fun getFooter(): List<CandleEntry> {
        val start = loadEndPos
        loadEndPos = loadEndPos + loadCount
        loadEndPos = if (loadEndPos > candleEntries!!.size) candleEntries!!.size else loadEndPos
        val entries = ArrayList<CandleEntry>()
        for (i in start until loadEndPos) {
            entries.add(candleEntries!!.get(i))
        }
        return entries
    }

    /**
     * 数据开始加载
     *
     * @param loadingType 加载框出现类型
     */
    fun loadBegin(
            loadingType: Int,
            bar: ProgressBar,
            chart: ChartView
    ) {
        this.constraintSet!!.setVisibility(bar.id, View.VISIBLE)
        this.constraintSet!!.connect(
                bar.id, ConstraintSet.START, chart.id,
                ConstraintSet.START, Utils.dpTopx(this, 30f)
        )
        this.constraintSet!!.connect(
                bar.id, ConstraintSet.END, chart.id,
                ConstraintSet.END, Utils.dpTopx(this, 30f)
        )
        when (loadingType) {
            LEFT_LOADING -> this.constraintSet!!.clear(bar.id, ConstraintSet.END)
            RIGHT_LOADING -> this.constraintSet!!.clear(bar.id, ConstraintSet.START)
        }
        this.chart_layout!!.setConstraintSet(constraintSet)
    }

    /**
     * 数据加载完毕
     */
    fun loadComplete(bar: ProgressBar?) {
        this.constraintSet!!.setVisibility(bar!!.id, View.INVISIBLE)
        this.chart_layout!!.setConstraintSet(constraintSet)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != task) {
            task!!.cancel(true)
        }
        if (null != candleEntries) {
            candleEntries!!.clear()
        }
        if (null != depthEntries) {
            depthEntries!!.clear()
        }
    }
}
