package com.example.minigame;

import android.graphics.Bitmap;

class Rank {

    private Integer gameRank = 0;
    // global 변수 누적 점수
    private Integer gameScore = 0;

    private String nickname = null;

    private Bitmap gameImg = null;

    public Rank(int gameRank, int gameScore, String nickname, Bitmap gameImg) {
        this.gameRank = gameRank;
        this.gameScore = gameScore;
        this.nickname = nickname;
        this.gameImg = gameImg;
    }

    public Rank() {

    }

    public Integer getGameRank() {
        return gameRank;
    }

    public void setGameRank(Integer gameRank) {
        this.gameRank = gameRank;
    }

    public Integer getGameScore() {
        return gameScore;
    }

    public void setGameScore(Integer gameScore) {
        this.gameScore = gameScore;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Bitmap getGameImg() {
        return gameImg;
    }

    public void setGameImg(Bitmap gameImg) {
        this.gameImg = gameImg;
    }
}
