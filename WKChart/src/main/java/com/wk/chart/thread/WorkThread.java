package com.wk.chart.thread;

import android.os.Handler;
import android.os.HandlerThread;


public class WorkThread<T> extends HandlerThread {
    private Handler mHandler;

    public WorkThread() {
        super("WorkThread", android.os.Process.THREAD_PRIORITY_DEFAULT);
        start();
        this.mHandler = new Handler(getLooper());
    }

    /**
     * 如果需要在后台线程做一件事情，那么直接调用post方法，使用非常方便
     */
    public void post(final T data, final WorkCallBack<T> callBack) {
        this.mHandler.post(() -> callBack.onWork(data));
    }

    public void postDelayed(final T data, final WorkCallBack<T> callBack, long nDelay) {
        this.mHandler.postDelayed(() -> callBack.onWork(data), nDelay);
    }

    /**
     * 删除所有队列中的消息
     */
    public void removeAllMessage() {
        mHandler.removeMessages(0);
    }

    /**
     * 退出HandlerThread
     */
    public void destroyThread() {
        quit();
        this.mHandler = null;
    }

    public interface WorkCallBack<T> {
        void onWork(T data);
    }
}
