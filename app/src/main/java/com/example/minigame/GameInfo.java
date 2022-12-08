package com.example.minigame;

public class GameInfo {

    // global 변수 gameStage
    private static Integer gameStage = 0;
    // global 변수 누적 점수
    private static Integer totalScore = 0;

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
}