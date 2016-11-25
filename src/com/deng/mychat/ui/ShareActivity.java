package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import tools.StringUtils;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.adapter.ImageAdapter;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.pic.Bimp;
import com.deng.mychat.pic.FileUtils;
import com.deng.mychat.tools.HttpDataGetter;
import com.deng.mychat.tools.RegexTool;
import com.deng.mychat.view.CustomProgressDialog;
import com.umeng.analytics.MobclickAgent;

public class ShareActivity extends AppActivity{
	
	private EditText editText;
	private GridView manyImgsGrid;
	private RelativeLayout urlDescL;
	
	private ImageView ulrIcon;
	private TextView ulrDesc;
	private ImageAdapter imgAdapter;
	private List<Picture>imgUrls;
	private String link="";
	private String descImg="";
	private String descText="";
	private List<String>picPaths=new ArrayList<String>();
	
	private CustomProgressDialog loadingPd;
	
	protected void onCreate (Bundle savedInstanceState) {  
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_from_other_app_layout);
		initUI();
	    // Get intent, action and MIME type  
		processIntent();
	}  
	  
	private void initUI() {
		// TODO Auto-generated method stub
		editText=(EditText)findViewById(R.id.contentTextEt);
		//singleImage=(ImageView)findViewById(R.id.singleImage);
		manyImgsGrid=(GridView)findViewById(R.id.manyImgsGrid);
		urlDescL=(RelativeLayout)findViewById(R.id.urlDescL);
		ulrIcon=(ImageView)findViewById(R.id.ulrIcon);
		ulrDesc=(TextView)findViewById(R.id.ulrDesc);
		imgUrls=new ArrayList<Picture>();
		imgAdapter= new ImageAdapter(context,imgUrls,CommonValue.DisplayOptions.default_options,4);
		manyImgsGrid.setAdapter(imgAdapter);
	}

	private void processIntent()
	{
		    Intent intent = getIntent();  
		    String action = intent.getAction();  
		    String type = intent.getType();  
		  
		    if (Intent.ACTION_SEND.equals(action) && type != null) {  
		        if ("text/plain".equals(type)) {  
		        	urlDescL.setVisibility(View.VISIBLE);
		            handleSendText(intent); // Handle text being sent  
		        } else if (type.startsWith("image/")) {  
		        	manyImgsGrid.setVisibility(View.VISIBLE);
		            handleSendImage(intent); // Handle single image being sent  
		        }  
		    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {  
		        if (type.startsWith("image/")) {  
		        	manyImgsGrid.setVisibility(View.VISIBLE);
		            handleSendMultipleImages(intent); // Handle multiple images being sent  
		        }  
		    } else {  
		    }  
	}
	
	void handleSendText(Intent intent) {  
	    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);  
	    if (sharedText != null) {  
	    	List<String> urls=RegexTool.findUrls(sharedText);
	    	
	    	if(urls.size()>0){
	    		link= urls.get(0);
	    		new HttpDataTask( urls.get(0)).execute();
	    		}
	    }  
	    
	}  
	
	
	  
	void handleSendImage(Intent intent) {  
	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);  
	    if (imageUri != null) {  
	        // Update UI to reflect image being shared 
	    	String[] proj = { MediaStore.Images.Media.DATA };
	    	Cursor actualimagecursor = managedQuery(imageUri,proj,null,null,null);
	    	int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    	actualimagecursor.moveToFirst();
	    	String img_path = actualimagecursor.getString(actual_image_column_index);
	    	
	    	imgUrls.add(new Picture(img_path,img_path));
	    	picPaths.add(img_path);
	    	imgAdapter.notifyDataSetChanged();
	    	actualimagecursor.close();
	    }  
	}  
	  
	void handleSendMultipleImages(Intent intent) {  
	    ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);  
	    if (imageUris != null) {  
	        // Update UI to reflect multiple images being shared 
	    	for(Uri u:imageUris)
	    	{
	    		String[] proj = { MediaStore.Images.Media.DATA };
		    	Cursor actualimagecursor = managedQuery(u,proj,null,null,null);
		    	int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    	actualimagecursor.moveToFirst();
		    	String img_path = actualimagecursor.getString(actual_image_column_index);
		    	imgUrls.add(new Picture(img_path,img_path));
		    	picPaths.add(img_path);
	    		imgAdapter.notifyDataSetChanged();
	    		actualimagecursor.close();
	    	}
	    }  
	} 
	
	
	private class HttpDataTask extends AsyncTask<Object, Integer, HashMap<String,Object>> {
	    
	private String url;
		
	public	HttpDataTask(String url)
	{
			this.url=url;
	}
	
	@Override
	public void onPreExecute()
	{
		loadingPd = CustomProgressDialog.show(ShareActivity.this, "", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
	}
	
	@Override
	protected HashMap<String, Object> doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		String data=HttpDataGetter.getHttpData(url);
		String title=HttpDataGetter.getTitle(data);
		List<String>imgs=HttpDataGetter.getImgUrls(data);
		HashMap hash=new HashMap<String, Object>();
		hash.put("title", title);
		hash.put("imgs", imgs);
		return hash;
	}
	
	@Override
	protected void onPostExecute(HashMap<String, Object> hash)
	{
		String title=(String) hash.get("title");
		ulrDesc.setText(title);
		descText=title;
		List<String>imgs=(List<String>) hash.get("imgs");
		if(imgs==null||imgs.size()==0)ulrIcon.setVisibility(View.GONE);
		else 
		{
			int index=imgs.size()/2;
			descImg=imgs.get(index);
			ulrIcon.setVisibility(View.VISIBLE);
			imageLoader.displayImage(imgs.get(index), ulrIcon, CommonValue.DisplayOptions.default_options);
		}
		loadingPd.dismiss();
	}
  }
	
	
	public void buttonClick(View v)
	{
		switch(v.getId())
		{
		case R.id.cancel:finish();
		case R.id.send:publishNews(editText.getText().toString(),picPaths,link, descImg,descText);
		}
		
	}

	public void publishNews(String contentText,List<String> files,final String link,final String descImg,final String descText)
	{
		
		if(!appContext.isLogin())
		{
			showToast("你还没有登录，请先登录");
			return;
		}
		
		if(StringUtils.empty(contentText.trim()) && (files==null || files.size()==0) && StringUtils.empty(link))
		{
			showToast("请输入文字或者选择照片");
			return;
		}
		
		String apiKey = appContext.getLoginApiKey();
		
		LocationPoint location=null;
		location=appContext.getLoginInfo().userInfo.location;
		
		if(location==null)location=new LocationPoint(30.0,114.001);
		
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("type","location");
		map.put("location",appContext.getNickname()+"location:"+location); 
		MobclickAgent.onEvent(context, "publish_news", map); 

		loadingPd = CustomProgressDialog.show(this, "正在发布..", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
				
		ApiClient.publishNews(apiKey,contentText, 
					files,
					link,StringUtils.empty(link)?0:1,
							descImg,descText,
							location.longitude,location.latitude,
					new ClientCallback(){
						@Override
						public void onSuccess(Object data) {
							loadingPd.dismiss();
							showToast("发布成功");
							finish();
						}

						@Override
						public void onFailure(String message) {
							
							loadingPd.dismiss();
							showToast("服务器连接故障");
						}

						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub
							loadingPd.dismiss();
							showToast("服务器连接故障");
						}

						@Override
						public void onRequestLogIn() {
							// TODO Auto-generated method stub
							
						}		 
		 });
	}
}
