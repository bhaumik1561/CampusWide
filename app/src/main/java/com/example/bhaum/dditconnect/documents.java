package com.example.bhaum.dditconnect;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class documents extends AppCompatActivity {

    LinearLayout tafl,cn,nis,oose,soa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tafl=(LinearLayout) findViewById(R.id.tafl);
        nis=(LinearLayout) findViewById(R.id.nis);
        soa=(LinearLayout) findViewById(R.id.soa);
        oose=(LinearLayout) findViewById(R.id.oose);
        cn=(LinearLayout) findViewById(R.id.cn);
        tafl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),documentspost.class);
                i.putExtra("sub","tafl");

                startActivity(i);
            }
        });
        nis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),documentspost.class);
                i.putExtra("sub","nis");

                startActivity(i);

            }
        });
        oose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),documentspost.class);
                i.putExtra("sub","oose");

                startActivity(i);


            }
        });

        cn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),documentspost.class);
                i.putExtra("sub","cn");

                startActivity(i);
            }
        });
        soa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),documentspost.class);
                i.putExtra("sub","soa");

                startActivity(i);

            }
        });




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

}
