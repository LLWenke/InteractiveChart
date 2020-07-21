package com.wk.demo.view;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.wk.chart.compat.FontStyle;

/**
 * 自定义字体库的CheckBox
 */
public class FontCheckBox extends AppCompatCheckBox {

  public FontCheckBox(Context context) {
    super(context);
    initFont();
  }

  public FontCheckBox(Context context, AttributeSet attrs) {
    super(context, attrs);
    initFont();
  }

  public FontCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initFont();
  }

  public void initFont() {
    if (null != getTypeface() && getTypeface().isBold()) {
      this.setTypeface(FontStyle.boldTypeFace);
    } else {
      this.setTypeface(FontStyle.typeFace);
    }
    this.postInvalidate();
  }
}
