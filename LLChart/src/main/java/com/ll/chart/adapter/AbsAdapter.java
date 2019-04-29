package com.ll.chart.adapter;

import android.os.Handler;
import android.os.Message;
import com.ll.chart.animator.ChartAnimator;
import com.ll.chart.compat.DataSetObservable;
import com.ll.chart.compat.Utils;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.enumeration.ObserverArg;
import com.ll.chart.thread.WorkThread;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public abstract class AbsAdapter<T extends AbsEntry>
    implements Handler.Callback, WorkThread.WorkCallBack<T>, ChartAnimator.AnimationListener<T> {

  private WorkThread<T> workThread = new WorkThread<>();//异步任务处理线程

  float high;//Y 轴上entry的最高值

  float low;//Y 轴上entry的最低值

  int maxYIndex;// Y 轴上entry的最高值索引

  int minYIndex;//Y 轴上entry的最低值索引

  private int highlightIndex;//高亮的 entry 索引

  private List<T> chartData;//数据列表列表

  private int scale;// 精度

  private final DataSetObservable dataSetObservable;//数据状态监听器

  private Handler uiHandler; //主线程Handler

  private ChartAnimator<T> animator;//动画

  private boolean isWorking = false;//是否为工作中

  private int dataSize;//数据大小

  AbsAdapter() {
    this.chartData = new ArrayList<>();
    this.uiHandler = new Handler(this);
    this.dataSetObservable = new DataSetObservable();
    this.animator = new ChartAnimator<>(this, 800);
  }

  AbsAdapter(AbsAdapter<T> absAdapter) {
    this();
    this.chartData.addAll(absAdapter.chartData);
    this.high = absAdapter.high;
    this.low = absAdapter.low;
    this.maxYIndex = absAdapter.maxYIndex;
    this.minYIndex = absAdapter.minYIndex;
    this.highlightIndex = absAdapter.highlightIndex;
    this.scale = absAdapter.scale;
    this.isWorking = absAdapter.isWorking;
    this.dataSize = chartData.size();
  }

  public int getMaxYIndex() {
    return maxYIndex;
  }

  public int getMinYIndex() {
    return minYIndex;
  }

  public int getHighlightIndex() {
    return highlightIndex;
  }

  /**
   * 获取集合中的最后一个数据下标
   */
  public int getLastPosition() {
    return getCount() > 0 ? getCount() - 1 : 0;
  }

  public void setHighlightIndex(int highlightIndex) {
    this.highlightIndex = highlightIndex;
  }

  public T getHighlightEntry() {
    return getItem(highlightIndex);
  }

  /**
   * 获取数据精度
   */
  public int getScale() {
    return scale;
  }

  /**
   * 设置数据精度
   */
  public void setScale(int scale) {
    this.scale = scale;
  }

  /**
   * 构建数据
   */
  abstract void buildData(List<T> data);

  /**
   * 获取数据数量
   */
  public int getCount() {
    return dataSize;
  }

  /**
   * 根据position获取数据
   */
  public T getItem(int position) {
    int size = getCount();
    if (size == 0) {
      return null;
    } else if (position < 0) {
      position = 0;
    } else if (position >= size) {
      position = size - 1;
    }
    return chartData.get(position);
  }

  /**
   * 刷新数据
   */
  public synchronized void resetData(List<T> data) {
    if (!Utils.listIsEmpty(data)) {
      setWorking(true);
      this.workThread.post(data, this, ObserverArg.init);
    } else {
      this.dataSetObservable.notifyObservers(ObserverArg.normal);
    }
  }

  /**
   * 向头部添加一组数据
   */
  public synchronized void addHeaderData(List<T> data) {
    if (!Utils.listIsEmpty(data)) {
      setWorking(true);
      data.addAll(chartData);
      this.workThread.post(data, this, ObserverArg.add);
    } else {
      this.dataSetObservable.notifyObservers(ObserverArg.normal);
    }
  }

  /**
   * 向尾部添加一组数据
   */
  public synchronized void addFooterData(List<T> data) {
    if (!Utils.listIsEmpty(data)) {
      setWorking(true);
      data.addAll(0, chartData);
      this.workThread.post(data, this, ObserverArg.add);
    } else {
      this.dataSetObservable.notifyObservers(ObserverArg.normal);
    }
  }

  /**
   * 向尾部部添加一条数据
   */
  public void addFooterData(T data) {
    if (null != data && !isWorking) {
      stopAnimator();
      this.chartData.add(data);
      this.dataSize = this.chartData.size();
      buildData(chartData);
      notifyDataSetChanged(ObserverArg.add);
    }
  }

  /**
   * 更新某个item
   *
   * @param position 索引值
   */
  public void changeItem(int position, T data) {
    if (null != data && position >= 0 && position < getCount() && !isWorking) {
      this.animator.startAnimator(getItem(position), data, position);
    }
  }

  @Override public void onAnimation(int position, T updateData) {
    this.chartData.set(position, updateData);
    buildData(chartData);
    notifyDataSetChanged(ObserverArg.push);
  }

  /**
   * 注册数据状态监听器
   */
  public void registerDataSetObserver(Observer observer) {
    this.dataSetObservable.addObserver(observer);
  }

  /**
   * 解绑数据状态监听器
   */
  public void unregisterDataSetObserver(Observer observer) {
    this.dataSetObservable.deleteObserver(observer);
  }

  /**
   * 工作线程中执行计算任务
   */

  @Override public void onWork(List<T> data, ObserverArg observerArg) {
    buildData(data);
    Message message = Message.obtain();
    message.obj = data;
    message.what = observerArg.ordinal();
    this.uiHandler.sendMessage(message);
  }

  /**
   * main 线程回调
   */
  @Override public boolean handleMessage(Message msg) {
    setWorking(false);
    if (msg.obj instanceof List) {
      this.chartData = (List<T>) msg.obj;
      this.dataSize = chartData.size();
    }
    notifyDataSetChanged(ObserverArg.getObserverArg(msg.what));
    return true;
  }

  /**
   * 解绑监听
   */
  public void unRegisterListener() {
    this.dataSetObservable.deleteObservers();
  }

  /**
   * 资源销毁（此方法在Activity/Fragment销毁的时候必须调用）
   */
  public void onDestroy() {
    this.workThread.destroyThread();
    stopAnimator();
  }

  /**
   * 数据刷新
   */
  public void notifyDataSetChanged(ObserverArg observerArg) {
    if (!Utils.listIsEmpty(chartData)) {
      this.dataSetObservable.notifyObservers(observerArg);
    } else {
      this.dataSetObservable.notifyObservers(ObserverArg.normal);
    }
  }

  /**
   * 是否在工作状态中
   */
  public boolean isWorking() {
    return isWorking;
  }

  /**
   * 设置工作状态
   */
  public void setWorking(boolean working) {
    this.isWorking = working;
    stopAnimator();
  }

  /**
   * 如果开启了动画，在任何改变数据（data）操作之前需调用此方法停止动画，否则会造成数据错乱
   */
  public void stopAnimator() {
    if (null != animator && animator.isRunning()) {
      this.animator.end();
    }
  }
}
