package com.deng.mychat.adapter;

import java.util.ArrayList;
import java.util.List;

import com.deng.mychat.R;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LabelAdapter extends BaseAdapter{
    private Context context;
    private List<String>labelList=new ArrayList<String>();
    
    public LabelAdapter(Context c,List<String>list)
    {
    	context=c;
    	labelList=list;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return labelList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return labelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView textView=null;
		if(convertView==null)
		{
			textView=new TextView(context);
			GridView.LayoutParams mPara=new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,GridView.LayoutParams.WRAP_CONTENT);
        	//textView.setGravity(Gravity.CENTER);
			textView.setLayoutParams(mPara);
        	textView.setMaxLines(1);
        	textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        	//textView.setFocusable(true);
        	//textView.setFocusableInTouchMode(true);
        	textView.setClickable(false);
        	textView.setEllipsize(TruncateAt.MARQUEE);
        	textView.setMarqueeRepeatLimit(-1);
        	//convertView.setTag(textView);
		}
		else
		{
			textView = (TextView)convertView; 
		}
		textView.setText(labelList.get(position));
		return textView;
	}
	
}
