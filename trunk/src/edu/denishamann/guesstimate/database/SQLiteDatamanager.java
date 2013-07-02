package edu.denishamann.guesstimate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatamanager extends SQLiteOpenHelper{

	private static final int DBVERSION = 1;

	public SQLiteDatamanager(Context context) { 
	      super(context, "guesstimate.db", null, DBVERSION); 
	} 
	 
	public void onCreate(SQLiteDatabase db) { 
	      db.execSQL("CREATE TABLE logging (trackid NUMERIC, timestamp NUMERIC, lat NUMERIC, long NUMERIC);"); 
	      db.execSQL("CREATE TABLE guesstimate (id INTEGER PRIMARY KEY, lat NUMERIC, long NUMERIC, descr TEXT);");
	      db.execSQL("CREATE TABLE highscore (name TEXT, score NUMERIC, difficulty NUMERIC);");
	} 
	 
	public void onUpgrade(SQLiteDatabase db,  
	              int oldVersion, int newVersion) { 
	      db.execSQL("DROP TABLE logging"); 
	      db.execSQL("DROP TABLE guesstimate"); 
	      db.execSQL("DROP TABLE highscore"); 
	      this.onCreate(db); 
	} 
	
		
	
}
