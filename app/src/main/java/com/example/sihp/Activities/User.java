package com.example.sihp.Activities;

import android.support.v4.content.ContextCompat;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;

public class User
{
    public String name,email;
    public int imageId;

    public User(String name, String email, int imageId) {
        this.name = name;
        this.email = email;
        this.imageId = imageId;
    }


    public static void loadImage(View view, int imageId)
    {
        CircleImageView circleImageView = (CircleImageView) view;
        circleImageView.setImageDrawable(ContextCompat.getDrawable(view.getContext(),imageId));

    }
}
