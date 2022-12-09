package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity implements RankRvAdapter.onListItemSelectedInterface {

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
        adapter = new RankRvAdapter(this, rankList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


    }

    @Override
    public void onItemSelected(View v, int position) {
        Toast.makeText(getApplicationContext(), "position : " + position, Toast.LENGTH_SHORT).show();


        GameInfo.setGameRank(rankList.get(position).getGameRank());
        GameInfo.setTotalScore(rankList.get(position).getGameScore());
        GameInfo.setNickname(rankList.get(position).getNickname());
        GameInfo.setImgBitmap(rankList.get(position).getGameImg());

        Intent intent = new Intent(getApplicationContext(), SomeoneRankActivity.class);
        startActivity(intent);

    }
}