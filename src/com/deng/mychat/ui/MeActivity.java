package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import tools.StringUtils;
import tools.UIHelper;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.UserEntity;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.image_viewer.ImagePagerActivity;
import com.deng.mychat.pic.PublishedActivity;
import com.deng.mychat.tools.NetworkTool;
import com.deng.mychat.util.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MeActivity extends AppActivity
{
    private Button cancelBtn;
    private Button setBtn;
    private ImageView headBg;
    private ImageView head;
	private RelativeLayout topImgLayout;
	
	private TextView name;
	private TextView des;
	
	private TextView signContent;//个性签名
	private TextView schoolContent;
	private TextView labelContent;
	private GridView zujiPics;
	
	private Button editBtn;
	private Button publishBtn;
	
	
    private List<Picture>myPhotoUrls=new ArrayList<Picture>();
    private GridAdapter myAlbumAdapter ;
    

    private List<String>labels;
    
    Map <String, ? extends Collection<Integer>>map;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_main_page);
		initUI();

	}
	
	
	public void initUI()
	{
		long a=System.currentTimeMillis();
		
		topImgLayout=(RelativeLayout)findViewById(R.id.top_img_ll);
		LayoutParams headbgPara=(LayoutParams) topImgLayout.getLayoutParams();
		headbgPara.width=this.getResources().getDisplayMetrics().widthPixels;
		headbgPara.height=(int) (headbgPara.width*0.65);
		topImgLayout.setLayoutParams(headbgPara);
		
	  headBg=(ImageView)findViewById(R.id.head_bg);  
	  RelativeLayout.LayoutParams headimgPara=(RelativeLayout.LayoutParams) headBg.getLayoutParams();
	  headimgPara.width=this.getResources().getDisplayMetrics().widthPixels;
	  headimgPara.height=(headimgPara.width/5)*3;
	  headBg.setLayoutParams(headimgPara);
	  
	  head=(ImageView)findViewById(R.id.head);
	  name=(TextView)findViewById(R.id.name);
	  des=(TextView)findViewById(R.id.des);
	  
	  signContent=(TextView)findViewById(R.id.sign_content);//个性签名
	  schoolContent=(TextView)findViewById(R.id.school_content);//个性签名
	  labelContent=(TextView)findViewById(R.id.label_content);//个性签名

	  zujiPics=(GridView)findViewById(R.id.zuji_pic_gridview);
		
	  editBtn=(Button)findViewById(R.id.edit_btn);
	  publishBtn=(Button)findViewById(R.id.publish_btn);
	  
	  cancelBtn=(Button)findViewById(R.id.cancelBtn);
	  cancelBtn.setVisibility(View.GONE);
		
	   labels=new ArrayList<String>();
	   
	   myAlbumAdapter = new GridAdapter(this);
	   zujiPics.setAdapter(myAlbumAdapter);
//	   labelAdapter=new LabelAdapter(appContext, labels);
//
//
//	   
//	   myImageView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				startImagePagerActivity(position);
//			}
//			
//			private void startImagePagerActivity(int position) {
//				Intent intent = new Intent(context, ImagePagerActivity.class);
//				String[]urls=new String[myPhotoUrls.size()];
//				for(int i=0;i<myPhotoUrls.size();i++)
//					urls[i]=myPhotoUrls.get(i);
//				intent.putExtra(Extra.IMAGES, urls);
//				intent.putExtra(Extra.IMAGE_POSITION, position);
//				context.startActivity(intent);
//			}
//		}); 
//	   
	   zujiPics.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent=new Intent(MeActivity.this,ZujiActivity.class);
				//intent.putExtra("user_id",appContext.getLoginInfo().getId());
				goToZuji() ;
			}
	   });

	   
	   showUserInfo();
	  new NewsPageTask(1,20,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
	  
	  Log.d("Page","news"+( System.currentTimeMillis()-a));
	}
	
	public void showUserInfo()
	{
		   UserEntity logeduser=appContext.getLoginInfo();
		   String picUrl=logeduser.userInfo.txPath.smallPicture;
		   if(!StringUtils.empty(picUrl))picUrl=picUrl.replace("F:/apachetomcat/webapps/",CommonValue.BASE_URL);
		   

//
			 DisplayImageOptions touxiang_options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.avatar_placeholder)
			.showImageForEmptyUri(R.drawable.avatar_placeholder)
			.showImageOnFail(R.drawable.avatar_placeholder)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new CircleBitmapDisplayer())
			.build();
//						 
			imageLoader.displayImage(picUrl, head, touxiang_options);
