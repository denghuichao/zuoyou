package com.deng.mychat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

public class MyGallery extends Gallery{

	public MyGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	 public MyGallery(Context context, AttributeSet attrs)     //Constructor that is called when inflating a view from XML
	 {
		 super(context, attrs);
	 }
	public MyGallery(Context context, AttributeSet attrs, int defStyle)     //Perform inflation from XML and apply a class-specific base style
	{
		super(context, attrs,defStyle);
	}
	@Override    
    public boolean dispatchTouchEvent(MotionEvent ev) {   
        getParent().requestDisallowInterceptTouchEvent(true);  
        return super.dispatchTouchEvent(ev);    
    }  
}
