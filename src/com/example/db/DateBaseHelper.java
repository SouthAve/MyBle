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
			+ " losting Integer not null , " // 0��ʾ����������1��ʾ��
			+ " anti_lost Integer not null , " // 0��ʾ����˫�������1��ʾ��
			+ " light_alarm Integer not null , " // 0��ʾ�����ƹⱨ����1��ʾ��
			+ " tones Integer not null , " // 1,2,3�ֱ��ʾΪ����1,����2,����3
			+ " alarm_distance Integer not null , " // ����0-100
			+ " state Integer not null " + ");"; // �豸״̬0��ʾ�����ӣ�1��ʾδ���ӣ�2��ʾ���ڷ�Χ��
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
