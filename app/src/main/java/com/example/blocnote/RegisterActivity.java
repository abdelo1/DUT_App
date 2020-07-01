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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.blocnote.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ProgressBar progressBar;
    private Button btn_inscription;
   private EditText  user_id,user_email,user_password,filiereText;
  private Spinner user_filiere;
   private FirebaseAuth mAuth;
    private String email, password,nom,filiere;
   private String filieres[]={"GEA","INFO","TC","LPE","GRH","GLT"};

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
        filiereText=(EditText) findViewById(R.id.filieretext);
        user_email=(EditText)findViewById(R.id.user_email);
        user_password=(EditText)findViewById(R.id.user_mdp);
        user_filiere=(Spinner)findViewById(R.id.filiere);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, filieres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_filiere.setAdapter(adapter);
        user_filiere.setOnItemSelectedListener(this);

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
                    signUp(nom,email.trim(),password);
                }
            }
        });



    }

    private void signUp(final String nom, final String email, final String password) {
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
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        preferences.edit().putString("mdp", password).apply();
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
                                                    Token token = new  Token(task.getResult().getToken()) ;
                                                    reference = FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    reference.setValue(token);

                                                }
                                            });
                                }

                           });

                        }
                        else {
                                if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                    Toast.makeText(getApplicationContext(), "Cet Email est deja utilise veuillez en choisir un autre !",
                                            Toast.LENGTH_SHORT).show();
                                else
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Inscription echouee veuillez reessayer ",
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

        if (TextUtils.isEmpty(filiere)) {
            Toast.makeText(this, "Veuillez choisir votre departement", Toast.LENGTH_SHORT).show();
            valid = false;
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        filiere=adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
