package com.example.bhaum.dditconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.example.bhaum.dditconnect.R.id.phn;
import static com.example.bhaum.dditconnect.R.id.signup;

public class Signup extends AppCompatActivity {
    Button signup;
    EditText name,email,sem,division,id;
    FirebaseDatabase fdb;
    SQLiteDatabase mydb=null;
    static String dbname="dditinfo.db";
    String dbpath="/data/data/com.example.bhaum.dditconnect/databases/";
    int image_req=111;
    Button b;
    ImageView ib;
    Bitmap bits;
    StorageReference mStorageReference;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name=(EditText) findViewById(R.id.name);
        email=(EditText) findViewById(R.id.email);
        sem=(EditText) findViewById(R.id.sem);
        id=(EditText) findViewById(R.id.uid);
        ib=(ImageView) findViewById(R.id.ib);
        division=(EditText) findViewById(R.id.division);
        signup=(Button)findViewById(R.id.signup);
        mStorageReference= FirebaseStorage.getInstance().getReference();

        fdb=FirebaseDatabase.getInstance();
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                uploadFile(filePath);

            }
        });

    }
    void signup(final ProgressDialog pd)
    {
        pd.setTitle("Registering...");
        pd.setMessage("Almost done....");
        pd.show();
        Map<String,Object> map=new LinkedHashMap<String, Object>();
        map.put("name",name.getText().toString());
        map.put("email",email.getText().toString());
        map.put("sem",sem.getText().toString());
        map.put("division",division.getText().toString());
        map.put("id",id.getText().toString());
        DatabaseReference myref=fdb.getReference("user");
        myref=myref.child(MainActivity.phno);

        myref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                MainActivity.uname=name.getText().toString();
                MainActivity.uemail=email.getText().toString();
                addDatatoTable(MainActivity.phno,name.getText().toString(),email.getText().toString(),sem.getText().toString(),division.getText().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_LONG).show();
            }
        });


    }
    private void ImageChooser(){
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image"),image_req);



    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==image_req && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            try{

                startCropImageActivity(data.getData());
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"hello"+e.getMessage(),Toast.LENGTH_SHORT).show();

                e.printStackTrace();
                bits=null;
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                // Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
                try {
                    filePath = result.getUri();
                    bits = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    ib.setImageBitmap(bits);

                          //uploadFile(filePath);


                }
                catch(Exception e)
                {
                    bits=null;
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toasty.error(this, "Cropping failed\nTry again", Toast.LENGTH_LONG).show();
            }
        }

    }

    void addDatatoTable(String phn,String nm,String em,String sm,String dv)
    {

        SQLiteDatabase mydb=null;

        String mypath = dbpath + dbname;

        try {

            mydb = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);

            // mydb.execSQL("delete from device_info");


           mydb.execSQL("insert into userinfo values('"+phn+"','"+nm+"','"+em+"','"+sm+"','"+dv+"')");
            mydb.close();
            Intent i=new Intent(getApplicationContext(),home.class);
            startActivity(i);
            finish();

        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }



    }


    private void uploadFile(Uri filePath) {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
           final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");

            progressDialog.setCancelable(false);
            progressDialog.show();

            //bnp.setVisibility(View.VISIBLE);

            StorageReference riversRef = mStorageReference.child("userprofilepics/"+MainActivity.phno+".jpg");

            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog


                            //  copyImage(new File(getPathofImage(filePath)));
                            //and displaying a success toast
                            // fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff008d")));
                           // Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                           signup(progressDialog);
                            // Toast.makeText(getApplicationContext(),"Successfully changed !!",Toast.LENGTH_SHORT).show();
                          //  Toasty.success(getApplicationContext(), "Successfully changed !!", Toast.LENGTH_LONG, true).show();

                            //Toast.makeText(getApplicationContext(), "Just a moment.. Almost done !!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            Toasty.error(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT, true).show();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }

        else {

        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setActivityTitle("Set Size")
                .setBorderCornerColor(Color.BLUE)
                .setBorderCornerLength(50)
                .setBorderCornerThickness(5)
                .setAspectRatio(1,1)
                .setGuidelinesColor(Color.parseColor("#00000000"))
                .setBackgroundColor(Color.parseColor("#7f000000"))
                .setCropShape(CropImageView.CropShape.OVAL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setOutputCompressQuality(35)
                .start(this);
    }


}
