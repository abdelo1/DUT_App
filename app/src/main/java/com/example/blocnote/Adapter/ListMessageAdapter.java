package com.example.blocnote.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blocnote.MessageActivity;
import com.example.blocnote.R;
import com.example.blocnote.model.Chat;
import com.example.blocnote.model.TimeAgo;
import com.example.blocnote.model.UserClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.example.blocnote.ProfileActivity.navigationView;


public class ListMessageAdapter extends RecyclerView.Adapter<ListMessageAdapter.SentMessageHolder> {

    private Context mcontext;
    private List<UserClass> listuser;
  String tamp;
String lastmessage;
String when;
DatabaseReference db;
    byte encriptionKey[]={9,115,51,86,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
    Cipher cipher;
    static Cipher decipher;
    static SecretKeySpec secretKeySpec;

    public ListMessageAdapter(Context context, List<UserClass> listuser ){
        this.mcontext=context;
        this.listuser=listuser;
        try {
            cipher = Cipher.getInstance("AES");
            decipher= Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec=new SecretKeySpec(encriptionKey,"AES");
            }



    @NonNull
    @Override
    public SentMessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_message,viewGroup,false);
            return new ListMessageAdapter.SentMessageHolder(view);


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
    @Override
    public void onBindViewHolder(@NonNull SentMessageHolder sentMessageHolder, int i) {

       final UserClass user =listuser.get(i);
       FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
       if (firebaseUser!=null)
       {
           db=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Friends");

           sentMessageHolder.username.setText(user.getNom());
           if (!user.getImageUrl().equals("default"))
               Glide.with(mcontext).load(user.getImageUrl()).apply(RequestOptions.centerInsideTransform()).into(sentMessageHolder.img);
           else
               sentMessageHolder.img.setImageResource(R.drawable.user_logo);
           lastMessage(user.getId(),sentMessageHolder.dernier,sentMessageHolder.date);
           sentMessageHolder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   db.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           Boolean isFriend=true;

                           for(DataSnapshot snapshot:dataSnapshot.getChildren())
                           {
                               System.out.println("snapshoot------>  "+snapshot);
                               if (snapshot.getKey().equals(user.getId()))
                               {

                                   isFriend=true;
                                   break;
                               }
                               else
                                   isFriend=false;
                           }

                           if(isFriend){
                               System.out.println("il sont ami   "+user.getNom());
                               Intent intent =new Intent(mcontext, MessageActivity.class);
                               intent.putExtra("receiverId",user.getId());
                               mcontext.startActivity(intent);
                           }
                           else
                           {
                               AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                               builder.setMessage("Vous n'etes plus ami avec cette personne , veuillez lui envoyez une demande d'ami pour pouvoir tchater avec elle.")
                                       .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {

                                           }
                                       })
                                       .create()
                                       .show();
                           }


                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });


               }
           });
       }

     }


    @Override
    public int getItemCount() {
        return listuser.size();
    }



    public class SentMessageHolder extends RecyclerView.ViewHolder {
       ImageView img;
       TextView date,username,dernier;

        SentMessageHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            dernier = (TextView) itemView.findViewById(R.id.dernier);
            username=(TextView) itemView.findViewById(R.id.text);
            img=(ImageView)itemView.findViewById(R.id.img);

        }

    }
    private  void lastMessage(final String userid , final TextView dernier,final TextView date){

    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Chats");
    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot snapshot:dataSnapshot.getChildren())
            {
                Chat chat=snapshot.getValue(Chat.class);
                if(chat!=null)
                {
                    if(chat.getReceiverid()!=null) {
                        if ((chat.getReceiverid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) && chat.getSenderid().equals(userid) ||
                                (chat.getReceiverid().equals(userid)) && chat.getSenderid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            lastmessage = chat.getMessage();

                            when = TimeAgo.getTimeAgo(chat.getTimeStamp());

                        }
                    }
                }



            }
            try {
                dernier.setText(dechiffreMessage(lastmessage));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            date.setText(when);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }
}
