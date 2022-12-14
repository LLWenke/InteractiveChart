package com.wk.chart.adapter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public abstract class AbsAdapter<T extends AbsEntry, F extends AbsBuildConfig>
        implements Handler.Callback, WorkThread.WorkCallBack<BuildData<T, F>>,
        ChartAnimator.AnimationListener<T> {

    private final DataSetObservable dataSetObservable;//数据状态监听器
    private final ScaleEntry scale;// 精度
    private final Handler uiHandler; //主线程Handler
    private final ChartAnimator<T> animator;//数据更新动画
    private F buildConfig; // 构建配置信息
    private WorkThread<BuildData<T, F>> workThread;//异步任务处理线程
    private RateEntry rate;// 比率
    private QuantizationEntry quantizationEntry;//量化
    private List<T> renderData;//数据列表（渲染）
    protected float high;//Y 轴上entry的最高值
    protected float low;//Y 轴上entry的最低值
    protected int maxYIndex;// Y 轴上entry的最高值索引
    protected int minYIndex;//Y 轴上entry的最低值索引
    private int highlightIndex;//高亮的 entry 索引
    private int dataSize;//数据大小
    private boolean liveState = true;//adapter存活状态

    AbsAdapter(@NonNull F buildConfig) {
        this.buildConfig = buildConfig;
        this.renderData = new ArrayList<>();
        this.uiHandler = new Handler(Looper.getMainLooper(), this);
        this.dataSetObservable = new DataSetObservable();
        this.animator = new ChartAnimator<>(this, 400);
        this.scale = new ScaleEntry(0, 0, "", "");
        this.rate = new RateEntry(1.0, scale.getQuoteUnit(), scale.getQuoteScale());
        this.quantizationEntry = new QuantizationEntry();
    }

    AbsAdapter(@NonNull AbsAdapter<T, F> absAdapter) {
        this(absAdapter.getBuildConfig());
        this.renderData.addAll(absAdapter.renderData);
        this.high = absAdapter.high;
        this.low = absAdapter.low;
        this.maxYIndex = absAdapter.maxYIndex;
        this.minYIndex = absAdapter.minYIndex;
        this.highlightIndex = absAdapter.highlightIndex;
        this.rate = absAdapter.rate;
        this.quantizationEntry = absAdapter.quantizationEntry;
        this.dataSize = renderData.size();
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
     * 设置量化配置
     *
     * @param minFormatNum 最小量化数（即小于此数，不量化）
     * @param scale        量化后小数的精度
     */
    public void setQuantization(long minFormatNum, int scale) {
        this.quantizationEntry.reset(minFormatNum, scale);
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
    }

    /**
     * 重置比率
     */
    public void resetRate() {
        this.rate = new RateEntry(1.0, scale.getQuoteUnit(), scale.getQuoteScale());
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
        stopAnimator();
        stopAsyTask();
        this.buildConfig = buildConfig;
        if (getCount() == 0) return;
        onAsyTask(buildConfig, cloneDataList(), ObserverArg.INIT, 0, null);
    }

    /**
     * 构建数据
     */
    abstract void buildData(@NonNull F buildConfig, @NonNull List<T> data, int startPosition);

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
        } catch (Exception e) {
            e.printStackTrace();
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
            onAsyTask(buildConfig, cloneDataList(), ObserverArg.FORMAT_UPDATE, 0, null);
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
            onAsyTask(buildConfig, data, buildConfig.isInit() ? ObserverArg.RESET : ObserverArg.INIT, 0, null);
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
            onAsyTask(buildConfig, data, buildConfig.isInit() ? ObserverArg.UPDATE : ObserverArg.INIT, 0, null);
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
            onAsyTask(buildConfig, data, ObserverArg.ADD, 0, null);
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
            onAsyTask(buildConfig, data, ObserverArg.ADD, getLastPosition(), null);
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
            onAsyTask(buildConfig, copyList, ObserverArg.UPDATE, getLastPosition(), null);
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
            onAsyTask(buildConfig, copyList, ObserverArg.REFRESH, position, position);
        }
    }

    /**
     * 动画执行中
     *
     * @param position   动画位置
     * @param updateData 更行数据
     */
    @Override
    public void onAnimation(int position, T updateData) {
        if (getItem(position).getTime().getTime() == updateData.getTime().getTime()) {
            this.renderData.set(position, updateData);
            notifyDataSetChanged(ObserverArg.REFRESH);
        }
    }

    /**
     * 动画刷新
     */
    public void animationRefresh() {
        if (getCount() == 0) {
            return;
        }
        notifyDataSetChanged(ObserverArg.REFRESH);
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
            Integer animPosition = buildData.getAnimPosition();
            if (null == animPosition) {
                this.buildConfig = buildData.getBuildConfig();
                this.renderData = buildData.getData();
                this.dataSize = renderData.size();
                notifyDataSetChanged(buildData.getObserverArg());
            } else if (animPosition < getCount() && animPosition < buildData.getDataSize()) {
                T oldItem = getItem(animPosition);
                T newItem = buildData.getData().get(animPosition);
                this.animator.startAnimator(oldItem, newItem, animPosition);
            }
        }
        return true;
    }

    /**
     * 开启异步任务
     */
    private void onAsyTask(@NonNull final F buildConfig,
                           @NonNull final List<T> data,
                           @NonNull final ObserverArg arg,
                           @NonNull Integer startPosition,
                           @Nullable Integer animPosition) {
        if (null == workThread) {
            this.workThread = new WorkThread<>();
        }
        this.workThread.post(new BuildData<>(buildConfig, data, arg, startPosition, animPosition), this);
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
     * 解绑监听
     */
    public void unRegisterListener() {
        this.dataSetObservable.deleteObservers();
    }

    /**
     * 数据刷新
     */
    public void notifyDataSetChanged(ObserverArg observerArg) {
        this.dataSetObservable.notifyObservers(observerArg);
    }

    /**
     * 汇率转换（此处已做精度控制）
     *
     * @param entry              传入的entry
     * @param isQuantization     是否量化转换
     * @param stripTrailingZeros 去除无用的0（如：2.4560->2.456）
     */
    public String rateConversion(ValueEntry entry, boolean isQuantization, boolean stripTrailingZeros) {
        return rateConversion(entry.result, null == entry.scale ? 0 : entry.scale,
                isQuantization, stripTrailingZeros);
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
        return ValueUtils.rateFormat(entry.result, null == entry.scale ? 0 : entry.scale,
                null, getQuantizationEntry(), stripTrailingZeros);
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
