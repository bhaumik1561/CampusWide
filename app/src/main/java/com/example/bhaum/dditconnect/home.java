package com.example.bhaum.dditconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import java.text.SimpleDateFormat;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bhaum.dditconnect.dummy.DummyContent1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;

@RequiresApi(api = Build.VERSION_CODES.N)
public class home extends AppCompatActivity implements ItemFragment2.OnListFragmentInteractionListener {
    private boolean PROFANITY_FILTER_ACTIVE = true;
    public  boolean takemeoffline=true;
    List<String> queid, question, name,time;
ImageButton nxt;
    ListView lv;
    EditText que;
    static FirebaseDatabase fdb;
    //String qid=createquestionid();
     String qtime=getTimeAndDate();
   static Context con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setIcon(R.drawable.lg);

        startService(new Intent(getBaseContext(),mynotifications.class));
        nxt=(ImageButton) findViewById(R.id.nxt);
        que=(EditText)findViewById(R.id.que) ;
       // lv=(ListView)findViewById(R.id.lv);
        fdb=FirebaseDatabase.getInstance();
        con=getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
           // window.setStatusBarColor(Color.parseColor("#01579B"));
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        getmeonline();

      nxt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {


                String question=""+que.getText().toString();
                if(!question.equals(""))
                {
                    postQuestion(question);

                }else
                {
                    Toasty.info(getApplicationContext(),"Please Enter Your Question",Toast.LENGTH_LONG).show();
                }



            }
        });


        setQuestionList();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(takemeoffline)
        getmeoffline();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(takemeoffline)
        getmeoffline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getmeonline();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    static void getmeonline()
    {
        fdb.getReference("user").child(MainActivity.phno).child("lastseen").setValue("Online");




    }
   static void getmeoffline()
    {
        fdb.getReference("user").child(MainActivity.phno).child("lastseen").setValue(getTimeAndDate().toString());

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            clearData();
            return true;
        } */

        if(id== R.id.userprofile)
        {
            takemeoffline=false;
            Intent i=new Intent(getApplicationContext(),Main2Activity.class);
            startActivity(i);


        }
        else if(id== R.id.userprofile1)
        {
            takemeoffline=false;
            Intent i=new Intent(getApplicationContext(),userprofile.class);
            startActivity(i);


        }
        else if(id== R.id.userprofile2)
        {
            takemeoffline=false;
            Intent i=new Intent(getApplicationContext(),documents.class);
            startActivity(i);

        }


        return super.onOptionsItemSelected(item);
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
    void setQuestionList()
    {

        android.support.v4.app.FragmentManager ff=getSupportFragmentManager();
        ff.beginTransaction().replace(R.id.mycont1,new ItemFragment2()).commit();

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void postQuestion(String question)
    {
        final ProgressDialog pd=new ProgressDialog(home.this);
        pd.setCancelable(false);
        pd.setTitle("Posting");
        pd.setMessage("Please Wait...");
        pd.show();
        if ((PROFANITY_FILTER_ACTIVE) ) {
            question= question.replaceAll(profanityfilter.censorWords(profanityfilter.ENGLISH), ":)");
        }
        final DatabaseReference myref=fdb.getReference("user").child(MainActivity.phno);
        final DatabaseReference queref=fdb.getReference("questions");
        Map<String,Object> map=new LinkedHashMap<String, Object>();

        map.put("question",question);
        map.put("time",qtime);
        map.put("aid",MainActivity.phno);
        map.put("aname",MainActivity.uname);
        final String qid=queref.push().getKey();
        queref.child(qid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.setMessage("Almost done....");

                myref.child("myquestion").child(qid).setValue(qtime).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        Toasty.success(getApplicationContext(),"Successfully posted ", Toast.LENGTH_LONG).show();
                        que.setText("");

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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



@RequiresApi(api = Build.VERSION_CODES.N)
String createquestionid(){

    Calendar c = Calendar.getInstance();

    SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
    String formattedDate = df.format(c.getTime());


    int hr=c.getTime().getHours();
    int min=c.getTime().getMinutes();
    int sec=c.getTime().getSeconds();
    Random r=new Random();
    int random=r.nextInt(1000);

    return formattedDate+hr+min+sec+random;

}

    @Override
    public void onListFragmentInteraction(DummyContent1.DummyItem1 item) {

    }
}
