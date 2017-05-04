package com.example.bhaum.dditconnect;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import java.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.slideup.SlideUp;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;

import static com.example.bhaum.dditconnect.home.getmeonline;

public class Main2Activity extends AppCompatActivity {
    ListView contact,lv2;
    List<String> name,chatname;

    FirebaseDatabase fdb;
    DatabaseReference myref,myref1;
    //....

    SlideUp slideUp1;
    Map<String, Object> mm,main,mm1,main1;

    //....


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        super.onPause();
        home.getmeoffline();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {
        super.onStop();
        home.getmeoffline();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        home.getmeonline();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = new ArrayList<>();
        chatname=new ArrayList<>();
        fdb = FirebaseDatabase.getInstance();
        myref = fdb.getReference("user");
        myref1=fdb.getReference("user").child(MainActivity.phno).child("friend");
        contact = (ListView) findViewById(R.id.contact);
        lv2=(ListView)findViewById(R.id.lv2);
        main=new LinkedHashMap<>();
        main1=new LinkedHashMap<>();
        final ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(Main2Activity.this, android.R.layout.simple_list_item_1, name);
        contact.setAdapter(itemsAdapter);
        final ArrayAdapter<String> itemsAdapter1 =
                new ArrayAdapter<String>(Main2Activity.this, android.R.layout.simple_list_item_1, chatname);
        lv2.setAdapter(itemsAdapter1);
        myref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{

                        Map<String, Object> user1 = (Map<String, Object>) dataSnapshot.getValue();
                    user1.put("uid",dataSnapshot.getKey());
                        main1.put(user1.get("name")+"",user1);

                        chatname.add(user1.get("name") + "");

                        itemsAdapter1.notifyDataSetChanged();


                    }
                catch (Exception e)
                {

                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv=(TextView)view;
                try {
                    Map<String, Object> umap = (Map<String, Object>) main1.get(tv.getText().toString());
                     String uid=""+umap.get("uid");
                    //  createChat(MainActivity.phno,uid,tv.getText().toString());
                 //   Toast.makeText(getApplicationContext(), main1.get(tv.getText().toString()).toString(), Toast.LENGTH_LONG).show();

               Intent ii = new Intent(getApplicationContext(), chatScreen.class);
                ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                ii.putExtra("chatid", "");
                ii.putExtra("name", uid);
                ii.putExtra("dp", "0");

                startActivity(ii);
                }
                catch(Exception e)
                {

                }

            }
        });





        contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                home.getmeonline();
                TextView tv=(TextView)view;
                String uid=""+main.get(tv.getText().toString());
                createChat(MainActivity.phno,uid,tv.getText().toString());
              //  Toast.makeText(getApplicationContext(),""+tv.getText()+" ---- "+i,Toast.LENGTH_LONG).show();

            }
        });
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mm = (Map<String, Object>) dataSnapshot.getValue();
                List<String> listofname = new ArrayList<String>();
                listofname.addAll(mm.keySet());
                if(name.size()>0)
                {
                    name.clear();
                }
                try{

                for (int i = 0; i < listofname.size(); i++) {

                    Map<String, Object> user = (Map<String, Object>) mm.get(listofname.get(i));
                    main.put(user.get("name")+"",""+listofname.get(i));

                    name.add(user.get("name") + "");

                    itemsAdapter.notifyDataSetChanged();


                }}
                catch (Exception e)
                {

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        slideUp1 = new SlideUp.Builder(findViewById(R.id.conlist))
                .withListeners(new SlideUp.Listener() {
                    @Override
                    public void onSlide(float percent) {

                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {

                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .build();


        findViewById(R.id.slup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slideUp1.isVisible()) {
                    slideUp1.hide();
                } else {

                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (slideUp1.isVisible()) {
                    slideUp1.hide();
                } else {
                    slideUp1.show();
                }
                //  Toast.makeText(getApplicationContext(),"Click : "+slideUp1.isVisible(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    void createChat(final String my,final String you,final String frdname)
    {


            final ProgressDialog loading=new ProgressDialog(Main2Activity.this);
            loading.setMessage("Start Messaging....");
            // loading = ProgressDialog.show(getActivity(), "Staring....", null, true, true);
            loading.setCancelable(false);
            loading.show();
            final DatabaseReference myRef = fdb.getReference();
            myRef.child("user").child(my).child("friend").child(you).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        // Toasty.info(getActivity()," Chat  : "+chatid,Toast.LENGTH_LONG).show();

                        myRef.child("user").child(my).child("friend").child(you).removeEventListener(this);
                        Intent i = new Intent(getApplicationContext(), chatScreen.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("chatid", "");
                        i.putExtra("name", you);
                        i.putExtra("dp", "0");

                        startActivity(i);
                        loading.dismiss();



                    } else {

                        myRef.child("user").child(my).child("friend").child(you).removeEventListener(this);

                        newChat(myRef,my,you,loading,frdname);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();Intent i = new Intent(getApplicationContext(), home.class);
        startActivity(i);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void newChat(final DatabaseReference myRef, final String my, final String you,final ProgressDialog loading,final String frdname)
    {
        final Map<String, Object> msg = new LinkedHashMap<String, Object>();
        final Map<String, Object> frdmsg = new LinkedHashMap<String, Object>();




        Map<String,Object> msgss=new LinkedHashMap<>();
        msgss.put("msg", "hey !");
        msgss.put("time", getTimeAndDate());

        msg.put("lastseen","offline");
        msg.put("chat",msgss);
        msg.put("name",frdname);


        frdmsg.put("lastseen","offline");
        frdmsg.put("chat",msgss);
        frdmsg.put("name",MainActivity.uname);

        myRef.child("user").child(my).child("friend").child(you).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //  Map<String,String> smallmaps=new LinkedHashMap<String, String>();
                //smallmaps.put("type","no");

                myRef.child("user").child(you).child("friend").child(my).setValue(frdmsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //  Toast.makeText(MyApplication.maincontex,"Chat created",Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getApplicationContext(), chatScreen.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        i.putExtra("name", you);

                        i.putExtra("dp", "0");
                        startActivity(i);


                        loading.dismiss();
                    }
                });





            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Myapp.maincontex, "Please check Internet", Toast.LENGTH_LONG).show();

            }
        });

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



}