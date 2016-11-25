package com.deng.mychat.adapter;

import java.util.List;

import com.deng.mychat.R;
import com.deng.mychat.bean.Picture;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.CommonValue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class ImageAdapter extends BaseAdapter{

	private Context context;
	private List<Picture>imageUrls;
	private LayoutInflater listContainer; 
	DisplayImageOptions options;
	private int cols=0;
	
	
	public ImageAdapter(Context c,List<Picture> imageUrls,DisplayImageOptions options,int cols)
	{
		context=c;
		this.imageUrls=imageUrls;
		listContainer = LayoutInflater.from(context);
		this.options=options;
		this.cols=cols;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageUrls.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return imageUrls.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//ImageView imageView;
		GalleryItem imageItem=null;
		if(convertView==null)
		{
			imageItem=new GalleryItem();
			convertView=listContainer.inflate(R.layout.item_gallery_image,null);
			imageItem.image  = (ImageView) convertView.findViewById(R.id.image);
			int pWidth=parent.getWidth();
			int width=pWidth/cols-3;
			int height=width;
			
			LayoutParams params=(LayoutParams) imageItem.image.getLayoutParams();
			params.height=height;
			params.width=width;
			imageItem.image.setLayoutParams(params);
			//imageItem.image.setMaxHeight(height);
			//imageItem.image.setMaxWidth(width);
			//Log.d("Album", ""+imageUrls.get(position));
			((AppActivity)context).imageLoader.displayImage(imageUrls.get(position).smallPicture, imageItem.image, CommonValue.DisplayOptions.default_options);
			convertView.setTag(imageItem); 
		}
		else
		{
			 imageItem = (GalleryItem)convertView.getTag();  
		}
		
		
		return convertView;

	}

	private  class GalleryItem {
		  public ImageView image; 
	}
	
}
