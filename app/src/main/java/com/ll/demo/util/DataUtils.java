
package com.ll.demo.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.ll.chart.adapter.DepthAdapter;
import com.ll.chart.compat.Utils;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.entry.DepthEntry;
import com.ll.demo.MyApp;
import com.ll.demo.model.DepthWrapper;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>股票测试数据</p>
 */

public class DataUtils {

  private static AsyncTask<Void, Void, Void> task;
  public static List<CandleEntry> candleEntries;
  public static List<DepthEntry> depthEntries;

  @SuppressLint("StaticFieldLeak")
  public static void loadData(int scale, int quoteScale, LoadingListener listener) {
    task = new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        if (Utils.listIsEmpty(candleEntries)) {
          candleEntries = DataUtils.getCandelData(scale);
        }
        if (Utils.listIsEmpty(depthEntries)) {
          depthEntries = DataUtils.getDepthData(scale,
              quoteScale);
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        listener.loadComplete();
      }
    }.execute();
  }

  private static List<CandleEntry> getCandelData(int scale) {
    String kLineData;
    List<CandleEntry> dataList = new ArrayList<>();
    try {
      InputStream in = MyApp.context.getResources().getAssets().open("kline1.txt");
      int length = in.available();
      byte[] buffer = new byte[length];
      in.read(buffer);
      kLineData = new String(buffer, "UTF-8");
      final String[] candleDatas = kLineData.split(",");

      for (String candleData : candleDatas) {
        String[] v = candleData.split("[|]");

        double open = Double.parseDouble(v[0]);
        double high = Double.parseDouble(v[1]);
        double low = Double.parseDouble(v[2]);
        double close = Double.parseDouble(v[3]);
        double volume = Double.parseDouble(v[4]);

        dataList.add(
            new CandleEntry(scale, open, high, low, close, volume, new Date(Date.parse(v[5]))));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    Collections.sort(dataList, (arg0, arg1) -> arg0.getTime().compareTo(arg1.getTime()));

    return dataList;
  }

  private static List<DepthEntry> getDepthData(int scale, int quoteScale) {
    try {
      InputStream in = MyApp.context.getResources().getAssets().open("depth_data.json");
      int length = in.available();
      byte[] buffer = new byte[length];
      in.read(buffer);
      String json = new String(buffer, 0, buffer.length, "UTF-8");
      Gson gson = new Gson();
      DepthWrapper data = gson.fromJson(json, DepthWrapper.class);

      Collections.sort(data.getBids(), (arg0, arg1) -> arg1.getPrice()
          .compareTo(arg0.getPrice()));

      Collections.sort(data.getAsks(), (arg0, arg1) -> arg0.getPrice()
          .compareTo(arg1.getPrice()));

      List<DepthEntry> depthData = new ArrayList<>();
      BigDecimal totalAmount = BigDecimal.ZERO;
      for (int i = 0; i < data.getBids().size(); i++) {
        DepthWrapper.Depth item = data.getBids().get(i);
        totalAmount = totalAmount.add(item.getAmount());
        depthData.add(new DepthEntry(null, scale, quoteScale,
            item.getPrice(), item.getAmount(), totalAmount, DepthAdapter.BID));
      }
      totalAmount = BigDecimal.ZERO;
      for (int i = 0; i < data.getAsks().size(); i++) {
        DepthWrapper.Depth item = data.getAsks().get(i);
        totalAmount = totalAmount.add(item.getAmount());
        depthData.add(new DepthEntry(null, scale, quoteScale,
            item.getPrice(), item.getAmount(), totalAmount, DepthAdapter.ASK));
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
