package com.deng.mychat.adapter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tools.StringUtils;

import com.deng.mychat.R;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.Prise;
import com.deng.mychat.bean.Reply;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.config.Constants.Extra;
import com.deng.mychat.image_viewer.ImagePagerActivity;
import com.deng.mychat.ui.ContactMainPageActivity;
import com.deng.mychat.ui.MapActivity;
import com.deng.mychat.ui.NewsDetilActivity;
import com.deng.mychat.ui.WebViewActivity;
import com.deng.mychat.view.MyGridView;
import com.deng.mychat.view.MyListView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyNewsAdapter extends BaseAdapter{

    private Context context;                        //运行上下文   
    private WCApplication appContext;
    private List<News> listItems;    //动态信息集合   
    private LayoutInflater listContainer;           //视图容器   
    private boolean[] hasChecked;                   //新闻选中状态  
    private int checkPos=-1;

    private  Set<String>priseList=new HashSet<String>();
	
    public MyNewsAdapter(Context context,WCApplication appContext, List<News> listItems) {   
        this.context = context;
        this.appContext=appContext;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
        this.listItems = listItems;  
        hasChecked = new boolean[getCount()];   
    } 
    
    public int getCount() {   
        // TODO Auto-generated method stub   
        return listItems.size();   
    }   
  
    public Object getItem(int arg0) {   
        // TODO Auto-generated method stub   
        return this.getItem(arg0);   
    }   
  
    public long getItemId(int arg0) {   
        // TODO Auto-generated method stub   
        return arg0;   
    }   


    private void checkedChange(int checkedID) {   
        hasChecked[checkedID] = !hasChecked[checkedID];   
        if(hasChecked(checkedID))checkPos=checkedID;
    }   
       
 
    public boolean hasChecked(int checkedID) {   
        return hasChecked[checkedID];   
    }   

    public void setCheckPos(int p)
    {
    	//int pp=this.checkPos;
    	this.checkPos=p;
    	this.notifyDataSetChanged();
    }
    
    public boolean hasPrise(News news,String userId)
    {
    	if(news==null || news.getPrise()==null || userId==null)return false;
    	
    	for(Prise p:news.getPrise())
    	{
    		if(p==null || p.authorInfo==null || p.authorInfo.userId==null)continue;
    		if(p.authorInfo.getId().equals(userId))return true;
    	}
    	return false;
    }
    
    public View getView(final int position, View cv, final ViewGroup parent) {   
   	 final News news= listItems.get(position);
   	 final MyHolder holder;
   	 final UserInfo user=news.authorInfo;
   	
   	 if(cv==null)
   	 {
   		holder=new MyHolder();
   		cv=listContainer.inflate(R.layout.news_item_layout, null);
   		holder.userIcon=(ImageView)cv.findViewById(R.id.userIcon);
   		holder.name=(TextView)cv.findViewById(R.id.name);
   		holder.title=(TextView)cv.findViewById(R.id.title);
   		holder.contentText=(TextView)cv.findViewById(R.id.contentText);
   		holder.singleImage=(ImageView)cv.findViewById(R.id.singleImage);
   		
   		holder.contentLL=(LinearLayout)cv.findViewById(R.id.content_ll);
   		holder.sharedContentText=(TextView)cv.findViewById(R.id.sharedContentText);
   		
   		holder.twoColGrid=(MyGridView)cv.findViewById(R.id.twoColGrid);
   		holder.threeColGrid=(MyGridView)cv.findViewById(R.id.threeColGrid);
   		holder.urlDescL=(RelativeLayout)cv.findViewById(R.id.urlDescL);
   		holder.ulrIcon=(ImageView)cv.findViewById(R.id.ulrIcon);
   		holder.ulrDesc=(TextView)cv.findViewById(R.id.ulrDesc);
   		
    	holder.time=(TextView)cv.findViewById(R.id.time);
    	holder.distance=(TextView)cv.findViewById(R.id.distance);

    	holder.commentBtn=(ImageButton)cv.findViewById(R.id.commentBtn);
    	holder.priseBtn=(ImageButton)cv.findViewById(R.id.priseBtn);
    	holder.shareBtn=(ImageButton)cv.findViewById(R.id.shareBtn);
    
    	
    	holder.numOfComment=(TextView)cv.findViewById(R.id.numOfComment);
    	holder.numOfPrise=(TextView)cv.findViewById(R.id.numOfPrise);
    	holder.numOfShare=(TextView)cv.findViewById(R.id.numOfShare);
    	
    	holder.rpl=(LinearLayout)cv.findViewById(R.id.rpl);
    	holder.div=cv.findViewById(R.id.div);
    	holder.priseViewL=(MyGridView)cv.findViewById(R.id.priseViewL);
    	holder.replayList=(MyListView)cv.findViewById(R.id.replayList);
    	 
    	cv.setTag(holder);   
     }else {   
         holder = (MyHolder)cv.getTag();   
     }    
   	
   	 
	((AppActivity)context).imageLoader.displayImage(user.txPath.smallPicture, holder.userIcon, CommonValue.DisplayOptions.touxiang_options);
	holder.name.setText(user.getName());
	
	holder.userIcon.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			goTocontactMainPage(user);
		}
		
	});
	
	holder.name.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			goTocontactMainPage(user);
		}
		
	});
	
	String typeStr="";
    if(news.shuoType==2)typeStr="转发了一条新鲜事";
    else if(news.shuoType==1)typeStr="分享了一条链接";
    holder.title.setText(typeStr);
    
    final News sharedNews=news.sharedNews;
    if(sharedNews!=null)
    {
    	holder.sharedContentText.setVisibility(View.VISIBLE);
    	holder.contentLL.setBackgroundResource(R.drawable.edittext1);
    	CharSequence c=Html.fromHtml("转自"+"<font color=#3A5FCD> "+sharedNews.authorInfo.userName+"</font>"+": "+sharedNews.getContextText());
    	
    	holder.sharedContentText.setText(c);
    	news.setpictures(sharedNews.pictures);	
    }
   else {holder.contentLL.setBackgroundDrawable(null);
   holder.sharedContentText.setVisibility(View.GONE);}
    
    if(news.shuoType==1 )
    {
    	holder.urlDescL.setVisibility(View.VISIBLE);
    	((AppActivity)context).imageLoader.displayImage(news.descImg, holder.ulrIcon, CommonValue.DisplayOptions.default_options);
    	String descText=news.descText;
   		holder.ulrDesc.setText(descText);
   		holder.urlDescL.setOnClickListener(new OnClickListener(){

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
    	holder.urlDescL.setVisibility(View.VISIBLE);
    	((AppActivity)context).imageLoader.displayImage(sharedNews.descImg, holder.ulrIcon, CommonValue.DisplayOptions.default_options);
    	String descText=sharedNews.descText;
   		holder.ulrDesc.setText(descText);
   		holder.urlDescL.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(context,WebViewActivity.class);
				intent.putExtra("url", sharedNews.shareUrl);
				context.startActivity(intent);
			}
   			
   		});
    }
    else holder.urlDescL.setVisibility(View.GONE);
    
    holder.contentLL.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(context,NewsDetilActivity.class);
			intent.putExtra("news", sharedNews==null?news:sharedNews);
			context.startActivity(intent);
		}
    	
    });
    
    if(StringUtils.empty(news.getContextText()))holder.contentText.setVisibility(View.GONE);
    else 
    {
    	holder.contentText.setVisibility(View.VISIBLE);
    	holder.contentText.setText(news.getContextText());
    }
   
    final List<Picture>pics=news.getPictures();
    int numOfImgs=pics==null?0:pics.size();
    if(numOfImgs==1)
    {
    	holder.singleImage.setVisibility(View.VISIBLE);
    	holder.twoColGrid.setVisibility(View.GONE);
    	holder.threeColGrid.setVisibility(View.GONE);
    	((AppActivity)context).imageLoader.displayImage(pics.get(0).smallPicture, holder.singleImage, CommonValue.DisplayOptions.default_options);

		holder.singleImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startImagePagerActivity(pics,0);
			}
    });
    }
    else if(numOfImgs==2 || numOfImgs==4)
    {
    	holder.singleImage.setVisibility(View.GONE);
    	holder.twoColGrid.setVisibility(View.VISIBLE);
    	holder.threeColGrid.setVisibility(View.GONE);
    	
    	holder.twoColGrid.setAdapter(new ImageAdapter(context,pics,CommonValue.DisplayOptions.default_options,2));
		holder.twoColGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(pics,position);
			}
    });
    }
    else if(numOfImgs!=0)
    {
    	holder.singleImage.setVisibility(View.GONE);
    	holder.twoColGrid.setVisibility(View.GONE);
    	holder.threeColGrid.setVisibility(View.VISIBLE);
    	holder.threeColGrid.setAdapter(new ImageAdapter(context,pics,CommonValue.DisplayOptions.default_options,3));
		holder.threeColGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(pics,position);
			}
    });
    }
    else
    {
    	holder.singleImage.setVisibility(View.GONE);
    	holder.twoColGrid.setVisibility(View.GONE);
    	holder.threeColGrid.setVisibility(View.GONE);
    }
	
    long time=news.getSecondsBeforeNow();
    String timeLabel="一周前";
    if(time<0)timeLabel="时间未知";
    else if(time<60)timeLabel="刚刚";
    else if(time<60*60)timeLabel=time/60+"分钟前";
    else if(time<60*60*24)timeLabel=time/3600+"小时前";
    else timeLabel=time/(3600*24)+"天前";
    holder.time.setText(timeLabel);
    
    final LocationPoint l=appContext.getLoginLocation();
	String distanceLabel="";
    int d=(int) LocationPoint.distanceBetween(l, new LocationPoint(news.latitude, news.longitude));
    if(d<1000) distanceLabel=d+"m";
    else if(d<1000000) distanceLabel=d/1000+"km";
    else distanceLabel="1000km外";
    holder.distance.setText(distanceLabel); 
    
