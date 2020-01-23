package com.example.educher_parent;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String CHILD_UNIQUE_ID = "uniqueIdChild";
    private static final String PREF_NAME = "parent_control";
    private static final String PACKAGE = "package";
    private static final String WHICH_PERSON = "whichperson";
    private static final String CHILD_DEVICE = "childeDevice";
    private static final String NIGHT_MODE = "night";
    private Context context;

    public PrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setPackage(String p){
        editor.putString(PACKAGE,p);
        editor.commit();
    }

    public String getPackage(){
        return sharedPreferences.getString(PACKAGE,null);
    }

    public void setChildUniqueId(String uniqueId){
        editor.putString(CHILD_UNIQUE_ID,uniqueId);
        editor.commit();
    }

    public String getChildUniqueId(){
        return sharedPreferences.getString(CHILD_UNIQUE_ID,null);
    }

    public void setWhichPerson(String whichPerson){
        editor.putString(WHICH_PERSON,whichPerson);
        editor.commit();
    }

    public String getWhichPerson(){
        return sharedPreferences.getString(WHICH_PERSON,null);
    }

    public void setChildDevice(Boolean device){
        editor.putBoolean(CHILD_DEVICE,device);
        editor.commit();
    }

    public Boolean getChildDevice(){
        return sharedPreferences.getBoolean(CHILD_DEVICE,false);
    }

    public void setNightMode(Boolean nightMode){
        editor.putBoolean(NIGHT_MODE,nightMode);
        editor.commit();
    }

    public Boolean getNightMood(){
        return sharedPreferences.getBoolean(NIGHT_MODE,false);
    }
}
