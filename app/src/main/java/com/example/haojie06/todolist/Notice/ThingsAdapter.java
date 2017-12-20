package com.example.haojie06.todolist.Notice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haojie06.todolist.R;
import com.example.haojie06.todolist.Things;

import java.util.List;

/**
 * Created by haojie06 on 2017/12/19.
 */

public class ThingsAdapter extends RecyclerView.Adapter<ThingsAdapter.ViewHolder> {
    private List<Things> mThingList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView time;
        ImageView status;
        LinearLayout linearLayout;


        public ViewHolder(View view)
        {
            super(view);
            content = (TextView) view.findViewById(R.id.content);
            time = (TextView) view.findViewById(R.id.time);
            status = (ImageView) view.findViewById(R.id.image);
            linearLayout = (LinearLayout) view.findViewById(R.id.layout);
        }
    }

    public ThingsAdapter(List<Things> thingsList)
    {
        mThingList = thingsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.linearLayout.setAlpha(0.5F);
        Things thing = mThingList.get(position);
        holder.content.setText(thing.getContent());
        holder.time.setText(thing.getTime());
        String color = thing.getColor();
        switch (color){
            case "0":
                holder.linearLayout.setBackgroundResource(R.drawable.huise);
                break;

            case "1":
                holder.linearLayout.setBackgroundResource(R.drawable.lvse);
                break;

            case "2":
                holder.linearLayout.setBackgroundResource(R.drawable.lan);
                break;

            case "3":
                holder.linearLayout.setBackgroundResource(R.drawable.huangse);
                break;

            case "4":
                holder.linearLayout.setBackgroundResource(R.drawable.hongse);
                break;

        }

        if (!thing.getClockTime().equals("null"))
        {
            holder.status.setImageResource(R.drawable.clock);
        }

    }

    @Override
    public int getItemCount() {
        return mThingList.size();
    }
}
