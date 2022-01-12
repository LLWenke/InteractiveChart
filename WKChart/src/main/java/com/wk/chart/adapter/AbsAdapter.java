package com.wk.chart.adapter;

import android.os.Handler;
import android.os.Message;

import com.wk.chart.animator.ChartAnimator;
import com.wk.chart.compat.DataSetObservable;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.config.AbsBuildConfig;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.BuildData;
import com.wk.chart.entry.QuantizationEntry;
import com.wk.chart.entry.RateEntry;
import com.wk.chart.entry.ScaleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.ObserverArg;
import com.wk.chart.thread.WorkThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import androidx.annotation.NonNull;

public abstract class AbsAdapter<T extends AbsEntry, F extends AbsBuildConfig>
        implements Handler.Callback, WorkThread.WorkCallBack<BuildData<T, F>>,
        ChartAnimator.AnimationListener<T> {

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
    private QuantizationEntry quantizationEntry;//量化
    private final Handler uiHandler; //主线程Handler
    private final ChartAnimator<T> animator;//数据更新动画
    private boolean isWorking = false;//是否为工作中
    private int dataSize;//数据大小
    private boolean liveState = true;//adapter存活状态

    AbsAdapter(@NonNull F buildConfig) {
        this.buildConfig = buildConfig;
        this.chartData = new ArrayList<>();
        this.uiHandler = new Handler(this);
        this.dataSetObservable = new DataSetObservable();
        this.animator = new ChartAnimator<>(this, 400);
        this.scale = new ScaleEntry(0, 0, "", "");
        this.rate = new RateEntry(1.0, scale.getQuoteUnit(), scale.getQuoteScale());
        this.quantizationEntry = new QuantizationEntry();
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
        this.quantizationEntry = absAdapter.quantizationEntry;
        this.isWorking = absAdapter.isWorking;
        this.dataSize = chartData.size();
        this.setScale(absAdapter.getScale().getBaseScale()
                , absAdapter.getScale().getQuoteScale()
                , absAdapter.getScale().getBaseUnit()
                , absAdapter.getScale().getQuoteUnit());
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
     * 设置量化配置
     *
     * @param minFormatNum 最小量化数（即小于此数，不量化）
     * @param scale        量化后小数的精度
     */
    public void setQuantization(long minFormatNum, int scale) {
        this.quantizationEntry.reset(minFormatNum, scale);
        notifyDataSetChanged(ObserverArg.UPDATE);
    }

    /**
     * 获取比率实例
     *
     * @return 比率值
     */
    public @NonNull
    RateEntry getRate() {
        return rate;
    }

    /**
     * 获取量化实例
     *
     * @return 取量值
     */
    public QuantizationEntry getQuantizationEntry() {
        return quantizationEntry;
    }

    /**
     * 设置比率
     *
     * @param rateValue 比率值
     * @param unit      单位
     * @param scale     精度
     */
    public void setRate(Double rateValue, String unit, int scale) {
        this.rate.setRate(rateValue);
        this.rate.setSign(unit);
        this.rate.setScale(scale);
        notifyDataSetChanged(ObserverArg.RATE_UPDATE);
    }

    /**
     * 重置比率
     */
    public void resetRate() {
        this.rate = new RateEntry(1.0, scale.getQuoteUnit(), scale.getQuoteScale());
        notifyDataSetChanged(ObserverArg.RATE_UPDATE);
    }

    /**
     * 获取汇率设置状态
     */
    public boolean getRateState() {
        return getRate().isSet();
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
        this.buildConfig = buildConfig;
        if (chartData.isEmpty()) {
            return;
        }
        setWorking(true);
        onAsyTask(buildConfig, chartData, ObserverArg.INIT);
    }

    /**
     * 构建数据
     */
    abstract void buildData(@NonNull F buildConfig, @NonNull List<T> data);

    /**
     * 在给定的范围内，计算最大值和最小值
     */
    public abstract void computeMinAndMax(int start, int end);

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
     * 清空数据
     */
    public synchronized void clearData() {
        stopAnimator();
        this.chartData.clear();
        this.dataSize = chartData.size();
        notifyDataSetChanged(ObserverArg.RESET);
    }

    /**
     * 重置数据
     */
    public synchronized void resetData(List<T> data) {
        ObserverArg observerArg = buildConfig.isInit() ? ObserverArg.RESET : ObserverArg.INIT;
        if (Utils.listIsEmpty(data)) {
            stopAnimator();
            this.chartData.clear();
            this.dataSize = chartData.size();
            notifyDataSetChanged(observerArg);
        } else {
            setWorking(true);
            onAsyTask(buildConfig, data, observerArg);
        }
    }

    /**
     * 更新数据
     */
    public synchronized void updateData(List<T> data) {
        ObserverArg observerArg = buildConfig.isInit() ? ObserverArg.UPDATE : ObserverArg.INIT;
        if (Utils.listIsEmpty(data)) {
            stopAnimator();
            this.chartData.clear();
            this.dataSize = chartData.size();
            notifyDataSetChanged(observerArg);
        } else {
            setWorking(true);
            onAsyTask(buildConfig, data, observerArg);
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
            notifyDataSetChanged(ObserverArg.UPDATE);
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
        }
        notifyDataSetChanged(ObserverArg.UPDATE);
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

    /**
     * 汇率转换（此处已做精度控制）
     *
     * @param entry              传入的entry
     * @param isQuantization     是否量化转换
     * @param stripTrailingZeros 去除无用的0（如：2.4560->2.456）
     */
    public String rateConversion(ValueEntry entry, boolean isQuantization, boolean stripTrailingZeros) {
        return rateConversion(entry.result, entry.getScale(), isQuantization, stripTrailingZeros);
    }

    /**
     * 汇率转换（此处已做精度控制）
     *
     * @param value              传入的value值
     * @param scale              精度
     * @param isQuantization     是否量化转换
     * @param stripTrailingZeros 去除无用的0（如：2.4560->2.456）
     */
    public String rateConversion(double value, int scale, boolean isQuantization, boolean stripTrailingZeros) {
        return rateConversion(ValueUtils.buildResult(value, scale), scale, isQuantization, stripTrailingZeros);
    }

    /**
     * 汇率转换（此处已做精度控制）
     *
     * @param result             传入的result值
     * @param scale              精度
     * @param isQuantization     是否量化转换
     * @param stripTrailingZeros 去除无用的0（如：2.4560->2.456）
     */
    public String rateConversion(long result, int scale, boolean isQuantization, boolean stripTrailingZeros) {
        if (isQuantization) {
            return ValueUtils.rateFormat(result, scale, getRate(), getQuantizationEntry(), stripTrailingZeros);
        } else {
            return ValueUtils.rateFormat(result, scale, getRate(), null, stripTrailingZeros);
        }
    }

    /**
     * 量化转换（此处已做精度控制）
     *
     * @param entry              传入的entry
     * @param stripTrailingZeros 去除无用的0（如：2.4560->2.456）
     */
    public String quantizationConversion(ValueEntry entry, boolean stripTrailingZeros) {
        return ValueUtils.rateFormat(entry.result, entry.getScale(), null, getQuantizationEntry(), stripTrailingZeros);
    }
}
