package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DateBaseHelper extends SQLiteOpenHelper {
	Context mContext;
	public static final String CREATE_LOST_DEVICE = "create table if not exists detail_record(id integer primary key autoincrement , "
			+ " name text , "
			+ " mac text  , "
			+ " image_url text , "
			+ " location text  , "
			+ " address text  , "
			+ " type Integer  "
			+ "category_id integer" + ");";
	public static final String CREATE_DEVICE_SETTINGS = "create table if not exists device_settings(id integer primary key autoincrement ,"
			+ " name text ,"
			+ " mac text not null ,"
			+ " image_url text ,"
			+ " losting Integer not null , " // 0表示开启防丢，1表示关
			+ " anti_lost Integer not null , " // 0表示开启双向防丢，1表示关
			+ " light_alarm Integer not null , " // 0表示开启灯光报警，1表示关
			+ " tones Integer not null , " // 1,2,3分别表示为铃声1,铃声2,铃声3
			+ " alarm_distance Integer not null , " // 距离0-100
			+ " state Integer not null " + ");"; // 设备状态0表示已连接，1表示未连接，2表示不在范围内
	public static final String CREATE_CATEGORY = "create table if not exists category("
			+ "id interger primary key autocrement"
			+ "category_name text"
			+ "category_code interger)";

	public DateBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_LOST_DEVICE);
		db.execSQL(CREATE_DEVICE_SETTINGS);
		Toast.makeText(mContext, "db create ok", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists detail_record");
		db.execSQL("drop table if exists device_settings");
		switch (oldVersion) {
		case 1:
			db.execSQL(CREATE_CATEGORY);
		case 2:
			db.execSQL("alter table detail_record add column category integer ");
		}
		onCreate(db);
	}

}