//			imageLoader.displayImage(picUrl, headBg, default_options);
		   
			name.setText(logeduser.userInfo.getName());
			
		    String desc="";
		   
		   int s=logeduser.userInfo.getSex();
			String sex="";
			if(s==1)
				sex="男  ";
			 else if(s==2)
				 sex="女 ";
			 else sex="";

		   String bithDay=logeduser.userInfo.birthDay;
		   
		   desc=sex+" "+logeduser.userInfo.schoolName;
		   
		   des.setText(desc);
		   signContent.setText(logeduser.userInfo.desc);
		   schoolContent.setText(logeduser.userInfo.schoolName);
		   
		   labels=logeduser.userInfo.getLabel();
		   String labelStr="";
		   if(labels.size()>0)
		   {
			   for(String str:labels)
				   labelStr+=str+" ";
		   }
		   
		   if(StringUtils.empty(labelStr))labelStr="暂无标签";
		   labelContent.setText(labelStr);
	}
	
	public void buttonClick(View view)//个人设置的按键按下
	{
		switch(view.getId())
		{
		case R.id.cancel:finish();break;
		case R.id.edit_btn:goToModifyUserInfo();break;
		case R.id.set_btn:goToPreferencePage(); break;
		case R.id.publish_btn:publishANews();break;
		case R.id.zujiBtn:goToZuji();break;
		case R.id.label_content:goToLabelPage();break;
		case R.id.head:goImagePage();break;
		}
	}
	
	private void goImagePage() {
		// TODO Auto-generated method stub
		 UserEntity logeduser=appContext.getLoginInfo();
		 String picUrl=logeduser.userInfo.txPath.largePicPath;
		 
		 if(StringUtils.empty(picUrl) || picUrl.equals("defaultPath"))return;
		 
		 if(!StringUtils.empty(picUrl))picUrl=picUrl.replace("F:/apachetomcat/webapps/",CommonValue.BASE_URL);
		 
		 String[]urls=new String[]{picUrl};


			Intent intent = new Intent(context, ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
			context.startActivity(intent);
	}
	
	private void goToZuji() {
		// TODO Auto-generated method stub
		Intent intent=new Intent(MeActivity.this,ZujiActivity.class);
		//intent.putExtra("user_id",appContext.getLoginInfo().getId());
		intent.putExtra("user", appContext.getLoginInfo().userInfo);
		startActivity(intent);
	}


	private void goToLabelPage() {
		// TODO Auto-generated method stub
		Intent intent=new Intent(this,SelectLabelAcitity.class);
		startActivityForResult(intent, 2);
	}


	public void goToModifyUserInfo()
	{
		Intent intent =new Intent(this,Register2.class);
		startActivityForResult(intent, 1);
	}
	
	 @Override  
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		 if(requestCode==1 || requestCode==2)
		 {
			this.showUserInfo(); 
		}	 
		}
	
	public void goToPreferencePage()
	{
		Intent intent =new Intent(this,MyPreferenceActivity.class);
		startActivity(intent);
	}

	private void handleAlbums(List<Picture> result, boolean isNetworkData,int mAction2) {
		if(result==null || result.size()==0)return;
		myPhotoUrls.clear();
		for(Picture s:result)
			{
				myPhotoUrls.add(s);
				myAlbumAdapter.notifyDataSetChanged();
			}
		
	}
	
	private int myCurrentAlbumPage=1;
	public void publishANews()
	{
		Intent intent =new Intent(this,PublishedActivity.class);
		startActivity(intent);
	}
	
	
	public class NewsPageTask extends AsyncTask<Object, Integer, Object> {		    
			private int mPageIndex;
			private int mPageSize;
			private int mAction;
			
		public	NewsPageTask(int pageIndex,int pageSize,int action)
			{
				this.mPageIndex=pageIndex;
				this.mPageSize=pageSize;
				this.mAction=action;
			}
		
		public Object doInBackground(Object... params) {
				return NewsManager.getInstance(context).getUserAlbum(appContext.getLoginUid());				
			}
	
			@Override
		public  void onCancelled() {
				super.onCancelled();
			}
	
			@Override
			public void onPostExecute(final Object result) {	
				String apiKey=appContext.getLoginApiKey();
				myCurrentAlbumPage=1;
				ApiClient.getUserPhotos(apiKey, appContext.getLoginInfo().userInfo.userId, 
						myCurrentAlbumPage, 300,  new ClientCallback(){
							@Override
							public void onSuccess(Object data) {
								
								myPhotoUrls.clear();
								List<Picture>urls=(ArrayList<Picture>)data;
								for(Picture s:urls)
								{
										myPhotoUrls.add(s);
										myAlbumAdapter.notifyDataSetChanged();
								}
							}
							@Override
						    public void onFailure(String message) {
										// TODO Auto-generated method stub	
								handleAlbums((List<Picture>)result,false, mAction);
							}
									@Override
							public void onError(Exception e) {
										// TODO Auto-generated method stub	
								handleAlbums((List<Picture>)result,false, mAction);
							}
									@Override
									public void onRequestLogIn() {
										// TODO Auto-generated method stub
										
									}
					});				
			}
		}
	
	
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位�?
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}


		public int getCount() {
			return myPhotoUrls.size();
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int coord = position;
			if(position>=4)return null;
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			imageLoader.displayImage(myPhotoUrls.get(position)==null?"":myPhotoUrls.get(position).smallPicture, holder.image, CommonValue.DisplayOptions.default_options);
			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

	}
	
}
