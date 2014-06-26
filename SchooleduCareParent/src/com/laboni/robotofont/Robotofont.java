package com.laboni.robotofont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class Robotofont extends TextView {

    public Robotofont(Context context) {
        super(context);
        style(context);
    }

    public Robotofont(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context);
    }

    public Robotofont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        style(context);
    }

    private void style(Context context) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "Roboto-Light.ttf");
        setTypeface(tf);
    }

}