package com.example.blocnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blocnote.Adapter.RequestViewAdapter;
import com.example.blocnote.model.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class NotifActivity extends AppCompatActivity {
    public static List<Request> mlist;
    private RecyclerView recycler;
    private TextView noNotif;
    private RequestViewAdapter adapter;
    private Toolbar toolbar;
    private Intent intent;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notif);
    toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Notifications");
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    mlist=new ArrayList<>();
    recycler=findViewById(R.id.recyclerRequest);
    recycler.setHasFixedSize(true);
    recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    noNotif=findViewById(R.id.notifText);
    intent =getIntent();
    try {
        title=intent.getStringExtra("title");
        if(title!=null && !title.isEmpty())
        {
            System.out.println("t");
            Toast.makeText(this,""+title,Toast.LENGTH_LONG);
        }

        else

        System.out.println("title===> "+title);

    }
    catch (NullPointerException e)
    {
        System.out.println(e.getMessage());
    }
        readRequest();

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
                    toolbar.setTitle("Notifications");
                    recycler.setVisibility(View.GONE);
                    noNotif.setVisibility(View.VISIBLE);
                }
                else{
                    toolbar.setTitle("Notifications ("+mlist.size()+")");
                    noNotif.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                }
                adapter=new RequestViewAdapter(getApplicationContext(),mlist);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
