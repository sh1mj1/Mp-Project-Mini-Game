package com.example.minigame;

import android.util.Log;

public class InterruptDriver implements InterruptListener {

    private boolean mConnectFlag;

    private TransThread mTranseThread;
    private InterruptListener mActivity;

    static {
        System.loadLibrary("JNIDriver");
    }

    public void setmActivity(InterruptListener a) {
        mActivity = a;
    }

    public InterruptDriver() {
        mConnectFlag = false;
    }


    @Override
    public void onReceive(int val) {
        Log.e("InterruptDriver", "onReceive called");
        if (mActivity != null) {
            mActivity.onReceive(val);
            Log.e("InterruptDriver", "onReceive called (snakeActivity.onReceive");

        }
    }

    // JNIDriver.so 에 정의될 JNI 메서드
    private native static int openDriver(String path);

    private native static void closeDriver();

    private native char readDriver();

    private native int getInterrupt();


    public int open(String driver) {
        Log.d("InterruptDriver", "openDriver available: " + String.valueOf(openDriver(driver)));
        if (mConnectFlag) {         // 이미 연결이 되어있는 경우 리턴(탈출)
            return -1;
        }
        if (openDriver(driver) > 0) { // openDriver 가 가능하면
            mConnectFlag = true;    // open device file
            mTranseThread = new TransThread();
            mTranseThread.start();  // create a thread and let it run
            return 1;
        } else {
            return -1;
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void close() {
        if (!mConnectFlag) {    // 이미 연결이 되어있지 않은 경우 리턴(탈출)
            return;
        }
        mConnectFlag = false;
        closeDriver();
    }

    public char read() {
        return readDriver();
    }

    private class TransThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (mConnectFlag) {
                    try {
                        Log.d("SnakeActivity", "Thread 가 돌아감.");
                        onReceive(getInterrupt());
                        Log.d("SnakeActivity", "snakeActivity.onReceive(getInterrupt()) called " + getInterrupt());
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {

            }
        }
    }

}