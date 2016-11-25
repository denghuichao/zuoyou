package com.deng.mychat.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MyClickableSpan extends ClickableSpan {
    private boolean mIsPressed;
    private int mPressedBackgroundColor;
    private int mNormalTextColor;
    private int mPressedTextColor;
    
    public  interface OnClickCallback{
    	public void onClick();
    }

    public void setOnClickCallback(OnClickCallback c)
    {
    	this.callback=c;
    }
    
    private OnClickCallback callback;
    
    public MyClickableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;
    }

    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
        ds.bgColor = mIsPressed ? mPressedBackgroundColor : 0x00eeeeee;
        ds.setUnderlineText(false);
    }

	@Override
	public void onClick(View widget) {
		//todo
		if(callback!=null)
			callback.onClick();
	}
}
