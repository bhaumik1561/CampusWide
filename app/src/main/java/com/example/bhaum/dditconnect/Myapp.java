package com.example.bhaum.dditconnect;

import android.app.Application;
import android.content.Context;

/**
 * Created by bhaum on 18-03-2017.
 */

public class Myapp  extends Application{

    static String aid;
   // static String myphone=MainActivity.phno;
  //  static String aname=MainActivity.uname;;

    public static Context maincontex;
    public void onCreate() {
        super.onCreate();
        maincontex=getApplicationContext();
    }


}
