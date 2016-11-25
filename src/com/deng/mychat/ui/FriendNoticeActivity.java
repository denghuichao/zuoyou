package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.List;

import tools.UIHelper;

import com.deng.mychat.R;
import com.deng.mychat.adapter.FriendNoticeAdapter;
import com.deng.mychat.bean.FriendNotice;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.NewsEntity;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.FriendNoticeManager;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.ApiClient.ClientCallback;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ListView;

public class FriendNoticeActivity extends AppActivity{

	private ListView listView;
	private List<FriendNotice>noticeList;//=new ArrayList<FriendNotice>();
	private FriendNoticeAdapter mAdapter;
	
	private ImageView indicatorImageView;
	private Animation indicatorAnimation;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_comfirm);	
		initUi();
	}
	
	public void initUi()
	{
		listView =(ListView)findViewById(R.id.listView);
		noticeList=new ArrayList<FriendNotice>();
		mAdapter=new FriendNoticeAdapter(context, appContext, noticeList);
		listView.setAdapter(mAdapter);
		
		indicatorImageView = (ImageView) findViewById(R.id.xindicator);
		indicatorAnimation = AnimationUtils.loadAnimation(this, R.anim.refresh_button_rotation);
		indicatorAnimation.setDuration(500);
		indicatorAnimation.setInterpolator(new Interpolator() {
				    private final int frameCount = 10;
				    @Override
				    public float getInterpolation(float input) {
				        return (float)Math.floor(input*frameCount)/frameCount;
				    }
		});
		
		new NoticePageTask().execute();
		//indicatorImageView.setVisibility(View.INVISIBLE);
		//indicatorImageView.clearAnimation();
	}
	
   public class NoticePageTask extends AsyncTask<Object, Integer, List<FriendNotice>> {

	
	 public List<FriendNotice> doInBackground(Object... params) {
			return FriendNoticeManager.getInstance(context).getNoticeList();
		}

		@Override
	  public  void onCancelled() {
			super.onCancelled();
	   }

		@Override
		public void onPreExecute()
		{
			 indicatorImageView.startAnimation(indicatorAnimation);
			 indicatorImageView.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onPostExecute(final List<FriendNotice> result) {
			
			noticeList.clear();
			noticeList.addAll(result);
			mAdapter.notifyDataSetChanged();
			indicatorImageView.setVisibility(View.INVISIBLE);
			indicatorImageView.clearAnimation();
		}
	}
}
