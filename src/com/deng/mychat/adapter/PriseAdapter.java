package com.deng.mychat.adapter;

import java.util.List;
import com.deng.mychat.R;
import com.deng.mychat.bean.Prise;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PriseAdapter extends BaseAdapter{

	private Context context;
	private List<Prise>listItems;
	private LayoutInflater listContainer;
	private boolean []hasChecked;
	
	public PriseAdapter(Context context, List<Prise> listItems)
	{
		this.context=context;
		 listContainer = LayoutInflater.from(context); 
		 this.listItems = listItems;   
	     hasChecked = new boolean[getCount()]; 
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MyHolder item=null;
		if(convertView==null)
		{
			item=new MyHolder();
			convertView= listContainer.inflate(R.layout.prise_layout,null);
			item.name=(TextView)convertView.findViewById(R.id.name);
			convertView.setTag(item);
		}
		else
		{
			item=(MyHolder)convertView.getTag();
		}
		
		String userName=listItems.get(position).authorInfo.getName();
 		item.name.setText(userName); 
 		
 		
 		Drawable img_on;
		Resources res = context.getResources();
		img_on = res.getDrawable(R.drawable.prise);
		img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
		if(position==0)item.name.setCompoundDrawables(img_on,null, null , null); //设置图标
		else item.name.setCompoundDrawables(null,null, null , null); //设置图标
		return convertView;
	}
	
	public class MyHolder
	{
		TextView name;
	}

}
