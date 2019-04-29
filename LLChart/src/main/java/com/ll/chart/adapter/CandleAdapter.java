package com.ll.chart.adapter;

import android.util.Log;
import com.ll.chart.compat.DateUtil;
import com.ll.chart.compat.Utils;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.enumeration.DisplayType;
import com.ll.chart.enumeration.PushType;
import com.ll.chart.module.base.AbsChartModule;
import java.util.Date;
import java.util.List;

public class CandleAdapter extends AbsAdapter<CandleEntry> {

  private DisplayType displayType = DisplayType.oneHour;//显示模式
  private CalculationCache calculationCache = new CalculationCache();//计算结果缓存类

  public CandleAdapter() {
    super();
  }

  public CandleAdapter(CandleAdapter adapter) {
    super(adapter);
    this.displayType = adapter.displayType;
    this.calculationCache = adapter.calculationCache;
  }

  /**
   * 获取显示模式
   */
  public DisplayType getDisplayType() {
    return displayType;
  }

  /**
   * 构建数据
   */
  @Override void buildData(List<CandleEntry> data) {
    //计算 MA MACD BOLL RSI KDJ 指标
    computeMA(data);
    computeMACD(data);
    computeBOLL(data);
    computeRSI(data);
    computeKDJ(data);
  }

  /**
   * 在给定的范围内，计算最大值和最小值
   */
  public void computeMinAndMax(int start, int end,
      List<AbsChartModule<? super AbsEntry>> chartModules) {
    low = Float.MAX_VALUE;
    high = -Float.MAX_VALUE;

    for (AbsChartModule item : chartModules) {
      if (item.isEnable()) {
        item.resetMinMax();
      }
    }
    for (int i = start; i < end; i++) {
      CandleEntry entry = getItem(i);
      if (entry.getLow().value < low) {
        low = entry.getLow().value;
        minYIndex = i;
      }
      if (entry.getHigh().value > high) {
        high = entry.getHigh().value;
        maxYIndex = i;
      }
      for (AbsChartModule item : chartModules) {
        if (item.isEnable()) {
          item.computeMinMax(i, entry);
        }
      }
    }
  }

  /**
   * 刷新数据
   */
  public synchronized void resetData(DisplayType type, List<CandleEntry> data) {
    if (!Utils.listIsEmpty(data)) {
      stopAnimator();
      this.displayType = type;
      this.calculationCache.init();
    }
    super.resetData(data);
  }

  /**
   * 向头部添加一组数据
   */
  @Override public synchronized void addHeaderData(List<CandleEntry> data) {
    if (!Utils.listIsEmpty(data)) {
      stopAnimator();
      this.calculationCache.init();
    }
    super.addHeaderData(data);
  }

  /**
   * 向尾部添加一组数据
   */
  @Override public synchronized void addFooterData(List<CandleEntry> data) {
    if (!Utils.listIsEmpty(data)) {
      stopAnimator();
      this.calculationCache.init();
    }
    super.addFooterData(data);
  }

  /**
   * 数据推送
   */
  public PushType dataPush(CandleEntry data) {
    if (null == data || getCount() == 0) {
      return PushType.INVALID;
    }
    Date endDate = data.getTime();
    if (isWorking() || null == endDate) {
      return PushType.INVALID;
    }
    this.calculationCache.index = getCount() - 1;
    PushType pushType = getPushType(endDate);
    switch (pushType) {
      case UPDATE://修改
        changeItem(getCount() - 1, data);
        break;
      case ADD://添加
        addFooterData(data);
        break;
    }
    Log.e("dataPush-->" + pushType, data.toString());
    return pushType;
  }

  /**
   * 判断数据是更新还是追加
   */
  private PushType getPushType(Date endDate) {
    long diff = DateUtil.getDiffBetween(getItem(getLastPosition()).getTime(),
        endDate, displayType.msec());
    //Log.e("lastTime:", DisplayTypeUtils.selectorFormat(getItem(getLastPosition()).getTime(),
    //    getDisplayType()));
    //Log.e("endDate:", DisplayTypeUtils.selectorFormat(endDate,
    //    getDisplayType()));
    if (diff < 0) {
      return PushType.INVALID;
    } else if (diff < displayType.value()) {
      return PushType.UPDATE;//修改
    } else if (diff == displayType.value()) {
      return PushType.ADD;//添加
    } else {
      return PushType.INTERMITTENT;//间断
    }
  }

