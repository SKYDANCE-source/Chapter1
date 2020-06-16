package com.bytedance.component.vedioplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.VideoView;

public class videoView extends VideoView {

    private int ScreenWidth = 1920;
    private int ScreenHeight = 1080;

    public videoView(Context context) {
        super(context);
    }

    public videoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public videoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int Width, int Height) {
        super.onMeasure(Width, Height);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //最大限度的展示宽和高
            int width = getDefaultSize(ScreenWidth, Width);
            int height = getDefaultSize(ScreenHeight, Height);

            setMeasuredDimension(width, height);
        }
    }
}