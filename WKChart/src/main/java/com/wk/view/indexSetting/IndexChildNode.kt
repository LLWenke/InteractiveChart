package com.wk.view.indexSetting

import androidx.annotation.DrawableRes
import com.chad.library.adapter.base.entity.node.BaseNode
import com.wk.chart.enumeration.IndexType

class IndexChildNode(
    @get:IndexType
    @param:IndexType
    val indexType: Int,
    val name: String,
    var flag: Double,
    var scale: Int,
    val color: Int,
    @param:DrawableRes
    private val checkedImageRes: Int?,
    @param:DrawableRes
    private val unCheckedImageRes: Int?,
    private var enable: Boolean
) : BaseNode() {

    @get:DrawableRes
    val imageRes: Int?
        get() = if (isEnable()) checkedImageRes else unCheckedImageRes

    fun isEnable(): Boolean {
        return enable && 0.0 != flag
    }

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }

    override val childNode: MutableList<BaseNode>?
        get() = null

}