//    holder.distance.setOnClickListener(new OnClickListener(){
//
//		@Override
//		public void onClick(View arg0) {
//			// TODO Auto-generated method stub
//			Intent intent=new Intent(context,MapActivity.class);
//			intent.putExtra("y", news.latitude);
//			intent.putExtra("x", news.longitude);
//			context.startActivity(intent);
//		}
//    	
//    });


    holder.numOfComment.setText(news.getReply().size()+"");
    holder.numOfPrise.setText(news.getPrise().size()+"");
    holder.numOfShare.setText(news.getShareCount()+"");
    
    if(news.getReply().size()==0 && news.getPrise().size()==0)
    	holder.rpl.setVisibility(View.GONE);
    else holder.rpl.setVisibility(View.VISIBLE);
    
    final ReplyAdapter ad=  new ReplyAdapter(context, news.getReply(),holder.replayList) ;     
  
	ad.setOnContentClickCallback(new ReplyAdapter.OnContentClickCallback() {
			
			@Override
			public void onContentClick(int pos) {
				// TODO Auto-generated method stub
				new PopupWindows(context, (View)parent,0,news.getReply().get(pos).author,1,news,holder);
			}

			@Override
			public void reflesh(int p) {
				// TODO Auto-generated method stub
				notifyDataSetChanged();
			}
		});
	
      holder.replayList.setAdapter(ad);
    
    holder.commentBtn.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			new PopupWindows(context, (View)parent,0,news.authorInfo,0,news,holder);
		}
    	
    });
    
    final List<Prise>ps=news.getPrise();
    final PriseImgAdapter pad=  new PriseImgAdapter(context, ps.size()>8?ps.subList(0, 8):ps,false,9); 
    
    holder.priseViewL.setAdapter(pad);
    holder.priseViewL.setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long p) {
			// TODO Auto-generated method stub
			if(arg2<7)goTocontactMainPage(ps.get(arg2).authorInfo);
		}
    	
    });
    
    if(news.getPrise().size()>0 && news.getReply().size()>0)
    	 holder.div.setVisibility(View.VISIBLE);
    else  holder.div.setVisibility(View.GONE);
    
    final boolean hasPrise=hasPrise(news,appContext.getLoginUid());
    if(hasPrise)holder.priseBtn.setBackgroundResource(R.drawable.prise_pressed);
    else  holder.priseBtn.setBackgroundResource(R.drawable.prise_normal);

    holder.priseBtn.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!hasPrise)priseANews(news,holder,pad);
			else cancelPrise(news,holder,pad);
		}});
    
    holder.shareBtn.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			new PopupWindows(context, (View)parent,1,news.authorInfo,0,news,holder);
		}
    	
    });

    cv.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(context,NewsDetilActivity.class);
			intent.putExtra("news", news);
			context.startActivity(intent);
		}

	});
    
    
    return cv;  
   }   

    private void startImagePagerActivity(List<Picture> pics,int position) {
    	
			String[]urls=new String[pics.size()];
			for(int i=0;i<pics.size();i++)
				urls[i]=pics.get(i).largePicPath;


			Intent intent = new Intent(context, ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
			context.startActivity(intent);
    	
	}
  //              replyANews(news, user.getId(),type,replyItem,chatContent.getText().toString());
    private void replyANews(final News news,final UserInfo toUser,final int type,final MyHolder replyItem/*,final ReplyAdapter ad*/,final String content)
    {
                 ApiClient.commentNews(appContext.getLoginApiKey(), news.shuoshuoId,
                		 toUser.userId, appContext.getLoginUid(), content, 
                		 type,new ClientCallback(){
         					@Override
         					public void onSuccess(Object data) {
         						Reply r=new Reply();
         						r.author=appContext.getLoginInfo().userInfo;
         						r.toId=toUser.userId;
         						r.toUser=toUser;
         						r.context=content;
         						r.replyType=type;
         						r.newsId=news.shuoshuoId;
         						news.addReply(r);
         						checkPos=-1;
         						notifyDataSetChanged();
         					//	isComnenting=false;
         					}

         					@Override
         					public void onFailure(String message) {
         						// TODO Auto-generated method stub
         						checkPos=-1;
         						Toast.makeText(context, "评论失败", Toast.LENGTH_SHORT).show();
         					//	isComnenting=false;
         					}

         					@Override
         					public void onError(Exception e) {
         						// TODO Auto-generated method stub
         						checkPos=-1;
         					//	isComnenting=false;
         					}

							@Override
							public void onRequestLogIn() {
								// TODO Auto-generated method stub
								
							}
         			
         		});

    }
    
    private void priseANews(final News news,final MyHolder replyItem,final PriseImgAdapter pad )
    {
    	synchronized(priseList){
    		if(priseList.contains(news.shuoshuoId))return;
    		priseList.add(news.shuoshuoId);
    	 }
		    final Prise r=new Prise();
		    r.authorInfo=appContext.getLoginInfo().userInfo;
		
			ApiClient.priseANews(appContext.getLoginApiKey(), news.shuoshuoId, appContext.getLoginUid(), new ClientCallback()
			{

					@Override
					public void onSuccess(Object data) {
						news.addPrise(r);
						checkPos=-1;
						notifyDataSetChanged();			
 			    		Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
 			    		priseList.remove(news.shuoshuoId);
					}

					@Override
					public void onFailure(String message) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
						priseList.remove(news.shuoshuoId);
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						priseList.remove(news.shuoshuoId);
					}

					@Override
					public void onRequestLogIn() {
						// TODO Auto-generated method stub
						
					}
			
		});
    }
    
    private void cancelPrise(final News news,final MyHolder replyItem,final PriseImgAdapter pad )
    {
    	
    	synchronized(priseList){
    		if(priseList.contains(news.shuoshuoId))return;
    		priseList.add(news.shuoshuoId);
    	}
			String zanId=news.deletePrise(appContext.getLoginUid());
			checkPos=-1;
			ApiClient.cancelPrise(appContext.getLoginApiKey(), news.shuoshuoId, appContext.getLoginUid(),zanId, new ClientCallback()
			{
					@Override
					public void onSuccess(Object data) {
 			    		Toast.makeText(context, "取消成功", Toast.LENGTH_SHORT).show();
 			    		notifyDataSetChanged();	
 			    		priseList.remove(news.shuoshuoId);
					}

					@Override
					public void onFailure(String message) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "取消失败", Toast.LENGTH_SHORT).show();
						priseList.remove(news.shuoshuoId);
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						priseList.remove(news.shuoshuoId);
						
					}

					@Override
					public void onRequestLogIn() {
						// TODO Auto-generated method stub
						
					}
			
		});
    }
    
    private void shareANews(final News news,final MyHolder replyItem,String content)
    {

    	 // if(isSharing)return ;
    	 // isSharing=true;
                ApiClient.shareNews(appContext.getLoginApiKey(),news.shuoshuoId, news.longitude, news.latitude,
                		content, new ClientCallback(){
        					@Override
        					public void onSuccess(Object data) {
        						// TODO Auto-generated method stub
        						news.shareCount++;
        						notifyDataSetChanged();	
         			    		Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
         			    		//isSharing=false;
        					}

        					@Override
        					public void onFailure(String message) {
        						// TODO Auto-generated method stub
        						Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
        						//isSharing=false;
        					}
        					@Override
        					public void onError(Exception e) {
        						// TODO Auto-generated method stub
        						//isSharing=false;
        					}

							@Override
							public void onRequestLogIn() {
								// TODO Auto-generated method stub
								
							}
        			
        		});
	
    }
    
    
    protected void goTocontactMainPage(UserInfo user) {
		// TODO Auto-generated method stub
    	Intent intent=new Intent(context,ContactMainPageActivity.class);
		intent.putExtra("isMyFriend", user.isMyFriend);
		intent.putExtra("user", user);
		context.startActivity(intent);
	}

    
    public class PopupWindows extends PopupWindow {

		public PopupWindows(final Context mContext, View parent,final int what,final UserInfo user,final int type,final News news,final MyHolder replyItem) {

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
						replyANews(news, user,type,replyItem,chatContent.getText().toString());
						//replyANews(news,replyItem,chatContent.getText().toString());
					}
					else if(what==1)
					{
						shareANews(news,replyItem,chatContent.getText().toString());
					}
					dismiss();
				}
			});

		}
	}
    
    
    public class MyHolder
    {
    	public ImageView userIcon; 
    	public TextView name;
    	public TextView title;
    	public TextView contentText;
    	
    	public LinearLayout contentLL;
    	public TextView sharedContentText;
    	
    	public ImageView singleImage;
    	public MyGridView twoColGrid;
    	public MyGridView threeColGrid;
    	
    	public RelativeLayout urlDescL;
    	public ImageView ulrIcon;
    	public TextView ulrDesc;
    	
    	public TextView time;
    	public TextView distance;
    	public ImageButton commentBtn;
    	public ImageButton priseBtn;
    	public ImageButton shareBtn;
    	
    	public TextView numOfComment;
    	public TextView numOfPrise;
    	public TextView numOfShare;
    	
    	public LinearLayout rpl;
    	public View div;
	    public MyGridView priseViewL;
	    public MyListView replayList;
    }    
}
