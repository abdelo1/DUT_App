package com.example.blocnote;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.blocnote.Adapter.MessageAdapter;
import com.example.blocnote.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    DatabaseReference reference;
Toolbar toolbar;
ImageView image_profile;
TextView mreceiverName,enligne;
EditText inputmMessage;
FloatingActionButton send;
RecyclerView recycler;
MessageAdapter adapter;
    String receiverName;
    String receiverId;
    String receiverPhoto;
    List<Chat> mchat;
   String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();

        image_profile=(ImageView) findViewById(R.id.img);
        mreceiverName=(TextView) findViewById(R.id.text);
        inputmMessage=(EditText) findViewById(R.id.inputMessage);
        send=(FloatingActionButton)findViewById(R.id.btnSend);
        enligne=(TextView) findViewById(R.id.isonline);



        recycler=(RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        LinearLayoutManager lm =new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lm);

        final Intent intent =getIntent();
        receiverName=intent.getStringExtra("receiverName");
        receiverId=intent.getStringExtra("receiverId");
        receiverPhoto=intent.getStringExtra("receiverPhoto");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputmMessage.getText().toString().equals(""))
                {}
                else{
                    sendMessage(inputmMessage.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getUid(),receiverId);
                    inputmMessage.setText("");
                    recycler.smoothScrollToPosition(recycler.getAdapter().getItemCount());

                }
            }
        });

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
               finish();
           }
       });



        if (receiverPhoto.equals("default"))
          image_profile.setImageResource(R.drawable.user_logo);
          else
        Glide.with(this).load(receiverPhoto).into(image_profile);
          mreceiverName.setText(receiverName);
          mreceiverName.setTypeface(null, Typeface.BOLD);

        this.readMessages(FirebaseAuth.getInstance().getCurrentUser().getUid(),receiverId);
        reference=FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
        reference.child("isonline").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online=dataSnapshot.getValue().toString();
                if (online.equals("true"))
                enligne.setVisibility(View.VISIBLE);
                else
                enligne.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("iswriting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String w=dataSnapshot.getValue().toString();
                if (w.equals("true"))
                {
                    enligne.setText(" est en train d'écrire ...");

                }
                else if(w.equals("false"))
                {
                    enligne.setText("En ligne");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        inputmMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reference=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                if (inputmMessage.getText().toString().length()==0)

                    reference.child("iswriting").setValue("false");
                else
                    reference.child("iswriting").setValue("true");



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void readMessages(final String myId, final String userId) {
        mchat=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Chats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    System.out.println("chat :"+snapshot);
                    Chat chat=snapshot.getValue(Chat.class);
                    if (chat.getReceiverid().equals(myId) && chat.getSenderid().equals(userId)|| chat.getReceiverid().equals(userId)&&
                    chat.getSenderid().equals(myId))
                        mchat.add(chat);
                }
                System.out.println("size "+mchat.size());
                adapter=new MessageAdapter(getApplicationContext(),mchat);
                recycler.setAdapter(adapter);
                recycler.smoothScrollToPosition(recycler.getAdapter().getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    protected void onResume() {
        super.onResume();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        reference.child("isonline").setValue("true");
    }
    protected void onPause() {
        super.onPause();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        reference.child("isonline").setValue("false");
    }


    private void sendMessage(String msg,String sender,String receiver) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Chats");
        DatabaseReference messref= FirebaseDatabase.getInstance().getReference("Notificationmess");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String time =  mdformat.format(calendar.getTime());
        long timeStamp = System.currentTimeMillis();

        HashMap<String,Object> hashmap=new HashMap<>();
        hashmap.put("senderid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashmap.put("receiverid",receiverId);
        hashmap.put("message",msg);
        hashmap.put("time",time);
        hashmap.put("timestamp",timeStamp);
        ref.push().setValue(hashmap);
        HashMap<String,String> description=new HashMap<>();
        description.put("envoye par",FirebaseAuth.getInstance().getCurrentUser().getUid());
        description.put("type","message");
        messref.child(receiverId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(description);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        finish();
        super.onBackPressed();
    }
}
