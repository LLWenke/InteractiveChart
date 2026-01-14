
package com.wk.demo.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.Utils;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.enumeration.MarkerPointType;
import com.wk.demo.MyApp;
import com.wk.demo.model.DepthWrapper;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * <p>股票测试数据</p>
 */

public class DataUtils {

    private static AsyncTask<Void, Void, Void> task;
    public static List<CandleEntry> candleEntries;
    public static List<DepthEntry> depthEntries;

    @SuppressLint("StaticFieldLeak")
    public static void loadData(LoadingListener listener) {
        task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (Utils.listIsEmpty(candleEntries)) {
                    candleEntries = DataUtils.getCandleData();
                }
                if (Utils.listIsEmpty(depthEntries)) {
                    depthEntries = DataUtils.getDepthData();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.loadComplete();
            }
        }.execute();
    }

    private static List<CandleEntry> getCandleData() {
        String kLineData;
        List<CandleEntry> dataList = new ArrayList<>();
        try {
            InputStream in = MyApp.context.getResources().getAssets().open("kline1.txt");
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            kLineData = new String(buffer, StandardCharsets.UTF_8);
            List<List<String>> lists = (new Gson()).fromJson(kLineData, new TypeToken<List<List<String>>>() {
            }.getType());
            for (List<String> list : lists) {
                String close = list.get(0);
                String high = list.get(1);
                String low = list.get(2);
                String open = list.get(3);
                long date = Long.parseLong(list.get(4));
                String volume = list.get(5);
                int type = (new Random()).nextInt(20);
                CandleEntry entry = new CandleEntry(open, high, low, close, volume, new Date(date));
                entry.setMarkerPointType(type > 3 ? MarkerPointType.NORMAL : type);
                dataList.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataList.sort(Comparator.comparing(AbsEntry::getTime));

        return dataList;
    }

    private static List<DepthEntry> getDepthData() {
        try {
            InputStream in = MyApp.context.getResources().getAssets().open("depth_data.json");
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            DepthWrapper data = gson.fromJson(json, DepthWrapper.class);

            data.getBids().sort((arg1, arg0) -> arg0.getPrice()
                    .compareTo(arg1.getPrice()));

            data.getAsks().sort(Comparator.comparing(DepthWrapper.Depth::getPrice));

            List<DepthEntry> depthData = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (int i = 0; i < data.getBids().size(); i++) {
                DepthWrapper.Depth item = data.getBids().get(i);
                totalAmount = totalAmount.add(item.getAmount());
                depthData.add(new DepthEntry(
                        item.getPrice().toPlainString(), item.getAmount().toPlainString(),
                        totalAmount.toPlainString(), DepthAdapter.BID, new Date()));
            }
            totalAmount = BigDecimal.ZERO;
            for (int i = 0; i < data.getAsks().size(); i++) {
                DepthWrapper.Depth item = data.getAsks().get(i);
                totalAmount = totalAmount.add(item.getAmount());
                depthData.add(new DepthEntry(
                        item.getPrice().toPlainString(), item.getAmount().toPlainString(),
                        totalAmount.toPlainString(), DepthAdapter.ASK, new Date()));
            }
            return depthData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void destroy() {
        if (null != task) {
            task.cancel(true);
        }
        if (null != candleEntries) {
            candleEntries.clear();
        }
        if (null != depthEntries) {
            depthEntries.clear();
        }
    }
}
