package com.example.minigame;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RankRvAdapter extends RecyclerView.Adapter<RankRvAdapter.MyViewHolder> {

    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
    String TAG = "RecyclerViewAdapter";

    public interface onListItemSelectedInterface{
        void onItemSelected(View v, int position);
    }

    private onListItemSelectedInterface mListener;
    Context mContext;



    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<Rank> rankArrayList;
    Context context;

    //생성자를 통하여 데이터 리스트 context를 받음
    public RankRvAdapter(Context context, ArrayList<Rank> rankArrayList, onListItemSelectedInterface listener) {
        this.rankArrayList = rankArrayList;
        this.context = context;
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함
        return rankArrayList.size();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");

        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rank_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,  int position) {
        Log.d(TAG, "onBindViewHolder");

        holder.rankItemTv.setText(rankArrayList.get(position).getGameRank().toString());
        holder.rankItemScore.setText(rankArrayList.get(position).getGameScore().toString());
        holder.rankItemNickname.setText(rankArrayList.get(position).getNickname());



    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rankItemTv;
        TextView rankItemScore;
        TextView rankItemNickname;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rankItemTv = itemView.findViewById(R.id.rank_item_tv);
            rankItemScore = itemView.findViewById(R.id.rank_item_score);
            rankItemNickname = itemView.findViewById(R.id.rank_item_nickname);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemSelected(view, getAdapterPosition());
//                    Toast.makeText(context, "position : " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }





}
