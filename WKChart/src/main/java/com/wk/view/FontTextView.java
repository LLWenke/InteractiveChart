package com.wk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.wk.chart.compat.FontStyle;


/**
 * 自定义字体库的TextView
 */
public class FontTextView extends AppCompatTextView {

    public FontTextView(Context context) {
        super(context);
        initFont();
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
