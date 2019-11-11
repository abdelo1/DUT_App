package com.example.blocnote.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.view.inputmethod.EditorInfo;


import com.example.blocnote.R;
import com.example.blocnote.Adapter.RecyclerViewAdapter;

import com.example.blocnote.model.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;


public class Fragment_users extends Fragment {
  List<UserClass> mUsers= new ArrayList<>();
   private RecyclerView recycler;
    UserClass user;
    private DatabaseReference mreference;
   private FirebaseAuth mAuth;
   SearchView searchView;
    RecyclerViewAdapter adapter;
    public Fragment_users(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =getLayoutInflater().inflate(R.layout.fragment_users,container,false);
        searchView=view.findViewById(R.id.search);
        searchView.setQueryHint("Rechercher ....");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchWord=newText.toLowerCase();
                List<UserClass> newList=new ArrayList<>();
                for(UserClass us:mUsers)
                {
                    if (us.getNom().toLowerCase().contains(searchWord))
                        newList.add(us);
                }
                adapter.updateList(newList);
                return false;
            }
        });
         recycler= view.findViewById(R.id.recycler);
        mAuth=FirebaseAuth.getInstance();
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mreference=FirebaseDatabase.getInstance().getReference("Users");

            readUsers();

            return view;
    }

    private void readUsers() {

        final String currentUserId= mAuth.getCurrentUser().getUid();
        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  mUsers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                     user =snapshot.getValue(UserClass.class);

                    String deleted=user.getIsDeleted();

                    if (deleted.equals("true"))
                       continue;
                    else {
                            if (!user.getId().equals(currentUserId))
                            {
                               mUsers.add(user);

                            }
                    }

                }


                adapter =new RecyclerViewAdapter(getContext(),mUsers);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
