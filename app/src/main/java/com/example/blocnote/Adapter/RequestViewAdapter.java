


package com.example.blocnote.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blocnote.ProfileActivity;
import com.example.blocnote.R;
import com.example.blocnote.model.Request;
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

import java.util.HashMap;
import java.util.List;


public class RequestViewAdapter extends RecyclerView.Adapter<RequestViewAdapter.ViewHolder> {
    private    DatabaseReference reference,requReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    // celui qui envoit la requete
    private UserClass mcurrentUser;
    private Context mcontext;
    private  List<Request> mlistrequest;
    private static List<Request> anotherList;

    private UserClass user;
    public RequestViewAdapter(Context context, List<Request> request ){
        this.mcontext=context;
        this.mlistrequest=request;
        this.anotherList=request;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_request,viewGroup,false);
        return new RequestViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        //on recupere un item de la liste
        final Request listItem =mlistrequest.get(i);
        //oon affcihe les utilisateurs ayant envoye une requete au user courant
        FirebaseDatabase.getInstance().getReference("Users").child(listItem.getReceiverId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mcurrentUser=dataSnapshot.getValue(UserClass.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (!listItem.getPhotourl().equals("default"))
            Glide.with(mcontext).load(listItem.getPhotourl()).apply(RequestOptions.centerInsideTransform()).into(viewHolder.image);
        else
            viewHolder.image.setImageResource(R.drawable.user_logo);
        viewHolder.text.setText(listItem.getNom());
        viewHolder.textClass.setText(listItem.getFiliere());
        viewHolder.refuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference("FriendRequest")
                        .child(listItem.getReceiverId())
                        .child(listItem.getSenderId());

                reference.child("handle").setValue("yes");
            }
        });
        viewHolder.text_requ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text =viewHolder.text_requ.getText().toString();
                // si l'utilisateur de lapp courant accepte une demande alors on met a jour la valeur -gere- de la demande a yes
                if (text.equals("Accepter"))
                {
                    reference = FirebaseDatabase.getInstance().getReference()
                            .child("FriendRequest")
                            .child(listItem.getReceiverId()).child(listItem.getSenderId());
                    reference.child("handle").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                //la demande est accepte
                                reference.child("accepted").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            //ajouter l'user de litem courant dans la liste damis du user de lapp
                                            //listItem.getReceiverid() c l'id de lutilisateur de lapp
                                            HashMap<String,String> hashMap=new HashMap<>();

                                            hashMap.put("nom",listItem.getNom());
                                            hashMap.put("urlphoto",listItem.getPhotourl());
                                            hashMap.put("filiere",listItem.getFiliere());
                                            hashMap.put("id",listItem.getSenderId());
                                            reference = FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(listItem.getReceiverId())
                                                    .child("Friends").child(listItem.getSenderId());

                                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        // et on fait l'inverse on ajoute lutilisateur de lapp parmi les amis de l'utilisateur de l'ittem courant
                                                        HashMap<String,String> hashMap=new HashMap<>();

                                                        hashMap.put("nom",mcurrentUser.getNom());
                                                        hashMap.put("urlphoto",mcurrentUser.getImageUrl());
                                                        hashMap.put("filiere",mcurrentUser.getFiliere());
                                                        hashMap.put("id",mcurrentUser.getId());
                                                        reference = FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(listItem.getSenderId())
                                                                .child("Friends").child(listItem.getReceiverId());
                                                        reference.setValue(hashMap);
                                                        FirebaseDatabase.getInstance().getReference("FriendRequest")
                                                                .child(listItem.getReceiverId()).child(listItem.getSenderId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                    Toast.makeText(mcontext,"request deleted",Toast.LENGTH_LONG);
                                                            }
                                                        });
                                                    }
                                                    Toast.makeText(mcontext,"Amis ajoute",Toast.LENGTH_LONG).show();
                                                }
                                            });

                                        }

                                    }
                                });

                        }
                    });
                }
                else
                {

                }


            }
        });
    }

    @Override
    public int getItemCount() {

        return mlistrequest.size();
    }
    public static int  nbreItem(){
        return anotherList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image ;
        TextView text;
        TextView textClass;
        Button text_requ;
        Button refuser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.img);
            text=itemView.findViewById(R.id.text);
            text_requ=itemView.findViewById(R.id.request);
            refuser=itemView.findViewById(R.id.refuser);
            textClass=itemView.findViewById(R.id.fil);
        }

    }
}

