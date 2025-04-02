package com.wk.view.indexSetting

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.entity.node.BaseNode
import com.wk.chart.R
import com.wk.chart.databinding.ActivityIndexSettingBinding
import com.wk.chart.entry.IndexConfigEntry
import com.wk.chart.enumeration.IndexType
import com.wk.view.ext.binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IndexSettingActivity : AppCompatActivity(), IndexManager.IndexConfigChangeListener {
    private val mBinding by binding<ActivityIndexSettingBinding>()
    private var mData: List<IndexBaseNode>? = null
    private var mAdapter: IndexAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        mBinding.runCatching {
            val manager = GridLayoutManager(this@IndexSettingActivity, 2)
            recyclerview.layoutManager = manager
            mAdapter = IndexAdapter()
            recyclerview.adapter = mAdapter
            IndexManager.addIndexBuildConfigChangeListener(this@IndexSettingActivity)
        }
    }

    private fun initData() {
        mBinding.runCatching {
            IndexManager.getIndexConfigs(this@IndexSettingActivity)?.let {
                lvLoading.visibility = View.VISIBLE
                MainScope().launch {
                    val data = async(context = Dispatchers.IO) {
                        buildData(it)
                    }
                    withContext(context = Dispatchers.Main) {
                        mData = data.await()
                        mAdapter?.setList(mData)
                        lvLoading.visibility = View.INVISIBLE
                    }
                }
                return
            }
        }
    }

    override fun onIndexConfigChange() {
        if (mData.isNullOrEmpty()) {
            initData()
        }
    }

    private fun buildData(indexTags: LinkedHashMap<Int, IndexConfigEntry>): List<IndexBaseNode> {
        val data = ArrayList<IndexBaseNode>()
        val unCheckedImageRes = R.drawable.ic_check_n
        for (item in indexTags) {
            if (item.key == IndexType.VOLUME_MA) {
                continue
            }
            val nodes: ArrayList<BaseNode> = ArrayList()
            for (i in item.value.flagEntries.indices) {
                val index = item.value.flagEntries[i]
                if (TextUtils.isEmpty(index.term)) {
                    continue
                }
                if (item.key == IndexType.CANDLE_MA || item.key == IndexType.EMA || item.key == IndexType.RSI || item.key == IndexType.WR) {
                    nodes.add(
                        IndexChildNode(
                            item.key,
                            index.term,
                            index.flag,
                            index.color,
                            getImageRes(i),
                            unCheckedImageRes,
                            index.isEnable
                        )
                    )
                } else {
                    nodes.add(
                        IndexChildNode(
                            item.key,
                            index.term,
                            index.flag,
                            0,
                            null,
                            null,
                            index.isEnable
                        )
                    )
                }
            }
            var footerTips: String? = null
            var baseTitle: String? = null
            var baseName: String? = null
            var isShowInterval = false
            when (item.key) {
                IndexType.CANDLE_MA -> {
                    footerTips = getString(R.string.wk_ma_tips)
                    baseTitle = getString(R.string.wk_main_index)
                    baseName = getString(R.string.wk_ma)
                }

                IndexType.EMA -> {
                    footerTips = getString(R.string.wk_ema_tips)
                    baseName = getString(R.string.wk_ema)
                }

                IndexType.BOLL -> {
                    footerTips = getString(R.string.wk_boll_tips)
                    baseName = getString(R.string.wk_boll)
                }

                IndexType.SAR -> {
                    footerTips = getString(R.string.wk_sar_tips)
                    baseName = getString(R.string.wk_sar)
                }

                IndexType.MACD -> {
                    footerTips = getString(R.string.wk_macd_tips)
                    baseTitle = getString(R.string.wk_auxiliary_index)
                    baseName = getString(R.string.wk_macd)
                    isShowInterval = true
                }

                IndexType.KDJ -> {
                    footerTips = getString(R.string.wk_kdj_tips)
                    baseName = getString(R.string.wk_kdj)
                }

                IndexType.RSI -> {
                    footerTips = getString(R.string.wk_rsi_tips)
                    baseName = getString(R.string.wk_rsi)
                }

                IndexType.WR -> {
                    footerTips = getString(R.string.wk_wr_tips)
                    baseName = getString(R.string.wk_wr)
                }
            }
            data.add(
                IndexBaseNode(
                    item.key,
                    nodes,
                    baseName,
                    baseTitle,
                    isShowInterval,
                    IndexFooterNode(item.key, footerTips)
                )
            )
        }
        return data
    }

    private fun getImageRes(position: Int): Int? {
        return when (position) {
            0 -> {
                R.drawable.ic_check_p_index0
            }

            1 -> {
                R.drawable.ic_check_p_index1
            }

            2 -> {
                R.drawable.ic_check_p_index2
            }

            3 -> {
                R.drawable.ic_check_p_index3
            }

            4 -> {
                R.drawable.ic_check_p_index4
            }

            5 -> {
                R.drawable.ic_check_p_index5
            }

            else -> {
                null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        IndexManager.removeIndexBuildConfigChangeListener(this)
        mData?.let {
            IndexManager.updateIndexConfigs(this, it)
        }
    }
}