package com.kookyapps.gpstankertracking.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


import com.kookyapps.gpstankertracking.R;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

public class RobotoRegularTextView extends AppCompatTextView {
    private Context context;

    public RobotoRegularTextView(Context context) {
        super(context);
        this.context = context;
        Typeface tfs = ResourcesCompat.getFont(context,R.font.roboto);
        setTypeface(tfs);
    }

    public RobotoRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        Typeface tfs = ResourcesCompat.getFont(context,R.font.roboto);
        setTypeface(tfs);
    }

    public RobotoRegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        Typeface tfs = ResourcesCompat.getFont(context,R.font.roboto_italic);
        setTypeface(tfs);
    }
}