  /**
   * 计算 MA
   */
  private void computeMA(List<CandleEntry> data) {
    long ma5 = calculationCache.ma5;
    long ma10 = calculationCache.ma10;
    long ma20 = calculationCache.ma20;
    long volumeMa5 = calculationCache.volumeMa5;
    long volumeMa10 = calculationCache.volumeMa10;

    for (int i = calculationCache.index, z = data.size(); i < z; i++) {
      CandleEntry entry = data.get(i);

      ma5 += entry.getClose().result;
      ma10 += entry.getClose().result;
      ma20 += entry.getClose().result;

      volumeMa5 += entry.getVolume().result;
      volumeMa10 += entry.getVolume().result;

      if (i >= 5) {
        ma5 -= data.get(i - 5).getClose().result;
        entry.setMa5(ma5 / 5);

        volumeMa5 -= data.get(i - 5).getVolume().result;
        entry.setVolumeMa5(volumeMa5 / 5);
      } else {
        entry.setMa5(ma5 / (i + 1));

        entry.setVolumeMa5(volumeMa5 / (i + 1));
      }
      if (i >= 10) {
        ma10 -= data.get(i - 10).getClose().result;
        entry.setMa10(ma10 / 10);

        volumeMa10 -= data.get(i - 10).getVolume().result;
        entry.setVolumeMa10(volumeMa10 / 5);
      } else {
        entry.setMa10(ma10 / (i + 1));

        entry.setVolumeMa10(volumeMa10 / (i + 1));
      }

      if (i >= 20) {
        ma20 -= data.get(i - 20).getClose().result;
        entry.setMa20(ma20 / 20);
      } else {
        entry.setMa20(ma20 / (i + 1));
      }

      //将倒数第二次的计算结果缓存
      if (i == z - 2) {
        this.calculationCache.ma5 = ma5;
        this.calculationCache.ma10 = ma10;
        this.calculationCache.ma20 = ma20;
        this.calculationCache.volumeMa5 = volumeMa5;
        this.calculationCache.volumeMa10 = volumeMa10;
      }
    }
  }

  /**
   * 计算 MACD
   */
  private void computeMACD(List<CandleEntry> data) {
    long ema12 = calculationCache.ema12;
    long ema26 = calculationCache.ema26;
    long dea = calculationCache.dea;
    long diff;
    long macd;

    for (int i = calculationCache.index, z = data.size(); i < z; i++) {
      CandleEntry entry = data.get(i);

      if (i == 0) {
        ema12 = entry.getClose().result;
        ema26 = entry.getClose().result;
      } else {
        // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
        // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
        ema12 = ema12 * 11 / 13 + entry.getClose().result * 2 / 13;
        ema26 = ema26 * 25 / 27 + entry.getClose().result * 2 / 27;
      }

      // DIF = EMA（12） - EMA（26） 。
      // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
      // 用（DIF-DEA）*2 即为 MACD 柱状图。
      diff = ema12 - ema26;
      dea = dea * 8 / 10 + diff * 2 / 10;
      macd = (diff - dea) * 2;

      entry.setDiff(diff);
      entry.setDea(dea);
      entry.setMacd(macd);

      //将倒数第二次的计算结果缓存
      if (i == z - 2) {
        this.calculationCache.ema12 = ema12;
        this.calculationCache.ema26 = ema26;
        this.calculationCache.dea = dea;
      }
    }
  }

  /**
   * 计算 BOLL 需要在计算 MA 之后进行
   */
  private void computeBOLL(List<CandleEntry> data) {
    for (int i = calculationCache.index, z = data.size(); i < z; i++) {
      CandleEntry entry = data.get(i);

      if (i == 0) {
        entry.setMb(entry.getClose().result);
        entry.setUp(0);
        entry.setDn(0);
      } else {
        int n = 20;// 1～20
        if (i < 20) {
          n = i + 1;
        }

        long md = 0;
        for (int j = i - n + 1; j <= i; j++) {
          long c = data.get(j).getClose().result;
          long m = entry.getMa20().result;
          long value = c - m;
          md += value * value;
        }

        md = md / (n - 1);
        md = (long) Math.sqrt(md);

        entry.setMb(entry.getMa20().result);
        entry.setUp(entry.getMb().result + 2 * md);
        entry.setDn(entry.getMb().result - 2 * md);
      }
    }
  }

