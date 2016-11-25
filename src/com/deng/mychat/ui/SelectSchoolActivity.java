package com.deng.mychat.ui;
import com.deng.mychat.R;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectSchoolActivity extends ListActivity{
	String[]schools;//=res.getStringArray(R.array.school_list);
	public void onCreate(Bundle savedInstanceState)
	{
			super.onCreate(savedInstanceState);
			this.setTitle("��ѡ��ѧУ");
			// this.getListView().setBackgroundColor(0x000);
			Resources res=getResources();
			schools=res.getStringArray(R.array.school_list);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,schools);

			setListAdapter(adapter);
	}
	
	  protected void onListItemClick(ListView l, View v, int position, long id)
	  {
	        String selectedSchool=schools[position];
	        Intent intent = new Intent();  
            intent.putExtra("result",  selectedSchool);  
            setResult(RESULT_OK, intent);//���ز���  
            this.finish();//�ر�Activity 
	  }

}
