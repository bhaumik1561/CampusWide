package com.example.bhaum.dditconnect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;

public class userprofile extends AppCompatActivity {
    Bitmap bits;
    String imagepath="/data/data/com.example.bhaum.dditconnect/dp/";
    File f1;
    CircleImageView ib1;
    StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView name=(TextView) findViewById(R.id.name);
        TextView sem=(TextView) findViewById(R.id.sem);
        TextView id1=(TextView) findViewById(R.id.id1);
        //TextView name=(TextView) findViewById(R.id.name);
        name.setText(MainActivity.uname);
        sem.setText(MainActivity.usem);   id1.setText(MainActivity.uemail);

        ib1=(CircleImageView) findViewById(R.id.ib1);
      //  Uri myuri=  Uri.parse("gs://dditconnect.appspot.com/userprofilepics/"+MainActivity.phno+".jpg");
        //ib1.setImageURI(myuri);

        try {
            f1 = new File(imagepath +MainActivity.phno + ".jpg");



            if (f1.exists()) {
                // Toasty.info(home.con,"file yes", Toast.LENGTH_SHORT).show();

                Glide.with(ib1.getContext())
                        .load(f1)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade()
                        .into(ib1);

                //   holder.imageView.setImageBitmap(BitmapFactory.decodeFile(f1.getAbsolutePath()));

            }
            else {
                // Toasty.info(home.con,"file not", Toast.LENGTH_SHORT).show();


                downloadFile(MainActivity.phno,ib1);
            }
        }
        catch(Exception e)
        {
            // Toast.makeText(getApplicationContext(), "Error "+e.getMessage(), Toast.LENGTH_LONG).show();
            //  File f = downloadFile(MainActivity.);

        }













        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }
    public File downloadFile(String dpid,final CircleImageView imageView) {

        File ff=new File(imagepath);
        if(!ff.exists())
        {
            ff.mkdir();
            // Toasty.info(home.con,"Directory Not exist", Toast.LENGTH_SHORT).show();
        }


        //  Toasty.info(home.con,"Download file "+dpid, Toast.LENGTH_SHORT).show();
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

}
