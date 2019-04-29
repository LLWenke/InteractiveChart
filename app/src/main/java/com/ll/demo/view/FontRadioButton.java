package com.ll.demo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import com.ll.chart.compat.FontConfig;

/**
 * 自定义字体库的RadioButton
 */
public class FontRadioButton extends AppCompatRadioButton {

  public FontRadioButton(Context context) {
    super(context);
    initFont();
  }

  public FontRadioButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    initFont();
  }

  public FontRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initFont();
  }

  public void initFont() {
    if (null != getTypeface() && getTypeface().isBold()) {
      this.setTypeface(FontConfig.boldTypeFace);
    } else {
      this.setTypeface(FontConfig.typeFace);
    }
    this.postInvalidate();
  }
}
