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
        double open = intent.getDoubleExtra("open", 0d);
        double high = intent.getDoubleExtra("high", 0d);
        double low = intent.getDoubleExtra("low", 0d);
        double close = intent.getDoubleExtra("close", 0d);
        double volume = intent.getDoubleExtra("volume", 0d);
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
        double closeUpdate = (Math.random() - 0.5) * 100;
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
            volumeValue = volume + Math.random() * (volume > 1_000_000_000 ? 100_00 : 500_00);
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
