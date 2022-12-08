package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RecordRankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_rank);

        TextView recordScoreTv = findViewById(R.id.record_score_Tv);

        recordScoreTv.setText(GameInfo.getTotalScore().toString());

        // TODO: 2022/12/06 카메라 조져야 해


    }
}