package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {
    private String GAMEOVER = "GAMEOVER";

    Intent intent;

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
                gameOver(new Intent(getApplication(), RecordRankActivity.class));
            }
        });

        goHomeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameOver(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    private void goNextStageClickListener(TextView nextStageBtn) {
        nextStageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GameInfo.getGameStage() == 1) {
                    gameOver(new Intent(getApplicationContext(), SnakeActivity.class));

                } else if (GameInfo.getGameStage() == 2) {
                    gameOver(new Intent(getApplicationContext(), RspActivity.class));
                } else {
                    // 마지막 스테이지 였을 때
                    Log.d(GAMEOVER, "Completely Game over");
                }
            }
        });
    }

    private void gameOver(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}