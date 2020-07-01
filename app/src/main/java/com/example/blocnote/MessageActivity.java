package com.example.blocnote;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.example.blocnote.model.UserClass;
import com.example.blocnote.notifications.ApiService;
import com.example.blocnote.notifications.Client;
import com.example.blocnote.notifications.Data;
import com.example.blocnote.notifications.Response;
import com.example.blocnote.notifications.Sender;
import com.example.blocnote.notifications.Token;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blocnote.Adapter.MessageAdapter;
import com.example.blocnote.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;

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
   ApiService apiService;
   byte encriptionKey[]={9,115,51,86,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
   Cipher cipher;
    static Cipher decipher;
   static SecretKeySpec secretKeySpec;

   boolean notify;
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
        try {
            cipher = Cipher.getInstance("AES");
            decipher= Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec=new SecretKeySpec(encriptionKey,"AES");

        final Intent intent =getIntent();

        receiverId=intent.getStringExtra("receiverId");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
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

        reference=FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass user=dataSnapshot.getValue(UserClass.class);
                receiverName=user.getNom();
                receiverPhoto=user.getImageUrl();
                if (receiverPhoto.equals("default"))
                    image_profile.setImageResource(R.drawable.user_logo);
                else
                    Glide.with(getApplicationContext()).load(receiverPhoto).apply(RequestOptions.fitCenterTransform()).into(image_profile);
                mreceiverName.setText(receiverName);
                mreceiverName.setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        apiService= Client.getRetrofit("https://fcm.googleapis.com/" ).create(ApiService.class);

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
    private String chiffreMessage(String message){
        byte[] stringByte=message.getBytes();
        byte[] encryptedByte=new byte [stringByte.length];
        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String str= null;
        try {
            str = new String(encryptedByte,"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
    public static String dechiffreMessage(String message) throws UnsupportedEncodingException {
        byte[] encryptedByte=message.getBytes("ISO-8859-1");
        String decryptedString =message ;

        byte [] decryption;
        if(decipher!=null) {
            try {

                decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);


                decryption = decipher.doFinal(encryptedByte);
                decryptedString = new String(decryption);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
        return decryptedString;


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
        hashmap.put("message",chiffreMessage(msg));
        hashmap.put("time",time);
        hashmap.put("timestamp",timeStamp);
        ref.push().setValue(hashmap);
        HashMap<String,String> description=new HashMap<>();
        description.put("envoye par",FirebaseAuth.getInstance().getCurrentUser().getUid());
        description.put("type","message");
        messref.child(receiverId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(description);

        final String message=msg;
        DatabaseReference db=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass user=dataSnapshot.getValue(UserClass.class);

                if(notify)
                {
                    sendNotification(receiverId,user.getNom(),message);
                    notify=false;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(final String receiverId, final String nom, final String message) {
       DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
       Query query=allTokens.orderByKey().equalTo(receiverId);
       query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot ds:dataSnapshot.getChildren())
               {
                   Token token=ds.getValue(Token.class);
                   Data data=new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),nom+" : "+message,"Nouveau message !",receiverId,R.drawable.ic_notifications_black_24dp);
                    Sender  sender=new Sender(data,token.getToken()) ;
                    apiService.sendNotif(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(MessageActivity.this, ""+response.message(), Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    Toast.makeText(MessageActivity.this, "error"+t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        finish();
        super.onBackPressed();
    }
}
