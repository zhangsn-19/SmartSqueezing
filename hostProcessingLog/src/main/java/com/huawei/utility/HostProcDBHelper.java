package com.huawei.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HostProcDBHelper extends SQLiteOpenHelper {
	public static final String sqlTableName = "talbe_file_list";
	public static final String sqlColName = "file_name";

	public HostProcDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public HostProcDBHelper(Context context, String name) {
		super(context, name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v("lance", "数据库创建文件名历史存储表");
		db.execSQL("create table talbe_file_list(_id integer primary key autoincrement, file_name varchar UNIQUE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("---------onUpgrade called----------------");
	}

	public void insertRecord(SQLiteDatabase db, String fileName) {
		ContentValues values = new ContentValues();
		values.put(sqlColName, fileName);
		db.insert(sqlTableName, null, values);
	}

	public String getLatestRecord(SQLiteDatabase db) {
		Cursor cursor = db.query(sqlTableName, new String[] { sqlColName },
				null, null, null, null, "_id " + "DESC");
		String name = "";
		while (cursor.moveToNext()) {
			name = cursor.getString(0);
			break;
		}
		cursor.close();
		return name;
	}

	public static String getLatestRecord(Context main_act) {
		String name = "";
		HostProcDBHelper dbHelper = new HostProcDBHelper(main_act, sqlTableName);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		name = dbHelper.getLatestRecord(db);
		db.close();
		return name;
	}

	public static void insertRecord(Context main_act, String fileName) {
		HostProcDBHelper dbHelper = new HostProcDBHelper(main_act, sqlTableName);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		dbHelper.insertRecord(db, fileName);
		db.close();
	}
}