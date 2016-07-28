package com.lyftoxi.lyftoxi.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class PasswordUtil {

    public static String encryptPassword(String passwordToHash)
    {
        String encoderPassword = null;
        byte[] salt = new byte[0];
        try {
            String encryptedPassword = getSecurePassword(passwordToHash);
            if(null!=encryptedPassword) {
                encoderPassword = URLEncoder.encode(encryptedPassword, "UTF-8");
            }
        }  catch (UnsupportedEncodingException e) {
            Log.e("lyftoxi.debug","password encryption error  UNSUPPORTED ENCODING"+e.getMessage());
        }catch (NoSuchAlgorithmException e) {
            Log.e("lyftoxi.debug","password encryption error NO SNUCH algorithm "+e.getMessage());
        }
        return encoderPassword;


    }

    private static String getSecurePassword(String passwordToHash) throws NoSuchAlgorithmException
    {

        String encryptedPassword = null;
        MessageDigest md = null;
        md = MessageDigest.getInstance("MD5");
        md.update(passwordToHash.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        encryptedPassword = sb.toString();
        return encryptedPassword;
    }

}
