package com.deng.mychat.ui;

import java.util.List;

import tools.StringUtils;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.deng.mychat.R;
import com.deng.mychat.adapter.ImageAdapter;
import com.deng.mychat.adapter.PriseImgAdapter;
import com.deng.mychat.adapter.ReplyAdapter;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.Prise;
import com.deng.mychat.bean.Reply;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.Constants.Extra;
import com.deng.mychat.image_viewer.ImagePagerActivity;
import com.deng.mychat.view.MyGridView;
import com.deng.mychat.view.MyListView;

public class NewsDetilActivity extends AppActivity{
	
	private ScrollView newsContentll;
	private ImageView userIcon; 
	private TextView name;
	private TextView distance;
	private TextView contentText;
	private LinearLayout contentLL;
	private TextView sharedContentText;
	private ImageView singleImage;
	private MyGridView twoColGrid;
	private MyGridView threeColGrid;
	
	private RelativeLayout urlDescL;
	private ImageView ulrIcon;
	private TextView ulrDesc;
	
	private TextView timeTv;
	private ImageButton commentBtn;
	private ImageButton priseBtn;
	private ImageButton shareBtn;
	
	private TextView numOfComment;
	private TextView numOfPrise;
	private TextView numOfShare;
	
	private LinearLayout rpl;
	private View div;
    private MyGridView priseViewL;
    private MyListView replayList;
    private RelativeLayout toplayout;
    private News news;
    private String newsId;
    
    private ReplyAdapter replyAd ;     

    private PriseImgAdapter priseAd ; 
    
    boolean hasPrise;
    
    private boolean isPrising;
    private boolean isSharing;
    
	private ImageView indicatorImageView;
	private Animation indicatorAnimation;	
    
    private  Handler uiHandler=new Handler(){
    	@Override
    	public void handleMessage(Message m)
    	{
	    	numOfShare.setText(news.getShareCount()+"");
			numOfPrise.setText(news.getPrise().size()+"");
			numOfComment.setText(news.getReply().size()+"");
			
			 if(news.getPrise().size()>0 && news.getReply().size()>0)
		    	 div.setVisibility(View.VISIBLE);
		    else  div.setVisibility(View.GONE);
			 
		    if(news.getReply().size()==0 && news.getPrise().size()==0)
			    	rpl.setVisibility(View.GONE);
			 else rpl.setVisibility(View.VISIBLE);
		    
		   // final boolean hasPrise=hasPrise(news,appContext.getLoginUid());
		    if(hasPrise)priseBtn.setBackgroundResource(R.drawable.prise_pressed);
		    else  priseBtn.setBackgroundResource(R.drawable.prise_normal);
    	}
    };
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_detil_layout);
		news=(News) this.getIntent().getSerializableExtra("news");
		newsId=this.getIntent().getStringExtra("applyId");
		initUI();
		if(news!=null)showNews();
		else 
		{
			getRencentNewsById(newsId);
		}
	}
	
	private void getRencentNewsById(String newsId) {
		// TODO Auto-generated method stub
		indicatorImageView.startAnimation(indicatorAnimation);
		indicatorImageView.setVisibility(View.VISIBLE);
		ApiClient.getNewsById(appContext.getLoginApiKey(), newsId, new ClientCallback(){

			@Override
			public void onSuccess(Object data) {
				// TODO Auto-generated method stub
				news=(News)data;
				showNews();
			}

			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				
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

	private void initUI()
	{
		newsContentll=(ScrollView)findViewById(R.id.newsContentll);
		toplayout=(RelativeLayout)findViewById(R.id.relative_ll);
		userIcon=(ImageView)findViewById(R.id.userIcon);
   		name=(TextView)findViewById(R.id.name);
   		distance=(TextView)findViewById(R.id.distance);
   		contentText=(TextView)findViewById(R.id.contentText);
   		contentLL=(LinearLayout)findViewById(R.id.content_ll);
   		sharedContentText=(TextView)findViewById(R.id.sharedContentText);
   		
   		singleImage=(ImageView)findViewById(R.id.singleImage);

   		twoColGrid=(MyGridView)findViewById(R.id.twoColGrid);
   		threeColGrid=(MyGridView)findViewById(R.id.threeColGrid);
   		urlDescL=(RelativeLayout)findViewById(R.id.urlDescL);
   		ulrIcon=(ImageView)findViewById(R.id.ulrIcon);
   		ulrDesc=(TextView)findViewById(R.id.ulrDesc);
   		
    	timeTv=(TextView)findViewById(R.id.time);

    	commentBtn=(ImageButton)findViewById(R.id.commentBtn);
    	priseBtn=(ImageButton)findViewById(R.id.priseBtn);
    	shareBtn=(ImageButton)findViewById(R.id.shareBtn);
    
    	
    	numOfComment=(TextView)findViewById(R.id.numOfComment);
    	numOfPrise=(TextView)findViewById(R.id.numOfPrise);
    	numOfShare=(TextView)findViewById(R.id.numOfShare);
    	
    	rpl=(LinearLayout)findViewById(R.id.rpl);
    	div=findViewById(R.id.div);
    	priseViewL=(MyGridView)findViewById(R.id.priseViewL);
    	replayList=(MyListView)findViewById(R.id.replayList);
    	
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
	}
	
	public void buttonClick(View v)
	{
		switch (v.getId())
		{
			case R.id.cancel:finish();break;
		}
	}
	
	public void saveOrUpdateNews(final News news,final boolean p)
	{
		new Thread(){

			@Override
			public void run() {
				
			NewsManager.getInstance(context).saveOrUpdateNews(appContext.getLoginLocation(),news);
			notifyNewsList(p);
				
		}}.start();
		
	}
	
	private void showNews()
	{
		
		indicatorImageView.setVisibility(View.INVISIBLE);
		indicatorImageView.clearAnimation();
		
		if(news!=null)newsContentll.setVisibility(View.VISIBLE);
		
		UserInfo user=news.authorInfo;
		
		((AppActivity)context).imageLoader.displayImage(user.txPath.smallPicture, userIcon, CommonValue.DisplayOptions.touxiang_options);
		
		userIcon.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				goTocontactMainPage();
			}
		});
		
			
		name.setText(user.getName());
		name.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				goTocontactMainPage();
			}
		});
		
		
		String distanceLabel="";
		
		final LocationPoint l=appContext.getLoginLocation();
	    long d=(long) LocationPoint.distanceBetween(l, new LocationPoint(news.latitude, news.longitude));
	    if(d<1000) distanceLabel=d+"m";
	    else if(d<1000000) distanceLabel=d/1000+"km";
	    else distanceLabel="1000km外";
	    distance.setText(distanceLabel); 
	    
