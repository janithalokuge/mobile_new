package com.example.janitha.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * Created by Siri on 11/2/2016.
 */

public class AppData {

    public static String HOME_LOCATOIN = "com.example.janitha.myapplication.HOME_LOCATION"; //returns LatLng object
    public static String HOME_LOCATOIN_FENCE_RADIUS = "com.example.janitha.myapplication.HOME_LOCATOIN_FENCE_RADIUS";

    public static String WORK_LOCATOIN = "com.example.janitha.myapplication.WORK_LOCATION"; //returns LatLng object
    public static String WORK_LOCATOIN_FENCE_RADIUS = "com.example.janitha.myapplication.WORK_LOCATOIN_FENCE_RADIUS";

    //to store the list of days where the user stays at home/work
    //returns ArrayList of customized Date Objects
    public static String USER_SCHEDULE = "com.example.janitha.myapplication.USER_SCHEDULE";

    //save WiFi, Mobile data connectivity preference at home/work
    //save which apps open when
    //returns ArrayList of customized user_pref_Objects
    public static String USER_PREFERENCE_LIST = "com.example.janitha.myapplication.USER_PREFERENCE_LIST";

    public static boolean saveData(Activity activity, String variableName, Object obj){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String json = new Gson().toJson(obj);
        editor.putString(variableName,json);
        return editor.commit();
    }

    public static Object getData(Activity activity, String variableName, Class<?> objectClass){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String json = sharedPref.getString(variableName, null);
        Object obj = new Gson().fromJson(json, objectClass);
        return obj;
    }

}
