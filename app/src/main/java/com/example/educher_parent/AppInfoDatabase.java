package com.example.educher_parent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AppInfoDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ParentControl";

    private static final String TABLE_CHILD_DEVICE = "childDevice";
    private static final String TABLE_APP_DATA = "appData";

    // Common column names
    private static final String KEY_ID = "id";


    // appData Table - column names
    private static final String PROCESS_NAME = "name";
    private static final String PROCESS_VALUE = "value";

    //childs devies info
    private static final String CHILD_KEY = "Childkey";
    private static final String CHILD_PHONE = "childphone";


    //Table to store the installed apps usage
    private static final String TABLE_APP_USAGE = "appuse";
    private static final String APP_PACKAGE = "appPackage";
    private static final String ALLOW_TIME = "allowtime";
    private static final String USAGE_TIME = "usagetime";
    private static final String SCHEDULE_DAY = "day";

    //  table create statement
    private static final String CREATE_TABLE_APP_DATA = "CREATE TABLE " + TABLE_APP_DATA
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ PROCESS_NAME + " TEXT,"
            + PROCESS_VALUE + " TEXT" + ")";

    private static final String CREATE_TABLE_CHILD_INFO = "CREATE TABLE " + TABLE_CHILD_DEVICE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ CHILD_KEY + " TEXT,"
            + CHILD_PHONE + " TEXT" + ")";

    //app usage table
    //CREATE TABLE appuse (id INTEGER PRIMARY KEY AUTOINCREMENT, appPackage TEXT, allowtime TEXT, usagetime TEXT, day TEXT);
    private static final String CREATE_TABLE_APP_USE = "CREATE TABLE " + TABLE_APP_USAGE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ APP_PACKAGE + " TEXT,"+ALLOW_TIME + " TEXT,"+USAGE_TIME+" TEXT,"+SCHEDULE_DAY+" TEXT"+")";

    private Context context;

    public AppInfoDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        SQLiteDatabase database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP_DATA);
        db.execSQL(CREATE_TABLE_CHILD_INFO);
        db.execSQL(CREATE_TABLE_APP_USE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD_DEVICE);

    }

    public long insertDataInAppDataTable(String name,String value){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROCESS_VALUE,value);
        values.put(PROCESS_NAME,name);

        long res = database.insert(TABLE_APP_DATA,null,values);
        return res;

    }

    public Cursor searchInAppDataTable(String name){
        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor =  database.query(TABLE_APP_DATA,new String[]{KEY_ID,PROCESS_NAME,PROCESS_VALUE},PROCESS_NAME + "=?",new String[]{name},null,null,null);
        return cursor;
    }

    public long insertChildDeviceInfo(String name,String key){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHILD_PHONE,name);
        contentValues.put(CHILD_KEY,key);
        long res = database.insert(TABLE_CHILD_DEVICE,null,contentValues);
        return res;
    }

    public Cursor searchChildDeviceTable(String key){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor =  database.query(TABLE_CHILD_DEVICE,new String[]{KEY_ID,CHILD_KEY,CHILD_PHONE},CHILD_KEY + "=?",new String[]{key},null,null,null);
        return cursor;
    }

    public Cursor fetchDeviceInfo(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor data = database.rawQuery("SELECT * FROM "+TABLE_CHILD_DEVICE,null);
        return data;
    }

    protected void removeAppFromDatabase(String key){
        SQLiteDatabase database = this.getWritableDatabase();
        String[] arg = {key};
        database.delete(TABLE_APP_DATA,PROCESS_NAME+"=?",arg);
    }

    //Update the allow time for the apps that are already present in the database
    public void updateAllowTime(String packageName, String usetime, String day){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALLOW_TIME, usetime);
        values.put(SCHEDULE_DAY,day);
        String[] arg = {packageName};
        db.update(TABLE_APP_USAGE,values,APP_PACKAGE + " = ?",arg);
    }

    //Insert the values in the usage table for a fresh app
    public void insert(String packageName, String allowTime, String day){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APP_PACKAGE,packageName);
        values.put(ALLOW_TIME,allowTime);
        values.put(SCHEDULE_DAY,day);
        values.put(USAGE_TIME,"00");
        long res = sqLiteDatabase.insert(TABLE_APP_USAGE,null,values);
        if (res>0){
            Toast.makeText(context, "App Added", Toast.LENGTH_SHORT).show();
        }

    }


    public void deleteScheduleremove(String packageName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APP_USAGE,APP_PACKAGE +"="+"'"+ packageName+"'",null);

    }

    public void update(String packageName, String usetime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USAGE_TIME, usetime);
        String[] arg = {packageName};
        db.update(TABLE_APP_USAGE,values,APP_PACKAGE + " = ?",arg);
    }



    public Cursor getusageApp(String packageName){

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_APP_USAGE,new String[]{KEY_ID,APP_PACKAGE,ALLOW_TIME,USAGE_TIME,SCHEDULE_DAY},
                APP_PACKAGE + "=?",new String[]{packageName},null,null,null);
        return cursor;
    }


}