//	    distance.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent=new Intent(context,MapActivity.class);
//				intent.putExtra("y", news.latitude);
//				intent.putExtra("x", news.longitude);
//				context.startActivity(intent);
//			}
//	    	
//	    });
		
		final News sharedNews=news.sharedNews;
	    if(sharedNews!=null)
	    {
	    	sharedContentText.setVisibility(View.VISIBLE);
	    	contentLL.setBackgroundResource(R.drawable.edittext1);
	    	CharSequence c=Html.fromHtml("转自"+"<font color=#3A5FCD> "+sharedNews.authorInfo.userName+"</font>"+": "+sharedNews.getContextText());
	    	
	    	sharedContentText.setText(c);
	    	news.setpictures(sharedNews.pictures);	
	    }
	   else {contentLL.setBackgroundDrawable(null);
	   sharedContentText.setVisibility(View.GONE);}
	    
	   contentLL.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(sharedNews==null)return;
				Intent intent=new Intent(context,NewsDetilActivity.class);
				intent.putExtra("news", sharedNews);
				context.startActivity(intent);
			}
	    	
	    });
		
		
	    if(StringUtils.empty(news.getContextText()))contentText.setVisibility(View.GONE);
	    else 
	    {
	    	contentText.setVisibility(View.VISIBLE);
	    	contentText.setText(news.getContextText());
	    }
	    
	    if(news.shuoType==1)
	    {
	    	urlDescL.setVisibility(View.VISIBLE);
	    	((AppActivity)context).imageLoader.displayImage(news.descImg,ulrIcon, CommonValue.DisplayOptions.default_options);
	   		ulrDesc.setText(news.descText);
	   		urlDescL.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(context,WebViewActivity.class);
					intent.putExtra("url", news.shareUrl);
					context.startActivity(intent);
				}
	   			
	   		});
	    }
	    else if (sharedNews!=null && sharedNews.shuoType==1)
	    {
	    	urlDescL.setVisibility(View.VISIBLE);
	    	((AppActivity)context).imageLoader.displayImage(sharedNews.descImg, ulrIcon, CommonValue.DisplayOptions.default_options);
	    	String descText=sharedNews.descText;
	   		ulrDesc.setText(descText);
	   		urlDescL.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(context,WebViewActivity.class);
					intent.putExtra("url", sharedNews.shareUrl);
					context.startActivity(intent);
				}
	   			
	   		});
	    }
	    else urlDescL.setVisibility(View.GONE);
	    final List<Picture>pics=news.getPictures();
	    int numOfImgs=pics==null?0:pics.size();
	    if(numOfImgs==1)
	    {
	    	singleImage.setVisibility(View.VISIBLE);
	    	twoColGrid.setVisibility(View.GONE);
	    	threeColGrid.setVisibility(View.GONE);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(0).smallPicture, singleImage, CommonValue.DisplayOptions.default_options);

			singleImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					startImagePagerActivity(pics,0);
				}
	    });
	    }
	    else if(numOfImgs==2 || numOfImgs==4)
	    {
	    	singleImage.setVisibility(View.GONE);
	    	twoColGrid.setVisibility(View.VISIBLE);
	    	threeColGrid.setVisibility(View.GONE);
	    	
	    	twoColGrid.setAdapter(new ImageAdapter(context,pics,CommonValue.DisplayOptions.default_options,2));
			twoColGrid.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					startImagePagerActivity(pics,position);
				}
	    });
	    }
	    else if(numOfImgs!=0)
	    {
	    	singleImage.setVisibility(View.GONE);
	    	twoColGrid.setVisibility(View.GONE);
	    	threeColGrid.setVisibility(View.VISIBLE);
	    	threeColGrid.setAdapter(new ImageAdapter(context,pics,CommonValue.DisplayOptions.default_options,3));
			threeColGrid.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					startImagePagerActivity(pics,position);
				}
	    });
	    }
	    else
	    {
	    	singleImage.setVisibility(View.GONE);
	    	twoColGrid.setVisibility(View.GONE);
	    	threeColGrid.setVisibility(View.GONE);
	    }
		
	    long time=news.getSecondsBeforeNow();
	    String timeLabel="一周前";
	    if(time<0)timeLabel="时间未知";
	    else if(time<60)timeLabel="刚刚";
	    else if(time<60*60)timeLabel=time/60+"分钟前";
	    else if(time<60*60*24)timeLabel=time/3600+"小时前";
	    else timeLabel=time/(3600*24)+"天前";
	    timeTv.setText(timeLabel);

	    numOfComment.setText(news.getReply().size()+"");
	    numOfPrise.setText(news.getPrise().size()+"");
	    numOfShare.setText(news.getShareCount()+"");
	    
	    if(news.getReply().size()==0 && news.getPrise().size()==0)
	    	rpl.setVisibility(View.GONE);
	    else rpl.setVisibility(View.VISIBLE);
	    
	    replyAd=  new ReplyAdapter(context, news.getReply(),replayList) ;    
	    
	    replyAd.setOnContentClickCallback(new ReplyAdapter.OnContentClickCallback() {
				
				@Override
				public void onContentClick(int pos) {
					// TODO Auto-generated method stub
					// PopupWindows(final Context mContext, View parent,final int what,final UserInfo user,final int type,final News news,final MyHolder replyItem)
					new PopupWindows(context, toplayout,0,news.getReply().get(pos).author,1,news);
				}

				@Override
				public void reflesh(int p) {
					// TODO Auto-generated method stub
					replyAd.notifyDataSetChanged();
				}
			});
	    
	    replayList.setAdapter(replyAd);

	    commentBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new PopupWindows(context, toplayout,0,news.authorInfo,0,news);
				//new PopupWindows(context, toplayout,0);
			}
	    	
	    });
	    
	    priseAd=  new PriseImgAdapter(context, news.getPrise(),true,9) ;     
	    priseViewL.setAdapter(priseAd);
	    
	    priseViewL.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long p) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(NewsDetilActivity.this,ContactMainPageActivity.class);
				intent.putExtra("isMyFriend", news.getPrise().get(arg2).authorInfo);
				intent.putExtra("user", news.authorInfo);
				context.startActivity(intent);
			}
	    	
	    });
	    
	    if(news.getPrise().size()>0 && news.getReply().size()>0)
	    	 div.setVisibility(View.VISIBLE);
	    else  div.setVisibility(View.GONE);
	    
	    hasPrise=hasPrise(news,appContext.getLoginUid());
	    if(hasPrise)priseBtn.setBackgroundResource(R.drawable.prise_pressed);
	    else  priseBtn.setBackgroundResource(R.drawable.prise_normal);

	    priseBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!hasPrise)priseANews();
				else cancelPrise();
			}});
	    
	    shareBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//shareANews(news,holder);
				
				new PopupWindows(context, toplayout,1,news.authorInfo,0,news);
				//new PopupWindows(context, toplayout,1);
			}
	    	
	    });
	}
	
	
	protected void goTocontactMainPage() {
		// TODO Auto-generated method stub
    	Intent intent=new Intent(this,ContactMainPageActivity.class);
		intent.putExtra("isMyFriend", news.authorInfo.isMyFriend);
		intent.putExtra("user", news.authorInfo);
		context.startActivity(intent);
	}
	
	
	 private void startImagePagerActivity(List<Picture> pics,int position) {
			String[]urls=new String[pics.size()];
			for(int i=0;i<pics.size();i++)
				urls[i]=pics.get(i).largePicPath;

				Intent intent = new Intent(context, ImagePagerActivity.class);
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
				context.startActivity(intent);	
		}
	 
	  public boolean hasPrise(News news,String userId)
	    {
	    	if(news==null || news.getPrise()==null || userId==null)return false;
	    	
	    	for(Prise p:news.getPrise())
	    	{
	    		if(p.authorInfo.getId().equals(userId))return true;
	    	}
	    	return false;
	    }
	 // final News news,final String toId,final int type
	  private void replyANews(final UserInfo user,final int type,final String content)
	    {
	                 ApiClient.commentNews(appContext.getLoginApiKey(), news.shuoshuoId,
	                		 user.userId, appContext.getLoginUid(), content, 
	                		 type,new ClientCallback(){
	         					@Override
	         					public void onSuccess(Object data) {
	         						Reply r=new Reply();
	         						r.author=appContext.getLoginInfo().userInfo;
	         						r.toId=user.userId;
	         						r.context=content;
	         						r.replyType=type;
	         						r.newsId=news.shuoshuoId;
	         						r.toUser=user;
	         						news.addReply(r);
	         						replyAd.notifyDataSetChanged();
	         						uiHandler.sendEmptyMessage(1);
	         						// notifyNewsList();
	         						saveOrUpdateNews(news,false);
	         						// isComnenting=false;
	         					}

	         					@Override
	         					public void onFailure(String message) {
	         						// TODO Auto-generated method stu
	         						Toast.makeText(context, "评论失败", Toast.LENGTH_SHORT).show();
	         						// isComnenting=false;
	         					}

	         					@Override
	         					public void onError(Exception e) {
	         						// TODO Auto-generated method stub
	         						// isComnenting=false;
	         					}

								@Override
								public void onRequestLogIn() {
									// TODO Auto-generated method stub
									
								}
	         			
	         		});

	    }
	    
	    private void priseANews( )
	    {
	    	  if(isPrising)return;
	    	  
	    	    isPrising=true;
			    final Prise r=new Prise();
			    r.newsId=news.shuoshuoId;
			    r.authorInfo=appContext.getLoginInfo().userInfo;

				ApiClient.priseANews(appContext.getLoginApiKey(), news.shuoshuoId, appContext.getLoginUid(), new ClientCallback()
				{
						@Override
						public void onSuccess(Object data) {
							news.addPrise(r);
							priseAd.notifyDataSetChanged();
							hasPrise=true;
	 			    		uiHandler.sendEmptyMessage(1); 
	 			    		saveOrUpdateNews(news,false);
	 			    		isPrising=false;
						}

						@Override
						public void onFailure(String message) {
							// TODO Auto-generated method stub
							Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
							 isPrising=false;
						}

						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub
							 isPrising=false;
						}

						@Override
						public void onRequestLogIn() {
							// TODO Auto-generated method stub
							
						}
				
			});
	    }
	    
	    private void cancelPrise( )
	    {
	    	   if(isPrising)return;
	    	   isPrising=true;
				String zanId=news.deletePrise(appContext.getLoginUid());
				priseAd.notifyDataSetChanged();
				ApiClient.cancelPrise(appContext.getLoginApiKey(), news.shuoshuoId, appContext.getLoginUid(),zanId, new ClientCallback()
				{
						@Override
						public void onSuccess(Object data) {
	 			    		Toast.makeText(context, "取消成功", Toast.LENGTH_SHORT).show();
	 			    		hasPrise=false;
	 			    		uiHandler.sendEmptyMessage(1);
	 			    		saveOrUpdateNews(news,false);
	 			    		 isPrising=false;
						}

						@Override
						public void onFailure(String message) {
							// TODO Auto-generated method stub
							Toast.makeText(context, "取消失败", Toast.LENGTH_SHORT).show();
							 isPrising=false;
						}

						@Override
						public void onError(Exception e) {
							isPrising=false;
						}

						@Override
						public void onRequestLogIn() {
							// TODO Auto-generated method stub
							
						}
			});
	    }
	    
	    private void shareANews(String content)
	    {
	    		if(isSharing)return ;
	    		isSharing=true;
	                ApiClient.shareNews(appContext.getLoginApiKey(),news.shuoshuoId, news.longitude, news.latitude,
	                		content, new ClientCallback(){
	        					@Override
	        					public void onSuccess(Object data) {
	        						// TODO Auto-generated method stub
	        						news.shareCount++;
	        						
	         			    		Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
	         			    		uiHandler.sendEmptyMessage(1);
	         			    		saveOrUpdateNews(news,true); 
	         			    		isSharing=false;
	         			    		//notifyNewsList();
	        					}

	        					@Override
	        					public void onFailure(String message) {
	        						// TODO Auto-generated method stub
	        						Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
	        						isSharing=false;
	        					}
	        					@Override
	        					public void onError(Exception e) {
	        						// TODO Auto-generated method stub
	        						isSharing=false;
	        					}

								@Override
								public void onRequestLogIn() {
									// TODO Auto-generated method stub
									
								}
	        			
	        		});
		
	    }
	  
	   public void notifyNewsList(boolean p)
	   {
	    if(!p)
	    {
	    	Intent i = new Intent(CommonValue.ACTION_NOTIFY);
		    i.putExtra("newsId", news.shuoshuoId);
			sendBroadcast(i);//传递过去
		}	
	    else
	    {
	    	 Intent i = new Intent(CommonValue.ACTION_REFLESH);
			 sendBroadcast(i);//传递过去	
	    }
	   }
	   
	    public class PopupWindows extends PopupWindow {

	    	public PopupWindows(final Context mContext, View parent,final int what,final UserInfo user,final int type,final News news)
	    	{//PopupWindows(final Context mContext, View parent,final int what) {

				View view = View
						.inflate(mContext, R.layout.input_layout, null);
				view.startAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.fade_ins));
				RelativeLayout input_ll = (RelativeLayout) view
						.findViewById(R.id.input_ll);
				input_ll.startAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.push_bottom_in_2));

				setWidth(LayoutParams.FILL_PARENT);
				setHeight(LayoutParams.WRAP_CONTENT);
				setBackgroundDrawable(new BitmapDrawable());
				setFocusable(true);
				setOutsideTouchable(true);
				setContentView(view);
				setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);  
				setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
				showAtLocation(parent, Gravity.BOTTOM, 0, 0);
				update();

				final EditText chatContent=(EditText)view.findViewById(R.id.chat_content);

				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);  


				//if(what==0)chatContent.setHint("评论"+news.authorInfo.userName);
				if(what==0)chatContent.setHint((type==0?"评论":"回复")+user.userName);
				
				Button commentSend = (Button) view
						.findViewById(R.id.comment_send);

				commentSend.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(StringUtils.empty(chatContent.getText().toString()))
						{
							Toast.makeText(mContext, "内容不能为空", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(what==0)
						{
							//final News news,final String toId,final int type
							replyANews(user,type,chatContent.getText().toString());
						}
						else if(what==1)
						{
							shareANews(chatContent.getText().toString());
						}
						dismiss();
					}
				});
			}
	    }
}

