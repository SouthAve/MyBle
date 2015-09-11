package com.example.myble;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	EditText username;
	EditText password;
	CheckBox checkBox;
	Button login;
	SharedPreferences sp;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();

	}

	private void rememberthepw() {
		// TODO Auto-generated method stub
		String un = username.getText().toString();
		String pass = password.getText().toString();
		if (un != null && pass != null) {
			if (checkBox.isChecked()) {
				editor.putString("account", un);
				editor.putString("password", pass);
				editor.putBoolean("remmember_password", checkBox.isChecked());
			} else {
				editor.clear();
			}
			editor.commit();
			finish();
		} else {
			Toast.makeText(this, "帐号或密码不能空", Toast.LENGTH_SHORT).show();
		}
	}

	private void init() {
		// TODO Auto-generated method stub

		// 第一种获得sharedpreferences的方法,Activity的
		//sp = getPreferences(MODE_PRIVATE);
		// 第二种，context
		//sp = getSharedPreferences("mybleSP", 0);
		// 第三种，preferenceManager
		 sp=PreferenceManager.getDefaultSharedPreferences(this);

		editor = sp.edit();

		username = (EditText) findViewById(R.id.username_ed);
		password = (EditText) findViewById(R.id.pw_ed);
		checkBox = (CheckBox) findViewById(R.id.remember_pass);
		login = (Button) findViewById(R.id.confirm_bt);
		boolean isRemember = sp.getBoolean("remmember_password", false);
		if (isRemember) {
			String account = sp.getString("account", "");
			String pw = sp.getString("password", "");
			username.setText(account);
			password.setText(pw);
			checkBox.setChecked(true);
		}
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rememberthepw();

			}
		});

	}
}
