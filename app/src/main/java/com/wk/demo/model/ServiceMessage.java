package com.wk.demo.model;

import com.wk.chart.entry.AbsEntry;

public class ServiceMessage {
  private int what;
  private AbsEntry entry;

  public int getWhat() {
    return what;
  }

  public void setWhat(int what) {
    this.what = what;
  }

  public AbsEntry getEntry() {
    return entry;
  }

  public void setEntry(AbsEntry entry) {
    this.entry = entry;
  }
}
