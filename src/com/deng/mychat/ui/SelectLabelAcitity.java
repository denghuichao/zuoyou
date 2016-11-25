package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.List;

import tools.AppManager;
import tools.UIHelper;

import com.deng.mychat.R;
import com.deng.mychat.adapter.LabelAdapter;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.view.CustomProgressDialog;
import com.deng.mychat.view.MyGridView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class SelectLabelAcitity extends AppActivity implements OnItemClickListener{
	
	private MyGridView movieGridView;
	private MyGridView musicGridView;
	private MyGridView tvGridView;
	private MyGridView sportGridView;
	//private MyGridView foodGridView;
	//private MyGridView bookGridView;
	private CustomProgressDialog loadingPd;
	private List<String>labelList=new ArrayList<String>();
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_interest_layout);
		initUi();
	}
	
	public void initUi()
	{
		tvGridView=(MyGridView)findViewById(R.id.tv_gridview);
		String []interest_tr=this.getResources().getStringArray(R.array.interst_travel_label_list);
		List<String>labelList_tr=new ArrayList<String>();
		for(String s:interest_tr)labelList_tr.add(s);
		tvGridView.setAdapter(new LabelAdapter(this,labelList_tr));
		tvGridView.setFocusable(true);
		tvGridView.setFocusableInTouchMode(true);
		tvGridView.setOnItemClickListener(this);
		
		movieGridView=(MyGridView)findViewById(R.id.movie_gridview);
		String []interest_movie=this.getResources().getStringArray(R.array.interest_movie_label_list);
		List<String>labelList_movie=new ArrayList<String>();
		for(String s:interest_movie)labelList_movie.add(s);
		movieGridView.setAdapter(new LabelAdapter(this,labelList_movie));
		movieGridView.setOnItemClickListener(this);
		
		musicGridView=(MyGridView)findViewById(R.id.music_gridview);
		String []interest_music=this.getResources().getStringArray(R.array.interest_music_label_list);
		List<String>labelList_music=new ArrayList<String>();
		for(String s:interest_music)labelList_music.add(s);
		musicGridView.setAdapter(new LabelAdapter(this,labelList_music));
		musicGridView.setOnItemClickListener(this);
		
		//.............................
		sportGridView=(MyGridView)findViewById(R.id.sport_gridview);
		String []interest_sport=this.getResources().getStringArray(R.array.interest_sport_label_list);
		List<String>labelList_sport=new ArrayList<String>();
		for(String s:interest_sport)labelList_sport.add(s);
		sportGridView.setAdapter(new LabelAdapter(this,labelList_sport));
		sportGridView.setOnItemClickListener(this);
		
//		foodGridView=(MyGridView)findViewById(R.id.food_gridview);
//		String []interest_food=this.getResources().getStringArray(R.array.interest_food_label_list);
//		List<String>labelList_food=new ArrayList<String>();
//		for(String s:interest_food)labelList_food.add(s);
//		foodGridView.setAdapter(new LabelAdapter(this,labelList_food));
//		foodGridView.setOnItemClickListener(this);
//
//		bookGridView=(MyGridView)findViewById(R.id.book_gridview);
//		String []interest_book=this.getResources().getStringArray(R.array.interest_book_list);
//		List<String>labelList_book=new ArrayList<String>();
//		for(String s:interest_book)labelList_book.add(s);
//		bookGridView.setAdapter(new LabelAdapter(this,labelList_book));
//		bookGridView .setOnItemClickListener(this);
	}
	

	public void buttonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.finishButton:
			String result="";
			Intent intent = new Intent();  
			setUserLabel();
			//finish();
			break;
		case R.id.backButton:
			AppManager.getAppManager().finishActivity(SelectLabelAcitity.this);
			break;//用户取消
		}
	}
	
	private void setUserLabel() {
		// TODO Auto-generated method stub
		loadingPd = CustomProgressDialog.show(this, "正在保存..", false, null);
		ApiClient.setUserLabel(appContext.getLoginApiKey(),labelList,new ClientCallback(){

			@Override
			public void onSuccess(Object data) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast("保存个人标签成功");
				appContext.setLoginUserLabel(labelList);
				AppManager.getAppManager().finishActivity(SelectLabelAcitity.this);
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				loadingPd.dismiss();
				showToast("保存个人标签失败");
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				showToast("保存个人标签失败");
				loadingPd.dismiss();
			}

			@Override
			public void onRequestLogIn() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		TextView tv=(TextView)view;
		String label=tv.getText().toString();
		if(labelList.contains(label))
		{
			labelList.remove(label);
			tv.setCompoundDrawables(null, null, null, null); 
		}
		else 
		{
			if(labelList.size()>=6)
			{
				showToast("选择的标签数量已达到上限");
				return;
			}
			
			Drawable img_on;
			Resources res = getResources();
			img_on = res.getDrawable(R.drawable.ok);
			img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
			tv.setCompoundDrawables(img_on,null, null , null); //设置图标
			labelList.add(label);
		}
	}
}
