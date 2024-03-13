package com.wk.demo.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.wk.chart.entry.CandleEntry;
import com.wk.demo.model.ServiceMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

public class PushService extends IntentService {
    public final static int CANDLE = 1012;
    public final static int DEPTH = 1013;
    private static boolean isPush;
    private final int UPDATE = 0;
    private final int ADD = 1;
    private int updateCount = 50;//更新次数

    public PushService() {
        super("PushService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isPush = true;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (null == intent) {
            return;
        }
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
                    updateCount = 50;
                    pushType = ADD;
                }
                CandleEntry pushData = candlePushDataOperation(open, high, low, close, volume, time, pushType);
                open = Double.parseDouble(pushData.getOpen().source);
                high = Double.parseDouble(pushData.getHigh().source);
                low = Double.parseDouble(pushData.getLow().source);
                close = Double.parseDouble(pushData.getClose().source);
                volume = Double.parseDouble(pushData.getVolume().source);
                time = pushData.getTime();
                ServiceMessage message = new ServiceMessage();
                message.setWhat(CANDLE);
                message.setEntry(pushData);
                EventBus.getDefault().post(message);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 蜡烛图推送数据运算
     */
    private CandleEntry candlePushDataOperation(double open, double high, double low,
                                                double close, double volume, Date time, int pushType) {
        double closeUpdate = (Math.random() - 0.5) * 10;
        double closeValue;
        double openValue;
        double highValue;
        double lowValue;
        double volumeValue;
        Date timeValue;
        if (pushType == UPDATE) {
            closeValue = close + closeUpdate;
            openValue = open;
            highValue = high;
            lowValue = low;

            highValue = Math.max(closeValue, highValue);
            lowValue = Math.min(closeValue, lowValue);
            volumeValue = volume + Math.random() * (volume > 1_000_000_000 ? 100_000 : 500_000);
            timeValue = time;
        } else {
            openValue = highValue = lowValue = closeValue = close;
            volumeValue = 0.0;
            timeValue = addDateMinute(time, 1);
        }

        return new CandleEntry(String.valueOf(openValue), String.valueOf(highValue), String.valueOf(lowValue),
                String.valueOf(closeValue), String.valueOf(volumeValue), timeValue);
    }

    private Date addDateMinute(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, day);
        date = cal.getTime();
        return date;
    }

    public static void stopPush() {
        isPush = false;
    }
}
