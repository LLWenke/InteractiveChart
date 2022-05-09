
package com.wk.chart.compat;

import android.view.MotionEvent;

import com.wk.chart.enumeration.TouchMoveType;

/**
 * <p>横向移动、垂直移动 识别，解决滑动冲突用的</p>
 */

public class GestureMoveActionCompat {
    /**
     * 本次 ACTION_DOWN 事件的坐标 x
     */
    private float lastMotionX;

    /**
     * 本次 ACTION_DOWN 事件的坐标 y
     */
    private float lastMotionY;

    /**
     * 触摸移动方向
     */
    private int touchMoveType = TouchMoveType.NONE;

    /**
     * 如果之前是垂直滑动，即使现在是横向滑动，仍然认为它是垂直滑动的
     * 如果之前是横向滑动，即使现在是垂直滑动，仍然认为它是横向滑动的
     * 防止在一个方向上来回滑动时，发生垂直滑动和横向滑动的频繁切换，造成识别错误
     *
     * @return 触摸移动方向
     */
    public int getTouchMoveType(MotionEvent e, float x, float y) {
        int touchSlop = 30;//避免程序识别错误的一个阀值。只有触摸移动的距离大于这个阀值时，才认为是一个有效的移动。
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastMotionY = y;
                lastMotionX = x;
                touchMoveType = TouchMoveType.NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = Math.abs(y - lastMotionY);
                float deltaX = Math.abs(x - lastMotionX);
                if (touchMoveType != TouchMoveType.VERTICAL && deltaX > deltaY && deltaX > touchSlop) {// 横向移动
                    touchMoveType = TouchMoveType.HORIZONTAL;
                } else if (touchMoveType != TouchMoveType.HORIZONTAL && deltaX < deltaY && deltaY > touchSlop) {// 垂直移动
                    touchMoveType = TouchMoveType.VERTICAL;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchMoveType = TouchMoveType.NONE;
                break;
        }
        return touchMoveType;
    }
}
