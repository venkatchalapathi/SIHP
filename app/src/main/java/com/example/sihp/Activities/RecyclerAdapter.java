/*
package com.example.sihp.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private Context context;
    private List<User> users = new ArrayList<>();

    public RecyclerAdapter(Context context, List<User> users)
    {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        */
/*ItemLayoutBinding itemLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),R.layout.item_layout,viewGroup,false);

        MyViewHolder myViewHolder = new MyViewHolder(itemLayoutBinding);
        return myViewHolder;*//*

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i)
    {
        User user = users.get(i);
        //viewHolder.itemLayoutBinding.setUser(user);



    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        //ItemLayoutBinding itemLayoutBinding;

        public MyViewHolder(@NonNull ItemLayoutBinding itemView)

        {
            super(itemView.getRoot());
            itemLayoutBinding = itemView;

        }
    }

}
*/
