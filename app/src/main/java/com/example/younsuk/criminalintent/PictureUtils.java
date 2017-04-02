package com.example.younsuk.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by Younsuk on 9/6/2015.
 */
public class PictureUtils {
    //----------------------------------------------------------------------------------------------
    public static Bitmap getScaledBitMap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        Bitmap bitmap = getScaledBitMap(path, size.x, size.y);
        return bitmap;
    }
    //----------------------------------------------------------------------------------------------
    public static Bitmap getScaledBitMap(String path, int destWidth, int destHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth){
            if(srcWidth > srcHeight) //landscape picture
                inSampleSize = Math.round(srcHeight / destHeight);
            else //(srcWidth < srcHeight) portrait picture
                inSampleSize = Math.round(srcWidth / destWidth);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }
    //----------------------------------------------------------------------------------------------
}
