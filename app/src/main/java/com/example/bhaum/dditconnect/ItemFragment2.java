package com.example.bhaum.dditconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bhaum.dditconnect.dummy.DummyContent;
import com.example.bhaum.dditconnect.dummy.DummyContent1;
import com.example.bhaum.dditconnect.dummy.DummyContent1.DummyItem1;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment2 extends Fragment implements RecyclerItemClickListener.OnItemClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    FirebaseDatabase fdb;
    DatabaseReference myref;
    RecyclerView recyclerView;
    MyItemRecyclerViewAdapter2 adpter;
    ChildEventListener chd;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment2() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment2 newInstance(int columnCount) {
        ItemFragment2 fragment = new ItemFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list2, container, false);
        fdb=FirebaseDatabase.getInstance();

        myref=fdb.getReference("questions");
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adpter=new MyItemRecyclerViewAdapter2(DummyContent1.ITEMS, mListener);
            recyclerView.setAdapter(adpter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(home.con,this));
        }
        chd=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> mm=(Map<String, Object>)dataSnapshot.getValue();
                List<String> ansid = new ArrayList<>();
                try {
                    Map<String, Object> ans = (Map<String, Object>) mm.get("answer");

                    ansid.addAll(ans.keySet());
                }
                catch(Exception e)
                {

                } List<String> viewid = new ArrayList<>();
                try {
                    Map<String, Object> vie = (Map<String, Object>) mm.get("view");

                    viewid.addAll(vie.keySet());
               //     Toast.makeText(getActivity(), "here : "+ vie,Toast.LENGTH_LONG).show();
                   // d.noans="View ("+ansid.size()+")";
                }
                catch(Exception e)
                {


                }

                DummyContent1.addItem(new DummyItem1(mm.get("aid")+"",""+dataSnapshot.getKey(),""+mm.get("aname"),""+mm.get("question"),""+mm.get("time"),"Comments ("+ansid.size()+")","View("+viewid.size()+")"));

                adpter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Map<String,Object> mm=(Map<String, Object>)dataSnapshot.getValue();
                DummyItem1 d=DummyContent1.ITEM_MAP.get(dataSnapshot.getKey()+"");

                List<String> ansid = new ArrayList<>();
                try {
                    Map<String, Object> ans = (Map<String, Object>) mm.get("answer");

                    ansid.addAll(ans.keySet());

                    d.noview="Comments ("+ansid.size()+")";
                }
                catch(Exception e)
                {


                }
                List<String> viewid = new ArrayList<>();
                try {
                    Map<String, Object> vie = (Map<String, Object>) mm.get("view");

                    viewid.addAll(vie.keySet());

                    d.noans="View ("+viewid.size()+")";
                }
                catch(Exception e)
                {


                }

           //     DummyContent1.addItem(new DummyItem1(""+dataSnapshot.getKey(),""+mm.get("aname"),""+mm.get("question"),""+mm.get("time"),"Comments ("+ansid.size()+")","View(0)"));

                adpter.notifyDataSetChanged();




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
        };


        setQuestions();


        return view;
    }
    void setQuestions()
    {
        if(DummyContent1.ITEM_MAP.size()>0)
        {
            DummyContent1.ITEMS.clear();
            DummyContent1.ITEM_MAP.clear();
        }
        myref.addChildEventListener(chd);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
     //   myref.addChildEventListener(chd);
        myref.removeEventListener(chd);
        mListener = null;
    }

    @Override
    public void onItemClick(View childView, int position) {

        passdata(DummyContent1.ITEMS.get(position).id);

    }
    public void passdata(String qqid){

        Intent li=new Intent(home.con,answer.class);
        li.putExtra("qid",qqid);
        startActivity(li);


    }
    @Override
    public void onItemLongPress(View childView, int position) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem1 item);
    }
}
