package com.wk.chart.adapter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.wk.chart.animator.ChartAnimator;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.config.AbsBuildConfig;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.BuildData;
import com.wk.chart.entry.ScaleEntry;
import com.wk.chart.enumeration.ObserverArg;
import com.wk.chart.formatter.DateFormatter;
import com.wk.chart.formatter.ValueFormatter;
import com.wk.chart.thread.WorkThread;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsAdapter<T extends AbsEntry, F extends AbsBuildConfig> implements Handler.Callback, WorkThread.WorkCallBack<BuildData<T, F>>, ChartAnimator.AnimationListener<T> {
    private final PropertyChangeSupport dataChangeSupport;//数据状态监听器
    private final DateFormatter dateFormatter;//日期格式化工具
    private final ValueFormatter valueFormatter;//数值格式化工具
    private final ChartAnimator<T> animator;//数据更新动画
    private final Handler uiHandler; //主线程Handler
    private final ScaleEntry scale;// 精度
    private F buildConfig; // 构建配置信息
    private WorkThread<BuildData<T, F>> workThread;//异步任务处理线程
    private List<T> renderData;//数据列表（渲染）
    protected int maxYIndex;// Y 轴上entry的最高值索引
    protected int minYIndex;//Y 轴上entry的最低值索引
    private int highlightIndex;//高亮的 entry 索引
    private int dataSize;//数据大小
    private boolean liveState = true;//adapter存活状态

    AbsAdapter(@NonNull F buildConfig) {
        this.buildConfig = buildConfig;
        this.renderData = new ArrayList<>();
        this.dataChangeSupport = new PropertyChangeSupport(this);
        this.dateFormatter = new DateFormatter();
        this.valueFormatter = new ValueFormatter();
        this.animator = new ChartAnimator<>(this, 400);
        this.uiHandler = new Handler(Looper.getMainLooper(), this);
        this.scale = new ScaleEntry(0, 0, "", "");
    }

    AbsAdapter(@NonNull AbsAdapter<T, F> absAdapter) {
        this(absAdapter.getBuildConfig());
        this.renderData.addAll(absAdapter.renderData);
        this.maxYIndex = absAdapter.maxYIndex;
        this.minYIndex = absAdapter.minYIndex;
        this.highlightIndex = absAdapter.highlightIndex;
        this.dataSize = renderData.size();
        this.setScale(absAdapter.getScale().getBaseScale(), absAdapter.getScale().getQuoteScale(),
                absAdapter.getScale().getBaseUnit(), absAdapter.getScale().getQuoteUnit());
    }

    /**
     * 获取最大值下标
     */
    public int getMaxYIndex() {
        return maxYIndex;
    }

    /**
     * 获取最小值下标
     */
    public int getMinYIndex() {
        return minYIndex;
    }

    /**
     * 获取高亮值下标
     */
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
        if (highlightIndex < 0) {
            this.highlightIndex = 0;
        } else if (highlightIndex >= getCount()) {
            this.highlightIndex = getLastPosition();
        } else {
            this.highlightIndex = highlightIndex;
        }
    }

    public T getHighlightEntry() {
        return getItem(highlightIndex);
    }

    /**
     * 获取当前adapter的存活状态
     *
     * @return 存活状态
     */
    public boolean getLiveState() {
        return liveState;
    }

    /**
     * 获取精度
     *
     * @return 精度值
     */
    public final ScaleEntry getScale() {
        return scale;
    }

    /**
     * 设置精度
     *
     * @param baseScale  Base精度
     * @param quoteScale quote精度
     * @param baseUnit   Base单位
     * @param quoteUnit  quote单位
     */
    public final void setScale(int baseScale, int quoteScale, String baseUnit, String quoteUnit) {
        this.scale.reset(baseScale, quoteScale, baseUnit, quoteUnit);
    }

    /**
     * 设置精度
     *
     * @param baseScale  Base精度
     * @param quoteScale quote精度
     */
    public void setScale(int baseScale, int quoteScale) {
        this.scale.reset(baseScale, quoteScale);
    }

    /**
     * 获取指标配置信息
     *
     * @return 配置信息类（复制品）
     */
    public F getBuildConfig() {
        return buildConfig;
    }

    /**
     * 设置指标配置信息
     */
    public void setBuildConfig(F buildConfig) {
        stopAnimator();
        stopAsyTask();
        this.buildConfig = buildConfig;
        if (getCount() == 0) return;
        onAsyTask(buildConfig, cloneDataList(), ObserverArg.INIT, 0);
    }

    /**
     * 构建数据
     */
    abstract void buildData(@NonNull F buildConfig, @NonNull List<T> data, int startPosition);

    /**
     * 在给定的范围内，计算最大值和最小值
     */
    public abstract void calculateMinAndMax(int start, int end);

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
        return renderData.get(position);
    }

    /**
     * 克隆当前数据源
     *
     * @return 克隆数据源
     */
    private ArrayList<T> cloneDataList() {
        try {
            return (ArrayList<T>) ((ArrayList<T>) renderData).clone();
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    /**
     * 格式更新
     *
     * @param isCalculateData 是否需要计算数据
     */
    public synchronized void applyFormatUpdate(boolean isCalculateData) {
        if (getCount() == 0) return;
        if (isCalculateData) {
            stopAnimator();
            stopAsyTask();
            onAsyTask(buildConfig, cloneDataList(), ObserverArg.FORMAT_UPDATE, 0);
        } else {
            notifyDataSetChanged(ObserverArg.FORMAT_UPDATE);
        }
    }

    /**
     * 配置属性更新
     */
    public synchronized void applyAttrUpdate() {
        notifyDataSetChanged(ObserverArg.ATTR_UPDATE);
    }

    /**
     * 清空数据
     */
    public synchronized void clearData() {
        stopAnimator();
        stopAsyTask();
        this.renderData.clear();
        this.dataSize = 0;
        notifyDataSetChanged(ObserverArg.RESET);
    }

    /**
     * 重置数据
     */
    public synchronized void resetData(List<T> data) {
        if (Utils.listIsEmpty(data)) {
            clearData();
        } else {
            stopAnimator();
            stopAsyTask();
            onAsyTask(buildConfig, data, buildConfig.isInit() ? ObserverArg.RESET : ObserverArg.INIT, 0);
        }
    }

    /**
     * 更新数据
     */
    public synchronized void updateData(List<T> data) {
        if (Utils.listIsEmpty(data)) {
            clearData();
        } else {
            stopAnimator();
            stopAsyTask();
            onAsyTask(buildConfig, data, buildConfig.isInit() ? ObserverArg.UPDATE : ObserverArg.INIT, 0);
        }
    }

    /**
     * 向头部添加一组数据
     */
    public synchronized void addHeaderData(List<T> data) {
        if (Utils.listIsEmpty(data)) {
            notifyDataSetChanged(ObserverArg.NORMAL);
        } else {
            stopAnimator();
            data.addAll(renderData);
            onAsyTask(buildConfig, data, ObserverArg.ADD, 0);
        }
    }

    /**
     * 向尾部添加一组数据
     */
    public synchronized void addFooterData(List<T> data) {
        if (Utils.listIsEmpty(data)) {
            notifyDataSetChanged(ObserverArg.NORMAL);
        } else {
            stopAnimator();
            data.addAll(0, renderData);
            onAsyTask(buildConfig, data, ObserverArg.ADD, getLastPosition());
        }
    }

    /**
     * 向尾部部添加一条数据
     */
    public void addFooterData(T data) {
        if (null != data) {
            stopAnimator();
            ArrayList<T> copyList = cloneDataList();
            copyList.add(data);
            onAsyTask(buildConfig, copyList, ObserverArg.UPDATE, getLastPosition());
        }
    }

    /**
     * 更新某个item
     *
     * @param position 索引值
     */
    public void changeItem(int position, T data) {
        if (null != data && position >= 0 && position < getCount()) {
            ArrayList<T> copyList = cloneDataList();
            copyList.set(position, data);
            this.animator.startAnimator(renderData, copyList, position);
        }
    }


    /**
     * 动画执行中
     *
     * @param position 动画位置
     * @param newList  新数据列表
     *
     */
    @Override
    public void onAnimation(int position, List<T> newList) {
        onAsyTask(buildConfig, newList, ObserverArg.REFRESH, position);
    }

    /**
     * 动画刷新
     */
    public void animationRefresh() {
        if (getCount() > 0) notifyDataSetChanged(ObserverArg.REFRESH);
    }

    /**
     * 添加数据状态监听器
     */
    public void addDataChangeSupport(PropertyChangeListener listener) {
        this.dataChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * 删除数据状态监听器
     */
    public void removeDataChangeSupport(PropertyChangeListener listener) {
        this.dataChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * 工作线程中执行计算任务
     */
    @Override
    public void onWork(BuildData<T, F> data) {
        buildData(data.getBuildConfig(), data.getData(), data.getStartPosition());
        Message message = Message.obtain();
        message.obj = data;
        this.uiHandler.sendMessage(message);
    }

    /**
     * main 线程回调
     */
    @Override
    public boolean handleMessage(Message msg) {
        if (msg.obj instanceof BuildData) {
            BuildData<T, F> buildData = (BuildData<T, F>) msg.obj;
            this.buildConfig = buildData.getBuildConfig();
            this.renderData = buildData.getData();
            this.dataSize = renderData.size();
            notifyDataSetChanged(buildData.getObserverArg());
        }
        return true;
    }

    /**
     * 开启异步任务
     */
    private void onAsyTask(@NonNull final F buildConfig, @NonNull final List<T> data, @NonNull final ObserverArg arg, @NonNull Integer startPosition) {
        if (null == workThread) {
            this.workThread = new WorkThread<>();
        }
        this.workThread.post(new BuildData<>(buildConfig, data, arg, startPosition), this);
    }

    /**
     * 停止异步任务
     */
    void stopAsyTask() {
        if (null != workThread) {
            this.workThread.removeAllMessage();
        }
        if (null != uiHandler) {
            this.uiHandler.removeMessages(0);
        }
    }

    /**
     * 停止动画
     */
    void stopAnimator() {
        if (animator.isRunning()) {
            this.animator.end();
        }
    }

    /**
     * 数据刷新
     */
    public void notifyDataSetChanged(ObserverArg observerArg) {
        this.dataChangeSupport.firePropertyChange(observerArg.name(), null, observerArg);
    }

    /**
     * 获取日期格式化工具
     *
     * @return 日期格式化工具
     */
    public DateFormatter getDateFormatter() {
        return dateFormatter;
    }

    /**
     * 获取数值格式化工具
     *
     * @return 数值格式化工具
     */
    public ValueFormatter getValueFormatter() {
        return valueFormatter;
    }

    /**
     * 资源销毁（此方法在Activity/Fragment销毁的时候必须调用）
     */
    public void onDestroy() {
        stopAnimator();
        stopAsyTask();
        this.liveState = false;
        if (null != workThread) {
            this.workThread.destroyThread();
            this.workThread = null;
        }
    }
}
