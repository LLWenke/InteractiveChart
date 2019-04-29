package com.ll.chart.compat;

import java.util.Observable;

public class DataSetObservable extends Observable {
  @Override public void notifyObservers(Object arg) {
    setChanged();
    super.notifyObservers(arg);
  }

  @Override public void notifyObservers() {
    setChanged();
    super.notifyObservers();
  }
}
