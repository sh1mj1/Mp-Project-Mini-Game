package com.example.minigame;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.core.Size;

public class RspActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {

    private static final String TAG = "HandPose::RspActivity";
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;

    private Mat mRgba;
    private Mat mGray;
    private Mat mIntermediateMat;

    private int mDetectorType = JAVA_DETECTOR;

    private CustomSufaceView mOpenCvCameraView;
    private List<Size> mResolutionList;

    private SeekBar minTresholdSeekbar = null;
    private SeekBar maxTresholdSeekbar = null;
    private TextView minTresholdSeekbarText = null;
    private TextView numberOfFingersText = null;
    private TextView numberOfFingersText1 = null;

    double iThreshold = 0;

    private Scalar mBlobColorHsv;
    private Scalar mBlobColorRgba;
    private ColorBlobDetector mDetector;
    private Mat mSpectrum;
    private boolean mIsColorSelected = false;

    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;
    private Scalar CONTOUR_COLOR_WHITE;

    public Handler mHandler = new Handler();
    public Handler matchHandler = new Handler();
    private boolean captureFinger = false;
    int numberOfFingers = 0;

    private CountDownTimer countDownTimer;
    private boolean canStart = false;
    private int count = 3;
    private static final int TOTAL = 4 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 900;
    private static final List<String> myRspLists = new ArrayList<>();
    private int myRsp;          // 1 -> 주먹, 2 -> 가위, 3 -> 보자기
    private int randomValue;    // 1 -> 주먹, 2 -> 가위, 3 -> 보자기
    private int resultOfMatch; // 1: 이김,     2 : 짐,      3: 비김.

    private int rspScore = 0;
    private boolean firstTouch;

    final Runnable mUpdateFingerCountResults = new Runnable() {
        public void run() {
            updateNumberOfFingers();
        }
    };
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(RspActivity.this);
                    // 640x480
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsp);

        Toast.makeText(RspActivity.this, "화면에 보이는 당신의 손을 터치하세요.", Toast.LENGTH_SHORT).show();

        initCamera();

        canStart = true;
        firstTouch = false;
        numberOfFingersText = (TextView) findViewById(R.id.numberOfFingers);
        numberOfFingersText1 = (TextView) findViewById(R.id.numberOfFingers1);

        ImageView computerRspIv = findViewById(R.id.computer_rsp);
        computerRspIv.setImageResource(R.drawable.ic_android);
        if (myRspLists.size() <= 2) {
            myRspLists.add("바위");
            myRspLists.add("가위");
            myRspLists.add("보");
        }

        initSeekBar();

        Button startBtn = findViewById(R.id.rsp_start_game_Btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canStart) {
                    myRsp = 0;
                    randomValue = 0;
                    Log.d("COUNT", "CountDown Start");
                    TextView rspCountDown = findViewById(R.id.rsp_count_down_Tv);
                    rspCountDown.setVisibility(View.VISIBLE);

                    countDownTimer();
                    countDownTimer.start();
                }
            }
        });

    }

    private void initCamera() {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCVLoader.initDebug() is False");
        } else {
            Log.d(TAG, "OpenCVLoader.initDebug() is True");
        }
        mOpenCvCameraView = (CustomSufaceView) findViewById(R.id.main_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    private void initSeekBar() {
        minTresholdSeekbarText = (TextView) findViewById(R.id.textView3);
        minTresholdSeekbar = (SeekBar) findViewById(R.id.seekBar1);

        minTresholdSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                minTresholdSeekbarText.setText(String.valueOf(progressChanged));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                minTresholdSeekbarText.setText(String.valueOf(progressChanged));
            }
        });
        minTresholdSeekbar.setProgress(20000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
            mOpenCvCameraView.releaseCamera();
        }
        countDownTimer.cancel();
        countDownTimer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        canStart = true;
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.releaseCamera();
            mOpenCvCameraView.disableView();
        }
        countDownTimer.cancel();
        countDownTimer = null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        canStart = true;
        if (firstTouch) {
            Toast.makeText(RspActivity.this, "하단의 Seekbar 을 조절하여 손가락 개수를 올바르게 인식하도록 하세요", Toast.LENGTH_SHORT).show();
            Toast.makeText(RspActivity.this, "손을 올바르게 인식한다면 Start 버튼을 누르세요", Toast.LENGTH_SHORT).show();
            firstTouch = false;
        }

        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int) event.getX() - xOffset;
        int y = (int) event.getY() - yOffset;

//        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) {
            return false;
        }

        Rect touchedRect = new Rect();

        touchedRect.x = (x > 5) ? x - 5 : 0;
        touchedRect.y = (y > 5) ? y - 5 : 0;

        touchedRect.width = (x + 5 < cols) ? x + 5 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y + 5 < rows) ? y + 5 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

