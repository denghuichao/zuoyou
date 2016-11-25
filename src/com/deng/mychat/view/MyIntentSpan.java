package com.deng.mychat.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MyIntentSpan extends MyClickableSpan{

	
	private Intent mIntent;  

	public MyIntentSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor,Intent intent) {

		super(normalTextColor, pressedTextColor, pressedBackgroundColor);
	
		mIntent=intent;

	}

	@Override
	public void onClick(View widget) {
		 Context context = widget.getContext();  
	     context.startActivity( mIntent );  
	}

}
