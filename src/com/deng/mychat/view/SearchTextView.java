package com.deng.mychat.view;
import com.deng.mychat.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
/**
 * SearchText
 * �����༭��
 * @author chrishao
 *
 */
public class SearchTextView extends LinearLayout {
	//������ť
	private ImageView ib_searchtext_delete;
	private EditText et_searchtext_search;
	public SearchTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//��һ������Ͳ���һ��view
		View view = LayoutInflater.from(context).inflate(R.layout.searchtext, null);
		//�ѻ�õ�view���ص�����ؼ���
		addView(view);
		//��������ť�Ӳ����ļ����ҵ�
		ib_searchtext_delete = (ImageView) view.findViewById(R.id.ib_searchtext_delete);
		et_searchtext_search = (EditText) view.findViewById(R.id.et_searchtext_search);
		//��ɾ����ť��ӵ���¼�
		ib_searchtext_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				et_searchtext_search.setText("");
			}
		});
		//���༭������ı��ı��¼�
		et_searchtext_search.addTextChangedListener(new MyTextWatcher());
	}
	
	//�ı��۲���
	private class MyTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		//���ı��ı�ʱ��Ĳ���
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			//����༭�����ı��ĳ��ȴ���0����ʾɾ����ť������ʾ
			if(s.length() > 0){
				ib_searchtext_delete.setVisibility(View.VISIBLE);
			}else{
				ib_searchtext_delete.setVisibility(View.GONE);
			}
		}
		
	}
}

