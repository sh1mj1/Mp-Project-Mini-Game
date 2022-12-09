package com.example.minigame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SomeoneRankActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_rank);
        TextView recordScoreTv = findViewById(R.id.record_score_Tv);
        ImageButton capturedImgIb = findViewById(R.id.capturedImage_Ib);
        EditText recordNickName = findViewById(R.id.record_name_Et);
        TextView recordTitleTv = findViewById(R.id.record_title_Tv);
        Button colorBtn = findViewById(R.id.color_Btn);
        Button edgeBtn = findViewById(R.id.edge_Btn);
        Button blurBtn = findViewById(R.id.blur_Btn);
        Button completeBtn = findViewById(R.id.complete_Btn);


        recordScoreTv.setVisibility(View.INVISIBLE);
        capturedImgIb.setVisibility(View.INVISIBLE);
        colorBtn.setVisibility(View.INVISIBLE);
        edgeBtn.setVisibility(View.INVISIBLE);
        blurBtn.setVisibility(View.INVISIBLE);
        completeBtn.setVisibility(View.INVISIBLE);
        recordScoreTv.setVisibility(View.INVISIBLE);

//        recordNickName.setText();
        recordNickName.setEnabled(false);
        capturedImgIb.setEnabled(false);

        recordTitleTv.setText("RANK -> " + GameInfo.getGameRank().toString());
        recordScoreTv.setText(GameInfo.getTotalScore().toString());
        capturedImgIb.setImageBitmap(GameInfo.getImgBitmap());
        recordNickName.setText(GameInfo.getNickname());


    }
}
