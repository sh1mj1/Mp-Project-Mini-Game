package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    public static ArrayList<Rank> rankList = new ArrayList<>();
    RecyclerView recyclerView;
    RankRvAdapter adapter;
    public static Context context_rank; // context 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        rankList = new ArrayList<Rank>();
        rankList.add(new Rank(1, 331, "20122711", null));
        rankList.add(new Rank(2, 332, "20221712", null));
        rankList.add(new Rank(3, 333, "20221713", null));
        rankList.add(new Rank(4, 334, "20221714", null));
        rankList.add(new Rank(GameInfo.getGameRank(), GameInfo.getTotalScore(), GameInfo.getNickname(), GameInfo.getImgBitmap()));



        recyclerView = findViewById(R.id.rank_Rv);
        adapter = new RankRvAdapter(this, rankList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


    }
}