package com.deng.mychat.ui;

import java.util.ArrayList;
import java.util.List;

import tools.StringUtils;
import tools.UIHelper;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deng.mychat.R;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.UserEntity;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.ContactManager;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.im.AWechatActivity;
import com.deng.mychat.image_viewer.ImagePagerActivity;
import com.deng.mychat.model.Notice;
import com.deng.mychat.pic.PublishedActivity;
import com.deng.mychat.util.CircleBitmapDisplayer;
import com.deng.mychat.view.CustomProgressDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ContactMainPageActivity extends AWechatActivity
{
    private Button cancelButton;
    private Button setBtn;
    private ImageView headBg;
    private ImageView head;
	private RelativeLayout topImgLayout;
	
	LinearLayout myPageOpll ;//=(LinearLayout)findViewById(R.id.my_page_opll);	  
	LinearLayout contactPageOpll ;//=(LinearLayout)findViewById(R.id.contact_page_opll);
	RelativeLayout reportLl;
	
	private TextView name;
	private TextView des;
	
	private TextView signContent;//个性签名
	private TextView schoolContent;
	private TextView labelContent;
	private GridView zujiPics;
	
	private Button chatBtn;
	private Button tuijianOrAddBtn;
	
	
    private List<Picture>myPhotoUrls=new ArrayList<Picture>();
    private GridAdapter myAlbumAdapter ;
    
    private UserInfo userInfo;
    private boolean isMyFriend;
    private List<String>labels;
    private boolean isMe=false;
    
    private CustomProgressDialog loadingPd;
    	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_main_page);

		userInfo=(UserInfo) getIntent().getSerializableExtra("user");
		isMyFriend= userInfo.isMyFriend==1;	
		isMe=appContext.getLoginUid().equals(userInfo.userId);
		initUI();
		 getUserInfo();
		 new NewsPageTask(1,5,UIHelper.LISTVIEW_ACTION_REFRESH).execute();
	}
	
	
	public void initUI()
	{
		topImgLayout=(RelativeLayout)findViewById(R.id.top_img_ll);
		LayoutParams headbgPara=(LayoutParams) topImgLayout.getLayoutParams();
		headbgPara.width=this.getResources().getDisplayMetrics().widthPixels;
		headbgPara.height=(int) (headbgPara.width*0.65);
		topImgLayout.setLayoutParams(headbgPara);
		
		headBg=(ImageView)findViewById(R.id.headbg);
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
	  myPageOpll =(LinearLayout)findViewById(R.id.my_page_opll);
	  myPageOpll.setVisibility(View.GONE);
	  
	  contactPageOpll =(LinearLayout)findViewById(R.id.contact_page_opll);
	  contactPageOpll.setVisibility(View.GONE);
	  
	  reportLl=(RelativeLayout)findViewById(R.id.report_ll);
	  reportLl.setVisibility(View.GONE);
	  
	  if(isMe)
	  {
		  myPageOpll.setVisibility(View.VISIBLE);
	  }
	  
	  setBtn=(Button)findViewById(R.id.set_btn);
	  setBtn.setText("");
	  
	  chatBtn=(Button)findViewById(R.id.chat_btn);	 
	  tuijianOrAddBtn=(Button)findViewById(R.id.tuijian_btn);  
	  labels=new ArrayList<String>();
	   
	   myAlbumAdapter = new GridAdapter(this);
	   zujiPics.setAdapter(myAlbumAdapter);
	   zujiPics.setOverScrollMode(View.OVER_SCROLL_NEVER);
	   zujiPics.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				goToZuji();
			}
	   });

	  //showUserInfo();
	 
	}
	
	public void getUserInfo()
	{
		loadingPd = CustomProgressDialog.show(this, "加载中...", false, null);//UIHelper.showProgress(this, null, "正在登录", true);
		loadingPd.setCancelable(true);
		ApiClient.getUserInfo( appContext.getLoginApiKey(), userInfo.userId, 
			 new ClientCallback(){
					@Override
					public void onSuccess(Object data) {
							userInfo=(UserInfo)data;
							isMyFriend= userInfo.isMyFriend==1;	
							isMe=appContext.getLoginUid().equals(userInfo.userId);
							showUserInfo();
							showUi();
							loadingPd.dismiss();
					}

					@Override
					public void onFailure(String message) {
							// TODO Auto-generated method stub
						loadingPd.dismiss();
					}

					@Override
					public void onError(Exception e) {
							// TODO Auto-generated method stub
						loadingPd.dismiss();
					}

					@Override
					public void onRequestLogIn() {
						// TODO Auto-generated method stub	
						loadingPd.dismiss();
					}
			
		});
	}

	
	public void showUi()
	{
		 reportLl.setVisibility((isMyFriend || isMe) ?View.GONE:View.VISIBLE);
		  
		 if(isMe)
		 {
			  contactPageOpll.setVisibility(isMyFriend?View.VISIBLE:View.GONE);
			  myPageOpll.setVisibility(View.VISIBLE);
		 }
		 else
		 {
			 contactPageOpll.setVisibility(isMyFriend?View.VISIBLE:View.GONE);
			 myPageOpll.setVisibility(View.GONE);
		 }
		  
		  setBtn=(Button)findViewById(R.id.set_btn);
		  setBtn.setText(isMe?"设置":"管理");
		  
		  if(!isMyFriend || isMe)chatBtn.setEnabled(false);
		  if(isMyFriend)tuijianOrAddBtn.setText("推荐好友");
		  else tuijianOrAddBtn.setText("加为好友");  
	}
	
	public void showUserInfo()
	{
		   	Picture picUrl=userInfo.txPath; 	
			 DisplayImageOptions touxiang_options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.avatar_placeholder)
			.showImageForEmptyUri(R.drawable.avatar_placeholder)
			.showImageOnFail(R.drawable.avatar_placeholder)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new CircleBitmapDisplayer())
			.build();
						 
			imageLoader.displayImage(picUrl.smallPicture, head, touxiang_options);
		   
			name.setText(userInfo.getName());
			
		    String desc="";
		   
		   int s=userInfo.getSex();
			String sex="";
			if(s==1)
				sex="男  ";
			 else if(s==2)
				 sex="女 ";
			 else sex="";

		   String bithDay=userInfo.birthDay;
		   
		   desc=desc+" "+userInfo.schoolName;
		   
		   des.setText(desc);
		   signContent.setText(userInfo.desc);
		   schoolContent.setText(userInfo.schoolName);
		   
		   labels=userInfo.getLabel();
		   String labelStr="";
		   if(labels.size()>0)
		   {
			   for(String str:labels)
				   labelStr+=str+" ";
		   }
		   
		   if(StringUtils.empty(labelStr))labelStr="暂无标签";
		   labelContent.setText(labelStr);
	}
	            //buttonClick
	public void buttonClick(View view)//个人设置的按键按下
	{
		switch(view.getId())
		{
			case R.id.cancelBtn:finish();break;
			case R.id.set_btn:
					if(isMe)goToPreferencePage();
					else goToContactManagerPage();
					break;
			case R.id.chat_btn:chatToContact();break;
			case R.id.tuijian_btn:tuijianOrAddFriend();break;
			case R.id.open_report:gotoFriendReportPage();break;
			case R.id.report_ll:gotoFriendReportPage();break;
			case R.id.zujiBtn: goToZuji();break;
			case R.id.head:goImagePage();break;
			case R.id.edit_btn:
				if(isMe)goToModifyUserInfo();
				break;
			case R.id.publish_btn:publishANews();break;
			case R.id.label_content:if(isMe)goToLabelPage();break;
		}
	}
	
	public void goToPreferencePage()
	{
		Intent intent =new Intent(this,MyPreferenceActivity.class);
		startActivity(intent);
	}
	
	private void goToLabelPage() {
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
			showUserInfo(); 
			showUi();
		}	 
		}
	
	private void goImagePage() {
		// TODO Auto-generated method stub
		 String picUrl=userInfo.txPath.largePicPath; 
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
		Intent intent=new Intent(this,ZujiActivity.class);
		//intent.putExtra("user_id",appContext.getLoginInfo().getId());
		intent.putExtra("user", userInfo);
		startActivity(intent);
	}
	
	public void goToContactManagerPage()
	{
		Intent intent=new Intent(this,ContactManagerPage.class);
		intent.putExtra("user", userInfo);
		startActivity(intent);
		
	}
	
	public void chatToContact()
	{
		createChat(userInfo.userId+XmppConnectionManager.BASE_XMPP_SERVER_NAME);
		
	}
	
	public void tuijianOrAddFriend()
	{
		if(!isMyFriend)
		{
			  String apiKey=appContext.getLoginApiKey();
			   ApiClient.addFriend(apiKey, userInfo.getId(), new ClientCallback(){
				@Override
				public void onSuccess(Object data) {
					//Toast.makeText(context,(String)data, Toast.LENGTH_SHORT).show();
					showToast((String)data);
				}

				@Override
				public void onFailure(String message) {
					//Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
					showToast(message);
				}

				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onRequestLogIn() {
					// TODO Auto-generated method stub
					
				}   
			  });
		}
		else
		{
			Intent intent=new Intent(this,SuggestFriendActivity.class);
			intent.putExtra("user", userInfo);
			startActivity(intent);
		}
	}
	
	public void gotoFriendReportPage()
	{
		Intent intent =new Intent(this,FriendReportPage.class);
		intent.putExtra("user", userInfo);
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
	
	private int myCurrentAlbumPage=0;

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
			return NewsManager.getInstance(context).getUserAlbum(userInfo.userId);				
		}

		@Override
	public  void onCancelled() {
			super.onCancelled();
		}

		@Override
		public void onPostExecute(final Object result) {
			
			
			String apiKey=appContext.getLoginApiKey();
			myCurrentAlbumPage=1;
			ApiClient.getUserPhotos(apiKey, userInfo.userId, 
					myCurrentAlbumPage, mPageSize,  new ClientCallback(){
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


	@Override
	protected void msgSend(String to) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void msgReceive(Notice notice) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void handReConnect(boolean isSuccess) {
		// TODO Auto-generated method stub
		
	}
	
}
