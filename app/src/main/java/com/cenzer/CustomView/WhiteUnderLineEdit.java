package com.cenzer.CustomView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class WhiteUnderLineEdit extends android.support.v7.widget.AppCompatEditText {


    public WhiteUnderLineEdit(Context context) {
        super(context);
        setFont();

    }

    public WhiteUnderLineEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public void setFont()
    {
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"fonts/sf_pro_text_regular.otf");
        this.setTypeface(typeface);
    }
}
