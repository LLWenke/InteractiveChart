package com.ll.chart.adapter;

import com.ll.chart.compat.Utils;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.entry.DepthEntry;
import com.ll.chart.module.base.AbsChartModule;
import java.util.ArrayList;
import java.util.List;

public class DepthAdapter extends AbsAdapter<DepthEntry> {
  public final static int BID = 0;//买单类型
  public final static int ASK = 1;//卖单类型
  private String baseSymbol;
  private String quoteSymbol;
  private int quoteScale;

  public DepthAdapter(String baseSymbol, String quoteSymbol, int quoteScale) {
    super();
    init(baseSymbol, quoteSymbol, quoteScale);
  }

  public DepthAdapter(DepthAdapter depthAdapter) {
    super(depthAdapter);
    init(depthAdapter.baseSymbol, depthAdapter.quoteSymbol, depthAdapter.quoteScale);
  }

  private void init(String baseSymbol, String quoteSymbol, int quoteScale) {
    this.baseSymbol = baseSymbol;
    this.quoteSymbol = quoteSymbol;
    this.quoteScale = quoteScale;
  }

  @Override void buildData(List<DepthEntry> data) {
    calculationData(data);
  }

  public String getBaseSymbol() {
    return baseSymbol;
  }

  public String getQuoteSymbol() {
    return quoteSymbol;
  }

  public int getQuoteScale() {
    return quoteScale;
  }

  /**
   * 在给定的范围内，计算最大值和最小值
   */
  public void computeMinAndMax(int start, int end,
      List<AbsChartModule<? super AbsEntry>> chartModules) {
    for (AbsChartModule item : chartModules) {
      if (item.isEnable()) {
        item.resetMinMax();
      }
    }
    for (int i = start; i < end; i++) {
      DepthEntry entry = getItem(i);
      for (AbsChartModule item : chartModules) {
        if (item.isEnable()) {
          item.computeMinMax(i, entry);
        }
      }
    }
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
      DepthEntry bidBean = new DepthEntry(null, getScale(), getQuoteScale(),
          asks.get(0).getPrice().value, 0, 0, BID);
      bids.add(bidBean);
    } else if (Utils.listIsEmpty(asks)) {
      DepthEntry askBean = new DepthEntry(null, getScale(), getQuoteScale(),
          bids.get(0).getPrice().result + (bids.get(0).getPrice().result - bids.get(bids.size() - 1)
              .getPrice().result), 0, 0, ASK);
      asks.add(askBean);
    }
    if (bids.size() == 1) {
      DepthEntry bidBean = new DepthEntry(null, getScale(), getQuoteScale(), 0, 0,
          bids.get(0).getTotalAmount().value, BID);
      bids.add(bidBean);
    }
    if (asks.size() == 1) {
      DepthEntry askBean = new DepthEntry(null, getScale(), getQuoteScale(),
          asks.get(0).getPrice().result + (bids.get(0).getPrice().result - bids.get(bids.size() - 1)
              .getPrice().result), 0, asks.get(0).getTotalAmount().result, ASK);
      asks.add(askBean);
    }
    //保持买单/卖单数据的价格跨度值一致
    long bidsDiff = bids.get(0).getPrice().result
        - bids.get(bids.size() - 1).getPrice().result;//买单数据的价格跨度值
    long asksDiff = asks.get(asks.size() - 1).getPrice().result
        - asks.get(0).getPrice().result;//卖单数据的价格跨度值

    if (bidsDiff > asksDiff) {
      //补齐最低值
      long minPrice = bids.get(0).getPrice().result - asksDiff;
      bids = bids.subList(0, indexOfDiff(minPrice, 0, bids.size() - 1, bids, 1));//剔除不在跨度范围内的数据
      DepthEntry minBean = new DepthEntry(null, getScale(), getQuoteScale(), minPrice, 0,
          bids.get(bids.size() - 1).getTotalAmount().result, BID);
      bids.add(minBean);
    } else if (bidsDiff < asksDiff) {
      //补齐最高值
      long maxPrice = asks.get(0).getPrice().result + bidsDiff;
      asks = asks.subList(0, indexOfDiff(maxPrice, 0, asks.size() - 1, asks, 2));//剔除不在跨度范围内的数据
      DepthEntry maxBean = new DepthEntry(null, getScale(), getQuoteScale(), maxPrice, 0,
          asks.get(asks.size() - 1).getTotalAmount().result, ASK);
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
