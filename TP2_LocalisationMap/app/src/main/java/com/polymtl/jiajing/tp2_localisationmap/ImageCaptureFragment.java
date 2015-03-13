package com.polymtl.jiajing.tp2_localisationmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.hardware.Camera;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.polymtl.jiajing.tp2_localisationmap.util.CameraOperations;
import com.polymtl.jiajing.tp2_localisationmap.util.ContextDisplayUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jiajing on 2015/3/11.
 */
public class ImageCaptureFragment extends DialogFragment{
    private static String TAG = "ImageCaptureFragment";
    private int idMaker = -1;
    private Camera myCamera;
    private SurfaceView surfaceViewImgCapture;
    private SurfaceHolder surfaceHolder;
    boolean previewing = false;
    private ImageButton btnImgCapture;
    private ImageButton btnImgSave;
    private ImageButton btnImgCancel;
    private BtnListeners btnListeners;
    private float previewRate = -1f;

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//            Log.i(TAG,"Surface Created");
//            myCamera = Camera.open();
//
//            try {
//                myCamera.setDisplayOrientation(90);
//                myCamera.setPreviewDisplay(holder);
//            } catch (IOException e) {
//                myCamera.release();
//                myCamera = null;
//                previewing = false;
//                e.printStackTrace();
//            }
            CameraOperations.getInstance().doOpenCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            if(previewing){
//                myCamera.stopPreview();
//                previewing = false;
//            }
//            Camera.Parameters parameters = myCamera.getParameters();
//            parameters.setPictureFormat(PixelFormat.JPEG);
//
//            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
//
//            parameters.setPreviewSize(640,480);
//            parameters.setFocusMode("auto");
//            parameters.setPictureSize(640,480);
//
//            surfaceViewImgCapture.setLayoutParams(new LinearLayout.LayoutParams(640,480));
//
//            myCamera.startPreview();
//            previewing = true;
            CameraOperations.getInstance().doStartPreview(surfaceHolder,previewRate);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            CameraOperations.getInstance().doStopCamera();
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_imagecapture, null);

        //get id maker
        idMaker = getArguments().getInt("idMaker");

        surfaceViewImgCapture = (SurfaceView) view.findViewById(R.id.surfaceViewImgCap);
        surfaceHolder = surfaceViewImgCapture.getHolder();
        surfaceHolder.addCallback(surfaceCallback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        ViewGroup.LayoutParams params = surfaceViewImgCapture.getLayoutParams();
        Point p = ContextDisplayUtil.getScreenMetrics(getActivity());
        params.width = (int)(p.x*0.9);
        params.height = (int)(p.y*0.9);
        previewRate = ContextDisplayUtil.getScreenRate(getActivity());
        surfaceViewImgCapture.setLayoutParams(params);

        btnImgCapture = (ImageButton) view.findViewById(R.id.btn_imgcapture);
        ViewGroup.LayoutParams p2 = btnImgCapture.getLayoutParams();
        p2.width = ContextDisplayUtil.dip2px(getActivity(), 50);
        p2.height = ContextDisplayUtil.dip2px(getActivity(), 50);
        btnImgCapture.setLayoutParams(p2);

        btnImgSave = (ImageButton) view.findViewById(R.id.btn_imgsave);
        btnImgSave.setLayoutParams(p2);
        btnImgCancel = (ImageButton) view.findViewById(R.id.btn_imgcancel);
        btnImgCancel.setLayoutParams(p2);

        btnListeners = new BtnListeners();
        btnImgCapture.setOnClickListener(btnListeners);
        btnImgSave.setOnClickListener(btnListeners);
        btnImgCancel.setOnClickListener(btnListeners);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    private class BtnListeners implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(v.getId()){
                case R.id.btn_imgcapture:
                    CameraOperations.getInstance().doTakePicture();
                    btnImgSave.setVisibility(View.VISIBLE);
                    btnImgSave.setClickable(true);
                    btnImgCancel.setVisibility(View.VISIBLE);
                    btnImgSave.setClickable(true);
                    btnImgCapture.setVisibility(View.INVISIBLE);
                    btnImgCapture.setClickable(false);
                    break;
                case R.id.btn_imgsave:
                    String picturePath = CameraOperations.getInstance().doSavePicture();
                    PictureTakenListener listener = (PictureTakenListener) getActivity();
                    listener.pictureTaken(idMaker, picturePath);
                    dismiss();
                    break;
                case R.id.btn_imgcancel:
                    CameraOperations.getInstance().doRestartPreview();
                    btnImgCapture.setVisibility(View.VISIBLE);
                    btnImgCapture.setClickable(true);
                    btnImgSave.setVisibility(View.GONE);
                    btnImgSave.setClickable(false);
                    btnImgCancel.setVisibility(View.GONE);
                    btnImgSave.setClickable(false);
                    break;
                default:break;
            }
        }
    }

    public interface PictureTakenListener {
        void pictureTaken(int idMaker, String picturePath);
    }
}
