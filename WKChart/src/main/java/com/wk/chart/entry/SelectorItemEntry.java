package com.wk.chart.entry;

import android.text.TextPaint;

public class SelectorItemEntry {
  private String lable;
  private TextPaint lablePaint;
  private String value;
  private TextPaint valuePaint;
  private String unit;
  private TextPaint unitPaint;

  public String getLable() {
    return lable;
  }

  public TextPaint getLablePaint() {
    return lablePaint;
  }

  public String getValue() {
    return value;
  }

  public TextPaint getValuePaint() {
    return valuePaint;
  }

  public String getUnit() {
    return unit;
  }

  public TextPaint getUnitPaint() {
    return unitPaint;
  }

  public SelectorItemEntry setLable(String lable) {
    this.lable = lable;
    return this;
  }

  public SelectorItemEntry setLablePaint(TextPaint lablePaint) {
    this.lablePaint = lablePaint;
    return this;
  }

  public SelectorItemEntry setValue(String value) {
    this.value = value;
    return this;
  }

  public SelectorItemEntry setValuePaint(TextPaint valuePaint) {
    this.valuePaint = valuePaint;
    return this;
  }

  public SelectorItemEntry setUnit(String unit) {
    this.unit = unit;
    return this;
  }

  public SelectorItemEntry setUnitPaint(TextPaint unitPaint) {
    this.unitPaint = unitPaint;
    return this;
  }
}

