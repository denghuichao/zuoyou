package com.deng.mychat.image_viewer;

import com.deng.mychat.R;
import com.deng.mychat.tools.FileUtils;
import com.deng.mychat.tools.HttpDataGetter;
import com.deng.mychat.tools.NetworkTool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ImagePagerActivity extends FragmentActivity {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";

	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	private String[] urls;
	private boolean isDownloading =false;
	
	private  Handler myHandler=new Handler(){
    	@Override
    	public void handleMessage(Message m)
    	{
    		if(m.what==1)
    			showToast("±£´æÍ¼Æ¬³É¹¦");
    		else showToast("±£´æÍ¼Æ¬Ê§°Ü");
    	}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);

		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);


		mPager = (HackyViewPager) findViewById(R.id.pager);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(
				getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
				.getAdapter().getCount());
		indicator.setText(text);
		// æ›´æ–°ä¸‹æ ‡
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				CharSequence text = getString(R.string.viewpager_indicator,
						arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
				pagerPosition=arg0;
			}

		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	public void btnClick(View v)
	{
		switch(v.getId())
		{
		 case R.id.download: downloadImage();break;
		}
	}
	
	private void downloadImage() {
		if(!isDownloading)
		{
			isDownloading=true;
			new Thread(downLoadImage).start();
		}
	}

	public void showToast(String text)
	{
		Toast toast=Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 0);	
		toast.show();
	}
	
	private Runnable downLoadImage = new Runnable(){  
        @Override  
        public void run() {  
            try {  
                String filePath = urls[pagerPosition];  
                String mFileName = filePath.substring(filePath.lastIndexOf("/"));  
                Bitmap mBitmap = BitmapFactory.decodeStream(HttpDataGetter.getImageStream(filePath));  
                FileUtils.saveFile(mBitmap, mFileName); 
                myHandler.sendEmptyMessage(1);
            } catch (Exception e) {  
                e.printStackTrace();  
                myHandler.sendEmptyMessage(2);
                
            }  
  
        }  
  
    };  
	
	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public String[] fileList;

		public ImagePagerAdapter(FragmentManager fm, String[] fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.length;
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList[position];
			return ImageDetailFragment.newInstance(url);
		}
	}
}