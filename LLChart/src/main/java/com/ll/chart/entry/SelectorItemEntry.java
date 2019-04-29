package com.ll.chart.entry;

import android.text.TextPaint;

public class SelectorItemEntry {
  private String label;
  private TextPaint labelPaint;
  private String value;
  private TextPaint valuePaint;
  private String unit;
  private TextPaint unitPaint;

  public String getLabel() {
    return label;
  }

  public TextPaint getLabelPaint() {
    return labelPaint;
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

  public SelectorItemEntry setLabel(String label) {
    this.label = label;
    return this;
  }

  public SelectorItemEntry setLabelPaint(TextPaint labelPaint) {
    this.labelPaint = labelPaint;
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

