package com.example.bhaum.dditconnect;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.slideup.SlideUp;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;

import static com.example.bhaum.dditconnect.home.fdb;

public class chatScreen extends AppCompatActivity {
    static ChatArrayAdapter1 chatArrayAdapter1;


    private ListView listView;
    static private AutoCompleteTextView chatText;
    private Button buttonSend;
    static Bitmap myBitmap=null;

    String imagepath="/data/data/com.example.bhaum.dditconnect/dp/";
    TextView chatlastseen;
    static String frd,frddp;
    static TextView frdlastseen,frdname;



    boolean istype=false;
    private StorageReference mStorageRef;
    ImageView userimg;
    public static FirebaseDatabase database;
    ValueEventListener v;







    static DatabaseReference getmsgref,sendmsgref,getproRef;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#01579B"));
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        frd=getIntent().getStringExtra("name");
       // getmeonline();

       // frdname.setText(frd);




        frddp=getIntent().getStringExtra("dp");
        setToolbar(frd);
        checkFrdDp();



        setMeOnline();
            database=FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //  Toast.makeText(getApplicationContext(),"Chat id :"+chatid,Toast.LENGTH_LONG).show();
        getmsgref=database.getReference("user").child(MainActivity.phno).child("friend").child(frd).child("chat");
        sendmsgref=database.getReference("user").child(frd).child("friend").child(MainActivity.phno).child("chat");
        setFrdStatus();

        istype=false;
        chatArrayAdapter1 = new ChatArrayAdapter1(getApplicationContext(), R.layout.activity_chat_singlemessage1);
        chatText=(AutoCompleteTextView)findViewById(R.id.chatText);
        listView = (ListView) findViewById(R.id.listView1);


        buttonSend=(Button)findViewById(R.id.bsend);
        chatlastseen=(TextView)findViewById(R.id.chatlastseen);


