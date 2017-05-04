package com.example.bhaum.dditconnect;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;


public class ChatMessage1 {

    public boolean left;
    public String message="";
    public String time;

    public ChatMessage1(boolean left, String message, String time) {
        super();
        this.left = left;
        this.message = message;
        this.time=time;

    }

}
