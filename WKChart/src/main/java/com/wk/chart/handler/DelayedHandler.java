package com.wk.chart.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.jetbrains.annotations.NotNull;

/**
 * 用于延时任务的handler(注意，将执行在main(UI)线程中)
 */
public class DelayedHandler extends Handler {
    private static DelayedHandler handler;
    private DelayedWorkListener listener;

    private DelayedHandler() {
        super(Looper.myLooper(), null);
    }

    public static synchronized DelayedHandler getInstance() {
        if (null == handler) {
            handler = new DelayedHandler();
        }
        return handler;
    }

    /**
     * 发送延时任务
     *
     * @param what  延时任务标识
     * @param delay 延时时间（ms）
     */
    public void postDelayedWork(final int what, final long delay) {
        sendMessageDelayed(obtainMessage(what), delay);
    }

    /**
     * 发送唯一性延时任务（此函数会保证延时任务的唯一性，如果当前Messages中存在此what任务，
     * 则取消Messages中对应的what任务，然后重新发送-----注意，延时会被重置）
     *
     * @param what  延时任务标识
     * @param delay 延时时间（ms）
     */
    public void postOnlyDelayedWork(final int what, final long delay) {
        cancelDelayedWork(what);
        postDelayedWork(what, delay);
    }

    public void cancelDelayedWork(int what) {
        if (hasMessages(what)) {
            removeMessages(what);
        }
    }

    @Override
    public void handleMessage(@NotNull Message msg) {
        if (null != listener) {
            this.listener.onDelayedWork(msg.what);
        }
    }

    public void setListener(DelayedWorkListener listener) {
        this.listener = listener;
    }

    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
    }

    public interface DelayedWorkListener {
        void onDelayedWork(int what);
    }
}
