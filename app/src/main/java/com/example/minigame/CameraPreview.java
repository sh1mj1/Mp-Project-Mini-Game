package com.example.minigame;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        try{
            if (mCamera == null) {
//                mCamera.setDisplayOrientation(180);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        }catch (IOException e){
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamea(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exit
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a a non-existent preview
        }

        setCamera(camera);
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void setCamera(Camera camera) {
        mCamera = camera;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        refreshCamea(mCamera);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.d("CameraPreview", "surfaceDestroyed Start");
    }

}