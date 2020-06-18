package com.cenzer.CustomView;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class LogoText extends TextView {
    Context context;
    public LogoText(Context context) {
        super(context);
        this.context=context;
        setFont();
    }

    public LogoText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        setFont();
    }

    public void setFont()
    {
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"fonts/hamerslag.ttf");
        this.setTypeface(typeface);
    }
}
