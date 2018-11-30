package com.example.jjnjs.gamersonline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Item> mItem;

    public RecyclerViewAdapter(Context mContext, ArrayList<Item> mItem) {
        this.mContext = mContext;
        this.mItem = mItem;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_chat_other,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.item.setText(mItem.get(position).name);
        holder.image.setImageBitmap(mItem.get(position).image);

        holder.cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OverviewInterface interface2 = (OverviewInterface)mContext;
                interface2.useItem(mItem.get(position).id);
                //Toast.makeText(mContext,String.valueOf(mItem.get(position).id), Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

            TextView item;
            ImageView image;
            LinearLayout cont;

            public MyViewHolder(View itemView) {
                super(itemView);

                item = (TextView) itemView.findViewById(R.id.itemname);
                image = (ImageView) itemView.findViewById(R.id.item_image);
                cont = (LinearLayout) itemView.findViewById(R.id.container1);

            }
    }
}
