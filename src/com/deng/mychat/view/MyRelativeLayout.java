package com.deng.mychat.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.widget.RelativeLayout;

public class MyRelativeLayout extends RelativeLayout {
    private int width;
    protected OnSizeChangedListenner onSizeChangedListenner;
    private boolean sizeChanged  = false; //�仯�ı�־
    private int height;
    private int screenWidth; //��Ļ���
    private int screenHeight; //��Ļ�߶�

    public MyRelativeLayout(Context paramContext,
                    AttributeSet paramAttributeSet) {
            super(paramContext, paramAttributeSet);
            Display localDisplay =( (Activity)paramContext).getWindowManager()
                            .getDefaultDisplay();
            this.screenWidth = localDisplay.getWidth() ;
            this.screenHeight = localDisplay.getHeight();
    }

    public MyRelativeLayout(Context paramContext,
                    AttributeSet paramAttributeSet, int paramInt) {
            super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.width = widthMeasureSpec;
            this.height = heightMeasureSpec;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    public void onSizeChanged(int w, int h, int oldw,
                    int oldh) {
            //������Ϊ�ա���Ȳ��䡢��ǰ�߶�����ʷ�߶Ȳ�Ϊ0 
            if ((this.onSizeChangedListenner!= null) && (w == oldw) && (oldw != 0)
                            && (oldh != 0)) {
                    if ((h >= oldh)
                                    || (Math.abs(h - oldh) <= 1 * this.screenHeight / 4)) {
                            if ((h <= oldh)
                                            || (Math.abs(h - oldh) <= 1 * this.screenHeight / 4))
                                    return;
                            this.sizeChanged  = false;
                    } else {
                            this.sizeChanged  = true;
                    }
                    this.onSizeChangedListenner.onSizeChange(this.sizeChanged ,oldh, h);
                    measure(this.width - w + getWidth(), this.height
                                    - h + getHeight());
            }
    }
    /**
     * ���ü����¼�
     * @param paramonSizeChangedListenner
     */
    public void setOnSizeChangedListenner(
                    MyRelativeLayout.OnSizeChangedListenner paramonSizeChangedListenner) {
            this.onSizeChangedListenner = paramonSizeChangedListenner;
    }
    /**
     * ��С�ı���ڲ��ӿ�
     * @author junjun
     *
     */
    public abstract interface OnSizeChangedListenner {
            public abstract void onSizeChange(boolean paramBoolean, int w,int h);
    }
}

