package com.cenzer.CustomView;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class HeaderText extends TextView {
    Context context;
    public HeaderText(Context context) {
        super(context);
        this.context=context;
        setFont();
    }

    public HeaderText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        setFont();
    }

    public void setFont()
    {
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"fonts/sf_pro_text_medium.otf");
        this.setTypeface(typeface);
    }
}
