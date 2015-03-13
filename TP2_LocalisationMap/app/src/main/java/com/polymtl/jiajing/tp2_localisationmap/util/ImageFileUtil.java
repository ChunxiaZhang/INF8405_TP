package com.polymtl.jiajing.tp2_localisationmap.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Jiajing on 2015/3/12.
 */
public class ImageFileUtil {
    private static final  String TAG = "ImageFileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static   String storagePath = "";
    private static final String DST_FOLDER_NAME = "TP2_LocalisationMap";

    /**
     * initialisation of the folder path for image files
     * @return
     */
    private static String initPath(){
        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if(!f.exists()){
                f.mkdir();
            }
        }
        return storagePath;
    }

    /**
     * save Bipmap as jpeg files
     * @param b
     */
    public static String saveBitmap(Bitmap b){

        String path = initPath();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date now = new Date();
        String strDate = sdfDate.format(now);

        String jpegName = path + "/" + strDate +".jpg";
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Log.i(TAG, "saveBitmap:Success");
            return jpegName;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "saveBitmap:Failure");
            e.printStackTrace();
        }
        return null;
    }
}
