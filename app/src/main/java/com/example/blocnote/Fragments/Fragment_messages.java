package com.example.blocnote.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.blocnote.Adapter.ListMessageAdapter;
import com.example.blocnote.R;
import com.example.blocnote.model.Chat;
import com.example.blocnote.model.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_messages extends Fragment {
    FloatingActionButton fab;
    List<Chat> mchat;
    List<UserClass> userlist;
    List<String>userListId;
    ListMessageAdapter adapter;
    private TextView noNotif;
    RecyclerView recyclerView;
    private OnItemSelectedListener listener;
    public Fragment_messages(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =getLayoutInflater().inflate(R.layout.fragment_messages,container,false);

        noNotif=view.findViewById(R.id.notifText);
        userListId= new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Chats");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               userListId.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSenderid().equals(FirebaseAuth.getInstance().getUid())) {
                           userListId.add(chat.getReceiverid());

                    }
                    if (chat.getReceiverid().equals(FirebaseAuth.getInstance().getUid())) {
                        userListId.add(chat.getSenderid());

                    }


                }

                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }

    private void readChat() {
        userlist=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                   UserClass user =snapshot.getValue(UserClass.class);
                   for(String id: userListId)
                   {

                     if(user.getId().equals(id) && !userlist.contains(user))
                     {

                             userlist.add(user);

                     }
                   }
                }
                if(userlist.size()==0)
                 noNotif.setVisibility(View.VISIBLE);
                else
                    noNotif.setVisibility(View.GONE);
                adapter=new ListMessageAdapter(getContext(),userlist);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycler) ;
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRssItemSelected();

            }
        });
    }


    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onRssItemSelected();
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

}
