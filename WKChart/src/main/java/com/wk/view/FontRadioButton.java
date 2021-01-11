package com.wk.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.wk.chart.compat.FontStyle;


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
            this.setTypeface(FontStyle.boldTypeFace);
        } else {
            this.setTypeface(FontStyle.typeFace);
        }
        this.postInvalidate();
    }

    @Override
    public void toggle() {
    }
}
