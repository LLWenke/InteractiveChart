package com.ll.demo.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.ll.chart.entry.CandleEntry;
import com.ll.demo.model.ServiceMessage;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import org.greenrobot.eventbus.EventBus;

public class PushService extends IntentService {
  public final static int CANDLE = 1012;
  public final static int DEPTH = 1013;
  private static boolean isPush;
  private final int UPDATE = 0;
  private final int ADD = 1;
  private int updateCount = 20;//更新次数

  public PushService() {
    super("PushService");
  }

  @Override public void onCreate() {
    super.onCreate();
    isPush = true;
  }

  @Override protected void onHandleIntent(@Nullable Intent intent) {
    if (null == intent) {
      return;
    }
    int scale = intent.getIntExtra("scale", 4);
    double open = intent.getFloatExtra("open", 0);
    double high = intent.getFloatExtra("high", 0);
    double low = intent.getFloatExtra("low", 0);
    double close = intent.getFloatExtra("close", 0);
    double volume = intent.getFloatExtra("volume", 0);
    Date time = new Date(intent.getLongExtra("time", System.currentTimeMillis()));
    while (isPush) {
      try {
        int pushType;
        if (updateCount > 0) {
          updateCount--;
          pushType = UPDATE;
        } else {
          updateCount = 20;
          pushType = ADD;
        }
        CandleEntry pushData =
            candlePushDataOperation(scale, open, high, low, close, volume, time, pushType);
        open = pushData.getOpen().value;
        high = pushData.getHigh().value;
        low = pushData.getLow().value;
        close = pushData.getClose().value;
        volume = pushData.getVolume().value;
        time = pushData.getTime();
        ServiceMessage message = new ServiceMessage();
        message.setWhat(CANDLE);
        message.setEntry(pushData);
        EventBus.getDefault().post(message);
        Thread.sleep(3_000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 蜡烛图推送数据运算
   */
  private CandleEntry candlePushDataOperation(int scale, double open, double high, double low,
      double close, double volume, Date time, int pushType) {
    double closeUpdate = (Math.random() - 0.5) * 2;
    BigDecimal closeValue;
    BigDecimal openValue;
    BigDecimal highValue;
    BigDecimal lowValue;
    BigDecimal volumeValue;
    Date timeValue;
    if (pushType == UPDATE) {
      closeValue = new BigDecimal(close + closeUpdate);
      openValue = new BigDecimal(open);
      highValue = new BigDecimal(high);
      lowValue = new BigDecimal(low);

      highValue = closeValue.compareTo(highValue) > 0 ? closeValue : highValue;
      lowValue = closeValue.compareTo(lowValue) < 0 ? closeValue : lowValue;
      double volumeUpdate = volume + Math.random() * (volume > 1_000_000_000 ? 100_000 : 500_000);
      volumeValue = new BigDecimal(volumeUpdate);
      timeValue = time;
    } else {
      openValue = highValue = lowValue = closeValue = new BigDecimal(String.valueOf(close));
      volumeValue = BigDecimal.ZERO;
      timeValue = addDateMinut(time, 1);
    }

    return new CandleEntry(scale, openValue, highValue, lowValue, closeValue, volumeValue,
        timeValue);
  }

  private Date addDateMinut(Date date, int day) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DAY_OF_MONTH, day);
    date = cal.getTime();
    cal = null;
    return date;
  }

  public static void stopPush() {
    isPush = false;
  }
}
