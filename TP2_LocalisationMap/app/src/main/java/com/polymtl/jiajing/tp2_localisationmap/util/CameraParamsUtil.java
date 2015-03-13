package com.polymtl.jiajing.tp2_localisationmap.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

/**
 * Created by Jiajing on 2015/3/12.
 */
public class CameraParamsUtil {
    private static final String TAG = "CameraParamsUtil";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CameraParamsUtil myCameraParams = null;
    private CameraParamsUtil(){

    }
    public static CameraParamsUtil getInstance(){
        if(myCameraParams == null){
            myCameraParams = new CameraParamsUtil();
            return myCameraParams;
        }
        else{
            return myCameraParams;
        }
    }

    public  Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list){
            if((s.width >= minWidth) && equalRate(s, th)){
                Log.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;//if size hasn't been found, we choose the smallest
        }
        return list.get(i);
    }
    public Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list){
            if((s.width >= minWidth) && equalRate(s, th)){
                Log.i(TAG, "PictureSize : w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;//if size hasn't been found, we choose the smallest
        }
        return list.get(i);
    }

    public boolean equalRate(Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.03)
        {
            return true;
        }
        else{
            return false;
        }
    }

    public  class CameraSizeComparator implements Comparator<Camera.Size>{
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }

    }

    /**
     * print supported preview sizes
     * @param params
     */
    public  void printSupportPreviewSize(Camera.Parameters params){
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        for(int i=0; i< previewSizes.size(); i++){
            Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = "+size.width+" height = "+size.height);
        }

    }

    /**
     * print supported picture sizes
     * @param params
     */
    public  void printSupportPictureSize(Camera.Parameters params){
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        for(int i=0; i< pictureSizes.size(); i++){
            Size size = pictureSizes.get(i);
            Log.i(TAG, "pictureSizes:width = "+ size.width
                    +" height = " + size.height);
        }
    }
    /**
     * print supported Focus Mode
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params){
        List<String> focusModes = params.getSupportedFocusModes();
        for(String mode : focusModes){
            Log.i(TAG, "focusModes--" + mode);
        }
    }
}
