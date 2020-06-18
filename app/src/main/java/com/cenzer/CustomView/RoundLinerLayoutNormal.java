package com.cenzer.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.cenzer.Constant.Constants;
import com.cenzer.R;

public class RoundLinerLayoutNormal extends LinearLayout {
    Paint paint;


    public RoundLinerLayoutNormal(Context context) {
        super(context);


        initBackground();
    }

    public RoundLinerLayoutNormal(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBackground();
    }

    public RoundLinerLayoutNormal(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBackground();
    }

    private void initBackground()
    {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShadowLayer(12, 0, 0, getResources().getColor(R.color.colorPrimary));
        paint.setColor(getResources().getColor(R.color.colorPrimary));
        // Important for certain APIs
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
        setBackground(Constants.generateBackgroundWithShadow(this,R.color.colorPrimary,
                R.dimen.radius_corner,R.color.colorPrimaryLite,R.dimen.elevation, Gravity.BOTTOM));
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawRect(10, 10, 300, 200, paint);
    }
}