//        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
//                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        mIntermediateMat = new Mat();

        Camera.Size resolution = mOpenCvCameraView.getResolution();

        String caption = "Resolution " + Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
//        Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();


        Camera.Parameters cParams = mOpenCvCameraView.getParameters();

        cParams.setPreviewSize(width, height);

        mOpenCvCameraView.setParameters(cParams);
//        Toast.makeText(this, "Focus mode : " + cParams.getFocusMode(), Toast.LENGTH_SHORT).show();

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
        CONTOUR_COLOR_WHITE = new Scalar(255, 255, 255, 255);

    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        iThreshold = minTresholdSeekbar.getProgress();

        //Imgproc.blur(mRgba, mRgba, new Size(5,5));
        Imgproc.GaussianBlur(mRgba, mRgba, new org.opencv.core.Size(3, 3), 1, 1);
        //Imgproc.medianBlur(mRgba, mRgba, 3);

        if (!mIsColorSelected) return mRgba;

        List<MatOfPoint> contours = mDetector.getContours();
        mDetector.process(mRgba);

        if (contours.size() <= 0) {
            return mRgba;
        }

        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(0).toArray()));

        double boundWidth = rect.size.width;
        double boundHeight = rect.size.height;
        int boundPos = 0;

        for (int i = 1; i < contours.size(); i++) {
            rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
            if (rect.size.width * rect.size.height > boundWidth * boundHeight) {
                boundWidth = rect.size.width;
                boundHeight = rect.size.height;
                boundPos = i;
            }
        }
        Rect boundRect = Imgproc.boundingRect(new MatOfPoint(contours.get(boundPos).toArray()));

        Imgproc.rectangle(mRgba, boundRect.tl(), boundRect.br(), CONTOUR_COLOR_WHITE, 2, 8, 0);

        int rectHeightThresh = 0;
        double a = boundRect.br().y - boundRect.tl().y;
        a = a * 0.7;
        a = boundRect.tl().y + a;


        //Core.rectangle( mRgba, boundRect.tl(), boundRect.br(), CONTOUR_COLOR, 2, 8, 0 );
        Imgproc.rectangle(mRgba, boundRect.tl(), new Point(boundRect.br().x, a), CONTOUR_COLOR, 2, 8, 0);

        MatOfPoint2f pointMat = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(boundPos).toArray()), pointMat, 2, false);
        contours.set(boundPos, new MatOfPoint(pointMat.toArray()));

        MatOfInt hull = new MatOfInt();
        MatOfInt4 convexDefect = new MatOfInt4();
        Imgproc.convexHull(new MatOfPoint(contours.get(boundPos).toArray()), hull);

        if (hull.toArray().length < 3) return mRgba;

        Imgproc.convexityDefects(new MatOfPoint(contours.get(boundPos).toArray()), hull, convexDefect);

        List<MatOfPoint> hullPoints = new LinkedList<MatOfPoint>();
        List<Point> listPo = new LinkedList<Point>();
        for (int j = 0; j < hull.toList().size(); j++) {
            listPo.add(contours.get(boundPos).toList().get(hull.toList().get(j)));
        }

        MatOfPoint e = new MatOfPoint();
        e.fromList(listPo);
        hullPoints.add(e);

        List<MatOfPoint> defectPoints = new LinkedList<MatOfPoint>();
        List<Point> listPoDefect = new LinkedList<Point>();

        for (int j = 0; j < convexDefect.toList().size(); j = j + 4) {
            Point farPoint = contours.get(boundPos).toList().get(convexDefect.toList().get(j + 2));
            Integer depth = convexDefect.toList().get(j + 3);
            if (depth > iThreshold && farPoint.y < a) {
                listPoDefect.add(contours.get(boundPos).toList().get(convexDefect.toList().get(j + 2)));
            }
        }

        MatOfPoint e2 = new MatOfPoint();
        e2.fromList(listPo);
        defectPoints.add(e2);

        Imgproc.drawContours(mRgba, hullPoints, -1, CONTOUR_COLOR, 3);

        int defectsTotal = (int) convexDefect.total();

        this.numberOfFingers = listPoDefect.size();
        if (this.numberOfFingers > 5) this.numberOfFingers = 5;

        mHandler.post(mUpdateFingerCountResults);

        for (Point p : listPoDefect) {
            Imgproc.circle(mRgba, p, 6, new Scalar(255, 0, 255));
        }

        return mRgba;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void updateNumberOfFingers() {
//        Log.d(TAG, "updateNumberOfFingers called, numberOfFingers: " + numberOfFingers);

        if (numberOfFingers >= 4) { // 보
            numberOfFingersText.setText(myRspLists.get(2));
            numberOfFingersText.setText("보");

        } else if ((numberOfFingers <= 3) && (numberOfFingers >= 2)) { // 가위

            numberOfFingersText.setText("가위");
        } else {
            numberOfFingersText.setText("바위");
        }
        numberOfFingersText1.setText(String.valueOf(this.numberOfFingers));
    }

    private void countDownTimer() {
        countDownTimer = new CountDownTimer(TOTAL, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long l) {
                TextView cntDownTv = findViewById(R.id.rsp_count_down_Tv);
                cntDownTv.setVisibility(View.VISIBLE);
                cntDownTv.setText(String.valueOf(count));

                if (count < 1) {
                    cntDownTv.setVisibility(View.INVISIBLE);
                }

                count--;
                if (count < 0) {
                    count = 3;
                    canStart = false;
                }
            }

            @Override
            public void onFinish() {
                findViewById(R.id.rsp_count_down_Tv).setVisibility(View.INVISIBLE);

                doRockScissorsPaper();
            }
        };
    }

    private void doRockScissorsPaper() {
        setComputerRspIv(); // 컴퓨터가 가위, 바위, 보 제시
        setMyRsp(); // 내 가위, 바위, 보 를 저장

        matchRsp(); // 승부 판가름
    }

    private void setMyRsp() {
        if (numberOfFingers >= 4) { // 보
            numberOfFingersText.setText("보");
            myRsp = 3; // 보자기
        } else if ((numberOfFingers <= 3) && (numberOfFingers >= 2)) { // 가위
            numberOfFingersText.setText("가위");
            myRsp = 2; // 가위
        } else {
            numberOfFingersText.setText("바위");
            myRsp = 1; // 바위
        }
    }

    private void setComputerRspIv() {
        // 컴퓨터가 가위, 바위, 보 제시
        randomValue = (int) (Math.random() * 3) + 1; // 1 : 주먹, 2 : 가위, 3 : 보자기
        ImageView computerRspIv = findViewById(R.id.computer_rsp);
        Log.d("Random Value", "randomValue : " + randomValue);

        if (randomValue == 1) {
            computerRspIv.setImageResource(R.drawable.ic_rock);
        } else if (randomValue == 2) {
            computerRspIv.setImageResource(R.drawable.ic_scissors);
        } else if (randomValue == 3) {
            computerRspIv.setImageResource(R.drawable.ic_paper);
        }

    }

    private void matchRsp() {
        if (myRsp == randomValue) {
            resultOfMatch = 3; // 비김
        }
        // 내가 이김 (바위, 가위) (가위, 보자기) (보자기, 바위) == (1, 2) (2,3) (3,1)
        else if (
                (((myRsp == 1) && (randomValue == 2)) ||
                        ((myRsp == 2) && (randomValue == 3)) ||
                        ((myRsp == 3) && (randomValue == 1)))) {
            resultOfMatch = 1; // 이김
            rspScore += 1;
        }
        // 내가 짐
        else if (
                (((myRsp == 2) && (randomValue == 1)) ||
                        ((myRsp == 3) && (randomValue == 2)) ||
                        ((myRsp == 1) && (randomValue == 3)))) {
            resultOfMatch = 2;
        } else {
            resultOfMatch = 0;
            Log.e(TAG, "Error - resultOfMatch is unvaild");
        }
        Log.d(TAG, "resultOfMatch " + resultOfMatch);

        if (resultOfMatch == 1) {
            // TODO: 2022/12/09 이김 다시 시작
            canStart = true;
            rspScore += 1;
            Log.d(TAG, "Match Result - Win!  rspScore : " + rspScore);
            count = 3;
//            matchHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    countDownTimer.start();
//                }
//            }, 2000);
//            countDownTimer.start();
        } else if (resultOfMatch == 2) {
            // TODO: 2022/12/09 짐
            Log.d(TAG, "Match Result - Lose! rspScore : " + rspScore);
            mOpenCvCameraView.releaseCamera();
            gameOver();
        } else if (resultOfMatch == 3) {
            canStart = true;
            // TODO: 2022/12/09 비김 다시 시작
            Log.d(TAG, "Match Result - Draw! rspScore : " + rspScore);
            count = 3;
//            matchHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    countDownTimer.start();
//                }
//            }, 2000);
//            countDownTimer.start();

        } else {
            resultOfMatch = 0;
            Log.d(TAG, "Match Result : Default");
        }

    }


    private void gameOver() {
        GameInfo.setTotalScore(rspScore + GameInfo.getTotalScore());
        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
        startActivity(intent);
    }
}