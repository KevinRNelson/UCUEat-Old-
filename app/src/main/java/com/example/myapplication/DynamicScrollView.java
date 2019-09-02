package com.example.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class DynamicScrollView extends ScrollView{

    private boolean enableScrolling = true;

    public DynamicScrollView(Context context) {
        super(context);
    }


    public DynamicScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public DynamicScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DynamicScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (scrollingEnabled()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onStartNestedScroll (View child, View target, int nestedScrollAxes){
        return true;
    }


    private boolean scrollingEnabled(){
        return enableScrolling;
    }


    public void setScrolling(boolean enableScrolling) {
        this.enableScrolling = enableScrolling;
    }
}
