package com.wk.chart.adapter;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.wk.chart.animator.ChartAnimator;
import com.wk.chart.compat.DataSetObservable;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.config.AbsBuildConfig;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.BuildData;
import com.wk.chart.entry.RateEntry;
import com.wk.chart.enumeration.ObserverArg;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.thread.WorkThread;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public abstract class AbsAdapter<T extends AbsEntry, F extends AbsBuildConfig>
        implements Handler.Callback, WorkThread.WorkCallBack<BuildData<T, F>>, ChartAnimator.AnimationListener<T> {

    private final DataSetObservable dataSetObservable;//数据状态监听器
    private F buildConfig; // 构建配置信息
    private WorkThread<BuildData<T, F>> workThread;//异步任务处理线程
    protected float high;//Y 轴上entry的最高值
    protected float low;//Y 轴上entry的最低值
    protected int maxYIndex;// Y 轴上entry的最高值索引
    protected int minYIndex;//Y 轴上entry的最低值索引
    private int highlightIndex;//高亮的 entry 索引
    private List<T> chartData;//数据列表
    private final ScaleEntry scale;// 精度
    private RateEntry rate;// 比率
    private Handler uiHandler; //主线程Handler
    private ChartAnimator<T> animator;//数据更新动画
    private boolean isWorking = false;//是否为工作中
    private int dataSize;//数据大小
    private boolean liveState = true;//adapter存活状态

    AbsAdapter(@NonNull F buildConfig) {
        this.buildConfig = buildConfig;
        this.chartData = new ArrayList<>();
        this.uiHandler = new Handler(this);
        this.dataSetObservable = new DataSetObservable();
        this.animator = new ChartAnimator<>(this, 800);
        this.scale = new ScaleEntry(0, 0, "", "");
        this.rate = new RateEntry(BigDecimal.ONE, scale.getQuoteUnit(), scale.quoteScale);
    }

    AbsAdapter(@NonNull AbsAdapter<T, F> absAdapter) {
        this(absAdapter.getBuildConfig());
        this.chartData.addAll(absAdapter.chartData);
        this.high = absAdapter.high;
        this.low = absAdapter.low;
        this.maxYIndex = absAdapter.maxYIndex;
        this.minYIndex = absAdapter.minYIndex;
        this.highlightIndex = absAdapter.highlightIndex;
        this.rate = absAdapter.rate;
        this.isWorking = absAdapter.isWorking;
        this.dataSize = chartData.size();
        this.setScale(absAdapter.getScale().getBaseScale()
                , absAdapter.getScale().getQuoteScale()
                , absAdapter.getScale().getBaseUnit()
                , absAdapter.getScale().getQuoteUnit());
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
     * 获取比率
     *
     * @return 比率值
     */
    public @NonNull
    RateEntry getRate() {
        return rate;
    }

    /**
     * 设置比率
     *
     * @param rateValue 比率值
     * @param unit      单位
     * @param scale     精度
     */
    public void setRate(BigDecimal rateValue, String unit, int scale) {
        this.rate.setRate(rateValue);
        this.rate.setUnit(unit);
        this.rate.setScale(scale);
        notifyDataSetChanged(ObserverArg.REFRESH);
    }

    /**
     * 重置比率
     */
    public void resetRate() {
        this.rate = new RateEntry(BigDecimal.ONE, scale.getQuoteUnit(), scale.quoteScale);
        notifyDataSetChanged(ObserverArg.REFRESH);
    }

    /**
     * 获取汇率设置状态
     */
    public boolean getRateState() {
        return getRate().getRate().compareTo(BigDecimal.ONE) != 0;
    }

    /**
     * 获取指标配置信息(深拷贝一份，不会影响源文件)
     *
     * @return 配置信息类（复制品）
     */
    public F getBuildConfig() {
        return (F) buildConfig.clone();
    }

    /**
     * 设置指标配置信息(深拷贝一份进行赋值，不会使用源文件)
     */
    public void setBuildConfig(F buildConfig) {
        setWorking(true);
        onAsyTask((F) buildConfig.clone(), chartData, ObserverArg.CONFIG_CHANGE);
    }

    /**
     * 构建数据
     */
    abstract void buildData(@NonNull F buildConfig, @NonNull List<T> data);

    /**
     * 在给定的范围内，计算最大值和最小值
     */
    public abstract void computeMinAndMax(int start, int end, List<AbsChartModule<? extends AbsEntry>> chartModules);

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
        if (Utils.listIsEmpty(data)) {
            this.chartData.clear();
            this.dataSize = chartData.size();
            notifyDataSetChanged(ObserverArg.INIT);
        } else {
            setWorking(true);
            onAsyTask(buildConfig, data, ObserverArg.INIT);
        }
    }

    /**
     * 向头部添加一组数据
     */
    public synchronized void addHeaderData(List<T> data) {
        if (Utils.listIsEmpty(data)) {
            notifyDataSetChanged(ObserverArg.NORMAL);
        } else {
            setWorking(true);
            data.addAll(chartData);
            onAsyTask(buildConfig, data, ObserverArg.ADD);
        }
    }

    /**
     * 向尾部添加一组数据
     */
    public synchronized void addFooterData(List<T> data) {
        if (Utils.listIsEmpty(data)) {
            notifyDataSetChanged(ObserverArg.NORMAL);
        } else {
            setWorking(true);
            data.addAll(0, chartData);
            onAsyTask(buildConfig, data, ObserverArg.ADD);
        }
    }

    /**
     * 向尾部部添加一条数据
     */
    public void addFooterData(T data) {
        if (null != data && !isWorking) {
            stopAnimator();
            this.chartData.add(data);
            this.dataSize = chartData.size();
            buildData(buildConfig, chartData);
            notifyDataSetChanged(ObserverArg.PUSH);
        }
    }

    /**
     * 更新某个item
     *
     * @param position 索引值
     */
    public void changeItem(int position, T data) {
        if (null != data && position >= 0 && position < getCount() && !isWorking) {
            this.animator.startAnimator(getItem(position), data, position, true);
        }
    }

    @Override
    public void onAnimation(int position, T updateData, boolean buildState) {
        if (buildState) {
            this.chartData.set(position, updateData);
            buildData(buildConfig, chartData);
            notifyDataSetChanged(ObserverArg.PUSH);
        } else {
            notifyDataSetChanged(ObserverArg.REFRESH);
        }

    }

    /**
     * 动画刷新
     */
    public void animationRefresh() {
        if (null == animator || animator.isRunning() || isWorking || getCount() == 0) {
            return;
        }
        T last = getItem(getLastPosition());
        this.animator.startAnimator(last, last, getLastPosition(), false);
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
    @Override
    public void onWork(BuildData<T, F> data, ObserverArg observerArg) {
        buildData(data.getBuildConfig(), data.getData());
        Message message = Message.obtain();
        message.obj = data;
        message.what = observerArg.ordinal();
        this.uiHandler.sendMessage(message);
    }

    /**
     * main 线程回调
     */
    @Override
    public boolean handleMessage(Message msg) {
        setWorking(false);
        if (msg.obj instanceof BuildData) {
            BuildData<T, F> buildData = (BuildData<T, F>) msg.obj;
            this.chartData = buildData.getData();
            this.buildConfig = buildData.getBuildConfig();
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
     * 开启异步任务
     */
    private void onAsyTask(final F buildConfig, final List<T> data, @NonNull final ObserverArg arg) {
        if (null == workThread) {
            this.workThread = new WorkThread<>();
        }
        this.workThread.post(new BuildData<>(buildConfig, data), this, arg);
    }

    /**
     * 资源销毁（此方法在Activity/Fragment销毁的时候必须调用）
     */
    public void onDestroy() {
        this.liveState = false;
        if (null != workThread) {
            this.workThread.destroyThread();
            this.workThread = null;
        }
        stopAnimator();
    }

    /**
     * 数据刷新
     */
    public void notifyDataSetChanged(ObserverArg observerArg) {
        this.dataSetObservable.notifyObservers(observerArg);
    }

    /**
     * 是否在工作状态中
     */
    boolean isWorking() {
        return isWorking;
    }

    /**
     * 设置工作状态
     */
    void setWorking(boolean working) {
        this.isWorking = working;
        stopAnimator();
    }

    /**
     * 如果开启了动画，在任何改变数据（data）操作之前需调用此方法停止动画，否则会造成数据错乱
     */
    void stopAnimator() {
        if (null != animator && animator.isRunning()) {
            this.animator.end();
        }
    }

    /**
     * 获取当前动画进度
     *
     * @return 动画进度值(0.0和1.0之间)
     */
    public float getAnimatorFraction() {
        return null == animator ? 1f : animator.getAnimatedFraction();
    }

    public static class ScaleEntry implements Serializable {
        private int baseScale;// base 精度
        private int quoteScale;// quote 精度
        private String baseUnit;// base 单位
        private String quoteUnit;// quote 单位

        public ScaleEntry(int baseScale, int quoteScale, String baseUnit, String quoteUnit) {
            this.baseScale = baseScale;
            this.quoteScale = quoteScale;
            this.baseUnit = baseUnit;
            this.quoteUnit = quoteUnit;
        }

        public ScaleEntry(int baseScale, int quoteScale) {
            this.baseScale = baseScale;
            this.quoteScale = quoteScale;
            this.baseUnit = "";
            this.quoteUnit = "";
        }

        void reset(int baseScale, int quoteScale) {
            this.baseScale = baseScale;
            this.quoteScale = quoteScale;
        }

        void reset(int baseScale, int quoteScale, String baseUnit, String quoteUnit) {
            this.baseScale = baseScale;
            this.quoteScale = quoteScale;
            this.baseUnit = baseUnit;
            this.quoteUnit = quoteUnit;
        }

        /**
         * 获取Base精度
         */
        public int getBaseScale() {
            return baseScale;
        }

        /**
         * 获取Quote精度
         */
        public int getQuoteScale() {
            return quoteScale;
        }

        /**
         * 设置Base单位
         */
        public String getBaseUnit() {
            return baseUnit;
        }

        /**
         * 设置Quote单位
         */
        public String getQuoteUnit() {
            return quoteUnit;
        }

    }

}
