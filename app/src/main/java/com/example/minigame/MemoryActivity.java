package com.example.minigame;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minigame.databinding.ActivityMemoryBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MemoryActivity extends AppCompatActivity {

    static {
        System.loadLibrary("JNIDriver");
    }

    String MEMORY = "MemoryActivity";

    // jni native function
    private native static int openLedDriver(String path);
    private native static int closeLedDirver();
    private native static void writeLedDriver(byte[] data, int length);

    // Random LED List (5 개부터 시작)
    List<Integer> randomIntList = new ArrayList<Integer>();

    // buttonList that you pressed
    List<Integer> pressedIntList = new ArrayList<Integer>();

    List<Button> buttonList = new ArrayList<Button>(8);

    TextView cntDownTv;

    int gameScore = 1; // score is same with the level
    // Count Down
    private CountDownTimer countDownTimer;
    private int count = 3;
    private static final int TOTAL = 4 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 900;

    byte[] data = {0, 0, 0, 0, 0, 0, 0, 0};


    @Override
    protected void onPause() {
        closeLedDirver();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (openLedDriver("/dev/sm9s5422_led") < 0) {
            Toast.makeText(MemoryActivity.this, "Driver open failed", Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        GameInfo.setGameStage(1);
        Log.d(MEMORY, "=== Game Start === GameInfo - GameStage: " + GameInfo.getGameStage() +
                "   GameInfo - GameScore: " + GameInfo.getTotalScore());

        TextView cntDownTv = findViewById(R.id.count_down_Tv);

//        openLedDriver("/dev/sm9s5422_led");
        Log.d(MEMORY, "openLedDriver called");

        initView();

        // LED 나온 순서대로 버튼 클릭
        // 클릭한 버튼의 위치를 list 에 저장
        setBtnClickListener();


    }

    private void emitLED(byte[] data) {
        TextView gameGuideTv = findViewById(R.id.memory_game_guide_Tv);

        for (int i = 0; i < (gameScore + 2); i++) {

            data[randomIntList.get(i)] = 1;

            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            writeLedDriver(data, data.length);
            data[randomIntList.get(i)] = 0;

            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            writeLedDriver(data, data.length);
        }
        gameGuideTv.setText("Press!");
    }

    // 랜덤 수 생성. gameScore 는 Level임.
    private void setRandomIntList(int gameScore) {
        for (int i = 0; i < (gameScore + 2); i++) {
            int randomValue = (int) (Math.random() * 8 + 0);
            randomIntList.add(randomValue);
            Log.d(MEMORY, "randomIntList : " + randomIntList);
        }
    }

    private void initView() {
        buttonList.clear();
        randomIntList.clear();
        pressedIntList.clear();

        if (buttonList.isEmpty()) {
            buttonList.add(findViewById(R.id.memory_0_Btn));
            buttonList.add(findViewById(R.id.memory_1_Btn));
            buttonList.add(findViewById(R.id.memory_2_Btn));
            buttonList.add(findViewById(R.id.memory_3_Btn));
            buttonList.add(findViewById(R.id.memory_4_Btn));
            buttonList.add(findViewById(R.id.memory_5_Btn));
            buttonList.add(findViewById(R.id.memory_6_Btn));
            buttonList.add(findViewById(R.id.memory_7_Btn));
        }

        Button memoryGameStartBtn = findViewById(R.id.memory_game_start_Btn);
        memoryGameStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.memory_game_guide_Tv).setVisibility(View.VISIBLE);
                findViewById(R.id.memory_game_start_Btn).setVisibility(View.GONE);
                findViewById(R.id.count_down_Tv).setVisibility(View.VISIBLE);

                // 숫자 카운트 다운. 0 이 되면 왼쪽 LED 점등.
                countDownTimer();
                countDownTimer.start();
                // set randomIntList
                setRandomIntList(gameScore);

            }
        });

    }

    private void countDownTimer() {
        countDownTimer = new CountDownTimer(TOTAL, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long l) {
                if(gameScore >=2) {
                    TextView gameGuideTv = findViewById(R.id.memory_game_guide_Tv);
                    gameGuideTv.setTextSize(50);
                    gameGuideTv.setText("Waiting...");
                }
                TextView cntDownTv = findViewById(R.id.count_down_Tv);
                cntDownTv.setText(String.valueOf(count));
                count--;
                if (count < 0){
                    cntDownTv.setVisibility(View.INVISIBLE);
                    TextView gameGuideTv = findViewById(R.id.memory_game_guide_Tv);
                    gameGuideTv.setTextSize(50);
                    gameGuideTv.setText("Waiting...");
                }
            }

            @Override
            public void onFinish() {
                findViewById(R.id.count_down_Tv).setVisibility(View.INVISIBLE);
                count = 3;
                // LED Emitting
                emitLED(data);
            }
        };
    }

    // LED 나온 순서대로 버튼 클릭
    // 클릭한 버튼의 위치를 list 에 저장
    private void setBtnClickListener() {

        for (int i = 0; i < 8; i++) {
            int finalI = i;
            buttonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pressedIntList.add(finalI);
                    int lastIndex = pressedIntList.size() - 1;
                    if (pressedIntList.get(lastIndex) == randomIntList.get(lastIndex)) {
                        // TODO: 2022/11/24 It's OK
                        Log.d(MEMORY, "It's Ok");
                    } else {
                        // TODO: 2022/11/24 Game Over
                        Log.d(MEMORY, "Level 통과 실패 (오답)");

//                        GameInfo gameInfo = new GameInfo();
                        GameInfo.setTotalScore(gameScore);
                        GameInfo.setGameStage(1);
                        Log.d(MEMORY, "=== Game Over === GameInfo - GameStage: " + GameInfo.getGameStage() +
                                "   GameInfo - GameScore: " + GameInfo.getTotalScore());

                        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                        startActivity(intent);
                    }

                    // Level  통과 시
                    if (pressedIntList.size() == randomIntList.size() ) {
                        if(pressedIntList.get(pressedIntList.size()-1).equals(randomIntList.get(randomIntList.size()-1 ))){
                            // TODO: 2022/11/24 다음 레벨
                            randomIntList.clear();
                            pressedIntList.clear();

                            Log.d(MEMORY, "Go Next Level");
                            gameScore += 1;

                            TextView memoryLevelTv = findViewById(R.id.memory_level_Tv);
                            memoryLevelTv.setText("Level " + gameScore);

                            Toast.makeText(MemoryActivity.this,
                                    "Next Level " + gameScore, Toast.LENGTH_LONG).show();

                            countDownTimer.start();

                            setRandomIntList(gameScore);
                        }


                    }

                }
            });
        }

        // new thread th1
        // th1 이

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
            Log.d(MEMORY, String.valueOf(e));
        }
        countDownTimer = null;
    }
}

