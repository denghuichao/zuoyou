package com.deng.mychat.adapter;

import java.util.Date;
import java.util.List;

import tools.StringUtils;

import com.deng.mychat.R;
import com.deng.mychat.bean.LocationPoint;
import com.deng.mychat.bean.News;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.bean.UserInfo;
import com.deng.mychat.config.ApiClient;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.deng.mychat.config.NewsManager;
import com.deng.mychat.config.WCApplication;
import com.deng.mychat.config.ApiClient.ClientCallback;
import com.deng.mychat.ui.NewsDetilActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ZujiAdapter extends BaseAdapter{

    private Context context;                        //运行上下文   
    private WCApplication appContext;
    private List<News> listItems;    //动态信息集合   
    private LayoutInflater listContainer;           //视图容器   
    private boolean[] hasChecked;                   //新闻选中状态  
    private int checkPos=-1;
   // private ListView listView;
	
    public ZujiAdapter(Context context,WCApplication appContext, List<News> listItems) {   
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

    
    public View getView(final int position, View cv, final ViewGroup parent) {   
   	 final News news= listItems.get(position);
   	 final MyHolder holder;
   	 
   	 
   	 if(cv==null)
   	 {
   		holder=new MyHolder();
   		
   		cv=listContainer.inflate(R.layout.zuji_item_layout, null);
   		
   		holder.nourlContent=(RelativeLayout)cv.findViewById(R.id.nourl_content);
   		holder.urlContent=(RelativeLayout)cv.findViewById(R.id.url_content);

   		holder.contentText=(TextView)cv.findViewById(R.id.contentText);
   		holder.numOfImgs=(TextView)cv.findViewById(R.id.numOfImgs);
    	holder.distance=(TextView)cv.findViewById(R.id.distance);
    	
   		holder.month=(TextView)cv.findViewById(R.id.month);
   		holder.dayOfMonth=(TextView)cv.findViewById(R.id.dayOfMonth);
    	
    	holder.imgLl=(FrameLayout)cv.findViewById(R.id.img_ll);
   		holder.singleImage=(ImageView)cv.findViewById(R.id.singleImage);
   		
   		holder.twoImgll=(RelativeLayout)cv.findViewById(R.id.twoImgll);
   		holder.threeImgll=(RelativeLayout)cv.findViewById(R.id.threeImgll);
   		holder.fourImgll=(RelativeLayout)cv.findViewById(R.id.fourImgll);
    	
   		holder.oneOfTwo=(ImageView)cv.findViewById(R.id.oneOfTwo);
   		holder.twoOfTwo=(ImageView)cv.findViewById(R.id.twoOfTwo);
   		
   		holder.oneOfThree=(ImageView)cv.findViewById(R.id.oneOfThree);
   		holder.twoOfThree=(ImageView)cv.findViewById(R.id.twoOfThree);
   		holder.threeOfThree=(ImageView)cv.findViewById(R.id.threeOfThree);
   		
   		holder.oneOfFour=(ImageView)cv.findViewById(R.id.oneOfFour);
   		holder.twoOfFour=(ImageView)cv.findViewById(R.id.twoOfFour);
   		holder.threeOfFour=(ImageView)cv.findViewById(R.id.threeOfFour);
   		holder.fourOfFour=(ImageView)cv.findViewById(R.id.fourOfFour);
   		
   		holder.urlIcon=(ImageView)cv.findViewById(R.id.ulrIcon);
   		holder.urlContentTv=(TextView)cv.findViewById(R.id.url_contentText);
   		holder.urlDesc=(TextView)cv.findViewById(R.id.ulrDesc);
   		holder.urlDistance=(TextView)cv.findViewById(R.id.url_distance);
   		

    	cv.setTag(holder);   
     }else {   
         holder = (MyHolder)cv.getTag();   
     }    
   	
   	 
   	UserInfo user=news.getUser(); 
   	
   	String distanceLabel="5km";
   	long dis=(long) LocationPoint.distanceBetween(appContext.getLoginLocation(), new LocationPoint(news.latitude, news.longitude));
	if(dis<1000)distanceLabel=(int)dis+"m";
	else if(dis<1000000)distanceLabel=((int)dis)/1000+"km";
	else distanceLabel="1000km以外";
	 
	
	 Date rTime=news.releaseTime;
	 if(rTime==null)rTime=new Date();
	    
	 Date now=new Date();
		
	    
	 if(now.getDate()==rTime.getDate() && now.getMonth()==rTime.getMonth() && now.getYear()==rTime.getYear())
	{
			holder.dayOfMonth.setText("今天");
			holder.month.setText("");
	}
	else
	{
			  int day=	rTime.getDate();
			  holder.dayOfMonth.setText((day>=10?day+"":"0"+day));
			  holder.month.setText((rTime.getMonth()+1)+"月");
	}
	
   	if(news.shuoType!=1)
   	{
   		holder.nourlContent.setVisibility(View.VISIBLE);
   		holder.urlContent.setVisibility(View.GONE);
	    if(StringUtils.empty(news.getContextText()))holder.contentText.setVisibility(View.GONE);
	    else 
	    {
	    	holder.contentText.setVisibility(View.VISIBLE);
	    	holder.contentText.setText(news.getContextText());
	    }
	    
	    final List<Picture>pics=news.getPictures();
	    if(pics.size()==0)
	    {
	    	RelativeLayout.LayoutParams p=(LayoutParams) holder.contentText.getLayoutParams();
	    	p.height=RelativeLayout.LayoutParams.WRAP_CONTENT;
	    	holder.contentText.setLayoutParams(p);
	    }
	    
	    if(pics.size()==0)
	    	{holder.imgLl.setVisibility(View.GONE);holder.numOfImgs.setVisibility(View.GONE);}
	    else
	    {holder.imgLl.setVisibility(View.VISIBLE);holder.numOfImgs.setVisibility(View.VISIBLE);}
	    holder.numOfImgs.setText((pics.size()==0?"":"共"+pics.size()+"张"));
	    
	    
	    int numOfImgs=pics==null?0:pics.size();
	    if(numOfImgs==1)
	    {
	    	holder.singleImage.setVisibility(View.VISIBLE);
	    	holder.twoImgll.setVisibility(View.GONE);
	    	holder.threeImgll.setVisibility(View.GONE);
	    	holder.fourImgll.setVisibility(View.GONE);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(0).smallPicture, holder.singleImage, CommonValue.DisplayOptions.default_options);
	
	    }
	    else if(numOfImgs==2)
	    {
	    	holder.singleImage.setVisibility(View.GONE);
	    	holder.twoImgll.setVisibility(View.VISIBLE);
	    	holder.threeImgll.setVisibility(View.GONE);
	    	holder.fourImgll.setVisibility(View.GONE);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(0).smallPicture, holder.oneOfTwo, CommonValue.DisplayOptions.default_options);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(1).smallPicture, holder.twoOfTwo, CommonValue.DisplayOptions.default_options);
	    }
	    else if(numOfImgs==3)
	    {
	    	holder.singleImage.setVisibility(View.GONE);
	    	holder.twoImgll.setVisibility(View.GONE);
	    	holder.threeImgll.setVisibility(View.VISIBLE);
	    	holder.fourImgll.setVisibility(View.GONE);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(0).smallPicture, holder.oneOfThree, CommonValue.DisplayOptions.default_options);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(1).smallPicture, holder.twoOfThree, CommonValue.DisplayOptions.default_options);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(2).smallPicture, holder.threeOfThree, CommonValue.DisplayOptions.default_options);
	    }
	    else if(numOfImgs>=4)
	    {
	    	holder.singleImage.setVisibility(View.GONE);
	    	holder.twoImgll.setVisibility(View.GONE);
	    	holder.threeImgll.setVisibility(View.GONE);
	    	holder.fourImgll.setVisibility(View.VISIBLE);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(0).smallPicture, holder.oneOfFour, CommonValue.DisplayOptions.default_options);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(1).smallPicture, holder.twoOfFour, CommonValue.DisplayOptions.default_options);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(2).smallPicture, holder.threeOfFour, CommonValue.DisplayOptions.default_options);
	    	((AppActivity)context).imageLoader.displayImage(pics.get(3).smallPicture, holder.fourOfFour, CommonValue.DisplayOptions.default_options);
	    }
	    holder.distance.setText(distanceLabel);     
   	}
   	else
   	{
   		holder.nourlContent.setVisibility(View.GONE);
   		holder.urlContent.setVisibility(View.VISIBLE);
   		((AppActivity)context).imageLoader.displayImage(news.descImg, holder.urlIcon, CommonValue.DisplayOptions.default_options);
   		holder.urlDistance.setText(distanceLabel);
   		holder.urlDesc.setText(news.descText);
   	  if(StringUtils.empty(news.getContextText()))holder.urlContentTv.setVisibility(View.GONE);
	    else 
	    {
	    	holder.urlContentTv.setVisibility(View.VISIBLE);
	    	holder.urlContentTv.setText(news.getContextText());
	    }
   	}

		        
	cv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(context,NewsDetilActivity.class);
				intent.putExtra("news", news);
				context.startActivity(intent);
			}

		});
		 
		 cv.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				new PopupWindows(context,parent,position);
				return false;
			}
			 
		 });
   
		 
    return cv;  
   }   

    class MyHolder
    {
    	public RelativeLayout nourlContent;
    	public RelativeLayout urlContent;
    	
    	public TextView dayOfMonth;
    	public TextView month;
    	public TextView contentText;
    	
    	public FrameLayout imgLl;
    	public ImageView singleImage;
    	public RelativeLayout twoImgll;
    	public RelativeLayout threeImgll;
    	public RelativeLayout fourImgll;
    	
    	public ImageView oneOfTwo;
    	public ImageView twoOfTwo;
    	
    	public ImageView oneOfThree;
    	public ImageView twoOfThree;
    	public ImageView threeOfThree;
    	
    	public ImageView oneOfFour;
    	public ImageView twoOfFour;
    	public ImageView threeOfFour;
    	public ImageView fourOfFour;

    	public TextView numOfImgs;
    	public TextView distance;
    	
    	public ImageView urlIcon;
    	public TextView urlContentTv;
    	public TextView urlDesc;
    	public TextView urlDistance;

    }    
    
    public class PopupWindows extends PopupWindow {

		public PopupWindows(final Context mContext, View parent,final int pos) {

			View view = View
					.inflate(mContext, R.layout.op_shuoshuo_layout, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_del);

			Button bt4 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			
			bt1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					ApiClient.deleteANews(appContext.getLoginApiKey(), listItems.get(pos).shuoshuoId, new  ClientCallback(){

						@Override
						public void onSuccess(Object data) {
							// TODO Auto-generated method stub
							String id=listItems.get(pos).shuoshuoId;
							listItems.remove(pos);
							notifyDataSetChanged();
							NewsManager.getInstance(mContext).delANews(id);
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
					dismiss();
				}			
			});
			
			
			
			
			bt4.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dismiss();
				}	
			});

		}
	}


    
}
