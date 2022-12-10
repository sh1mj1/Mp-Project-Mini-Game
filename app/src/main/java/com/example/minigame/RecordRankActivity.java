package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RecordRankActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private String TAG = "RecordRankActivity";
    private String myNickname;
    public Bitmap tempBitmap;
    Bitmap buf_bitmap;
    public Rank myRank = new Rank();
    private final int REQ_CODE_SELECT_IMAGE = 100;

    // Mark -JNI Method/////////////////////////////////////////
    static {
        System.loadLibrary("JNIDriver");
    }


    // blur GPU
    public native static Bitmap GaussianBlurBitmap(Bitmap bitmap);

    public native static Bitmap gaussianBlurBitmap_2(Bitmap bitmap);

    public native static Bitmap gaussianBlurBitmap_3(Bitmap bitmap);

    public native static Bitmap GrayScaleBitmap(Bitmap bitmap);

    public native static Bitmap grayScaleBitmap_2(Bitmap bitmap);

    public native static Bitmap grayScaleBitmap_3(Bitmap bitmap);


    // Mark -camera/////////////////////////////////////////

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d("RecordRankActivity", "OpenCVLoader.initDebug() is False");
        } else {
            Log.d("RecordRankActivity", "OpenCVLoader.initDebug() is True");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_rank);


        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCVLoader.initDebug() is False");
        } else {
            Log.d(TAG, "OpenCVLoader.initDebug() is True");
        }

        TextView recordScoreTv = findViewById(R.id.record_score_Tv);
        ImageView capturedImgIb = findViewById(R.id.capturedImage_Ib);

        Button colorBtn = findViewById(R.id.color_Btn);
        Button edgeBtn = findViewById(R.id.edge_Btn);
        Button blurBtn = findViewById(R.id.blur_Btn);
        Button completeBtn = findViewById(R.id.complete_Btn);


        // Mark -First Setting/////////////////////////////////////////
        recordScoreTv.setText(GameInfo.getTotalScore().toString());

        tempBitmap = GameInfo.getImgBitmap();

        capturedImgIb.setRotation(180f);
        capturedImgIb.setImageBitmap(tempBitmap);
//        saveBitmapAsFile(tempBitmap, "/data/local/tmp/tempBitmap.bmp");

//        File file = new File("/data/local/tmp");
//        OutputStream os = null;
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            os = new FileOutputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }



//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//
//        buf_bitmap = BitmapFactory.decodeFile("data/local/tmp/tempBitmap.bmp", options);

        // TODO: 2022/12/06 사진 편집
        // Mark -Edit Picture and Sync/////////////////////////////////////////
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2022/12/10 edit color
            }
        });

        edgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectBitmapEdge();
                capturedImgIb.setImageBitmap(tempBitmap);
            }
        });
        blurBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2022/12/10 edit blur

                tempBitmap = GaussianBlurBitmap(tempBitmap);
                capturedImgIb.setImageBitmap(tempBitmap);
//                buf_bitmap = GaussianBlurBitmap(buf_bitmap);
//                capturedImgIb.setImageBitmap(buf_bitmap);

            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2022/12/10 어떤 클래스에 비트맵 정보를 저장해야 함
                complete();
            }
        });


        // 카메라로 이동
        capturedImgIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                startActivity(intent);
            }
        });


    }

    private void complete() {
        // TODO: 2022/12/10 내 ID 읽어서 가져가기
        // get nickname
        Intent intent = new Intent(getApplicationContext(), RankActivity.class);
        EditText recordNickName = findViewById(R.id.record_name_Et);
        myNickname = String.valueOf(recordNickName.getText());
        Log.d(TAG, "nickname - " + myNickname);
        GameInfo.setGameRank(5);
        GameInfo.setNickname(myNickname);
        GameInfo.setImgBitmap(tempBitmap);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.)
        startActivity(intent);
    }


    public void detectBitmapEdge() {
        Mat src = new Mat();
        Utils.bitmapToMat(tempBitmap, src);
        Mat edge = new Mat();
        // Image Processing
        Imgproc.Canny(src, edge, 50, 150);
        Utils.matToBitmap(edge, tempBitmap);
        src.release();
        edge.release();
    }

    private void saveBitmapAsFile(Bitmap bitmap, String filepath) {
        File file = new File(filepath);
        OutputStream os = null;

        try {
            file.createNewFile();
            os = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

