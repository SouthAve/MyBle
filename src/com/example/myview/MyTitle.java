package com.example.myview;

import com.example.myble.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MyTitle extends LinearLayout {

	public MyTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.title, this);
		Button titleBack = (Button) findViewById(R.id.title_back);
		Button titleEdit = (Button) findViewById(R.id.title_edit);
		titleBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			((Activity)getContext()).finish();	
			}
		});
	}

}
