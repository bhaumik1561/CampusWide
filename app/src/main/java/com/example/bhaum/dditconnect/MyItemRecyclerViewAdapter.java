package com.example.bhaum.dditconnect;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bhaum.dditconnect.ItemFragment.OnListFragmentInteractionListener;
import com.example.bhaum.dditconnect.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    String imagepath="/data/data/com.example.bhaum.dditconnect/dp/";
    File f1;
    StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
    public MyItemRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
        holder.mtime.setText(mValues.get(position).details);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        try {
           f1 = new File(imagepath +mValues.get(position).aid + ".jpg");
            ImageView imageView = holder.imageView;

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

                holder.imageView.setImageResource(R.drawable.profile);
              //  downloadFile(mValues.get(position).aid,holder.imageView);
            }
        }
        catch(Exception e)
        {
            // Toast.makeText(getApplicationContext(), "Error "+e.getMessage(), Toast.LENGTH_LONG).show();
            // File f = downloadFile(MainActivity.myphone);

        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;
        public TextView mtime;
        public CircleImageView imageView,profile;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mtime=(TextView)view.findViewById(R.id.timss);
            imageView=(CircleImageView)view.findViewById(R.id.imageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
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
}
