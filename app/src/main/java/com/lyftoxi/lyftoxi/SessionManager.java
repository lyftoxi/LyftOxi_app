package com.lyftoxi.lyftoxi;

import java.util.HashMap;
 
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
 
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
     
    // Editor for Shared preferences
    Editor editor;
     
    // Context
    Context _context;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;
     
    // Sharedpref file name
    private static final String PREF_NAME = "GogPref";
     
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
     
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Mbile (make variable public to access from outside)
    public static final String KEY_MOBILE = "mobile";

    // Email (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // ID (make variable public to access from outside)
    public static final String KEY_ID = "id";

    // SEX (make variable public to access from outside)
    public static final String KEY_SEX = "sex";

    //USED to see if user has seen notification or NOT
    public static final String KEY_PROMO_DIALOG_CODE = "prompDialogCode";
     
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
     
    /**
     * Create login session
     * */
    public void createLoginSession(String id, String name, String mobile, String email, String sex){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        //Internal ID
        editor.putString(KEY_ID, id);
         
        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
         
        // Storing email in pref
        editor.putString(KEY_MOBILE, mobile);

        // Storing email in pref
        editor.putString(KEY_SEX, sex);
         
        // commit changes
        editor.commit();
    }   
     
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
            // Staring Login Activity
            _context.startActivity(i);
        }
         
    }
     
     
     
    /**
     * Get stored session data
     * */
    public UserInfo getUserDetails(){
        UserInfo userInfo = new UserInfo();
        // user id
        userInfo.setUID(pref.getString(KEY_ID, null));
        // user name
        userInfo.setName(pref.getString(KEY_NAME, null));
         
        // user mobile id
        userInfo.setPhNo(pref.getString(KEY_MOBILE, null));

        // user email id
        userInfo.setEmail(pref.getString(KEY_EMAIL, null));

        // user email sex
        userInfo.setSex(pref.getString(KEY_SEX, null));
         
        // return user
        return userInfo;
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
         
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         
        // Staring Login Activity
        _context.startActivity(i);
    }
     
    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }


    public String getPromoDialogCode()
    {
        return pref.getString(KEY_PROMO_DIALOG_CODE, null);
    }

    public void setPromoDialogCode(String promoDialogCode)
    {
        editor.putString(KEY_PROMO_DIALOG_CODE, promoDialogCode);
        editor.commit();
    }


}