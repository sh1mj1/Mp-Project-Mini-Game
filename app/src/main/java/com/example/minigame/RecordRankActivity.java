package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordRankActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private final String TAG = "RecordRankActivity";
    private String myNickname;
    private Bitmap bitmap;
    public Rank myRank = new Rank();
    // Mark -camera/////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_rank);

        TextView recordScoreTv = findViewById(R.id.record_score_Tv);
        ImageView capturedImgIb = findViewById(R.id.capturedImage_Ib);

        Button colorBtn = findViewById(R.id.color_Btn);
        Button edgeBtn = findViewById(R.id.edge_Btn);
        Button blurBtn = findViewById(R.id.blur_Btn);
        Button completeBtn = findViewById(R.id.complete_Btn);


        // Mark -First Setting/////////////////////////////////////////
        recordScoreTv.setText(GameInfo.getTotalScore().toString());
        bitmap = GameInfo.getImgBitmap();
        capturedImgIb.setImageBitmap(bitmap);


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
                // TODO: 2022/12/10 edit edge
            }
        });
        blurBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2022/12/10 edit blur
            }
        });
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2022/12/10 어떤 클래스에 비트맵 정보를 저장해야 함
                complete();
            }
        });


        // 카메라 이동
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


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

