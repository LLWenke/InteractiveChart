package com.wk.view.indexSetting

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wk.chart.compat.config.IndexBuildConfig
import com.wk.chart.entry.IndexConfigEntry
import com.wk.chart.enumeration.IndexType
import com.wk.chart.formatter.ValueFormatter
import com.wk.view.unit.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.SoftReference


object IndexManager {
    private const val KEY_CHART_INDEX_CONFIG = "chart_index_config"
    private var mDefaultIndexConfigs: LinkedHashMap<Int, IndexConfigEntry>? = null
    private var mIndexConfigs: LinkedHashMap<Int, IndexConfigEntry>? = null
    private var mListener: ArrayList<SoftReference<IndexConfigChangeListener>> = ArrayList()
    private val mFormatter: ValueFormatter = ValueFormatter()
    private val mGson: Gson = Gson()

    fun setIndexBuildConfig(indexConfigs: LinkedHashMap<Int, IndexConfigEntry>) {
        mIndexConfigs = indexConfigs
    }

    fun getIndexConfigs(context: Context): LinkedHashMap<Int, IndexConfigEntry>? {
        mIndexConfigs?.let {
            return it
        }
        Preferences.getString(context, KEY_CHART_INDEX_CONFIG, null)?.let {
            GlobalScope.launch {
                val data = async(context = Dispatchers.IO) {
                    val typeT = object : TypeToken<LinkedHashMap<Int, IndexConfigEntry>>() {}.type
                    mGson.fromJson<LinkedHashMap<Int, IndexConfigEntry>>(it, typeT)
                }
                withContext(context = Dispatchers.Main) {
                    mIndexConfigs = data.await()
                    notifyAllListener()
                }
            }
        }
        return null
    }

    fun getIndexConfigs(@IndexType indexType: Int): IndexConfigEntry? {
        mDefaultIndexConfigs?.let {
            return it[indexType]
        }
        mDefaultIndexConfigs = IndexBuildConfig().getDefaultIndexConfig(mFormatter)
        return getIndexConfigs(indexType)
    }

    fun updateIndexConfigs(context: Context, updateIndexConfigData: List<IndexBaseNode>) {
        mIndexConfigs?.let {
            GlobalScope.launch(context = Dispatchers.IO) {
                for (item in updateIndexConfigData) {
                    it[item.indexType]?.let { index ->
                        item.childNode?.let { nodes ->
                            for (i in nodes.indices) {
                                val node = nodes[i]
                                if (node is IndexChildNode && i < index.flagEntries.size) {
                                    val flagEntry = index.flagEntries[i]
                                    flagEntry.isEnable = node.isEnable()
                                    flagEntry.setFlag(node.flag, mFormatter)
                                } else {
                                    break
                                }
                            }
                        }
                    }
                }
                withContext(context = Dispatchers.Main) {
                    saveIndexConfigs(context)
                    notifyAllListener()
                }
            }
        }
    }

    private fun saveIndexConfigs(context: Context) {
        mIndexConfigs?.let {
            GlobalScope.launch {
                val json = async(context = Dispatchers.IO) {
                    mGson.toJson(it)
                }
                withContext(context = Dispatchers.Main) {
                    Preferences.saveString(context, KEY_CHART_INDEX_CONFIG, json.await())
                }
            }
        }
    }

    private fun notifyAllListener() {
        for (item in mListener) {
            item.get()?.onIndexConfigChange()
        }
    }

    /**
     * 添加监听
     *
     * @param listener 监听
     */
    fun addIndexBuildConfigChangeListener(listener: IndexConfigChangeListener) {
        var isHas = false
        for (item in mListener) {
            if (item.get() === listener) {
                isHas = true
                break
            }
        }
        if (!isHas) {
            mListener.add(SoftReference(listener))
        }
    }

    /**
     * 移除监听
     *
     * @param listener 监听
     */
    fun removeIndexBuildConfigChangeListener(listener: IndexConfigChangeListener) {
        for (i in mListener.indices) {
            val item: SoftReference<IndexConfigChangeListener> = mListener[i]
            if (item.get() === listener) {
                mListener.removeAt(i)
                break
            }
        }
    }

    interface IndexConfigChangeListener {
        fun onIndexConfigChange()
    }
}