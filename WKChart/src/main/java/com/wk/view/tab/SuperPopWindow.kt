package com.wk.view.tab

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.PopupWindow
import com.wk.chart.R
import com.wk.chart.compat.Utils


abstract class SuperPopWindow(var context: Context, private var anchor: View) : PopupWindow() {
    companion object {
        const val TOP = 101
        const val BOTTOM = 102
    }

    private val margin = Utils.dp2px(context, 0f)
    private var isInit = false
    private var lastShowTime = 0L
    private var align = BOTTOM
    private var maskView: View? = null
    private var windowManager: WindowManager? = null
    private var anchorLocation: IntArray = IntArray(2)

    init {
        //根布局
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        contentView = initContentView()
    }

    private fun initCommonContentView() {
        if (isInit) {
            return
        }
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        this.width = getWindowWidth() - margin * 2
//        this.height = contentView.measuredHeight
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.isOutsideTouchable = true
        this.update()
        this.isInit = true
    }

    /**
     * 布局绑定,id绑定
     * 需要子类实现
     */
    abstract fun initContentView(): View

    /**
     * 如果有需要,子类会重写该方法,
     */
    open fun show(align: Int): Boolean {
        if (System.currentTimeMillis() - lastShowTime < 500) {
            return false
        }
        this.align = align
        this.initCommonContentView()
        this.addMask(align)
        if (align == TOP) {
            this.hideNavigationBar(contentView)
            this.contentView.setBackgroundResource(R.drawable.bg_card_top_radius)
            this.showAsDropDown(anchor, margin, -(contentView.measuredHeight + margin + anchor.height), Gravity.TOP or Gravity.START)
        } else {
            this.contentView.setBackgroundResource(R.drawable.bg_card_bottom_radius)
            this.showAsDropDown(anchor, margin, margin)
        }
        return true
    }

    private fun addMask(align: Int) {
        anchor.getLocationOnScreen(anchorLocation)
        val token = anchor.windowToken
        val wl = WindowManager.LayoutParams()
        val maskHeight: Int
        val offsetY: Int
        if (align == TOP) {
            maskHeight = anchorLocation[1]
            offsetY = -getStatusBarHeight()
            setSystemUiVisibly(wl)
        } else {
            maskHeight = getWindowHeight() - anchorLocation[1] - anchor.height
            offsetY = anchorLocation[1] + anchor.height
        }
        wl.width = WindowManager.LayoutParams.MATCH_PARENT
        wl.height = maskHeight
        wl.y = offsetY
        wl.format = PixelFormat.TRANSLUCENT //不设置这个弹出框的透明遮罩显示为黑色
        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL //该Type描述的是形成的窗口的层级关系
        wl.token = token //获取当前Activity中的View中的token,来依附Activity
        maskView = View(context).also {
            it.setBackgroundColor(0x7f000000)
            it.setOnClickListener {
                dismiss()
            }
            it.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return@OnKeyListener true
                }
                false
            })
            /**
             * 通过WindowManager的addView方法创建View，产生出来的View根据WindowManager.LayoutParams属性不同，效果也就不同了。
             * 比如创建系统顶级窗口，实现悬浮窗口效果！
             */
            windowManager?.addView(it, wl)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    protected fun hideNavigationBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setSystemUiVisibly(view)
        }
    }

    private fun setSystemUiVisibly(view: View) {
        view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                // 隐藏导航栏
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // 全屏(隐藏状态栏)
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                // 沉浸式
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun setSystemUiVisibly(lp: WindowManager.LayoutParams) {
        lp.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                // 隐藏导航栏
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // 全屏(隐藏状态栏)
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                // 沉浸式
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun getWindowHeight(): Int {
        windowManager?.let {
            val outMetrics = DisplayMetrics()
            it.defaultDisplay.getRealMetrics(outMetrics)
            return outMetrics.heightPixels
        }
        return 0
    }

    private fun getWindowWidth(): Int {
        windowManager?.let {
            val outMetrics = DisplayMetrics()
            it.defaultDisplay.getRealMetrics(outMetrics)
            return outMetrics.widthPixels
        }
        return 0
    }

    private fun removeMask() {
        maskView?.let {
            windowManager?.removeViewImmediate(maskView)
            maskView = null
        }
        if (align == TOP) {
            hideNavigationBar(anchor.rootView)
        }
    }

    override fun dismiss() {
        lastShowTime = System.currentTimeMillis()
        removeMask()
        super.dismiss()
    }

    /**
     * 获取状态栏高度
     * @return
     */
    open fun getStatusBarHeight(): Int {
        var result = 0
        //获取状态栏高度的资源id
        val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}