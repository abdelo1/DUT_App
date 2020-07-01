package com.example.blocnote.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.blocnote.Adapter.RecyclerViewAdapterFriends;
import com.example.blocnote.R;
import com.example.blocnote.model.Friend;
import com.example.blocnote.model.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_friends extends Fragment {
  List<Friend> mFriends;
SearchView searchView;
   private RecyclerView recycler;
   UserClass mUser;
   Friend friend;
    private TextView noNotif;
    private DatabaseReference mreference;
   private FirebaseAuth mAuth;
    public static RecyclerViewAdapterFriends adapter;
    public Fragment_friends(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =getLayoutInflater().inflate(R.layout.fragment_friends,container,false);
         recycler= view.findViewById(R.id.recycler);
        noNotif=view.findViewById(R.id.notifText);
         searchView=view.findViewById(R.id.search);
        searchView.setQueryHint("Rechercher ....");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextSubmit(String s) {
                 return false;
             }

             @Override
             public boolean onQueryTextChange(String s) {
                 String searchWord=s.toLowerCase().toString();
                 List<Friend> newList=new ArrayList<>();
                 for(Friend friend :mFriends)
                 {
                     if (friend.getNom().toLowerCase().contains(searchWord) || friend.getFiliere().toLowerCase().contains(searchWord))
                         newList.add(friend);
                 }
                 adapter.updateList(newList);

                 return false;
             }
         });
        mAuth=FirebaseAuth.getInstance();
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mreference=FirebaseDatabase.getInstance().getReference("Users");

        mFriends= new ArrayList<>();
        readUsers();
            return view;
    }

    private void readUsers() {

        final String currentUserId= mAuth.getCurrentUser().getUid();
        mreference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser=dataSnapshot.getValue(UserClass.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mreference.child(currentUserId).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  mFriends.clear();
                for (final DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    friend =snapshot.getValue(Friend.class);
                     mFriends.add(friend);

                }

                if(mFriends.size()==0)
                    noNotif.setVisibility(View.VISIBLE);
                else
                    noNotif.setVisibility(View.GONE);
                adapter =new RecyclerViewAdapterFriends(getContext(),mFriends);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
