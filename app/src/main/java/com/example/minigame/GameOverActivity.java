package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {
    private String GAMEOVER = "GAMEOVER";

    private TextView nextStageBtn;
    private TextView gameOverTv;
    private TextView totalScoreTv;
    private TextView recordRankTv;
    private TextView goHomeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        nextStageBtn = findViewById(R.id.next_stage_Tv);
        gameOverTv = findViewById(R.id.game_over_Tv);
        totalScoreTv = findViewById(R.id.game_over_total_score_Tv);
        recordRankTv = findViewById(R.id.record_rank_Tv);
        goHomeTv = findViewById(R.id.go_home_Tv);

//        game_over_total_score_Tv


        Log.d(GAMEOVER, " === Game Over === GameInfo - GameStage: " + GameInfo.getGameStage() +
                "  GameInfo - GameScore: " + GameInfo.getTotalScore());



        totalScoreTv.setText(String.valueOf(GameInfo.getTotalScore()));

        if (GameInfo.getGameStage() >= 3) {
            nextStageBtn.setVisibility(View.GONE);
            gameOverTv.setVisibility(View.VISIBLE);
        }

        goNextStageClickListener(nextStageBtn);

        recordRankTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), RecordRankActivity.class);
                startActivity(intent);
            }
        });

        goHomeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void goNextStageClickListener(TextView nextStageBtn) {
        nextStageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GameInfo.getGameStage() == 1) {
                    Intent intent = new Intent(getApplicationContext(), SnakeActivity.class);
                    startActivity(intent);

                } else if (GameInfo.getGameStage() == 2) {
                    Intent intent2 = new Intent(getApplicationContext(), RspActivity.class);
                    startActivity(intent2);
                } else {
                    Log.d(GAMEOVER, "Completely Game over");
                }
            }
        });
    }




}