        String[] countries = {};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,countries);
        chatText.setAdapter(adapter);


        setKeypressListener();
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter1);

        /*chatArrayAdapter1.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //  Toast.makeText(con,"Changedd...",Toast.LENGTH_SHORT).show();
                listView.setSelection(chatArrayAdapter1.getCount() - 1);
            }
        });*/

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {


                //  Toast.makeText(MainActivity.con,"Send",Toast.LENGTH_SHORT).show();
                String msg=chatText.getText().toString();
                if(!msg.equals("")) {


                    sendMsg(msg);

                }



            }
        });


        setFrdChatStatus();



        v=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    Map<String, String> recmsg = (Map<String, String>) dataSnapshot.getValue();

                    try {
                        chatArrayAdapter1.add(new ChatMessage1(true, recmsg.get("msg"), recmsg.get("time")));

                    } catch (Exception e) {

                    }
                }
                else {


                       /* Iserror = true;
                        Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                        startActivity(i);
                        msgref.child(frd).removeEventListener(v);
                        finish();
                        chatFragment.setRefresh();

                        msgref.child(frd).child("friend").child(MyApplication.myphone).removeValue();
                          */
                    Toasty.error(getApplicationContext(), "Your friend might be delete", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        getMsg();
        setFrdStatus();
        // fetchAudioUrlFromFirebase();


    }



    void checkFrdDp()
    {

        try {
            File imgFile = new File(imagepath +frd + ".jpg");
            if (imgFile.exists()) {
                //  Toast.makeText(getApplicationContext(), "Exist" +imgFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                setDp(imgFile);


            }
            else
            {
                // Toast.makeText(getApplicationContext(), "Not Exist", Toast.LENGTH_LONG).show();

            }
        }
        catch(Exception e)
        {
            // Toast.makeText(getApplicationContext(), "Error "+e.getMessage(), Toast.LENGTH_LONG).show();
            // File f = downloadFile(MainActivity.myphone);

        }
    }






    @RequiresApi(api = Build.VERSION_CODES.N)
    static void sendMsg(final String msg1)
    {
       // final Map<String, Object> msg = new LinkedHashMap<String, Object>();

     //  String msgid=sendmsgref.push().getKey();
        Map<String,String> msgdata=new LinkedHashMap<String, String>();



        msgdata.put("msg",msg1);
        msgdata.put("time",getTimeAndDate());
       // msg.put(msgid,msgdata);



        sendmsgref.setValue(msgdata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                chatArrayAdapter1.add(new ChatMessage1(false, msg1, getTimeAndDate()));
                chatText.setText("");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Myapp.maincontex,"Check Internet",Toast.LENGTH_SHORT).show();
            }
        });



    }

    void getMsg()
    {

        getmsgref.addValueEventListener(v);


    }


    void setFrdChatStatus()
    {
        DatabaseReference myRef = database.getReference("user").child(MainActivity.phno).child("friend").child(frd).child("lastseen");

        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


              //  frdlastseen.setText(getProperTime(dataSnapshot.getValue()+""));

                String data=dataSnapshot.getValue()+"";
                if(data.equals("yes"))
                {
                    chatlastseen.setText("Typing....");
                }
                else if(data.equals("no"))
                {
                    chatlastseen.setText("Offline");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    static String getProperTime(String timeoff)
    {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesday=df.format(cal.getTime());
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());
        if(timeoff.contains(formattedDate))
        {
            timeoff=timeoff.replace(formattedDate,"Today ");
        }
        else if(timeoff.contains(yesday))
        {
            timeoff=timeoff.replace(yesday,"Yesterday ");
        }

        return timeoff;
    }
    void setFrdStatus()
    {
        DatabaseReference myRef = database.getReference("user").child(frd);

        myRef.child("lastseen").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                frdlastseen.setText(getProperTime(dataSnapshot.getValue()+""));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                frdname.setText(dataSnapshot.getValue()+"");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

/*
        myRef.child("dp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    final String dtime = dataSnapshot.getValue() + "";
                    getproRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            try {
                                String dold = dataSnapshot.getValue().toString();
                                if(!dtime.equals(dold))
                                {
                                    downloadFile(frd);
                                    getproRef.setValue(dtime);
                                    // Toast.makeText(getApplicationContext(),"Not same \n"+dtime+"\n"+dold,Toast.LENGTH_SHORT).show();

                                }
                                getproRef.removeEventListener(this);

                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }catch(Exception e)
                {

se                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/

    }
    void setToolbar(String name)
    {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View childLayout = inflater.inflate(R.layout.toolbartype, (ViewGroup) findViewById(R.id.mainroot));

        childLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // createDiloug(myBitmap);

            }
        });
        frdname=(TextView)childLayout.findViewById(R.id.csname);
        frdlastseen=(TextView)childLayout.findViewById(R.id.cslastseen);
        userimg=(ImageView)childLayout.findViewById(R.id.urimg);
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    static String getTimeAndDate()
    {
        Calendar c = Calendar.getInstance();


        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());


        int hr=c.getTime().getHours();
        int min=c.getTime().getMinutes();
        String time="";
        if(hr<=12)
        {  if(hr==0)
        {
            hr=12;
        }
            time=hr+":"+min+" am";
        }
        else {

            hr=hr%12;
            if(hr==0)
            {
                hr=12;
            }
            time=hr+":"+min+" pm";
        }

        String formattedTime = time;
        return  formattedTime + " " + formattedDate;
    }


    public File downloadFile(String dpid) {


        final File f = new File(imagepath+dpid+".jpg");
        mStorageRef.child("userprofilepics/" + dpid + ".jpg").getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                try{
                    setDp(f);

                }catch (Exception e)
                {
                    Toasty.error(getApplicationContext(),"Please restart app...\n Something gose wrong with your mobile",Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userimg.setImageResource(R.drawable.finallogo);
            }
        });


        return f;
    }

    void setDp(File f)
    {
        myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        userimg.setImageBitmap(getRoundedShape(myBitmap));
    }

    Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 100;
        int targetHeight = 100;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    void sendVibrate(final View mView)
    {
        database.getReference(frd).child("vibrate").child(MainActivity.phno).setValue(getRandom()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // sp.play(click, audio.getStreamVolume(audio.STREAM_MUSIC), audio.getStreamVolume(audio.STREAM_MUSIC), 1, 0, 1.0f);

                ObjectAnimator
                        .ofFloat(mView, "translationX", 0, 25, -25, 25, -25,15, -15, 6, -6, 0)
                        .setDuration(2000)
                        .start();
            }
        });


    }

    String getRandom()
    {
        String rand;
        Random r=new Random();
        rand=r.nextInt(9)+ ""+r.nextInt(9)+ ""+r.nextInt(9)+ ""+r.nextInt(9);


        return rand;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setMeOnline();


        //    startService();
        // Toast.makeText(getApplicationContext(),"Chat Resause",Toast.LENGTH_SHORT).show();
    }

    void setMeOnline()
    {


    }
    void setMeOffline()
    {

    }
    @Override
    public void onBackPressed() {


        Intent i = new Intent(getApplicationContext(), Main2Activity.class);
        startActivity(i);
        finish();
        try
        {
        getmsgref.removeEventListener(v);
    }catch (Exception e)
        {

        }

        //chatFragment.setRefresh();
        //finish();




    }




    @Override
    protected void onPause()
    {
        super.onPause();
        //  Toast.makeText(getApplicationContext(),"Pause",Toast.LENGTH_SHORT).show();
        // stopService();

        setMeOffline();try
    {
        getmsgref.removeEventListener(v);
    }catch (Exception e)
    {

    }


    }
    public void setKeypressListener()
    {
        final DatabaseReference myRef =database.getReference("user").child(frd).child("friend").child(MainActivity.phno).child("lastseen");



        chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (chatText.getText().toString().length() > 0) {
                    if (!istype) {
                        // Toast.makeText(Main2Activity.con,"typing",Toast.LENGTH_SHORT).show();
                        myRef.setValue("yes");
                        istype = true;
                    }


                } else {
                    // Toast.makeText(Main2Activity.con,"clear",Toast.LENGTH_SHORT).show();
                    myRef.setValue("no");
                    istype = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected  void onStop()
    {
        super.onStop();


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Toast.makeText(getApplicationContext(),"chat Destroy",Toast.LENGTH_SHORT).show();


    }


    @Override
    protected void onRestart()
    {
        super.onRestart();
        // Toast.makeText(getApplicationContext(),"chat Restart",Toast.LENGTH_SHORT).show();
        setMeOnline();

    }



}
