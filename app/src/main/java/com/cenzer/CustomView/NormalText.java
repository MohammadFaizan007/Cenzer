package com.cenzer.CustomView;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class NormalText extends TextView {
    Context context;
    public NormalText(Context context) {
        super(context);
        this.context=context;
        setFont();
    }

    public NormalText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        setFont();
    }

    public void setFont()
    {
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"fonts/sf_pro_text_regular.otf");
        this.setTypeface(typeface);
    }
}
