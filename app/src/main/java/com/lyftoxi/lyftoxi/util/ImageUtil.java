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

    public Bitmap getProfilePic(Context context)
    {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File f=new File(directory,"user_avatar.jpg");
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
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


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
