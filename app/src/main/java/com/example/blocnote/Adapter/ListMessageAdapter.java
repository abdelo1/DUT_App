package com.example.blocnote.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.blocnote.MessageActivity;
import com.example.blocnote.R;
import com.example.blocnote.model.Chat;
import com.example.blocnote.model.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ListMessageAdapter extends RecyclerView.Adapter<ListMessageAdapter.SentMessageHolder> {

    private Context mcontext;
    private List<UserClass> listuser;
  String tamp;
String lastmessage;

    public ListMessageAdapter(Context context, List<UserClass> listuser ){
        this.mcontext=context;
        this.listuser=listuser;
            }



    @NonNull
    @Override
    public SentMessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_message,viewGroup,false);
            return new ListMessageAdapter.SentMessageHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull SentMessageHolder sentMessageHolder, int i) {
       final UserClass user =listuser.get(i);

        sentMessageHolder.username.setText(user.getNom());
        if (!user.getImageUrl().equals("default"))
            Glide.with(mcontext).load(user.getImageUrl()).into(sentMessageHolder.img);
        else
            sentMessageHolder.img.setImageResource(R.drawable.user_logo);
        lastMessage(user.getId(),sentMessageHolder.dernier,sentMessageHolder.date);
        sentMessageHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mcontext, MessageActivity.class);
                intent.putExtra("receiverId",user.getId());
                intent.putExtra("receiverName",user.getNom());
                intent.putExtra("receiverPhoto",user.getImageUrl());
                mcontext.startActivity(intent);

            }
        });
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
                if ((chat.getReceiverid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))&&chat.getSenderid().equals(userid)||
                        (chat.getReceiverid().equals(userid))&&chat.getSenderid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())  )
                {
                    lastmessage= chat.getMessage();
                    tamp=chat.getTime();


                }


            }
            dernier.setText(lastmessage);
            date.setText( tamp);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }
}
