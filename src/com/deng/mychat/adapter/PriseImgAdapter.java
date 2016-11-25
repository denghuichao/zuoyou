package com.deng.mychat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.deng.mychat.R;
import com.deng.mychat.bean.Prise;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.util.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class PriseImgAdapter extends BaseAdapter {
	private LayoutInflater inflater; // 视图容器
	private int selectedPosition = -1;// 选中的位�?
	private boolean shape;
	private boolean showMore;
	private List<Prise> priseList;
	private Context mContext;
	private int cols=0;
	
	DisplayImageOptions touxiang_options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.avatar_placeholder)
		.showImageForEmptyUri(R.drawable.avatar_placeholder)
		.showImageOnFail(R.drawable.avatar_placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new CircleBitmapDisplayer())
		.build();
	
	boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public PriseImgAdapter(Context context,List<Prise>list,boolean showMore,int cols) {
		inflater = LayoutInflater.from(context);
		this.mContext=context;
		priseList=list;
		this.showMore=showMore;
		this.cols=cols;
	}


	public int getCount() {
		return priseList.size();
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

			
			convertView = inflater.inflate(R.layout.prise_img_item_layout,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			
			int pWidth=parent.getWidth();
			int width=pWidth/cols-1;
			int height=width;
			
			LayoutParams params=(LayoutParams) holder.image.getLayoutParams();
			params.height=height;
			params.width=width;
			holder.image.setLayoutParams(params);
			
			holder.heart=(ImageView) convertView
					.findViewById(R.id.heart);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(!showMore)
		{
			if(position<7){
				((AppActivity)mContext).imageLoader.
						displayImage(priseList.get(position).
						authorInfo.txPath.smallPicture, holder.image, touxiang_options);
				holder.heart.setVisibility(View.VISIBLE);
				}
			else {holder.image.setBackgroundResource(R.drawable.more_img);
				holder.heart.setVisibility(View.GONE);
			}	
		}
		else
		{
			((AppActivity)mContext).imageLoader.
			displayImage(priseList.get(position).
			authorInfo.txPath.smallPicture, holder.image, touxiang_options);
			holder.heart.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
		public ImageView heart;
	}

}