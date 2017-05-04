package com.example.bhaum.dditconnect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;

/**
 * Created by bhaum on 06-04-2017.
 */

public class mynotifications extends Service {
    FirebaseDatabase fdb;
    DatabaseReference myref;





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Toasty.success(Myapp.maincontex,"Service start",Toast.LENGTH_LONG).show();
            onaddquestion();



        return START_STICKY;
    }



    private void sendNotification(String message, String tick, String title,String qqid, boolean sound, boolean vibrate, int iconID) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("qid",qqid);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setLights(Color.BLUE, 500, 500);
        if (sound) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        }

        if (vibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        notificationBuilder.setDefaults(notification.defaults);
        notificationBuilder.setSmallIcon(iconID)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setTicker(tick)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random r=new Random();

        notificationManager.notify(r.nextInt(50) /* ID of notification */, notificationBuilder.build());
    }

    public void showNotification(String qtitle,String Auth)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
      //  mBuilder.setSmallIcon(R.drawable.ic_plus_one_black_24dp);
        mBuilder.setContentTitle(Auth);
        mBuilder.setContentText(qtitle);
        Intent resultIntent = new Intent(this, home.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(home.class);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


// notificationID allows you to update the notification later on.
        int notificationID=0;
        mNotificationManager.notify(notificationID, mBuilder.build());



// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);


    }

    public void onaddquestion()
    {
     fdb=FirebaseDatabase.getInstance();
        myref=fdb.getReference("questions");
        myref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Map<String,Object> qus=(Map<String, Object>) dataSnapshot.getValue();
                if(qus.get("read")!=null)
                {
                    Map<String,String> allreads=(Map<String, String>)qus.get("read");
                    if(!allreads.containsKey(MainActivity.phno))
                    {
                        dataSnapshot.getRef().child("read").child(MainActivity.phno).setValue("read");
                        sendNotification(qus.get("question")+"",qus.get("aname")+"",qus.get("aname")+"",dataSnapshot.getKey(),true,true,R.drawable.finallogo);
                        //Toasty.success(Myapp.maincontex,"New Question Post :"+dataSnapshot.getRef(),Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    dataSnapshot.getRef().child("read").child(MainActivity.phno).setValue("read");
                    sendNotification(qus.get("question")+"",qus.get("aname")+"",qus.get("aname")+"",dataSnapshot.getKey(),true,true,R.drawable.finallogo);
                   // Toasty.success(Myapp.maincontex,"New Question Post :"+dataSnapshot.getRef(),Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

               // Toasty.info(Myapp.maincontex," Question Change :"+dataSnapshot.getValue(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                //Toasty.error(Myapp.maincontex,"delete :"+dataSnapshot.getValue(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
