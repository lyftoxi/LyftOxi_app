package com.lyftoxi.lyftoxi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class ImageUtil {


	public String saveToInternalStorage(Context context,Bitmap bitmapImage,String name){
        ContextWrapper cw = new ContextWrapper(context);
       File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
       File mypath=new File(directory,name);

       FileOutputStream fos = null;
       try {           
           fos = new FileOutputStream(mypath);
           bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
       } catch (Exception e) {
             e.printStackTrace();
       } finally {
             try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
       } 
       Log.d("myTag", "ABSOLUTE PATH................"+mypath.getAbsolutePath());
       return mypath.getAbsolutePath();
	}
       
       public Bitmap loadImageFromStorage(String path)
       {

    	   Bitmap b = null;
           try {
               File f=new File(path);
               b = BitmapFactory.decodeStream(new FileInputStream(f));
           } 
           catch (FileNotFoundException e) 
           {
               e.printStackTrace();
           }
           return b;
       }
}
