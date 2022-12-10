package com.example.minigame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minigame.databinding.ActivitySnakeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SnakeActivity extends AppCompatActivity implements SurfaceHolder.Callback, InterruptListener {

    String SNAKE = "SnakeActivity";

    InterruptDriver mDriver;
    boolean mTreadRun = true;

    // list of snake points / snake length
    private final List<SnakePoints> snakePointsList = new ArrayList<>();
    //surface holder to draw snake on surface's canvas
    private SurfaceView surfaceView;
    private TextView scoreTV;

    // surface holder to draw snake on surface's canvas
    private SurfaceHolder surfaceHolder;

    // snake moving position (must be right, left, up, down)
    private String movingPosition = "Right"; // defulat = r(right)

    private int score = 0;

    // snake size / point size
    // can change this value to make bigger size snake
    private static final int pointSize = 28;
    private static final int defaultTalePoints = 3;

    // snake color
    private static final int snakeColor = Color.YELLOW;

    // snake moving speed. value must be lie between 1 - 1000
    private static final int snakeMovingSpeed = 800;

    // random point position cordinates on the surfaceView
    private int positionX, positionY;

    // timer to move snake / change snake position after a specific time (snakeMovingSpeed)
    private Timer timer;

    // canvas to draw snake and show on surface view
    private Canvas canvas = null;

    // point color / single paint color of a snake
    private Paint pointColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        GameInfo.setGameStage(2);
        Log.d(SNAKE, "=== Game Start === GameInfo - GameStage: " + GameInfo.getGameStage() +
                "   GameInfo - GameScore: " + GameInfo.getTotalScore());

        // open Interrupt Driver
        mDriver = new InterruptDriver();
        mDriver.setmActivity(this);

        if (mDriver.open("/dev/sm9s5422_interrupt") < 0) {
            Toast.makeText(SnakeActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
            Log.d(SNAKE, "open Driver Failed");
        } else {
            Log.d(SNAKE, "open Driver Success");
        }


        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.score_Tv);

        final AppCompatImageButton upBtn = findViewById(R.id.up_Btn);
        final AppCompatImageButton downBtn = findViewById(R.id.down_Btn);
        final AppCompatImageButton leftBtn = findViewById(R.id.left_Btn);
        final AppCompatImageButton rightBtn = findViewById(R.id.right_Btn);

        // adding callback to surfaceView
        surfaceView.getHolder().addCallback(this);

        // TODO: 2022/11/28 Have to change to GPIO Button
        setArrowKeysListener(upBtn, downBtn, leftBtn, rightBtn);
    }

    private void setArrowKeysListener(AppCompatImageButton upBtn, AppCompatImageButton downBtn, AppCompatImageButton leftBtn, AppCompatImageButton rightBtn) {
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!movingPosition.equals("Down")) {
                    movingPosition = "Up";
                }
            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!movingPosition.equals("Up")) {
                    movingPosition = "Down";
                }

            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!movingPosition.equals("Right")) {
                    movingPosition = "Left";
                }
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!movingPosition.equals("Left")) {
                    movingPosition = "Right";
                }
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        // when surface is created then get surfaceHolder from it and assign to surfaceHolder
        this.surfaceHolder = surfaceHolder;

        // init data for snake / surfaceView
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void init() {

        // clear snake points / snake length
        snakePointsList.clear();

        // set default score as 0
        scoreTV.setText("0");

        // make score 0
        score = 0;
        // set default moving position
        movingPosition = "Right";

        // default snake starting position on the screen
        int startPositionX = (pointSize) * defaultTalePoints;

        // making snake's default length / points
        for (int i = 0; i < defaultTalePoints; i++) {
            // adding points to snake's tale
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            // increasing value for next point as snake's tale
            startPositionX = startPositionX - (pointSize * 2);
        }


        // add random point on the screen to be eaten by the snake
        addPoint();

        // start moving snake / start game
        moveSnake();

    }

    private void addPoint(){
        // getting surfaceView width and height to add point on the surface to be eaten by the snake
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        // check if randomXPosition is even or odd value. We need only even number
        if ((randomXPosition % 2) != 0) {
            randomXPosition += 1;
        }
        if ((randomYPosition % 2) != 0) {
            randomYPosition += 1;
        }
        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;
    }

    private void moveSnake(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // getting head position
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                // check if snake eaten a point
                if (headPositionX == positionX && positionY == headPositionY) {
                    //grow snake after eaten point
                    growSnake();
                    // add another random point on the screen
                    addPoint();
                }

                // check of which side snake is moving
                switch (movingPosition) {
                    case "Right":
                        // move snake's head to right.
                        // other points follow snake's head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "Left":
                        // move snake's head to left.
                        // other points follow snake's head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "Up":
                        // move snake's head to up.
                        // other points follow snake's head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;
                    case "Down":
                        // move snake's head to down.
                        // other points follow snake's head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }

                // check if game over. whether snake touch edges or snake itself
                if (checkGameOver(headPositionX, headPositionY)) {
                    // stop timer / stop moving snake
                    timer.purge();
                    timer.cancel();

                    // Game Over

                    GameInfo.setTotalScore(score + GameInfo.getTotalScore());
                    Log.d(SNAKE, " === Game Over === GameInfo - GameStage: " + GameInfo.getGameStage() +
                            "  GameInfo - GameScore: " + GameInfo.getTotalScore());

                    Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                } else {
                    // lock canvas on surfaceHolder to draw on it
                    canvas = surfaceHolder.lockCanvas();

                    // clear canvas with white color
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                    // change snake's head position. other snake points will follow snake's head
                    canvas.drawCircle(snakePointsList.get(0).getPositionX(), snakePointsList.get(0).getPositionY(), pointSize, createPointColor());

                    // draw random point circle on the surface to be eaten by the snake
                    canvas.drawCircle(positionX, positionY, pointSize, createPointColor());

                    // other points is following snake's head. position 0 is head of snake
                    for (int i = 1; i < snakePointsList.size(); i++){
                        int getTempPositionX = snakePointsList.get(i).getPositionX();
                        int getTempPositionY = snakePointsList.get(i).getPositionY();

                        snakePointsList.get(i).setPositionY(headPositionY);
                        snakePointsList.get(i).setPositionX(headPositionX);
                        canvas.drawCircle(snakePointsList.get(i).getPositionX(), snakePointsList.get(i).getPositionY(), pointSize, createPointColor());

                        // change head position
                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;

                    }
                    // unlock canvas to draw on a surfaceView

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }
        }, 1000-snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }


    private void growSnake(){
        //create new snake point
        SnakePoints snakePoints = new SnakePoints(0, 0);

        // add point to the snake's tale
        snakePointsList.add(snakePoints);

        // increase score
        score++;

        // setting score to TextView
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });

    }
    private boolean checkGameOver(int headPositionX, int headPositionY){
        boolean gameOver = false;
        // check if snake's head touches edges
        if ((snakePointsList.get(0).getPositionX() < 0) ||
                snakePointsList.get(0).getPositionY() < 0 ||
                snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionY() >= surfaceView.getHeight()
        ) {
            gameOver = true;
        } else {
            // check if snake's head touches snake itself
            for (int i = 0; i < snakePointsList.size(); i++) {
                if (headPositionX == snakePointsList.get(i).getPositionX() &&
                        headPositionY == snakePointsList.get(i).getPositionY()
                ) {
                    gameOver = true;
                    break;
                }
            }
        }
        return gameOver;
    }
    private Paint createPointColor(){
        // check if color not defined before
        if (pointColor == null) {
            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);
        }

        return pointColor;

    }


    @Override
    protected void onPause() {
        super.onPause();
        // close Driver
//        mDriver.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // open Driver again
//        if (mDriver.open("/dev/sm9s5422_interrupt") < 0) {
//            Toast.makeText(SnakeActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(SnakeActivity.this, "Driver Open Success", Toast.LENGTH_SHORT).show();
//        }
    }


    public void onReceive(int val) {
        Message text = Message.obtain();
        text.arg1 = val;
        handler.sendMessage(text);
        Log.d("Handler", "text :" + String.valueOf(text));
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
//                    tv.setText("UP");
                    movingPosition = "Up";
                    Log.d(SNAKE, "movingPosition" + movingPosition);
                    break;
                case 2:
//                    tv.setText("DOWN");
                    movingPosition = "Down";
                    Log.d(SNAKE, "movingPosition" + movingPosition);
                    break;
                case 3:
//                    tv.setText("LEFT");
                    movingPosition = "Left";
                    Log.d(SNAKE, "movingPosition" + movingPosition);
                    break;
                case 4:
//                    tv.setText("RIGHT");
                    movingPosition = "Right";
                    Log.d(SNAKE, "movingPosition" + movingPosition);
                    break;
                case 5:
//                    tv.setText("CENTER");
                    Log.d(SNAKE, "The function of Center Button is not implemented yet");
                    Log.d(SNAKE, "movingPosition" + movingPosition);
                    break;
            }
        }
    };

}

/*

 */