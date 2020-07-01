package com.example.blocnote.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blocnote.MessageActivity;
import com.example.blocnote.ProfileActivity;
import com.example.blocnote.R;
import com.example.blocnote.RegisterActivity;
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
import java.util.HashMap;
import java.util.List;

import static com.example.blocnote.ProfileActivity.navigationView;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private    DatabaseReference FriendRequreference,mReference,notificationRef,friendRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private UserClass user;
    private Context mcontext;
    private List<UserClass> mlistUsers;
    public static Boolean create=false;
    public static int index=0;

    public RecyclerViewAdapter(Context context, List<UserClass> mlistUsers ){
        this.mcontext=context;
        this.mlistUsers=mlistUsers;

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            mReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid());
            mReference.keepSynced(true);
            friendRef=FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Friends");
        }




        notificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");
     notificationRef.keepSynced(true);

        FriendRequreference=FirebaseDatabase.getInstance().getReference().child("FriendRequest");
if(mReference!=null)
{
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



    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_users,viewGroup,false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
//parmi tous les utilisateurs on verifie ceux qui sont ses amis
        final UserClass a_user_of_listAll=mlistUsers.get(i);
        //on verifie que l'utilisateur de l'item courant fait partie de ses amis
        if(friendRef!=null) {
            friendRef.child(a_user_of_listAll.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        // si oui on remplace le texte par ami
                        viewHolder.text_requ.setText("Ami");
                        viewHolder.text_requ.setEnabled(false);
                        viewHolder.text_requ.setBackgroundColor(R.color.colorGrey);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //on verifie si lutilisateur de l'app est l'ami de l'utilisateur de l'item courant
            FriendRequreference.child(a_user_of_listAll.getId()).child(currentUser.getUid()).child("accepted").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //si lutilissateur de l'app est parmi les demandes d'ami et que l'utilisateur de l'item courant a accepte  sa demande
                    if (dataSnapshot.getValue() != null && dataSnapshot.getValue().equals("yes")) {
                        //remplacer le texte par ami
                        viewHolder.text_requ.setText("Ami");
                        viewHolder.text_requ.setEnabled(false);
                        viewHolder.text_requ.setBackgroundColor(R.color.colorGrey);

                    }
                    if (dataSnapshot.getValue() != null && dataSnapshot.getValue().equals("no")) {
                        //si l'utilisateur  de l'app est parmi les demandes d'ami de l'utilisateur de l'item courant et que la demande n'est pas encore accepte r
                        //le texte par annuler demande puisque la demande d'ami est en attente
                        viewHolder.text_requ.setText("Annuler demande");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (!a_user_of_listAll.getImageUrl().equals("default"))
                Glide.with(mcontext).load(a_user_of_listAll.getImageUrl()).apply(RequestOptions.centerInsideTransform()).into(viewHolder.image);
            else
                viewHolder.image.setImageResource(R.drawable.user_logo);
            viewHolder.text.setText(a_user_of_listAll.getNom());
            viewHolder.textClass.setText(a_user_of_listAll.getFiliere());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.text_requ.getText().toString().equals("Ami")) {
                        Intent intent = new Intent(mcontext, MessageActivity.class);
                        intent.putExtra("receiverId", a_user_of_listAll.getId());

                        mcontext.startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                        builder.setMessage("Pour pouvoir tchater avec cet utilisateur , veuillez lui envoyez une demande d'ami.")
                                .setPositiveButton("Envoyer demande d'ami", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        //lenvoyeur de la demande d'ami qui est lutilisateur de lapp
                                        hashMap.put("senderId", user.getId());
                                        // celui qui recoit
                                        hashMap.put("receiverId", a_user_of_listAll.getId());
                                        // si la demande est traite
                                        hashMap.put("handle", "no");
                                        // si la demande est accepte
                                        hashMap.put("accepted", "no");
                                        //nom ,photo et filiere de celui qui a envoye
                                        hashMap.put("nom", user.getNom());
                                        hashMap.put("photourl", user.getImageUrl());
                                        hashMap.put("filiere", user.getFiliere());
                                        //on ajoute la demande d'ami dams la liste de demande de la personne a qui on envoit
                                        FriendRequreference.child(a_user_of_listAll.getId()).child(user.getId()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    navigationView.getMenu().getItem(1).setActionView(R.layout.menu_dot);
                                                    // si la demande est ajoutee avec brio
                                                    final HashMap<String, String> hashnMap = new HashMap<>();
                                                    //on cree la notification
                                                    hashnMap.put("envoye par", user.getId());
                                                    hashnMap.put("type", "requete d'amis");
                                                    notificationRef.child(a_user_of_listAll.getId()).push().setValue(hashnMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        //le texte channge de ajouter ami a annuler demande
                                                                        Toast.makeText(mcontext, "Demande d'amis envoyée", Toast.LENGTH_LONG).show();
                                                                        viewHolder.text_requ.setText("Annuler demande");
                                                                    }
                                                                }
                                                            });

                                                }

                                            }
                                        });
                                    }
                                }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                                .create()
                                .show();
                    }
                }
            });
//quand l'utilisateur de lapp appuie sur le texte

            viewHolder.text_requ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = viewHolder.text_requ.getText().toString();
                    // si c ajouter ami
                    if (text.equals("Ajouter ami")) {

                        HashMap<String, String> hashMap = new HashMap<>();
                        //lenvoyeur de la demande d'ami qui est lutilisateur de lapp
                        hashMap.put("senderId", user.getId());
                        // celui qui recoit
                        hashMap.put("receiverId", a_user_of_listAll.getId());
                        // si la demande est traite
                        hashMap.put("handle", "no");
                        // si la demande est accepte
                        hashMap.put("accepted", "no");
                        //nom ,photo et filiere de celui qui a envoye
                        hashMap.put("nom", user.getNom());
                        hashMap.put("photourl", user.getImageUrl());
                        hashMap.put("filiere", user.getFiliere());
                        //on ajoute la demande d'ami dams la liste de demande de la personne a qui on envoit
                        FriendRequreference.child(a_user_of_listAll.getId()).child(user.getId()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // si la demande est ajoutee avec brio
                                    final HashMap<String, String> hashnMap = new HashMap<>();
                                    //on cree la notification
                                    hashnMap.put("envoye par", user.getId());
                                    hashnMap.put("type", "requete d'amis");
                                    notificationRef.child(a_user_of_listAll.getId()).push().setValue(hashnMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //le texte channge de ajouter ami a annuler demande
                                                        Toast.makeText(mcontext, "Demande d'amis envoyée", Toast.LENGTH_LONG).show();
                                                        viewHolder.text_requ.setText("Annuler demande");
                                                    }

                                                }
                                            });

                                }

                            }
                        });

                    }

                    // si le texte est annuler demande
                    else {
                        // on retire la demande la de la liste de demande d'amis de l'utilisateur a qui on a envoye
                        FriendRequreference.child(a_user_of_listAll.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    viewHolder.text_requ.setText("Ajouter ami");

                            }
                        });
                        notificationRef.child(user.getId()).removeValue();
                    }

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mlistUsers.size();
    }

    public  void updateList(List <UserClass> newList){
        mlistUsers=new ArrayList<>();
        mlistUsers.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image ;
        TextView text;
        TextView textClass;
        Button text_requ;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.img);
            text=itemView.findViewById(R.id.text);
            text_requ=itemView.findViewById(R.id.request);
            textClass=itemView.findViewById(R.id.fil);
        }

    }
}
