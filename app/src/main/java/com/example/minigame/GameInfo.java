package com.example.minigame;

import android.graphics.Bitmap;

public class GameInfo {

    private static Integer gameRank = 0;

    // global 변수 gameStage
    private static Integer gameStage = 0;
    // global 변수 누적 점수
    private static Integer totalScore = 0;

    private static Bitmap imgBitmap = null;

    private static String nickname = "";

    public static Integer getGameStage() {
        return gameStage;
    }

    public static void setGameStage(Integer gameStage) {
        GameInfo.gameStage = gameStage;
    }

    public static Integer getTotalScore() {
        return totalScore;
    }

    public static void setTotalScore(Integer totalScore) {
        GameInfo.totalScore = totalScore;
    }

    public static Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public static void setImgBitmap(Bitmap imgBitmap) {
        GameInfo.imgBitmap = imgBitmap;
    }

    public static String getNickname() {
        return nickname;
    }

    public static void setNickname(String nickname) {
        GameInfo.nickname = nickname;
    }

    public static Integer getGameRank() {
        return gameRank;
    }

    public static void setGameRank(Integer gameRank) {
        GameInfo.gameRank = gameRank;
    }
}