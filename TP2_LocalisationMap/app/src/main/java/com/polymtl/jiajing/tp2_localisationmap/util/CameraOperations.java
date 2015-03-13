package com.polymtl.jiajing.tp2_localisationmap.util;

import java.io.IOException;
import java.util.List;

import com.polymtl.jiajing.tp2_localisationmap.util.CameraParamsUtil;
import com.polymtl.jiajing.tp2_localisationmap.util.ImageFileUtil;
import com.polymtl.jiajing.tp2_localisationmap.util.ImageUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Jiajing on 2015/3/12.
 */
public class CameraOperations {
    private static final String TAG = "CameraOperations";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraOperations mCameraInterface;
    private Bitmap bitmapPicture = null;

    private CameraOperations(){
    }

    public static synchronized CameraOperations getInstance(){
        if(mCameraInterface == null){
            mCameraInterface = new CameraOperations();
        }
        return mCameraInterface;
    }
    /**
     * Open camera
     */
    public void doOpenCamera(){
        Log.i(TAG, "Camera open....");
        mCamera = Camera.open();
        Log.i(TAG, "Camera opened over....");
    }
    /**
     * start preview
     * @param holder
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate){
        Log.i(TAG, "doStartPreview...");
        if(isPreviewing){
            mCamera.stopPreview();
            return;
        }
        if(mCamera != null){

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//setup jpeg as pictures format
            CameraParamsUtil.getInstance().printSupportPictureSize(mParams);
            CameraParamsUtil.getInstance().printSupportPreviewSize(mParams);
            //setup PreviewSize and PictureSize
            Size pictureSize = CameraParamsUtil.getInstance().getPropPictureSize(
                    mParams.getSupportedPictureSizes(),previewRate, 800);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Size previewSize = CameraParamsUtil.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);

            mCamera.setDisplayOrientation(90);

            CameraParamsUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if(focusModes.contains("continuous-video")){
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();//start preview
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters(); //get again camera params for print
            Log.i(TAG, "Final Setup:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, "Final Setup:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    /**
     * Restart Preview
     */
    public void doRestartPreview(){
        if(mCamera != null && !isPreviewing){
            mCamera.startPreview();
            isPreviewing = true;
        }
    }

    /**
     * stop preview and release Camera
     */
    public void doStopCamera(){
        if(null != mCamera)
        {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * take picture
     */
    public void doTakePicture(){
        if(isPreviewing && (mCamera != null)){
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    /*three callbacks for shutter effect, take pictures*/
    ShutterCallback mShutterCallback = new ShutterCallback()
    {
        // callback for shutter effect
        public void onShutter() {
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mRawCallback = new PictureCallback()
    {
        // callback for picture before compression
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback()
    {
        // callback for jpeg pictures taken
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if(null != data){
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//transfer data to bipmap
                mCamera.stopPreview();
                isPreviewing = false;
            }
            //save pictures
            if(null != b)
            {
                //rotate pictures
                bitmapPicture = ImageUtil.getRotateBitmap(b, 90.0f);

            }
            //start preview again
            mCamera.stopPreview();
            isPreviewing = false;
        }
    };

    public String doSavePicture(){
        if(bitmapPicture!= null){
            String pictureName = ImageFileUtil.saveBitmap(bitmapPicture);
            if (pictureName != null){
                return pictureName;
            }
        }
        return null;
    }
}
