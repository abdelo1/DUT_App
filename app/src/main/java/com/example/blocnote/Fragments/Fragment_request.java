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
import android.widget.TextView;

import com.example.blocnote.Adapter.RequestViewAdapter;
import com.example.blocnote.R;
import com.example.blocnote.model.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_request extends Fragment {
  public static List<Request> mlist;
   private RecyclerView recycler;
    private TextView noNotif;
   private RequestViewAdapter adapter;
    public Fragment_request(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mlist=new ArrayList<>();
        View view =getLayoutInflater().inflate(R.layout.fragment_request,container,false);
        recycler=view.findViewById(R.id.recyclerRequest);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        noNotif=view.findViewById(R.id.notifText);
        readRequest();
        return view;
    }

    public void readRequest(){
        DatabaseReference reference;
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        String currentUserId=mAuth.getCurrentUser().getUid();
        // on va recuperer toutes les demandes d'ami de lutilisateur de l'app
        reference= FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(currentUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String NO="no";
                    //si la requete a ete gere on ne l'ajoute pas
                   Request request=snapshot.getValue(Request.class);
                      if (request.getHandle().equals(NO))
                    mlist.add(request);
                }
                //s'il nya pas de demandes on affiche le texte
                if (mlist.size()==0)
                {
                    recycler.setVisibility(View.GONE);
                    noNotif.setVisibility(View.VISIBLE);
                }
                else{
                    noNotif.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                }
                adapter=new RequestViewAdapter(getContext(),mlist);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
