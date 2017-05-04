package com.example.bhaum.dditconnect;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import java.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bhaum.dditconnect.dummy.DummyContent;
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

import java.io.File;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class answer extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {
    private boolean PROFANITY_FILTER_ACTIVE = true;
    Button send;
    TextView question,ansname,anstime;
    CircleImageView pro;
    AutoCompleteTextView ans;
    FirebaseDatabase fdb;
    DatabaseReference myref;
    ValueEventListener val;
  static String qid;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ans=(AutoCompleteTextView) findViewById(R.id.ans);
        send=(Button) findViewById(R.id.send);
        anstime=(TextView) findViewById(R.id.anstime);
        question=(TextView) findViewById(R.id.question);
        ansname=(TextView) findViewById(R.id.ansname);
        fdb=FirebaseDatabase.getInstance();
        pro=(CircleImageView)findViewById(R.id.pro);
        myref=fdb.getReference("questions");

        Intent i=getIntent();
         qid=i.getStringExtra("qid");
        myref.child(qid).child("view").child(MainActivity.phno).setValue(getTimeAndDate());


    //    Toasty.info(getApplicationContext(),"Question : "+qid, Toast.LENGTH_LONG).show();

        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(!ans.getText().toString().equals(""))
                {
                    postAns(ans.getText().toString());
                }
            }
        });

        setAnswerList();
        val=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                //   Toasty.info(getApplicationContext(),map+"",Toast.LENGTH_LONG).show();



                ansname.setText(map.get("aname")+"");
                question.setText(map.get("question")+"");
                anstime.setText(map.get("time")+"");
                setdp(map.get("aid")+"");



            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        setQuestion(qid);
    }
    String imagepath="/data/data/com.example.bhaum.dditconnect/dp/";
    StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
    void setdp(String aid){
        try {

           File f1 = new File(imagepath +aid + ".jpg");
            ImageView imageView = pro;


            if (f1.exists()) {
                // Toasty.info(home.con,"file yes", Toast.LENGTH_SHORT).show();

                Glide.with(imageView.getContext())
                        .load(f1)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade()
                        .into(imageView);

                //   holder.imageView.setImageBitmap(BitmapFactory.decodeFile(f1.getAbsolutePath()));

            }
            else {
                // Toasty.info(home.con,"file not", Toast.LENGTH_SHORT).show();

               pro.setImageResource(R.drawable.profile);
                downloadFile(aid,pro);
            }
        }
        catch(Exception e) {
            // Toast.makeText(getApplicationContext(), "Error "+e.getMessage(), Toast.LENGTH_LONG).show();
            //  File f = downloadFile(MainActivity.);
        }

        }
    public File downloadFile(String dpid,final CircleImageView imageView) {

        File ff=new File(imagepath);
        if(!ff.exists())
        {
            ff.mkdir();
            Toasty.info(home.con,"Directory Not exist", Toast.LENGTH_SHORT).show();
        }


        Toasty.info(home.con,"Download file "+dpid, Toast.LENGTH_SHORT).show();
        final File f = new File(imagepath+dpid+".jpg");
        mStorageRef.child("userprofilepics/" + dpid + ".jpg").getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(), "Download complete", Toast.LENGTH_SHORT).show();
                try{
                    imageView.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));


                }catch (Exception e)
                {
                    imageView.setImageResource(R.drawable.profile);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageResource(R.drawable.profile);
            }
        });


        return f;
    }





    void setAnswerList()
    {

        android.support.v4.app.FragmentManager ff=getSupportFragmentManager();
        ff.beginTransaction().replace(R.id.mycont,new ItemFragment()).commit();

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void postAns(String ans)
    {
        if ((PROFANITY_FILTER_ACTIVE) ) {
            ans= ans.replaceAll(profanityfilter.censorWords(profanityfilter.ENGLISH), ":)");
        }

        Map<String,Object> ansss=new LinkedHashMap<>();
        ansss.put("time",getTimeAndDate());
        ansss.put("ans",ans);
        ansss.put("aname",MainActivity.uname);
        ansss.put("aid",MainActivity.phno);
       final ProgressDialog p=new ProgressDialog(answer.this);
        p.setCancelable(false);
        p.setMessage("Please wait..");
        p.setTitle("Posting answer");
        p.show();

        myref.child(qid).child("answer").child(getAnsId()).setValue(ansss).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                p.dismiss();
                Toasty.success(getApplicationContext(),"Answer Posted Successfully",Toast.LENGTH_SHORT).show();
                AutoCompleteTextView ans=(AutoCompleteTextView)findViewById(R.id.ans);
                ans.setText("");

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
    String getAnsId(){

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
    void setQuestion(String que)
    {

        myref.child(que).addValueEventListener(val);


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        myref.child(qid).removeEventListener(val);
       // Toasty.info(getApplicationContext(),"back clcik",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
