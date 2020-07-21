package com.wk.chart.thread;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;

import com.wk.chart.enumeration.ObserverArg;


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
    public void post(final T data, final WorkCallBack<T> callBack, @NonNull final ObserverArg arg) {
        this.mHandler.post(() -> callBack.onWork(data, arg));
    }

    public void postDelayed(final T data, final WorkCallBack<T> callBack, @NonNull final ObserverArg arg, long nDelay) {
        this.mHandler.postDelayed(() -> callBack.onWork(data, arg), nDelay);
    }

    /**
     * 退出HandlerThread
     */
    public void destroyThread() {
        quit();
        this.mHandler = null;
    }

    public interface WorkCallBack<T> {
        void onWork(T data, ObserverArg observerArg);
    }
}
