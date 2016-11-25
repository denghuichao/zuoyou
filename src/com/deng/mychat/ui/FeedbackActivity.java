package com.deng.mychat.ui;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.deng.mychat.R;
import com.deng.mychat.config.AppActivity;
import com.deng.mychat.config.XmppConnectionManager;
import com.deng.mychat.im.AChating;
import com.deng.mychat.im.Chating;
import com.deng.mychat.model.IMMessage;
import com.deng.mychat.model.Notice;

public class FeedbackActivity  extends AChating{
	private EditText editText;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_page);
		editText=(EditText)findViewById(R.id.editText);
	}
	

	
	public void buttonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.cancelBtn:
				finish();break;
		case R.id.send:
			String message = editText.getText().toString();
			if ("".equals(message)) {
				Toast.makeText(this, "消息不能为空",
						Toast.LENGTH_SHORT).show();
			} else {

				try {
					//sendMessage(message);
					 editText.setText("");
				} catch (Exception e) {
					showToast("信息发送失败");
					 editText.setText(message);
				}
			}
		}
	}

	@Override
	protected void receiveNotice(Notice notice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void receiveNewMessage(IMMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void refreshMessage(List<IMMessage> messages) {
		// TODO Auto-generated method stub
		
	}
}
