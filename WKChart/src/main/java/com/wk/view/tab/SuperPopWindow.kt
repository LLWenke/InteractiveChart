package com.wk.view.tab

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.wk.chart.compat.Utils


abstract class SuperPopWindow(var context: Context, var anchor: View) : PopupWindow() {
    companion object {
        const val TOP = 101
        const val BOTTOM = 102
    }

    private val margin = Utils.dp2px(context, 5f)
    private var lastShowTime = 0L

    init {
        //根布局
        contentView = initView()
        anchor.post { initCommonContentView() }
    }

    private fun initCommonContentView() {
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        this.width = anchor.width - margin * 2
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.isTouchable = true
        this.isFocusable = false
        this.isOutsideTouchable = true
        // 刷新状态
        this.update()
    }

    /**
     * 布局绑定,id绑定
     * 需要子类实现
     */
    abstract fun initView(): View

    /**
     * 如果有需要,子类会重写该方法,
     */
    fun show(align: Int): Boolean {
        if (System.currentTimeMillis() - lastShowTime < 500) {
            return false
        }
        if (align == TOP) {
            showAsDropDown(anchor, 0, -contentView.measuredHeight - anchor.height - margin)
        } else {
            showAsDropDown(anchor, margin, margin)
        }
        return true
    }

    override fun dismiss() {
        lastShowTime = System.currentTimeMillis()
        super.dismiss()
    }
}