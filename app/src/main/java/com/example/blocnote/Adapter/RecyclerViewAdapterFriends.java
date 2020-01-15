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
import com.example.blocnote.MessageActivity;
import com.example.blocnote.R;
import com.example.blocnote.model.Friend;
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

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapterFriends extends RecyclerView.Adapter<RecyclerViewAdapterFriends.ViewHolder> {
    private    DatabaseReference FriendRequreference,mReference,notificationRef,friendRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private UserClass user;
    private Context mcontext;
    private List<Friend> mlistFriends;



    public RecyclerViewAdapterFriends(Context context, List<Friend> mlistFriends ){
        this.mcontext=context;
        this.mlistFriends=mlistFriends;

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("Users");
        mReference.keepSynced(true);

     friendRef=FirebaseDatabase.getInstance().getReference("Users")
             .child(currentUser.getUid()).child("Friends");

        notificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");
     notificationRef.keepSynced(true);

        FriendRequreference=FirebaseDatabase.getInstance().getReference().child("FriendRequest");

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user =dataSnapshot.getValue(UserClass.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_friends,viewGroup,false);
        return new RecyclerViewAdapterFriends.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final Friend muser=mlistFriends.get(i);
        System.out.println("dans friend adapteer  "+muser.getFiliere());
        if (!muser.getUrlphoto().equals("default"))
            Glide.with(mcontext).load(muser.getUrlphoto()).into(viewHolder.image);
        else
            viewHolder.image.setImageResource(R.drawable.user_logo);
        viewHolder.text.setText(muser.getNom());
        viewHolder.textClass.setText(muser.getFiliere());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Intent intent =new Intent(mcontext, MessageActivity.class);
                  intent.putExtra("receiverId",muser.getId());
                  mcontext.startActivity(intent);

            }
        });
        viewHolder.text_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setMessage("Etes vous sûre de vouloir retirer "+muser
                .getNom()+" de votre liste d'amis ?")
                        .setPositiveButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Oui",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mReference.child(currentUser.getUid()).child("Friends").child(muser.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mReference.child(muser.getId()).child("Friends").child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            notifyDataSetChanged();
                                            Toast.makeText(mcontext,"Vous et "+ muser.getNom()+" n'êtes plus amis ",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                })
                        .create()
                        .show();

        }
        });
    }


    @Override
    public int getItemCount() {
        return mlistFriends.size();
    }


public  void updateList(List <Friend> newList){
   mlistFriends=new ArrayList<>();
   mlistFriends.addAll(newList);
   notifyDataSetChanged();
}
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image ;
        TextView text;
        TextView textClass;
        TextView text_remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.img);
            text=itemView.findViewById(R.id.text);
            text_remove=itemView.findViewById(R.id.request);
            textClass=itemView.findViewById(R.id.fil);
        }

    }
}
