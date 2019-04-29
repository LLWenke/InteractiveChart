package com.ll.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.ll.chart.enumeration.DataDisplayType;
import com.ll.demo.R;
import com.ll.demo.util.DataUtils;
import com.ll.demo.util.LoadingListener;

/**
 * <p>MainActivity</p>
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
    LoadingListener {
  private LinearLayout loadingTips;
  private Button historicalDataBtn;
  private Button realTimeDataBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    this.loadingTips = findViewById(R.id.loading_tips);
    this.realTimeDataBtn = findViewById(R.id.btn_real_time_data);
    this.historicalDataBtn = findViewById(R.id.btn_historical_data);

    this.realTimeDataBtn.setOnClickListener(this);
    this.historicalDataBtn.setOnClickListener(this);

    this.loadingTips.setVisibility(View.VISIBLE);
    this.realTimeDataBtn.setEnabled(false);
    this.historicalDataBtn.setEnabled(false);
    DataUtils.loadData(4, 2, this);
  }

  @Override
  public void onClick(View v) {
    Intent intent = new Intent(this, ChartActivity.class);
    switch (v.getId()) {
      case R.id.btn_real_time_data:
        intent.putExtra(ChartActivity.DATA_SHOW_KEY, DataDisplayType.REAL_TIME.ordinal());
        break;
      case R.id.btn_historical_data:
        intent.putExtra(ChartActivity.DATA_SHOW_KEY, DataDisplayType.PAGING.ordinal());
        break;
    }
    startActivity(intent);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    DataUtils.destroy();
  }

  @Override public void loadComplete() {
    this.loadingTips.setVisibility(View.INVISIBLE);
    this.realTimeDataBtn.setEnabled(true);
    this.historicalDataBtn.setEnabled(true);
  }
}
