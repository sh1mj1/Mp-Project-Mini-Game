package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class CaptureActivity extends AppCompatActivity {

    private final String TAG = "CaptureActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView captureedImageHolder;
    public Handler mHandler = new Handler();
    private Bitmap bitmap;


    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.setDisplayOrientation(180);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h);


            if (bitmap == null) {
                Toast.makeText(CaptureActivity.this, "Capture image is empty", Toast.LENGTH_LONG).show();
                return;
            }
            bitmap = scaleDownBItmapImage(bitmap, 450, 300);

            GameInfo.setImgBitmap(bitmap);

            Intent intent = new Intent(getApplicationContext(), RecordRankActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, 700);

        }
    };

    private Bitmap scaleDownBItmapImage(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder(); // if you are using MediaRecorder, release it
        releaseCamera();        // release the camera immediately on pause event
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void releaseMediaRecorder() {
        mCamera.lock();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        Button btn = (Button) findViewById(R.id.button_capture);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(180);

        // Create out Preview view and set is as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, pictureCallback);

            }
        });


    }

    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use of does not exits)
        }
        return c;


    }
}