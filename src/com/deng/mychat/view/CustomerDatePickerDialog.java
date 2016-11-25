package com.deng.mychat.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

public class CustomerDatePickerDialog extends DatePickerDialog {  
	private boolean showDay;
    public CustomerDatePickerDialog(Context context,  
            OnDateSetListener callBack, int year, int monthOfYear, 
            int dayOfMonth,boolean showDay) {  
        super(context, callBack, year, monthOfYear, dayOfMonth);  
        this.showDay=showDay;
    }  
  
    @Override  
    public void onDateChanged(DatePicker view, int year, int month, int day) {  
        super.onDateChanged(view, year, month, day);  
        setTitle(year + "Äê" + (month + 1) + "ÔÂ"+(showDay?day+"ÈÕ":""));  
    }  
}  
