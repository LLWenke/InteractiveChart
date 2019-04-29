package com.ll.demo;

import android.app.Application;
import android.content.Context;
import com.ll.demo.util.DataUtils;

public class MyApp extends Application {
  public static Context context;

  @Override public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
  }

  @Override public void onLowMemory() {
    super.onLowMemory();
    DataUtils.destroy();
  }
}
