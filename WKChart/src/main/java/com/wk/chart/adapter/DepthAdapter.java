package com.wk.chart.adapter;

import androidx.annotation.NonNull;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.config.NormalBuildConfig;
import com.wk.chart.entry.DepthEntry;

import java.util.ArrayList;
import java.util.List;

public class DepthAdapter extends AbsAdapter<DepthEntry, NormalBuildConfig> {
    public final static int BID = 0;//买单类型
    public final static int ASK = 1;//卖单类型

    public DepthAdapter(int baseScale, int quoteScale, String baseUnit, String quoteUnit) {
        super(new NormalBuildConfig());
        setScale(baseScale, quoteScale, baseUnit, quoteUnit);
    }

    @Override
    void buildData(@NonNull NormalBuildConfig buildConfig, @NonNull List<DepthEntry> data) {
        buildConfig.setInit(true);
        calculationData(data);
    }

    /**
     * 在给定的范围内，计算最大值和最小值
     */
    @Override
    public void computeMinAndMax(int start, int end) {
    }

    /**
     * 数据计算
     */
    private void calculationData(List<DepthEntry> data) {
        if (Utils.listIsEmpty(data)) {
            return;
        }
        List<DepthEntry> bids = new ArrayList<>();//买单数据
        List<DepthEntry> asks = new ArrayList<>();//卖单数据
        for (int i = 0, z = data.size(); i < z; i++) {
            DepthEntry item = data.get(i);
            switch (item.getType()) {
                case BID://买单
                    bids.add(item);
                    break;
                case ASK://卖单
                    asks.add(item);
                    break;
            }
        }
        data.clear();
        if ((Utils.listIsEmpty(bids) && Utils.listIsEmpty(asks))) {
            return;
        }
        if (Utils.listIsEmpty(bids)) {
            DepthEntry bidBean = new DepthEntry(getScale(), asks.get(0).getPrice().value,
                    0, 0, BID, null);
            bids.add(bidBean);
        } else if (Utils.listIsEmpty(asks)) {
            DepthEntry askBean = new DepthEntry(getScale(),
                    bids.get(0).getPrice().result + (bids.get(0).getPrice().result - bids.get(bids.size() - 1)
                            .getPrice().result), 0, 0, ASK, null);
            asks.add(askBean);
        }
        if (bids.size() == 1) {
            DepthEntry bidBean = new DepthEntry(getScale(), 0, 0,
                    bids.get(0).getTotalAmount().value, BID, null);
            bids.add(bidBean);
        }
        if (asks.size() == 1) {
            DepthEntry askBean = new DepthEntry(getScale(),
                    asks.get(0).getPrice().result + (bids.get(0).getPrice().result - bids.get(bids.size() - 1)
                            .getPrice().result), 0, asks.get(0).getTotalAmount().result, ASK, null);
            asks.add(askBean);
        }
        //保持买单/卖单数据的价格跨度值一致
        long bidsDiff = bids.get(0).getPrice().result - bids.get(bids.size() - 1).getPrice().result;//买单数据的价格跨度值
        long asksDiff = asks.get(asks.size() - 1).getPrice().result - asks.get(0).getPrice().result;//卖单数据的价格跨度值

        if (bidsDiff > asksDiff) {
            //补齐最低值
            long minPrice = bids.get(0).getPrice().result - asksDiff;
            bids = bids.subList(0, indexOfDiff(minPrice, 0, bids.size() - 1, bids, 1));//剔除不在跨度范围内的数据
            DepthEntry minBean = new DepthEntry(getScale(), minPrice, 0,
                    bids.get(bids.size() - 1).getTotalAmount().result, BID, null);
            bids.add(minBean);
        } else if (bidsDiff < asksDiff) {
            //补齐最高值
            long maxPrice = asks.get(0).getPrice().result + bidsDiff;
            asks = asks.subList(0, indexOfDiff(maxPrice, 0, asks.size() - 1, asks, 2));//剔除不在跨度范围内的数据
            DepthEntry maxBean = new DepthEntry(getScale(), maxPrice, 0,
                    asks.get(asks.size() - 1).getTotalAmount().result, ASK, null);
            asks.add(maxBean);
        }
        data.addAll(bids);
        data.addAll(asks);
    }

    /**
     * 二分查找当前值的index
     */
    private int indexOfDiff(long value, int start, int end, List<DepthEntry> data,
                            int type) {
        int count = data.size();
        if (count == 0) {
            return 0;
        } else if (end == start) {
            return end + 1;
        } else if (end - start == 1) {
            return end;
        }
        int mid = start + (end - start) / 2;
        long midValue = data.get(mid).getPrice().result;
        switch (type) {
            case 1://反向查找
                if (value < midValue) {
                    return indexOfDiff(value, mid, end, data, type);
                } else if (value > midValue) {
                    return indexOfDiff(value, start, mid, data, type);
                } else {
                    return mid + 1;
                }
            case 2://正向查找
                if (value < midValue) {
                    return indexOfDiff(value, start, mid, data, type);
                } else if (value > midValue) {
                    return indexOfDiff(value, mid, end, data, type);
                } else {
                    return mid + 1;
                }
        }
        return 0;
    }

}
