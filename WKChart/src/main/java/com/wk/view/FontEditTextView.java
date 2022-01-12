package com.wk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import com.wk.chart.compat.FontStyle;

/**
 * 自定义字体库的EditTextView
 */
public class FontEditTextView extends AppCompatEditText {
    private OnTextChangedListener listener;

    public FontEditTextView(Context context) {
        super(context);
        initFont();
    }

    public FontEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public FontEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFont();
    }

    public void setListener(OnTextChangedListener listener) {
        this.listener = listener;
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
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (null != listener) {
            listener.onTextChanged(text);
        }
    }

    public interface OnTextChangedListener {
        void onTextChanged(CharSequence text);
    }
}
