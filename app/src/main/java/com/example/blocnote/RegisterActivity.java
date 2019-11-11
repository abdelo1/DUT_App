package com.example.blocnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private Button btn_inscription;
   private EditText  user_id,user_email,user_password,user_filiere;
   private FirebaseAuth mAuth;
    private String email;
   private String password;
   private String nom;
   private String filiere;

private  String token;
    DatabaseReference reference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        progressBar=(ProgressBar) findViewById(R.id.progress);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        user_id=(EditText) findViewById(R.id.user_id);

        user_email=(EditText)findViewById(R.id.user_email);
        user_password=(EditText)findViewById(R.id.user_mdp);
        user_filiere=(EditText)findViewById(R.id.filiere);
        btn_inscription=(Button) findViewById(R.id.btn_inscription);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( RegisterActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                 token = instanceIdResult.getToken();


            }
        });

        btn_inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()){
                    signUp(nom,email,password);
                }
            }
        });



    }

    private void signUp(final String nom, final String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId =user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String,String> hashMap = new HashMap<>();

                            hashMap.put("nom",nom);
                            hashMap.put("imageUrl","default");
                            hashMap.put("id",userId);
                            hashMap.put("isDeleted","false");
                            hashMap.put("filiere",filiere);
                            hashMap.put("token",token);
                            hashMap.put("isonline","true");
                            hashMap.put("iswriting","false");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                        {
                                        Intent intent = new Intent(RegisterActivity.this, ChooseProfilActivity.class);
                                        startActivity(intent);


                                    }

                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.w("Eror", "getInstanceId failed", task.getException());
                                                        return;
                                                    }

                                                    // Get new Instance ID token
                                                    String token = task.getResult().getToken();
                                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                    preferences.edit().putString("Token_device", token).apply();

                                                }
                                            });
                        }

                    });



                        }
                        else {

                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Inscription echouee veuillez ressayer ",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }


    private boolean validateForm() {
        boolean valid = true;

        email =user_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            user_email.setError("Required.");
            valid = false;
        } else {
            user_email.setError(null);
        }

        filiere =user_filiere.getText().toString();
        if (TextUtils.isEmpty(filiere)) {
            user_filiere.setError("Required.");
            valid = false;
        } else {
            user_filiere.setError(null);
        }

        nom=user_id.getText().toString();
        if (TextUtils.isEmpty(nom)) {
            user_id.setError("Required.");
            valid = false;
        } else {
            user_id.setError(null);
        }

       password = user_password.getText().toString();
        if (TextUtils.isEmpty(password) ) {
            user_password.setError("Required.");
            valid = false;
        }
        else if(password.length()<6){
            user_password.setError("Le mot de passe doit contenir plus de 6 caracteres");
            valid=false;
        }
            else {
            user_password.setError(null);
        }


        return valid;
    }

}
