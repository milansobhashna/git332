package com.example.git332.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.example.git332.AndroidVersion;
import com.example.git332.R;
import com.example.git332.test;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private ArrayList<AndroidVersion> android_versions;
    private Context mContext;

    public DataAdapter(Context context,ArrayList<AndroidVersion> android_versions) {
        this.android_versions = android_versions;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout,viewGroup,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.img_android.setImageResource(R.drawable.back);
        Picasso.with(mContext).load(android_versions.get(i).getImg_id()).resize(200,200).into(viewHolder.img_android);

       viewHolder.gridLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
           public void onClick(View v) {

                Intent intent;
                intent=new Intent(mContext, test.class);
                intent.putExtra("url",android_versions.get(i).getImg_id());
                mContext.startActivities(new Intent[]{intent});
            }
        });
    }

    @Override
    public int getItemCount() {
        return android_versions.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img_android;
        GridLayout gridLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_android=(ImageView)itemView.findViewById(R.id.img_android);
            gridLayout=(GridLayout)itemView.findViewById(R.id.gridlayout_id);

        }

    }


}