  /**
   * 计算 RSI
   */
  private void computeRSI(List<CandleEntry> data) {
    long rsi1;
    long rsi2;
    long rsi3;
    long rsi1ABSEma = calculationCache.rsi1ABSEma;
    long rsi2ABSEma = calculationCache.rsi2ABSEma;
    long rsi3ABSEma = calculationCache.rsi3ABSEma;
    long rsi1MaxEma = calculationCache.rsi1MaxEma;
    long rsi2MaxEma = calculationCache.rsi2MaxEma;
    long rsi3MaxEma = calculationCache.rsi3MaxEma;

    for (int i = calculationCache.index, z = data.size(); i < z; i++) {
      CandleEntry entry = data.get(i);

      if (i == 0) {
        rsi1 = 0;
        rsi2 = 0;
        rsi3 = 0;
        rsi1ABSEma = 0;
        rsi2ABSEma = 0;
        rsi3ABSEma = 0;
        rsi1MaxEma = 0;
        rsi2MaxEma = 0;
        rsi3MaxEma = 0;
      } else {
        long Rmax = Math.max(0, entry.getClose().result - data.get(i - 1).getClose().result);
        long RAbs = Math.abs(entry.getClose().result - data.get(i - 1).getClose().result);

        rsi1MaxEma = (Rmax + (6 - 1) * rsi1MaxEma) / 6;
        rsi1ABSEma = (RAbs + (6 - 1) * rsi1ABSEma) / 6;

        rsi2MaxEma = (Rmax + (12 - 1) * rsi2MaxEma) / 12;
        rsi2ABSEma = (RAbs + (12 - 1) * rsi2ABSEma) / 12;

        rsi3MaxEma = (Rmax + (24 - 1) * rsi3MaxEma) / 24;
        rsi3ABSEma = (RAbs + (24 - 1) * rsi3ABSEma) / 24;

        rsi1 = rsi1MaxEma * 100 / rsi1ABSEma;
        rsi2 = rsi2MaxEma * 100 / rsi2ABSEma;
        rsi3 = rsi3MaxEma * 100 / rsi3ABSEma;
      }

      entry.setRsi1(rsi1);
      entry.setRsi2(rsi2);
      entry.setRsi3(rsi3);

      //将倒数第二次的计算结果缓存
      if (i == z - 2) {
        this.calculationCache.rsi1ABSEma = rsi1ABSEma;
        this.calculationCache.rsi2ABSEma = rsi2ABSEma;
        this.calculationCache.rsi3ABSEma = rsi3ABSEma;
        this.calculationCache.rsi1MaxEma = rsi1MaxEma;
        this.calculationCache.rsi2MaxEma = rsi2MaxEma;
        this.calculationCache.rsi3MaxEma = rsi3MaxEma;
      }
    }
  }

  /**
   * 计算 KDJ
   */
  private void computeKDJ(List<CandleEntry> data) {
    long k = calculationCache.k;
    long d = calculationCache.d;

    for (int i = calculationCache.index, z = data.size(); i < z; i++) {
      CandleEntry entry = data.get(i);

      int startIndex = i - 8;
      if (startIndex < 0) {
        startIndex = 0;
      }

      long max9 = Long.MIN_VALUE;
      long min9 = Long.MAX_VALUE;
      for (int index = startIndex; index <= i; index++) {
        max9 = Math.max(max9, data.get(index).getHigh().result);
        min9 = Math.min(min9, data.get(index).getLow().result);
      }
      long rsv;
      if (max9 != min9) {
        rsv = 100 * (entry.getClose().result - min9) / (max9 - min9);
      } else {
        rsv = 100 * (entry.getClose().result - min9);
      }
      if (i == 0) {
        k = rsv;
        d = rsv;
      } else {
        k = (rsv + 2 * k) / 3;
        d = (k + 2 * d) / 3;
      }

      entry.setK(k);
      entry.setD(d);
      entry.setJ(3 * k - 2 * d);

      //将倒数第二次的计算结果缓存
      if (i == z - 2) {
        this.calculationCache.k = k;
        this.calculationCache.d = d;
      }
    }
  }

  /**
   * 计算结果缓存类
   */
  class CalculationCache {
    //平均线
    long ma5 = 0;
    long ma10 = 0;
    long ma20 = 0;
    long volumeMa5 = 0;
    long volumeMa10 = 0;
    //macd
    long ema12 = 0;
    long ema26 = 0;
    long dea = 0;
    //RSI
    long rsi1ABSEma = 0;
    long rsi2ABSEma = 0;
    long rsi3ABSEma = 0;
    long rsi1MaxEma = 0;
    long rsi2MaxEma = 0;
    long rsi3MaxEma = 0;
    //KDJ
    long k = 0;
    long d = 0;
    //数据起始下标
    int index = 0;

    public void init() {
      //平均线
      ma5 = 0;
      ma10 = 0;
      ma20 = 0;
      volumeMa5 = 0;
      volumeMa10 = 0;
      //macd
      ema12 = 0;
      ema26 = 0;
      dea = 0;
      //RSI
      rsi1ABSEma = 0;
      rsi2ABSEma = 0;
      rsi3ABSEma = 0;
      rsi1MaxEma = 0;
      rsi2MaxEma = 0;
      rsi3MaxEma = 0;
      //KDJ
      k = 0;
      d = 0;
      //数据起始下标
      index = 0;
    }
  }
}
