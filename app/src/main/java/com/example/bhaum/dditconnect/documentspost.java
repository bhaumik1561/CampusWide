package com.example.bhaum.dditconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.esafirm.rxdownloader.RxDownloader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.slideup.SlideUp;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class documentspost extends AppCompatActivity {
    EditText docname,docdesc;
    Button upload,choosefile;
    int image_req=1234;
    FirebaseDatabase fdb;
    Uri filepath;
    StorageReference mStorageReference;
    String subname,fileid;

    List<String> name;

    DatabaseReference myref,my1;


    SlideUp slideUp1;
    Map<String, Object> mm,main,temp;
    ListView lv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentspost);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name=new ArrayList<>();
        docdesc=(EditText) findViewById(R.id.docdesc);
        docname=(EditText) findViewById(R.id.docname);
        upload=(Button) findViewById(R.id.upload);
        choosefile=(Button) findViewById(R.id.choosefile);
        lv1=(ListView)findViewById(R.id.lv1);
        fdb=FirebaseDatabase.getInstance();
        subname=getIntent().getStringExtra("sub");
        mStorageReference= FirebaseStorage.getInstance().getReference();
        my1 = fdb.getReference("documents").child(subname);
        main=new LinkedHashMap<>();
        final ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(documentspost.this, android.R.layout.simple_list_item_1, name);
        lv1.setAdapter(itemsAdapter);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                temp=(Map<String, Object>)main.get(""+i);

                downloader(""+temp.get("uri"),""+temp.get("name"));
                Toast.makeText(getApplicationContext(),"click : "+temp.get("uri")+"\n name : "+temp.get("name"),Toast.LENGTH_LONG).show();

            }
        });
        my1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    mm = (Map<String, Object>) dataSnapshot.getValue();
                //    Toast.makeText(getApplicationContext(),"Vlaue : "+mm,Toast.LENGTH_LONG).show();
                    List<String> listofname = new ArrayList<String>();
                    listofname.addAll(mm.keySet());
                    if(name.size()>0)
                    {
                        name.clear();
                    }
                    try {

                        for (int i = 0; i < listofname.size(); i++) {

                            Map<String, Object> documents = (Map<String, Object>) mm.get(listofname.get(i));
                            name.add(documents.get("name") + "");
                            main.put(name.size()-1  + "",documents);


                            itemsAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {

                    }
                }catch(Exception e)
                {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                fileid=createfileid();
                uploadFile(filepath);



            }
        });
        choosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChooser();

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
    private void ImageChooser(){
        Intent intent= new Intent();
        intent.setType("file/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select file"),image_req);



    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==image_req && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            filepath=data.getData();

        }

        }










    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadFile(final Uri filePath) {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(documentspost.this);
            progressDialog.setTitle("Uploading");

            progressDialog.setCancelable(false);
            progressDialog.show();

            //bnp.setVisibility(View.VISIBLE);

            final StorageReference riversRef = mStorageReference.child("documents/"+subname+"/"+MainActivity.phno+"/"+fileid+"/"+getFileName(filePath));
         final    String qid=getFileName(filePath);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String qtime=getTimeAndDate();
                            progressDialog.setMessage("Almost done !!!");
                      /*      DatabaseReference myref=fdb.getReference("documents").child(subname).child(MainActivity.phno).child(fileid);
                            Map<String,Object> map=new LinkedHashMap<String, Object>();
                           map.put("filename",getFileName(filePath));
                            map.put("title",docname.getText());
                            map.put("description",docdesc.getText());
                            map.put("time",qtime);
                            map.put("aid",MainActivity.phno);
                            map.put("aname",MainActivity.uname);
                            */

                            Uri url=taskSnapshot.getMetadata().getDownloadUrl();

                            progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Successfully Uploaded !!", Toast.LENGTH_LONG).show();
                            DatabaseReference myref=fdb.getReference("documents").child(subname);

                            Map<String,Object> map=new LinkedHashMap<String, Object>();
                            map.put("uri",url+"");
                            map.put("name",docname.getText().toString());

                            myref.push().setValue(map);




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toasty.error(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT, true).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            progressDialog.dismiss();
                        }
                    });
        }

        else {

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    String createfileid(){

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
    public void downloader(String uri,String fname){
        String url = uri;

        RxDownloader.getInstance(documentspost.this)
                .download(url, Environment.getDataDirectory()+"/DDIT CONNECT" + "/" + fname , getMimeType(uri)); // url, filename, and mimeType



    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }



}
