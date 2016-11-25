package com.deng.mychat.ui;

import com.deng.mychat.R;
import com.deng.mychat.config.AppActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class GuideActivity extends AppActivity  implements  ViewFactory {
	
	private LinearLayout mLinearLayout;
	private ImageSwitcher mSwitcher;
	
	private ImageView[] mImageViewDots ;
	
	private int mIndex = 0 ;
	
	private int mImageRes[] = new int[]{
			R.drawable.yd1,
			R.drawable.yd2,
			R.drawable.yd3,
			R.drawable.yd4
	};

	 private GestureDetector mGestureDetector;
	 private static final int SWIPE_MIN_DISTANCE = 6;
	 private static final int SWIPE_THRESHOLD_VELOCITY = 50;
	 
	 private final int CHANGE_VIEW_IMG_LEFT=1;
	 private final int CHANGE_VIEW_IMG_RIGHT=2;
	 
	 private Handler myHandler=new Handler()
	 {
		 public void handleMessage(Message msg)
		 {
			 if(msg.what==CHANGE_VIEW_IMG_RIGHT)
			 {
				 mSwitcher.setInAnimation(GuideActivity.this, R.anim.left_in) ;
				 mSwitcher.setOutAnimation(GuideActivity.this, R.anim.right_out) ;
				 mSwitcher.setBackgroundResource(mImageRes[mIndex%mImageRes.length]);
			 }
			 else if(msg.what==CHANGE_VIEW_IMG_LEFT)
			 {
				 mSwitcher.setInAnimation(GuideActivity.this, R.anim.right_in) ;
				 mSwitcher.setOutAnimation(GuideActivity.this, R.anim.left_out) ;
				 mSwitcher.setBackgroundResource(mImageRes[mIndex%mImageRes.length]);//setImageResource(mImageRes[mIndex%mImageRes.length]); 
			 }
			 if(msg.what==CHANGE_VIEW_IMG_RIGHT || msg.what==CHANGE_VIEW_IMG_LEFT)
			 {
				 for(int i=0 ;i<mImageViewDots.length ;i++){
					if(i == mIndex%mImageRes.length){
							mImageViewDots[i].setBackgroundResource(R.drawable.dot_foucs);
					}else{
						mImageViewDots[i].setBackgroundResource(R.drawable.dot_normal);
					}
					}
			 }
		 }
	 };
	 
	 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_use_direct);
		mLinearLayout = (LinearLayout)findViewById(R.id.dots);
		mSwitcher = (ImageSwitcher)findViewById(R.id.switcher);
		initDots();
		mSwitcher.setFactory(this);
		mSwitcher.setBackgroundResource(mImageRes[0]);
		mImageViewDots[0].setBackgroundResource(R.drawable.dot_foucs);

		mGestureDetector=new GestureDetector(myOnGestureListener);
		mGestureDetector.setIsLongpressEnabled(true);
	}
	
	public void initDots(){
		mLinearLayout.removeAllViews() ;
		mImageViewDots = new ImageView[mImageRes.length] ;
		for(int i=0 ;i<mImageRes.length ;i++){
			ImageView dot = new ImageView(this);
			dot.setBackgroundResource(R.drawable.dot_normal);
			dot.setLayoutParams(new LayoutParams(30,30)) ;
			TextView tv = new TextView(this);
			tv.setLayoutParams(new LayoutParams(30,30));
			mImageViewDots[i] = dot ;
			mLinearLayout.addView(dot);
			mLinearLayout.addView(tv);
		}
	}


	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		return new ImageView(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return mGestureDetector.onTouchEvent(event);
	}
	
	public void redirectTo()
	{
		if(!appContext.isLogin()){
			
			Intent intent = new Intent(this,Login.class);
			startActivity(intent);
			finish();	
		}
		else {
			startService();
			Intent intent = new Intent(this, Tabbar.class);
	        startActivity(intent);
	        finish();
		}
	}
	
	private  SimpleOnGestureListener myOnGestureListener=new SimpleOnGestureListener(){    
		@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {   
			
			 if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	            	Log.d("Touch", "Fling Right");
	            	if(mIndex == 0){
	    				mIndex = 0 ;
	    			}
	            	else{
	    				--mIndex ;
	    				myHandler.sendEmptyMessage(CHANGE_VIEW_IMG_RIGHT);
	    			}
				} 
	            else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	            	Log.d("Touch", "Fling Left");
	            	if(mIndex == mImageRes.length - 1){
	    				redirectTo();
	    			}else{
	    				++mIndex ;
	    				myHandler.sendEmptyMessage(CHANGE_VIEW_IMG_LEFT);
	    			}
	        }
			return true;  
		}        
    } ; 
	
}

